package fr.lirmm.bridge.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Utilitaire pour créer un proxy Java transparent vers les fonctions Python.
 */
public class PythonProxy {

    /**
     * Crée une instance d'interface qui délègue ses appels au connecteur Python.
     * 
     * @param interfaceClass L'interface Java définissant les signatures des
     *                       fonctions Python.
     * @param connector      Le connecteur (gRPC, Graal, etc.) à utiliser.
     * @param <T>            Le type de l'interface.
     * @return Une implémentation dynamique de l'interface.
     */
    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> interfaceClass, IPythonConnector connector) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[] { interfaceClass },
                new PythonInvocationHandler(connector));
    }

    private static class PythonInvocationHandler implements InvocationHandler {
        private final IPythonConnector connector;

        public PythonInvocationHandler(IPythonConnector connector) {
            this.connector = connector;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // Gestion des méthodes de base de Object
            if (method.getDeclaringClass() == Object.class) {
                return method.invoke(this, args);
            }
            String functionName = method.getName();
            Class<?> returnType = method.getReturnType();

            // Support pour les appels asynchrones / futur si on veut ajouter plus tard
            // Pour l'instant on fait du direct transparent
            return connector.execute(functionName, returnType, args);
        }
    }
}
