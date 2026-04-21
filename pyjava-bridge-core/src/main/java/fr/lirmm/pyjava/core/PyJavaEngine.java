package fr.lirmm.pyjava.core;

import fr.lirmm.pyjava.api.PyJavaBridge;
import fr.lirmm.pyjava.connectors.JepConnector;
import fr.lirmm.pyjava.connectors.GrpcConnector;
import fr.lirmm.pyjava.connectors.GraalvmConnector;
import fr.lirmm.pyjava.linker.DynamicLinker;
import org.json.JSONObject;

/**
 * Moteur principal orchestrant les connecteurs et la liaison dynamique.
 */
public class PyJavaEngine extends DynamicLinker implements PyJavaBridge {

    private final IPythonConnector connector;

    public PyJavaEngine(JSONObject config) {
        super(config);

        String mode = config.optString("bridge_mode", "jep").toLowerCase();
        int grpcPort = config.optInt("grpc_port", 50051);

        this.connector = switch (mode) {
            case "grpc" -> new GrpcConnector("localhost", grpcPort);
            case "graalvm" -> new GraalvmConnector();
            default -> new JepConnector();
        };
    }

    // ?[Dynamic-Linker] -> en cours de developpement
    @Override
    public <T> T pyFunc(Class<T> interfaceClass) {
        return this.bind(interfaceClass);
    }

    @Override
    public Object invoke(String moduleName, String functionName, Object... args) {
        return invoke(moduleName, functionName, Object.class, args);
    }

    @Override
    public <T> T invoke(String moduleName, String functionName, Class<T> returnType, Object... args) {
        // On délègue directement au connecteur sans répétition de logique
        return connector.invoke(moduleName, functionName, returnType, args);
    }

    @Override
    public void close() {
        if (connector != null)
            connector.close();
    }
}