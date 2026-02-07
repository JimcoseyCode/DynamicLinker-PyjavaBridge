package fr.lirmm.bridge;

import java.util.List;
import java.util.Map;

public class Client {
    public static void main(String[] args) {
        String prototypeArg = "rep";
        String fileArg = "../custom_test.py"; // Par défaut

        for (int i = 0; i < args.length; i++) {
            if ("--prototype".equals(args[i]) && i + 1 < args.length) {
                prototypeArg = args[i + 1];
            } else if ("--file".equals(args[i]) && i + 1 < args.length) {
                fileArg = args[i + 1];
            }
        }

        PythonConnectorFactory.Prototype prototype = PythonConnectorFactory.fromString(prototypeArg);
        System.out.println("Démarrage avec le prototype : " + prototype);
        System.out.println("Fichier cible : " + fileArg);

        try (IPythonConnector bridge = PythonConnectorFactory.createConnector(prototype)) {
            bridge.connect(fileArg);

            System.out.println("--- Test des appels de fonction python depuis java ---");

            try {
                String hello = bridge.execute("sayHello", String.class, "Raphael");
                System.out.println("func (sayHello) : " + hello);
            } catch (Exception e) {
                System.out.println("sayHello non disponible ou erreur : " + e.getMessage());
            }

            try {
                Map user = bridge.execute("get_user_info", Map.class, 18);
                System.out.println("get_user_info(18): " + user);
            } catch (Exception e) {
                System.out.println("get_user_info non disponible ou erreur : " + e.getMessage());
            }

            try {
                List fibo = bridge.execute("fibonacci", List.class, 10);
                System.out.println("fibonacci(10): " + fibo);
            } catch (Exception e) {
                System.out.println("fibonacci non disponible ou erreur : " + e.getMessage());
            }

            try {
                Integer p = bridge.execute("puissance", Integer.class, 2, 2);
                System.out.println("puissance(2,2): " + p);
            } catch (Exception e) {
                System.out.println("puissance non disponible ou erreur : " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("Erreur fatale : " + e.getMessage());
            e.printStackTrace();
        }
    }
}