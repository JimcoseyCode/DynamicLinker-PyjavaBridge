package fr.lirmm.bridge;

import fr.lirmm.bridge.core.BridgeService;
import fr.lirmm.bridge.generated.Func_somme_entiers;
import fr.lirmm.bridge.generated.Func_formater_message;
import fr.lirmm.bridge.generated.Func_est_grand;
import java.io.File;

public class DemoPrimitives {

    public static void main(String[] args) {
        // Chemin vers Python
        String pythonEnvPath = new File("../python-env").getAbsolutePath();
        BridgeService service = new BridgeService("GRPC", pythonEnvPath);

        try {
            // Initialisation
            Func_somme_entiers.setBridge(service);
            Func_formater_message.setBridge(service);
            Func_est_grand.setBridge(service);

            System.out.println("☕ JAVA: Démarrage des tests de types primitifs...\n");

            // --- TEST 1 : ENTIERS (int) ---
            int x = 15;
            int y = 27;
            System.out.println("1️⃣  Envoi de deux entiers : " + x + " + " + y);
            Object resSomme = Func_somme_entiers.run(x, y);
            System.out.println("   ✅ Réponse Python : " + resSomme + " (Type: " + resSomme.getClass().getSimpleName() + ")\n");

            // --- TEST 2 : TEXTE ET NOMBRE (String, int) ---
            String prenom = "Alice";
            int age = 30;
            System.out.println("2️⃣  Envoi mixé : \"" + prenom + ", " + age);
            Object resMsg = Func_formater_message.run(prenom, age);
            System.out.println("   ✅ Réponse Python : \"" + resMsg + "\"\n");

            // --- TEST 3 : FLOTTANT ET BOOLEEN (double -> boolean) ---
            double taille = 1.85;
            System.out.println("3️⃣  Envoi d'un double : " + taille);
            Object resBool = Func_est_grand.run(taille);
            System.out.println("   ✅ Réponse Python : " + resBool + " (Est-ce un booléen ? " + (resBool instanceof Boolean) + ")");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            service.stop();
        }
    }
}
