package fr.lirmm.bridge.impl;

import fr.lirmm.bridge.IPythonConnector;
import jep.SharedInterpreter;
import java.io.File;

/**
 * Implémentation du pont pour le prototype "Rep" (utilisant JEP).
 */
public class RepConnector implements IPythonConnector {
    private SharedInterpreter interp;

    @Override
    public void connect(String pythonFile) throws Exception {
        System.out.println("[Rep/JEP] Initialisation du SharedInterpreter...");
        
        File file = new File(pythonFile);
        if (!file.exists()) {
            throw new IllegalArgumentException("Le fichier Python spécifié n'existe pas : " + pythonFile);
        }

        this.interp = new SharedInterpreter();
        
        String absolutePath = file.getAbsoluteFile().getParent();
        String fileName = file.getName();
        String moduleName = fileName.endsWith(".py") ? fileName.substring(0, fileName.length() - 3) : fileName;

        interp.exec("import sys");
        interp.exec("sys.path.append('" + absolutePath + "')");
        // On importe le module spécifié
        interp.exec("from " + moduleName + " import *");
        
        System.out.println("[Rep/JEP] Fichier '" + pythonFile + "' chargé.");
    }

    @Override
    public <T> T execute(String functionName, Class<T> returnType, Object... args) {
        if (interp == null) {
            throw new IllegalStateException("Le connecteur JEP n'est pas connecté.");
        }

        try {
            interp.set("args", args);
            interp.exec("result = " + functionName + "(*args)");
            Object result = interp.getValue("result");
            
            if (result == null) return null;
            
            if (returnType.isInstance(result)) {
                return returnType.cast(result);
            } else if (result instanceof Long && (returnType == Integer.class || returnType == int.class)) {
                return returnType.cast(((Long) result).intValue());
            } else if (result instanceof Double && (returnType == Float.class || returnType == float.class)) {
                return returnType.cast(((Double) result).floatValue());
            } else {
                return (T) result;
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur JEP lors de l'exécution de " + functionName + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void close() {
        if (interp != null) {
            System.out.println("[Rep/JEP] Fermeture de l'interpréteur.");
            interp.close();
        }
    }
}
