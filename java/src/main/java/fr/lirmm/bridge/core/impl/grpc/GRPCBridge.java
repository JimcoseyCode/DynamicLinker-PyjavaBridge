package fr.lirmm.bridge.core.impl.grpc;

import java.io.File;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import fr.lirmm.bridge.core.PythonBridge;

/**
 * gRPC brigde.
 * Gère intelligemment le serveur : si un serveur tourne déjà, il l'utilise,
 * sinon il en lance un nouveau.
 */
public class GRPCBridge extends PythonBridge {
    private Process serverProcess;
    private boolean isExternalServer = false;

    public GRPCBridge(String pythonFile) throws Exception {
        super(new GRPCConnector());

        if (isServerRunning("localhost", 50051)) {
            System.out.println("[gRPC] Un serveur tourne déjà. communication a celui-ci ...");
            this.isExternalServer = true;
        } else {
            lancerServeur();
        }

        this.connector.connect(pythonFile);
    }

    /**
     * Vérifie si un serveur grpc est deja en cours
     */
    private boolean isServerRunning(String host, int port) {
        try (Socket s = new Socket(host, port)) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void lancerServeur() {
        try {
            // Détection de la racine du projet
            File workingDir = new File(".").getCanonicalFile();
            if (workingDir.getName().equals("java")) {
                workingDir = workingDir.getParentFile();
            }

            // Détection du binaire Python (Windows vs Unix)
            String os = System.getProperty("os.name").toLowerCase();
            String pythonBin = os.contains("win") ? "venv/Scripts/python.exe" : "venv/bin/python3";
            File pythonExe = new File(workingDir, pythonBin);

            if (!pythonExe.exists()) {
                System.out.println("[gRPC] Attention: venv non trouvé, tentative avec le Python global.");
                pythonExe = new File(os.contains("win") ? "python" : "python3");
            }

            String serverScript = "java/src/main/java/fr/lirmm/bridge/core/impl/grpc/python/server.py";

            ProcessBuilder pb = new ProcessBuilder(pythonExe.getAbsolutePath(), "-u", serverScript);
            pb.directory(workingDir);
            pb.inheritIO();

            System.out.println("[gRPC] Aucun serveur détecté. Lancement direct via Python...");
            this.serverProcess = pb.start();

            // Attente pour laisser le temps au serveur de démarrer et compiler Protobuf
            TimeUnit.SECONDS.sleep(3);
        } catch (Exception e) {
            System.err.println("[gRPC] Erreur fatale au lancement: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        super.close();
        // On n'arrête le serveur que si c'est NOUS qui l'avons lancé
        if (!isExternalServer && serverProcess != null && serverProcess.isAlive()) {
            System.out.println("[gRPC] Arrêt du serveur Python (lancé par ce bridge).");
            serverProcess.destroy();
        } else if (isExternalServer) {
            System.out.println("[gRPC] Déconnexion (le serveur externe reste actif).");
        }
    }
}
