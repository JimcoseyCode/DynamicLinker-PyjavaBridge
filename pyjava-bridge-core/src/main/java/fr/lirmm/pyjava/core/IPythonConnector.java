package fr.lirmm.pyjava.core;

import fr.lirmm.pyjava.linker.PythonInvoker;

// ! Interface unifiée pour les connecteurs Python.
public interface IPythonConnector extends PythonInvoker, AutoCloseable {

    /**
     * Utilitaire pour générer le nom complet (module.fonction) à partir des
     * segments.
     */
    default String getQualifiedName(String module, String function) {
        if (module == null || module.isEmpty()) {
            return function;
        }
        return module + "." + function;
    }

    @Override
    <T> T invoke(String module, String function, Class<T> returnType, Object... args);

    @Override
    void close();
}