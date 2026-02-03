package fr.lirmm.bridge;

import java.util.List;
import java.util.Map;

public class Client {
    public static void main(String[] args) {
        System.out.println("Test des appels de fonction python depuis Java ");

        try (PythonBridge bridge = new PythonBridge()) {
            System.out.println(bridge.execute("sayHello", String.class, "Raphael"));
            Map user = bridge.execute("get_user_info", Map.class, 18);
            System.out.println("Informations user (18):  " + user);
            System.out.println(user);
            // Liste de fibonnaci
            System.out.println("fibonacci(10):   " + bridge.execute("fibonacci", List.class, 10));
            System.out.println("puissance(2,2): " + bridge.execute("puissance", Integer.class, 2, 2));

        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
