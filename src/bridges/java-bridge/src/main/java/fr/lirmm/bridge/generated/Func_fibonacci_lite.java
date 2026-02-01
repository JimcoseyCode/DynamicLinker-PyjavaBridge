package fr.lirmm.bridge.generated;

import fr.lirmm.bridge.core.BridgeService;
import java.util.Arrays;
import java.util.List;

/**
 * AUTO-GENERATED WRAPPER FOR PYTHON FUNCTION 'fibonacci_lite'
 * Source: @user_func in Python
 */
public class Func_fibonacci_lite {

    private static BridgeService bridge;

    public static void setBridge(BridgeService b) {
        bridge = b;
    }

    /**
     * Executes the python function 'fibonacci_lite' transparently.
     * @param args Arguments to pass to Python
     * @return Result from Python (typed as Object, cast as needed)
     */
    public static Object run(Object... args) {
        if (bridge == null) {
            throw new RuntimeException("Bridge not initialized! Call Func_fibonacci_lite.setBridge(service) first.");
        }
        try {
            return bridge.call("fibonacci_lite", args);
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute python function 'fibonacci_lite'", e);
        }
    }
    
    /**
     * Convenience method expecting a List, matching the old interface.
     */
    public static Object run(List<Object> args) {
        return run(args.toArray());
    }
}
