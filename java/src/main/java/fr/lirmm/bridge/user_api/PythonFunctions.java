package fr.lirmm.bridge.user_api;

/**
 * Interface Generée automatique /scrips/generate_interface.py
 * Basée sur les décorateurs @user_func et les Type Hints Python ou sans typagee en python mais qui seront du type object en java qui
    qui est un type heritée par toute les .
 */
public interface PythonFunctions {
    Object LOL(Object name);
    Double calculer_integrale(Double a, Double b, Integer n);
    java.util.List<Object> fibonacci(Integer n);
    Integer multiply(Integer a, Integer b);
    Integer puissance(Integer a, Integer b);
    String reverse_string(String s);
    String sayHello(String name);
}
