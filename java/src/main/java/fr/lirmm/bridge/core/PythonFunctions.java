package fr.lirmm.bridge.core;

/**
 * Interface GENEREE AUTOMATIQUEMENT par tools/generate_interface.py
 * Basée sur les décorateurs @user_func et les Type Hints Python.
 */
public interface PythonFunctions {
    java.util.List<Object> fibonacci(Integer n);
    Integer multiply(Integer a, Integer b);
    Integer puissance(Integer a, Integer b);
    String putain(String name);
    String reverse_string(String s);
    String sayHello(String name);
}
