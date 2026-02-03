package fr.lirmm.bridge;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import com.google.gson.Gson;
import java.io.Closeable;
import java.util.concurrent.TimeUnit;

public class PythonBridge implements Closeable {
    private final ManagedChannel channel;
    private final BridgeServiceGrpc.BridgeServiceBlockingStub stub;
    private final Gson gson;

    public PythonBridge() {
        this("localhost", 50051);
    }

    public PythonBridge(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.stub = BridgeServiceGrpc.newBlockingStub(channel);
        this.gson = new Gson();
    }

    public String execute(String functionName, Object... args) {
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

    public <T> T execute(String functionName, Class<T> returnType, Object... args) {
        String jsonResult = execute(functionName, args);
        return gson.fromJson(jsonResult, returnType);
    }

    @Override
    public void close() {
        try {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
