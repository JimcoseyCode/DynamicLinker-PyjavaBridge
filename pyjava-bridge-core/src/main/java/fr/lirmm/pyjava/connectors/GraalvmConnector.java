package fr.lirmm.pyjava.connectors;

import fr.lirmm.pyjava.core.IPythonConnector;
import io.github.cdimascio.dotenv.Dotenv;
import org.graalvm.polyglot.*;
import java.nio.file.Paths;

// ! [CONNECTOR] -> graalVM
public class GraalvmConnector implements IPythonConnector {
    private final Context polyglotEngine;

    public GraalvmConnector() {
        // * Initialisation du moteur d'exécution le context
        this.polyglotEngine = Context.newBuilder("python")
                .allowAllAccess(true)
                .option("python.ForceImportSite", "true")
                .build();

        // * Configuration de l'environnement (Chemins et Dépendances)
        Dotenv config = Dotenv.load();
        String projectRoot = Paths.get("").toAbsolutePath().normalize().toString().replace("\\", "/");
        String venvPackages = config.get("PYTHON_SITE_PACKAGES", "").replace("\\", "/");
        polyglotEngine.eval("python", String.format(
                "import sys; sys.path.extend(['%s', '%s']); " +
                        "import pyjava.decorators, pyjava.module_loader",
                projectRoot, venvPackages));
        System.out.println("[GraalVM] Connecteur prêt et environnement configuré.");
    }

    @Override
    public <T> T invoke(String moduleName, String methodName, Class<T> returnType, Object... params) {
        String qualifiedName = moduleName + "." + methodName;
        try {
            // * -> Chargement dynamique du module par son noms l'un des fonction utilitaire
            // * exploitant le config.json
            polyglotEngine.eval("python", "pyjava.module_loader.load_module_by_name('" + moduleName + "')");

            // * Localisation de la fonction dans le registre des fonctions exposées
            Value registry = polyglotEngine.eval("python", "pyjava.decorators.EXPOSED_FUNCTIONS");
            Value targetFunction = registry.getHashValue(Value.asValue(qualifiedName));

            if (targetFunction == null || targetFunction.isNull()) {
                targetFunction = registry.getHashValue(Value.asValue(methodName));
            }

            if (targetFunction == null || targetFunction.isNull()) {
                throw new RuntimeException("Fonction non enregistrée : " + qualifiedName);
            }

            // * Exécution et conversion automatique du résultat vers le type Java
            Value pyResult = targetFunction.execute(params);

            return (returnType == void.class || returnType == Void.class)
                    ? null
                    : pyResult.as(returnType);

        } catch (Exception e) {
            throw new RuntimeException("[GraalVM Error] Échec de l'appel à " + qualifiedName, e);
        }
    }

    @Override
    public void close() {
        if (polyglotEngine != null) {
            polyglotEngine.close();
            System.out.println("[GraalVM] Moteur polyglotte arrêté.");
        }
    }
}