package fr.lirmm.bridge;

import fr.lirmm.bridge.core.BridgeService;
import fr.lirmm.bridge.generated.Func_addition;
import fr.lirmm.bridge.generated.Func_dire_bonjour;

import java.io.File;

/**
 * Cette classe montre comment un développeur Java utilise le pont manuellement.
 */
public class ManualDemo {

    public static void main(String[] args) {
        System.out.println("☕ JAVA: Démarrage de l'application Java...");

        // 1. Définition du chemin vers l'environnement Python
        // On remonte de 4 niveaux par rapport à java-bridge/src/main/java/fr/lirmm/bridge/
        // Mais ici on exécute depuis la racine du projet maven, donc on peut utiliser un chemin relatif
        // Le chemin attendu est le dossier contenant le venv ou l'interpréteur.
        // Dans ce projet : src/bridges/python-env
        // Depuis src/bridges/java-bridge, on remonte d'un cran pour aller dans src/bridges/python-env
        String pythonEnvPath = new File("../python-env").getAbsolutePath();

        System.out.println("☕ JAVA: Utilisation de l'environnement Python : " + pythonEnvPath);

        // 2. Initialisation du Service
        BridgeService service = new BridgeService("GRPC", pythonEnvPath);

        try {
            // --- EXEMPLE 1 : ADDITION ---
            System.out.println("\n--- Test 1 : Appel de 'addition' ---");
            
            // Configuration statique du pont pour la classe générée
            Func_addition.setBridge(service);
            
            // Appel statique
            Object resultatAdd = Func_addition.run(10, 50);
            
            System.out.println("☕ JAVA: J'ai appelé addition(10, 50)");
            System.out.println("☕ JAVA: Python m'a répondu -> " + resultatAdd);


            // --- EXEMPLE 2 : DIRE BONJOUR ---
            System.out.println("\n--- Test 2 : Appel de 'dire_bonjour' ---");
            
            Func_dire_bonjour.setBridge(service);
            
            // Appel avec une chaîne
            Object resultatMsg = Func_dire_bonjour.run("Raphael");
            
            System.out.println("☕ JAVA: J'ai appelé dire_bonjour('Raphael')");
            System.out.println("☕ JAVA: Python m'a répondu -> " + resultatMsg);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 3. Nettoyage
            service.stop(); // C'est .stop() et pas .close() dans votre version
            System.out.println("\n☕ JAVA: Fin du programme. Python arrêté.");
        }
    }
}