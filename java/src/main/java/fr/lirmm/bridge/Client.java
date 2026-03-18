package fr.lirmm.bridge;

import fr.lirmm.bridge.core.PythonBridge;
import fr.lirmm.bridge.core.PythonConnectorFactory;
import fr.lirmm.bridge.core.PythonFunctions;

/**
 * Client de démonstration du flux de travail "Zéro Compilation".
 */
public class Client {
    public static void main(String[] args) {
        // Fichier principal par défaut
        String fichierPython = "./user_func.py";

        System.out.println("=== BRIDGE JAVA <-> PYTHON (Zéro Compilation) ===");

        try (PythonBridge bridge = PythonConnectorFactory.createBridge(
                PythonConnectorFactory.Prototype.GRPC,
                fichierPython)) {
            // --- OPTION 1 : L'appel via Proxy (Propre, mais nécessite compilation) ---
            System.out.println("\n[Proxy] Appel via interface PythonFunctions.java :");
            PythonFunctions api = bridge.getApi(PythonFunctions.class);
            System.out.println(" Fibonacci(5) = " + api.fibonacci(5));

            // --- OPTION 2 : L'appel DYNAMIQUE (Ultra-rapide, aucune compilation
            // nécessaire) ---
            // On peut appeler N'IMPORTE QUELLE fonction Python par son nom,
            // même si elle n'est pas dans PythonFunctions.java !

            System.out.println("\n[Dynamique] Appel d'une fonction sans interface (bridge.call) :");

            // Essayons d'appeler 'multiply' (déjà là) mais sans interface
            Object res1 = bridge.call("multiply", 10, 5);
            System.out.println("   Appel 'multiply' (10*5) = " + res1);

            // Appel avec typage automatique du retour
            String resHello = bridge.execute("sayHello", String.class, "Étudiant");
            System.out.println("   Appel 'sayHello' = " + resHello);

            System.out.println("\nCONSEIL : Si vous ajoutez une nouvelle fonction en Python, ");
            System.out.println("utilisez 'bridge.call(\"ma_fonction\", args)' pour la tester tout de suite !");

        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
