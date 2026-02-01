package fr.lirmm.bridge.impl.grpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.lirmm.bridge.core.PythonExecutor;
import fr.lirmm.bridge.grpc.BridgeServiceGrpc;
import fr.lirmm.bridge.grpc.FunctionRequest;
import fr.lirmm.bridge.grpc.FunctionResponse;
import fr.lirmm.bridge.grpc.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GrpcPythonExecutor implements PythonExecutor {

    private final String pythonEnvPath;
    private Process serverProcess;
    private ManagedChannel channel;
    private BridgeServiceGrpc.BridgeServiceBlockingStub stub;
    private final ObjectMapper mapper = new ObjectMapper();
    private static final int PORT = 50051;

    public GrpcPythonExecutor(String pythonEnvPath) {
        this.pythonEnvPath = pythonEnvPath;
    }

    @Override
    public void initialize() {
        try {
            // 1. Start Python Server
            startServerProcess();
            
            // 2. Create Channel
            channel = ManagedChannelBuilder.forAddress("localhost", PORT)
                    .usePlaintext()
                    .build();
            stub = BridgeServiceGrpc.newBlockingStub(channel);

            // 3. Health check / Wait for connection
            boolean connected = false;
            for (int i = 0; i < 10; i++) {
                try {
                    stub.listFunctions(Empty.newBuilder().build());
                    System.out.println("✅ Connected to Python gRPC Server!");
                    connected = true;
                    break;
                } catch (Exception e) {
                    System.out.println("⏳ Waiting for Python gRPC server...");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            if (!connected) {
                 throw new RuntimeException("Could not connect to Python gRPC Server.");
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize gRPC Bridge", e);
        }
    }

    private void startServerProcess() throws IOException {
        String pythonExec = new File(pythonEnvPath, "venv/bin/python").getAbsolutePath();
        File script = new File(pythonEnvPath, "app/grpc_server.py");
        
        if (!new File(pythonExec).exists()) {
             pythonExec = "python3";
        }

        System.out.println("🚀 Starting Python gRPC Server: " + pythonExec + " " + script.getAbsolutePath());

        ProcessBuilder pb = new ProcessBuilder(pythonExec, script.getAbsolutePath());
        pb.inheritIO(); 
        serverProcess = pb.start();
    }

    @Override
    public Object execute(String functionName, List<Object> args) throws Exception {
        String argsJson = mapper.writeValueAsString(args);
        
        FunctionRequest request = FunctionRequest.newBuilder()
                .setFunctionName(functionName)
                .setArgsJson(argsJson)
                .build();
        
        FunctionResponse response = stub.execute(request);
        
        if (!response.getSuccess()) {
            throw new RuntimeException("Python Error: " + response.getErrorMessage());
        }
        
        return mapper.readValue(response.getResultJson(), Object.class);
    }

    @Override
    public List<String> getAvailableFunctions() throws Exception {
        try {
            return stub.listFunctions(Empty.newBuilder().build()).getFunctionNamesList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public void close() {
        if (channel != null) {
            channel.shutdown();
            try {
                if (!channel.awaitTermination(5, TimeUnit.SECONDS)) {
                    channel.shutdownNow();
                }
            } catch (InterruptedException e) {
                channel.shutdownNow();
            }
        }
        if (serverProcess != null) {
            serverProcess.destroy();
        }
    }

    @Override
    public String getStrategyName() {
        return "gRPC";
    }
}
