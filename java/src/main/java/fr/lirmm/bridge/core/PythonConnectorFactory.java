package fr.lirmm.bridge.core;

import fr.lirmm.bridge.core.impl.grpc.GRPCBridge;

/**
 ** Utilitaire pour chosir un pont specifique a une implemntation d'un prototype
 * donne
 ** Nous permet d'avoir la meme base modulaire pour ce connecter au technologie
 ** disponible
 */
public class PythonConnectorFactory {

    public enum Prototype {
        GRPC, // Serveur grpc
        GRAAL, // GraalVM
        REP
    }

    /**
     * ! Méthode principale pour créer un un pont complet prêt à l'emploi.
     */
    public static PythonBridge createBridge(Prototype type, String pythonFile) throws Exception {
        switch (type) {
            case GRPC:
                return new GRPCBridge(pythonFile);
            case GRAAL:
                return null;

            case REP:
                return null;

            default:
                throw new IllegalArgumentException("Ce prototype n'est pas supporté : " + type);
        }
    }

    // * Convertis string type -> en Type[Prototype]
    public static Prototype fromString(String type) {
        // ** Default type[null] -> grpc
        if (type == null)
            return Prototype.GRPC;
        switch (type.toLowerCase()) {
            case "grpc":
                return Prototype.GRPC;
            case "graal":
                return Prototype.GRAAL;
            case "rep":
                return Prototype.REP;
            default:
                throw new IllegalArgumentException("Prototype inconnu dans le système : " + type);
        }
    }
}
