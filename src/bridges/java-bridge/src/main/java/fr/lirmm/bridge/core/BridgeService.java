package fr.lirmm.bridge.core;

import fr.lirmm.bridge.impl.grpc.GrpcPythonExecutor;
import java.util.Arrays;
import java.util.List;
import java.lang.reflect.Method;

public class BridgeService {
    private final PythonExecutor executor;

    public BridgeService(String strategy, String pythonPath) {
        // Stratégie unique : gRPC
        this.executor = new GrpcPythonExecutor(pythonPath);
        
        this.executor.initialize();
        initializeProxies();
    }

    private void initializeProxies() {
        try {
            // On tente d'initialiser tous les proxies générés pour qu'ils aient accès au bridge
            // Note: Dans une version réelle on scannerait le package generated
            String[] commonFuncs = {"main", "calcul_intensif", "calcul_python_pur"};
            for (String f : commonFuncs) {
                try {
                    Class<?> clazz = Class.forName("fr.lirmm.bridge.generated.Func_" + f);
                    Method setBridge = clazz.getMethod("setBridge", BridgeService.class);
                    setBridge.invoke(null, this);
                } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object call(String functionName, Object... args) throws Exception {
        // --- PRIORITÉ À L'OPTIMISATION NATIVE ---
        try {
            String className = "fr.lirmm.bridge.generated.Func_" + functionName;
            Class<?> clazz = Class.forName(className);
            
            // On vérifie si c'est une implémentation native (via un test simple ou juste existence)
            Method method = clazz.getMethod("run", Object[].class);
            
            // Si on est ici, on exécute en Java
            return method.invoke(null, (Object) args);
            
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            // --- FALLBACK VERS PYTHON ---
            return callPython(functionName, args);
        }
    }

    /**
     * Appelle directement la fonction Python sans vérifier l'existence d'une classe Java locale.
     * Utilisé par les proxies générés pour éviter la récursion infinie.
     */
    public Object callPython(String functionName, Object... args) throws Exception {
        return executor.execute(functionName, Arrays.asList(args));
    }

    public List<String> listFunctions() throws Exception {
        return executor.getAvailableFunctions();
    }

    public void stop() {
        executor.close();
    }
}