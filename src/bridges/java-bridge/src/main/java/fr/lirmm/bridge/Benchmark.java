package fr.lirmm.bridge;

import fr.lirmm.bridge.core.PythonExecutor;
import fr.lirmm.bridge.impl.grpc.GrpcPythonExecutor;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class Benchmark {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: Benchmark <strategy> <pythonPath>");
            return;
        }
        String strategy = "GRPC"; // Forcé
        String pythonPath = args[1];

        // Seul gRPC est supporté
        PythonExecutor executor = new GrpcPythonExecutor(pythonPath);

        try {
            executor.initialize();
            
            System.out.println("=== Starting Dynamic Benchmark (" + strategy + ") ===");
            
            // 1. Discovery
            List<String> functions = executor.getAvailableFunctions();
            System.out.println("🔍 Fonctions trouvées : " + functions);
            
            if (functions.isEmpty()) {
                System.out.println("⚠️ Aucune fonction trouvée. Avez-vous décoré vos fonctions avec @user_func ?");
            }

            // 2. Benchmarking loop
            for (String func : functions) {
                System.out.println("\n------------------------------------------------");
                System.out.println("⚡ Test de performance : " + func);
                
                // Heuristic simple arguments: try integers list
                // In a real system, we might need metadata about arguments
                List<Object> testArgs = Arrays.asList(10); 
                
                try {
                    // Warmup
                    System.out.print("   Warmup...");
                    for(int i=0; i<5; i++) executor.execute(func, testArgs);
                    System.out.println(" OK");

                    // Measure
                    int iterations = 100;
                    long start = System.nanoTime();
                    for (int i = 0; i < iterations; i++) {
                        executor.execute(func, testArgs);
                    }
                    long end = System.nanoTime();

                    double totalMs = (end - start) / 1_000_000.0;
                    double latency = totalMs / iterations;
                    
                    System.out.printf("   ✅ Résultat (%d iter) : %.3f ms/appel\n", iterations, latency);
                    
                } catch (Exception e) {
                    System.out.println("   ❌ Echec du test (arguments incompatibles ?): " + e.getMessage());
                }
            }
            
        } finally {
            executor.close();
            // Force kill for gRPC threads
            System.exit(0);
        }
    }
}