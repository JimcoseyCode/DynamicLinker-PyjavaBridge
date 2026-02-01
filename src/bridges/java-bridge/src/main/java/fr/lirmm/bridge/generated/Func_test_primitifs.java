package fr.lirmm.bridge.generated;
import java.util.*;
public class Func_test_primitifs {
    public static Object run(List args) {
        String s = (String)args.get(0);
        int i = ((Number)args.get(1)).intValue();
        double d = ((Number)args.get(2)).doubleValue();
        boolean b = (Boolean)args.get(3);
        return "Java a reçu: String=" + s + ", Int=" + i + ", Double=" + d + ", Bool=" + b;
    }
}
