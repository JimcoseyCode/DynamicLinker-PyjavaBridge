package fr.lirmm.pyjava.api;

// ! Interface (Bridge) -> principale pour interagir avec Python .
public interface PyJavaBridge extends AutoCloseable {

    // ? [DynamicLinker] -> execution de maniere transparente de @user_func
    // ? directement en java en passant l'interface du module generer

    <T> T pyFunc(Class<T> interfaceClass);

    // ? Invoque une fonction Python de manière dynamique.
    Object invoke(String module_user_func_package, String user_func_name, Object... args);

    // ? Invoque une fonction Python avec un type de retour definisable.
    <T> T invoke(String module_user_func_package, String user_func_name, Class<T> returnType_func, Object... args);

    @Override
    void close();
}
