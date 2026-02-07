package fr.lirmm.bridge.impl;

import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;

import fr.lirmm.bridge.BridgeServiceGrpc;
import fr.lirmm.bridge.FunctionCall;
import fr.lirmm.bridge.FunctionResult;
import fr.lirmm.bridge.IPythonConnector;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

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
        System.out.println(
                "[gRPC] Note: Le serveur gRPC charge les fichiers automatiquement depuis son répertoire racine.");
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.stub = BridgeServiceGrpc.newBlockingStub(channel);
    }

    private String executeRaw(String functionName, Object... args) {
        if (stub == null) {
            throw new IllegalStateException("Le connecteur gRPC n'est pas connecté. Appelez connect() d'abord.");
        }
        FunctionCall request = FunctionCall.newBuilder()
                .setFunctionName(functionName)
                .setArgsJson(gson.toJson(args))
                .build();

        FunctionResult response = stub.execute(request);

        if (!response.getSuccess()) {
            throw new RuntimeException("Erreur Python [" + functionName + "]: " + response.getErrorMessage());
        }

        return response.getResultJson();
    }

    @Override
    public <T> T execute(String functionName, Class<T> returnType, Object... args) {
        String jsonResult = executeRaw(functionName, args);
        return gson.fromJson(jsonResult, returnType);
    }

    @Override
    public void close() {
        if (channel != null) {
            try {
                channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}