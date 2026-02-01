package fr.lirmm.bridge;

import fr.lirmm.bridge.core.BridgeService;
import java.io.File;
import java.util.List;

public class UniversalRunner {
    public static void main(String[] args) throws Exception {
        // Configuration
        String strategy = "GRPC"; // Par défaut
        if (args.length > 0) strategy = args[0];
        
        String rawPath = "src/bridges/python-env";
        if (args.length > 1) rawPath = args[1];
        
        String pythonPath = new File(rawPath).getAbsolutePath();

        // 1. Initialisation du Pont
        System.out.println("🔌 Démarrage du Pont Java-Python (" + strategy + ")...");
        BridgeService bridge = new BridgeService(strategy, pythonPath);

        try {
            // 2. Découverte des fonctions
            List<String> functions = bridge.listFunctions();
            
            if (functions.isEmpty()) {
                System.err.println("⚠️ Aucune fonction @user_func trouvée.");
                System.err.println("👉 Astuce : Définissez une fonction 'main' décorée dans votre fichier Python.");
                return;
            }

            // 3. Exécution de la fonction 'main' (Point d'entrée)
            if (functions.contains("main")) {
                System.out.println("🚀 Exécution de la fonction 'main' pilotée par Java...");
                System.out.println("--------------------------------------------------");
                
                // Appel au main Python via Java
                // On peut passer des arguments si besoin, ici on lance vide
                bridge.call("main");
                
                System.out.println("--------------------------------------------------");
                System.out.println("✅ Exécution terminée avec succès.");
            } else {
                // Mode interactif / Liste
                System.out.println("ℹ️ Pas de fonction 'main' détectée.");
                System.out.println("📂 Fonctions disponibles pour appel Java : " + functions);
                System.out.println("👉 Pour lancer un script complet, nommez votre fonction principale 'main'.");
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur critique lors de l'exécution : " + e.getMessage());
            e.printStackTrace();
        } finally {
            bridge.stop();
            System.exit(0);
        }
    }
}
