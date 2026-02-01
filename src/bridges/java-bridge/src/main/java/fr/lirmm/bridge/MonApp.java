package fr.lirmm.bridge;

import fr.lirmm.bridge.core.BridgeService;
import fr.lirmm.bridge.generated.Func_multiplication;
import fr.lirmm.bridge.generated.Func_inverser_texte;
import java.io.File;

public class MonApp {
    public static void main(String[] args) {
        // Chemin vers l'environnement python (inchangé)
        String pythonEnvPath = new File("../python-env").getAbsolutePath();
        
        // On initialise le bridge
        BridgeService service = new BridgeService("GRPC", pythonEnvPath);

        try {
            // 1. Configuration des proxies
            Func_multiplication.setBridge(service);
            Func_inverser_texte.setBridge(service);

            // 2. Appel de la multiplication
            System.out.println("Java: J'appelle multiplication(6, 7)...");
            Object res1 = Func_multiplication.run(6, 7);
            System.out.println("Java: Résultat = " + res1);

            // 3. Appel de l'inversion de texte
            System.out.println("Java: J'appelle inverser_texte('Hello World')...");
            Object res2 = Func_inverser_texte.run("Hello World");
            System.out.println("Java: Résultat = " + res2);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            service.stop();
        }
    }
}
