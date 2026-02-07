package fr.lirmm.bridge;

public interface IPythonConnector extends AutoCloseable {
    /**
     * Initialise la connexion avec l'environnement Python et charge le fichier
     * spécifié.
     * 
     * @param pythonFile Chemin vers le fichier Python à charger.
     * @throws Exception en cas d'erreur lors de l'initialisation.
     */
    void connect(String pythonFile) throws Exception;

    /**
     * Exécute une fonction Python.
     * 
     * @param functionName Nom de la fonction à appeler.
     * @param returnType   Type de retour attendu.
     * @param args         Arguments de la fonction.
     * @param <T>          Type générique de retour.
     * @return Le résultat de la fonction converti dans le type spécifié.
     */
    <T> T execute(String functionName, Class<T> returnType, Object... args);

    /**
     * Ferme la connexion et libère les ressources.
     */
    @Override
    void close();
}