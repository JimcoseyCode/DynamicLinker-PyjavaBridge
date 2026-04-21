import sys
from pathlib import Path
import subprocess
import os

sys.path.insert(0, str(Path(__file__).resolve().parent.parent))

from pyjava.utils.system import exec_cmd, check_os, detect_java_home
from pyjava.utils.config import load_config, save_config
# ? [INFO] -> installation des modules pyjava_bridge dans le projet principale {modules:["pyjava-dynamic-linker", "pyjava-bridge-core"]}
def install_lib_pyjava_utility(root_dir):
    
    modules = ["pyjava-dynamic-linker", "pyjava-bridge-core"]
    for module in modules:
        exec_cmd("mvn install -q", cwd=str(root_dir / module))
    exec_cmd("mvn dependency:copy-dependencies -DincludeScope=runtime -q", cwd=str(root_dir))

def run_setup():
    print("[Pyjava_Bridge] -> Setup ..")
    root_dir = Path.cwd()
    venv_dir = root_dir / "venv"
    env_file = root_dir / ".env"
    
    if not venv_dir.exists():
        print("[*] Création de l'environnement virtuel...")
        exec_cmd(f"{sys.executable} -m venv {venv_dir}")
    
    bin_dir = "Scripts" if os.name == "nt" else "bin"
    pip = venv_dir / bin_dir / "pip"
    print("[*] Installation des dépendances...")
    exec_cmd(f"{pip} install --upgrade pip", capture_output=True)
    exec_cmd(f"{pip} install jep==4.3.1 grpcio grpcio-tools")

    java_home = detect_java_home()
    print(f"[*] ({java_home})")
    print(f"[*] Génération du fichier .env ...")
    site_pkgs = list(venv_dir.rglob("site-packages"))[0].resolve()
    ext = "*.jnilib" if sys.platform == "darwin" else ("*.dll" if os.name == "nt" else "*.so")
    jep_lib = list(venv_dir.rglob(f"jep/{ext}"))[0].resolve()
    
    env_content = (
        f"JEP_LIBRARY_PATH={jep_lib}\n"
        f"PYTHON_SITE_PACKAGES={site_pkgs}\n"
        f"VIRTUAL_ENV={venv_dir.resolve()}\n"
        f"JAVA_HOME={java_home}\n"
    )
    env_file.write_text(env_content, encoding="utf-8")

    config_path = root_dir / "config.json"
    if not config_path.exists():
        save_config({
            "bridge_mode": "jep",
            "grpc_port": 50051,
            "working_dir": str((root_dir / "working_directory").resolve())
        })
    # !  installation de mes modules interne  pyjava_Bridge 
    install_lib_pyjava_utility(root_dir)
    print("[Pyjava_Bridge] -> Setup terminé avec succès !")


if __name__ == "__main__":
    run_setup()