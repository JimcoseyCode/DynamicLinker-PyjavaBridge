package fr.lirmm.bridge;

import fr.lirmm.bridge.impl.GRPCConnector;
import fr.lirmm.bridge.impl.GraalVMConnector;
import fr.lirmm.bridge.impl.RepConnector;

// ? Le service qui fera l intermediaire entre les [Prototype => (gRPC , REP , GraalVM)]
// ? 
public class PythonConnectorFactory {
    // ! Les Types de prototype disponible
    public enum Prototype {
        GRPC,
        GRAAL,
        REP
    }

    // ? Methode pour creer le pont mis en parametre en fonction du type
    public static IPythonConnector createConnector(Prototype type) {
        switch (type) {
            case GRPC:
                return new GRPCConnector();
            case GRAAL:
                return new GraalVMConnector();
            case REP:
                return new RepConnector();
            default:
                throw new IllegalArgumentException(
                        "Prototype non supporté : \n" + type + "!!!!" + "Protoytype disponible = "
                                + Prototype.values());
        }
    }

    public static Prototype fromString(String type) {
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
                throw new IllegalArgumentException("Type de prototype inconnu : " + type);
        }
    }
}
