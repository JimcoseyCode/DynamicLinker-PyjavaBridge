import os
import subprocess
import sys


def start_grpc_server():
    # 1. Localiser le dossier du serveur gRPC
    current_dir = os.path.dirname(os.path.abspath(__file__))
    server_script = os.path.join(
        current_dir, "./src/main/java/fr/lirmm/bridge/core/impl/grpc/python/start_server.sh"
    )

    if not os.path.exists(server_script):
        print(f"Erreur : Le script {server_script} est introuvable.")
        sys.exit(1)

    print("<--- Lancement du serveur gRPC Python --->")

    try:
        # 2. Exécuter le script shell (qui gère venv, grpc_tools et le nettoyage du port)
        # On utilise subprocess.run pour rester bloquant et voir les logs
        subprocess.run(["bash", server_script], cwd=os.path.dirname(server_script))
    except KeyboardInterrupt:
        print("\nArrêt du serveur gRPC.")
    except Exception as e:
        print(f"Erreur lors du lancement : {e}")


if __name__ == "__main__":
    start_grpc_server()
