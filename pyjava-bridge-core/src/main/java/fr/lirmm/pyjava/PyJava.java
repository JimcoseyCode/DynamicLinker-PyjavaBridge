package fr.lirmm.pyjava;

import fr.lirmm.pyjava.api.PyJavaBridge;
import fr.lirmm.pyjava.core.PyJavaEngine;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;

// !  Point d'entrée principal pour charger le pont Python.
public class PyJava {

    /** Charge le pont avec la configuration par défaut. */
    public static PyJavaBridge load() throws Exception {
        return load("config.json");
    }

    /** Charge le pont à partir d'un fichier de configuration spécifique. */
    public static PyJavaBridge load(String configPath) throws Exception {
        String content = Files.readString(Paths.get(configPath));
        JSONObject config = new JSONObject(content);
        return new PyJavaEngine(config);
    }
}
