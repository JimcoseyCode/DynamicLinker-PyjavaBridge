package fr.lirmm.bridge.core;

/**
 * Interface de liaison du systeme pont java python
 */
public interface IPythonConnector extends AutoCloseable {

    /**
     * Initialise la connexion au serveur ou à l'interpréteur prototypée rep et
     * graal.
     */
    void connect(String pythonFile) throws Exception;

    /**
     * Exécute une fonction Python de maniere direct et classique avec parametrage
     * simplifiée
     */
    <T> T execute(String functionName, Class<T> returnType, Object... args);

    @Override
    void close();
}
