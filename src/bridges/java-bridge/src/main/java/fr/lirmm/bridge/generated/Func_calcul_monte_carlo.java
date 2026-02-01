package fr.lirmm.bridge.generated;

import fr.lirmm.bridge.core.BridgeService;
import java.util.Arrays;
import java.util.List;

/**
 * AUTO-GENERATED WRAPPER FOR PYTHON FUNCTION 'calcul_monte_carlo'
 * Source: @user_func in Python
 */
public class Func_calcul_monte_carlo {

    private static BridgeService bridge;

    public static void setBridge(BridgeService b) {
        bridge = b;
    }

    /**
     * Executes the python function 'calcul_monte_carlo' transparently.
     * @param args Arguments to pass to Python
     * @return Result from Python (typed as Object, cast as needed)
     */
    public static Object run(Object... args) {
        if (bridge == null) {
            throw new RuntimeException("Bridge not initialized! Call Func_calcul_monte_carlo.setBridge(service) first.");
        }
        try {
            return bridge.call("calcul_monte_carlo", args);
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute python function 'calcul_monte_carlo'", e);
        }
    }
    
    /**
     * Convenience method expecting a List, matching the old interface.
     */
    public static Object run(List<Object> args) {
        return run(args.toArray());
    }
}
