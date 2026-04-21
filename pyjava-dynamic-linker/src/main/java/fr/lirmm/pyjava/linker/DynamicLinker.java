package fr.lirmm.pyjava.linker;

import org.json.JSONObject;
import java.lang.reflect.Proxy;
import java.util.Iterator;

/**
 * Coeur du système de liaison dynamique.
 * Centralise la logique de Proxy pour éviter les répétitions dans les moteurs.
 */
public abstract class DynamicLinker implements PythonInvoker {
    
    protected final JSONObject config;

    public DynamicLinker(JSONObject config) {
        this.config = config;
    }

    /**
     * Attache une interface Java à une implémentation Python via Proxy.
     */
    @SuppressWarnings("unchecked")
    public <T> T bind(Class<T> contract) {
        final String modulePath = resolveModule(contract);
        
        return (T) Proxy.newProxyInstance(
            contract.getClassLoader(),
            new Class<?>[]{contract},
            (proxy, method, args) -> {
                if (method.getName().equals("close") && this instanceof AutoCloseable) {
                    ((AutoCloseable) this).close();
                    return null;
                }

                Object[] finalArgs = (args == null) ? new Object[0] : args;
                // Appel centralisé vers l'implémentation de invoke()
                return this.invoke(modulePath, method.getName(), method.getReturnType(), finalArgs);
            }
        );
    }

    /**
     * Résolution intelligente du module Python à partir du config.json.
     */
    protected String resolveModule(Class<?> contract) {
        String interfaceName = contract.getSimpleName();
        JSONObject modules = config.optJSONObject("modules");
        
        if (modules != null) {
            Iterator<String> keys = modules.keys();
            while (keys.hasNext()) {
                String fullPath = keys.next();
                JSONObject meta = modules.getJSONObject(fullPath).optJSONObject("infosmodule");
                if (meta != null && interfaceName.equals(meta.optString("java_interface"))) {
                    return fullPath;
                }
            }
        }
        return interfaceName.toLowerCase();
    }
}