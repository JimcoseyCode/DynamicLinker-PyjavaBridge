import subprocess, os, sys
# [Init] -> initialisation de l'environnement virtuel et installation des requirment de grpc
requirement_path_grpc = "java/src/main/java/fr/lirmm/bridge/core/impl/grpc/python/requirements.txt"
def check_os():
    return os.name
def dir_venv_target():
    return "Scripts" if check_os() == "nt" else "bin"
def main():
        
    print("--- 1. Création de l'environnement virtuel ---")
    subprocess.run([sys.executable, "-m", "venv", "venv"], check=True)

    print("\n--- 2. Installation des dépendances Python ---")
    target_bin_dir = dir_venv_target()
    pip_exe = os.path.join("venv", target_bin_dir, "pip")
    subprocess.run([pip_exe, "install", "-r", requirement_path_grpc], check=True)

    print("\n--- 3. Compilation des dependance java bridge ---")
    subprocess.run("mvn clean compile", shell=True, check=True, cwd="java")
    print("\nInitialisation terminée !")
    print("--- 4. Ouverture du terminal avec l'environnement activé ---")
    if check_os() == "Scripts":
        activate_script = os.path.join("venv", "Scripts", "activate.bat")
        subprocess.run(["cmd", "/k", activate_script])
    else:
        activate_script = os.path.join("venv", "bin", "activate")
        subprocess.run(["bash", "-c", f"source {activate_script} && exec bash"])
if __name__ == "__main__":
    main()