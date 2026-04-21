package fr.lirmm.pyjava.connectors;

import fr.lirmm.pyjava.core.IPythonConnector;
import io.github.cdimascio.dotenv.Dotenv;
import jep.SharedInterpreter;
import jep.Interpreter;
import jep.MainInterpreter;

import java.io.File;
import java.nio.file.Paths;

// ? [CONNECTOR] -> JEP
public class JepConnector implements IPythonConnector {

    private final Interpreter pythonEngine;
    private static boolean jepInitialized = false;

    public JepConnector() {
        Dotenv dotenv = Dotenv.load();
        String jepLibPath = dotenv.get("JEP_LIBRARY_PATH");
        String sitePackages = dotenv.get("PYTHON_SITE_PACKAGES");
        String venvPath = dotenv.get("VIRTUAL_ENV");
        // ! Verification et set la lib jep pour qu'il soit recconu par L interpreter
        // partagée
        if (!jepInitialized && jepLibPath != null) {
            if (new File(jepLibPath).exists()) {
                try {
                    MainInterpreter.setJepLibraryPath(jepLibPath);
                    jepInitialized = true;
                } catch (Exception e) {
                    System.err.println("[JeP] Échec du chargement de la lib native : " + e.getMessage());
                }
            } else {
                System.err.println("[JeP] Chemin introuvable : " + jepLibPath);
            }
        }
        // Init de l'interpreteur python
        this.pythonEngine = new SharedInterpreter();

        // Configuration de l'environnement python
        configurePythonEnvironment(sitePackages);
        // ? chargment des modules python mais que de maniere dynamqiue pour faciliter
        // le demarage lazy loading modules que lors de l'execution
        loadPythonModules();
    }

    private void configurePythonEnvironment(String sitePackages) {
        String rootDir = Paths.get(".").toAbsolutePath().normalize().toString().replace("\\", "/");
        pythonEngine.exec("import sys; import os;");
        // Ajout de la racine du projet (pour trouver tes modules locaux)
        pythonEngine.exec("sys.path.append('" + rootDir + "')");

        // Ajout du dossier site-packages du venv (pour trouver Jep, gRPC, etc.)
        if (sitePackages != null) {
            pythonEngine.exec("sys.path.append('" + sitePackages.replace("\\", "/") + "')");
            // System.out.println(" [JeP] Site-packages chargé : " + sitePackages);
        } else {
            System.err.println("[JeP] Attention : PYTHON_SITE_PACKAGES est absent du .env");
        }
    }

    private void loadPythonModules() {
        try {
            // Importation des outils de base du bridge
            pythonEngine.exec("import pyjava.decorators");
            pythonEngine.exec("import pyjava.module_loader");

            // Accès au dictionnaire des fonctions décorées
            pythonEngine.exec("exposed = pyjava.decorators.EXPOSED_FUNCTIONS");
            System.out.println("[JeP] Bridge prêt (Lazy Loading activé).");
        } catch (Exception e) {
            System.err.println("[JeP] Erreur lors de l'initialisation : " + e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T invoke(String module, String function, Class<T> returnType, Object... args) {
        String functionName = getQualifiedName(module, function);
        try {
            // Tentative de chargement dynamique si la fonction n'est pas encore enregistrée
            if (functionName.contains(".")) {
                String moduleName = functionName.substring(0, functionName.lastIndexOf('.'));
                pythonEngine.exec("pyjava.module_loader.load_module_by_name('" + moduleName + "')");
            }

            // On passe les arguments Java à Python
            pythonEngine.set("__tmp_args", args);

            // Appel de la fonction via le dictionnaire 'exposed'
            Object result = pythonEngine.getValue("exposed['" + functionName + "'](*__tmp_args)");

            // Gestion du type de retour
            if (returnType == void.class || returnType == Void.class)
                return null;

            if (result instanceof Number num) {
                if (returnType == Long.class || returnType == long.class)
                    return (T) Long.valueOf(num.longValue());
                if (returnType == Integer.class || returnType == int.class)
                    return (T) Integer.valueOf(num.intValue());
                if (returnType == Double.class || returnType == double.class)
                    return (T) Double.valueOf(num.doubleValue());
                if (returnType == Float.class || returnType == float.class)
                    return (T) Float.valueOf(num.floatValue());
            }

            return (T) result;
        } catch (Exception e) {
            throw new RuntimeException("[JeP] Erreur d'exécution (" + functionName + ") : " + e.getMessage(), e);
        }
    }

    @Override
    public void close() {
        if (interp != null) {
            pythonEngine.close();
            System.out.println("[JeP] Interpréteur fermé.");
        }
    }
}