package fr.lirmm.bridge.core.impl.grpc;

import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;

import fr.lirmm.bridge.FunctionResult;
import fr.lirmm.bridge.core.BridgeServiceGrpc;
import fr.lirmm.bridge.core.FunctionCall;
import fr.lirmm.bridge.core.IPythonConnector;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * Connecteur gRPC standard : simple, robuste et performant.
 */
public class GRPCConnector implements IPythonConnector {
    private ManagedChannel channel;
    private BridgeServiceGrpc.BridgeServiceBlockingStub stub;
    private final Gson gson = new Gson();
    private final String host;
    private final int port;

    public GRPCConnector() {
        this("localhost", 50051);
    }

    public GRPCConnector(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void connect(String pythonFile) throws Exception {
        System.out.println("[gRPC] Connexion au serveur (" + host + ":" + port + ")...");
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.stub = BridgeServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public <T> T execute(String functionName, Class<T> returnType, Object... args) {
        if (stub == null) {
            throw new IllegalStateException("Le connecteur gRPC n'est pas connecté. Appelez connect() d'abord.");
        }

        // Java -> Proto (JSON)
        FunctionCall request = FunctionCall.newBuilder()
                .setFunctionName(functionName)
                .setArgsJson(gson.toJson(args))
                .build();

        // Appel gRPC (standard)
        FunctionResult response = stub.withDeadlineAfter(5, TimeUnit.SECONDS).execute(request);

        if (!response.getSuccess()) {
            throw new RuntimeException("Erreur Python [" + functionName + "]: " + response.getErrorMessage());
        }

        // Proto -> Java (JSON)
        return gson.fromJson(response.getResultJson(), returnType);
    }

    @Override
    public void close() {
        if (channel != null) {
            try {
                channel.shutdown().awaitTermination(2, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
