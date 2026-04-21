package fr.lirmm.pyjava.connectors;

import fr.lirmm.pyjava.core.IPythonConnector;
import fr.lirmm.pyjava.grpc.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

// ![CONNECTOR] -> gRPC
public class GrpcConnector implements IPythonConnector {
    private final ManagedChannel transport;
    private final BridgeServiceGrpc.BridgeServiceBlockingStub remoteExecutor;

    public GrpcConnector(String host, int port) {
        this.transport = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        this.remoteExecutor = BridgeServiceGrpc.newBlockingStub(transport);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T invoke(String module, String function, Class<T> returnType, Object... args) {
        // ? [INFO] -> preparation du payload du transit entre le serveur python et java
        var request = FunctionRequest.newBuilder()
                .setFunctionName(module + "." + function)
                .setArgsJson(new JSONArray(args).toString())
                .build();

        // invoque de la fonction python
        FunctionResult response = remoteExecutor.execute(request);

        if (!response.getSuccess()) {
            throw new RuntimeException("[gRPC Python Error] " + response.getErrorMessage());
        }

        if (returnType == void.class || returnType == Void.class)
            return null;

        String rawResult = response.getResultJson();
        return castResult(rawResult, returnType);
    }

    /**
     * ? Convertit le JSON reçu de Python vers le type Java attendu.
     */
    @SuppressWarnings("unchecked")
    private <T> T castResult(String json, Class<T> targetType) {
        if (targetType == String.class)
            return (T) json.replaceAll("^\"|\"$", "");

        // ? [INFO] -> Utilisation d'un conteneur temporaire pour parser n'importe quel
        // * type (Numérique, Map, List)
        Object parsed = new JSONObject("{\"tmp\":" + json + "}").get("tmp");
        // * cast le type python avec type_match java
        if (parsed instanceof Number num) {
            if (targetType == Long.class || targetType == long.class)
                return (T) Long.valueOf(num.longValue());
            if (targetType == Double.class || targetType == double.class)
                return (T) Double.valueOf(num.doubleValue());
            if (targetType == Integer.class || targetType == int.class)
                return (T) Integer.valueOf(num.intValue());
        }
        if (parsed instanceof JSONObject jo && targetType == java.util.Map.class)
            return (T) jo.toMap();
        if (parsed instanceof JSONArray ja && targetType == java.util.List.class)
            return (T) ja.toList();

        return (T) parsed;
    }

    @Override
    public void close() {
        if (transport != null)
            transport.shutdown();
    }
}