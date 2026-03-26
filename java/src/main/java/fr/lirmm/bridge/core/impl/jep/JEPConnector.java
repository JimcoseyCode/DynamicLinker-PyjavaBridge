package fr.lirmm.bridge.core.impl.jep;

import java.io.File;

import fr.lirmm.bridge.core.IPythonConnector;
import jep.SharedInterpreter;
import jep.Interpreter;
// import jep.JepConfig;
// import jep.MainInterpreter;

public class JEPConnector implements IPythonConnector {
    private Interpreter interp;

    static {
        // configuration de jep 
        try {
            // On vérifie si les propriétés système ont été injectées par Maven ou init_env.py
            String jepLib = System.getProperty("jep.library.path");
            
            // Si absent, on tente la détection dynamique de secours
            if (jepLib == null) {
                System.out.println("[JEP-AUTO] Détection dynamique du venv...");
                String userDir = System.getProperty("user.dir");
                File current = new File(userDir);
                File venvDir = null;
                while (current != null) {
                    File v = new File(current, "venv");
                    if (v.exists() && v.isDirectory()) { venvDir = v; break; }
                    current = current.getParentFile();
                }

                if (venvDir != null) {
                    File sp = findSitePackagesStatic(venvDir);
                    if (sp != null) {
                        String os = System.getProperty("os.name").toLowerCase();
                        String libName = os.contains("mac") ? "libjep.jnilib" : (os.contains("win") ? "jep.dll" : "libjep.so");
                        jepLib = new File(new File(sp, "jep"), libName).getAbsolutePath();
                        System.setProperty("jep.library.path", jepLib);
                        System.setProperty("jep.python.path", sp.getAbsolutePath());
                    }
                }
            }

            if (jepLib != null) {
                try {
                    jep.MainInterpreter.setJepLibraryPath(jepLib);
                    System.out.println("auto configuration (JeP) configuré : " + jepLib);
                } catch (Throwable t) {}
            }
        } catch (Exception e) {
            System.err.println("[Erreur auto-config : " + e.getMessage());
        }
    }

    private static File findSitePackagesStatic(File venvDir) {
        File winLib = new File(venvDir, "Lib/site-packages");
        if (winLib.exists()) return winLib;
        File unixLibBase = new File(venvDir, "lib");
        if (unixLibBase.exists()) {
            File[] versions = unixLibBase.listFiles(f -> f.getName().startsWith("python"));
            if (versions != null) {
                for (File v : versions) {
                    File sp = new File(v, "site-packages");
                    if (sp.exists()) return sp;
                }
            }
        }
        return null;
    }

    @Override
    public void connect(String pythonFile) throws Exception {
        System.out.println("Démarrage de l'interpréteur...");
        try {
            this.interp = new SharedInterpreter();
            // On injecte PROJECT_ROOT dynamiquement
            String userDir = System.getProperty("user.dir");
            File venvDir = findVenv(new File(userDir));
            if (venvDir != null) {
                interp.set("PROJECT_ROOT_FROM_JAVA", venvDir.getParentFile().getAbsolutePath());
            }

            if (pythonFile != null && !pythonFile.isEmpty()) {
                File file = findScript(new File(userDir), pythonFile);
                if (file != null) {
                    interp.set("__file__", file.getAbsolutePath());
                    interp.runScript(file.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            throw new Exception("Erreur JeP : " + e.getMessage(), e);
        }
    }

    private File findVenv(File startDir) {
        File current = startDir;
        while (current != null) {
            File venv = new File(current, "venv");
            if (venv.exists() && venv.isDirectory()) return venv;
            current = current.getParentFile();
        }
        return null;
    }

    private File findScript(File startDir, String name) {
        File f = new File(name);
        if (f.exists()) return f;
        f = new File(startDir, "src/main/java/fr/lirmm/bridge/core/impl/jep/" + name);
        if (f.exists()) return f;
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T execute(String functionName, Class<T> returnType, Object... args) {
        Object result = interp.invoke(functionName, args);
        if (result instanceof Number) {
            Number num = (Number) result;
            if (returnType == Integer.class || returnType == int.class) {
                return (T) Integer.valueOf(num.intValue());
            } else if (returnType == Long.class || returnType == long.class) {
                return (T) Long.valueOf(num.longValue());
            } else if (returnType == Double.class || returnType == double.class) {
                return (T) Double.valueOf(num.doubleValue());
            } else if (returnType == Float.class || returnType == float.class) {
                return (T) Float.valueOf(num.floatValue());
            }
        }
        
        return returnType.cast(result);
    }

    @Override
    public void close() {
        if (interp != null) interp.close();
    }
}