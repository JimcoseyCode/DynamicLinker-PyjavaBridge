package fr.lirmm.bridge.generated;
import java.util.*;
public class Func_fibonacci_performance {
    public static Object run(List args) {
        int n = ((Number)args.get(0)).intValue();
        if (n <= 1) return n;
        long a=0, b=1; for(int i=2; i<=n; i++) { long t=a+b; a=b; b=t; }
        return b;
    }
}
