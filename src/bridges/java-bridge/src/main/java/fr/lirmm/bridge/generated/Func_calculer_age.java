package fr.lirmm.bridge.generated;

import fr.lirmm.bridge.core.BridgeService;
import java.util.Arrays;
import java.util.List;

/**
 * AUTO-GENERATED WRAPPER FOR PYTHON FUNCTION 'calculer_age'
 * Source: @user_func in Python
 */
public class Func_calculer_age {

    private static BridgeService bridge;

    public static void setBridge(BridgeService b) {
        bridge = b;
    }

    /**
     * Executes the python function 'calculer_age' transparently.
     * @param args Arguments to pass to Python
     * @return Result from Python (typed as Object, cast as needed)
     */
    public static Object run(Object... args) {
        if (bridge == null) {
            throw new RuntimeException("Bridge not initialized! Call Func_calculer_age.setBridge(service) first.");
        }
        try {
            return bridge.callPython("calculer_age", args);
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute python function 'calculer_age'", e);
        }
    }
    
    /**
     * Convenience method expecting a List, matching the old interface.
     */
    public static Object run(List<Object> args) {
        return run(args.toArray());
    }
}
