package fr.lirmm.bridge.core;

/**
 * Classe de base simplifiée pour faire le pont entre Java et Python.
 * Elle permet d'exécuter des fonctions Python soit directement,
 * soit via une interface Java (Proxy).
 */
public class PythonBridge implements AutoCloseable {
    protected final IPythonConnector connector;

    public PythonBridge(IPythonConnector connector) {
        this.connector = connector;
    }

    /**
     * Crée un objet qui implémente l'interface donnée et appelle Python.
     * C'est la méthode la plus simple pour l'utilisateur.
     */
    public <T> T getApi(Class<T> interfaceClass) {
        return PythonProxy.create(interfaceClass, connector);
    }

    /**
     * Appelle une fonction Python dynamiquement.
     * Pas besoin d'interface, pas besoin de compilation !
     */
    public Object call(String functionName, Object... args) {
        return execute(functionName, Object.class, args);
    }

    /**
     * Exécute une fonction Python directement par son nom avec un type de retour
     * précis.
     */
    public <T> T execute(String functionName, Class<T> returnType, Object... args) {
        return connector.execute(functionName, returnType, args);
    }

    @Override
    public void close() {
        try {
            connector.close();
        } catch (Exception e) {
            System.err.println("Erreur lors de la fermeture du bridge: " + e.getMessage());
        }
    }
}
