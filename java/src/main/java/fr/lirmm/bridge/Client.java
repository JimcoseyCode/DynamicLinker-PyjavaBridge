package fr.lirmm.bridge;

import fr.lirmm.bridge.core.PythonBridge;
import fr.lirmm.bridge.core.PythonConnectorFactory;
import fr.lirmm.bridge.user_api.PythonFunctions;

public class Client {
    public static void main(String[] args) {
        System.out.println("Client java de test des implementations prevu grpc graal rep ");
        // * Init du pont avec en parametre le type du prototype utiliser
        // on demande au factory de nous creer le pont en passant son type ça nous
        // permet de cacher la complexitée la logique metier du systeme impliquée
        try (PythonBridge bridge = PythonConnectorFactory.createBridge(
                PythonConnectorFactory.Prototype.JEP,
                null)) {
            // * L'appel de fonction par proxy pour la transparence entre java et python
            // * necessite une compilation pour definir les signature des fonction exposée
            // * Qui se trouve dans une le fichier pythonFunctions qui defini tout les
            // * fonctions exposée entre le serveur et les user_func
            System.out.println("\n[Proxy] Appel via interface PythonFunctions.java :");
            // importation de l interdace pour avoir acces au methode definis qui seront nos
            // user_func
            PythonFunctions userFunc = bridge.proxyCall(PythonFunctions.class);
            // execution transparente
            System.out.println("calculer_integrale(0.0, 3.0) : " + userFunc.calculer_integrale(0.0, 3.0,10));

            System.out.println(" Fibonacci(10) = " + userFunc.fibonacci(10));
            System.out.println(" LOL " + userFunc.LOL("raphael"));
            // utilisation sans proxy non dynmaiquye
            Object res1 = bridge.call("multiply", 10, 5);
            System.out.println("   Appel 'multiply' (10*5) = " + res1);

            // Appel avec typage automatique du retour
            String resHello = bridge.execute("sayHello", String.class, "raphael");
            System.out.println("sayHello = " + resHello);

        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }
}
