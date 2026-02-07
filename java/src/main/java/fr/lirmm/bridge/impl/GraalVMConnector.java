package fr.lirmm.bridge.impl;

import fr.lirmm.bridge.IPythonConnector;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Implémentation du pont utilisant GraalVM Polyglot pour une exécution directe.
 */
public class GraalVMConnector implements IPythonConnector {
    private Context context;

    @Override
    public void connect(String pythonFile) throws Exception {
        System.out.println("[GraalVM] Initialisation du contexte Polyglot...");
        
        File file = new File(pythonFile);
        if (!file.exists()) {
            throw new IllegalArgumentException("Le fichier Python spécifié n'existe pas : " + pythonFile);
        }

        String rootDir = new File("..").getCanonicalPath();
        String grpcImplPath = new File("../src/prototype/grpc_implementation").getCanonicalPath();
        String fileDir = file.getAbsoluteFile().getParent();

        this.context = Context.newBuilder("python")
                .allowAllAccess(true)
                .option("python.Executable", "python3")
                // On ajoute le répertoire du fichier au PythonPath
                .option("python.PythonPath", rootDir + ":" + grpcImplPath + ":" + fileDir)
                .build();

        // Chargement du fichier
        Source source = Source.newBuilder("python", file).build();
        
        // Exécution du script
        this.context.eval(source);
        System.out.println("[GraalVM] Fichier '" + pythonFile + "' chargé avec succès.");
    }

    @Override
    public <T> T execute(String functionName, Class<T> returnType, Object... args) {
        if (context == null) {
            throw new IllegalStateException("Le connecteur GraalVM n'est pas connecté.");
        }

        Value function = context.getBindings("python").getMember(functionName);
        
        if (function == null || !function.canExecute()) {
            throw new RuntimeException("La fonction Python '" + functionName + "' n'est pas exécutable ou n'existe pas.");
        }

        Value result = function.execute(args);
        
        if (returnType == String.class) {
            return returnType.cast(result.asString());
        } else if (returnType == Integer.class || returnType == int.class) {
            return returnType.cast(result.asInt());
        } else if (returnType == Boolean.class || returnType == boolean.class) {
            return returnType.cast(result.asBoolean());
        } else if (returnType == Long.class || returnType == long.class) {
            return returnType.cast(result.asLong());
        } else if (returnType == Double.class || returnType == double.class) {
            return returnType.cast(result.asDouble());
        } else {
            return result.as(returnType);
        }
    }

    @Override
    public void close() {
        if (context != null) {
            System.out.println("[GraalVM] Fermeture du contexte.");
            context.close();
        }
    }
}