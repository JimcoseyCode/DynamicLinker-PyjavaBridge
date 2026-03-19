#!/usr/bin/env python3
import os
import sys
def main():
    print("GRPC - SERVEUR")
    # * Initialisation de l'environnement python d'execution [venv]
    project_root = os.path.dirname(os.path.abspath(__file__))
    venv_python = os.path.join(project_root, "venv", "bin", "python3")
    if not os.path.exists(venv_python):
        print("Erreur : L'environnement virtuel n'est pas installé.")
        print("Veuillez d'abord exécuter les commandes d'installation.")
        sys.exit(1)

    server_script = os.path.join(project_root, "java", "src", "main", "java", "fr", "lirmm", "bridge", "core", "impl", "grpc", "python", "server.py")
    
    try:
        # Execution du serveur grpc environnement virtuel ok
        os.execl(venv_python, venv_python, "-u", server_script)
       
    except Exception as e:
        print(f"Erreur lors du lancement du serveur : {e}")
if __name__ == "__main__":
    main()
