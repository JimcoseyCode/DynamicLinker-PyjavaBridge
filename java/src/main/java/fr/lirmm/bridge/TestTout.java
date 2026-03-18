package fr.lirmm.bridge;

import fr.lirmm.bridge.core.PythonBridge;
import fr.lirmm.bridge.core.PythonConnectorFactory;
import fr.lirmm.bridge.core.PythonConnectorFactory.Prototype;
import fr.lirmm.bridge.core.PythonFunctions;

/**
 * Client de test complet pour vérifier chaque prototype du pont.
 */
public class TestTout {

    public static void main(String[] args) {
        // Détection intelligente du fichier
        String fichierPython = new java.io.File("user_func.py").exists() ? "user_func.py" : "../user_func.py";

        System.out.println("Utilisation du fichier : " + new java.io.File(fichierPython).getAbsolutePath());
        System.out.println("=== DÉBUT DES TESTS DES PROTOTYPES ===");

        // 1. Test du mode gRPC (Standard)
        testerPrototype(Prototype.GRPC, fichierPython);

        // 2. Test du mode GraalVM (Natif Polyglot)
        // Note: Nécessite un JDK GraalVM avec le composant Python installé.
        try {
            testerPrototype(Prototype.GRAAL, fichierPython);
        } catch (Throwable e) {
            System.err.println("[Test] Échec GraalVM (Normal si pas sur un JDK GraalVM): " + e.getMessage());
        }

        // 3. Test du mode REP / JEP (Java Embedded Python)
        // Note: Nécessite la bibliothèque native JEP installée sur le système.
        try {
            testerPrototype(Prototype.REP, fichierPython);
        } catch (Throwable e) {
            System.err.println("[Test] Échec REP/JEP (Normal si JEP n'est pas installé): " + e.getMessage());
        }

        System.out.println("\n=== FIN DES TESTS ===");
    }

    private static void testerPrototype(Prototype type, String fichier) {
        System.out.println("\n--- TEST DU MODE : " + type + " ---");

        try (PythonBridge bridge = PythonConnectorFactory.createBridge(type, fichier)) {
            PythonFunctions api = bridge.getApi(PythonFunctions.class);

            // Test simple
            int resultat = api.puissance(2, 3);
            System.out.println("[+] " + type + " : puissance(2, 3) = " + resultat);

            if (resultat == 8) {
                System.out.println("✅ SUCCÈS pour " + type);
            } else {
                System.out.println("❌ ÉCHEC pour " + type + " (Résultat incorrect)");
            }

            // Test de chaîne
            String hello = api.sayHello("Étudiant");
            System.out.println("[+] " + type + " : sayHello = " + hello);

        } catch (Exception e) {
            System.err.println("❌ ERREUR lors du test " + type + " : " + e.getMessage());
        }
    }
}
