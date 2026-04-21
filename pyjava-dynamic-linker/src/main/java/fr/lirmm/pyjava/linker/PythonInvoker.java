package fr.lirmm.pyjava.linker;

/**
 * Interface socle représentant tout composant capable d'exécuter du code Python.
 * C'est le contrat de base utilisé par le DynamicLinker.
 */
public interface PythonInvoker {
    /**
     * Exécute une fonction Python.
     * @param module Nom du module (facultatif).
     * @param function Nom de la fonction.
     * @param returnType Type de retour attendu.
     * @param args Arguments de la fonction.
     */
    <T> T invoke(String module, String function, Class<T> returnType, Object... args);
}