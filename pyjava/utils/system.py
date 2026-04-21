import subprocess
import shlex
import sys
import os
import re
from pathlib import Path

# ?[INFO] -> Wrapper pour simplifier l'appel a subprocess de maniere elegante et maintenable 
def exec_cmd(cmd: str, **kwargs):
    return subprocess.run(shlex.split(str(cmd)), check=True, text=True, **kwargs)
# ?[INFO] -> get current os 
def check_os():
    return sys.platform

# ? [INFO] -> Tente de détecter automatiquement JAVA_HOME avec un ordre priorité pour GraalVM ainsi de suite 
def detect_java_home(curr_os = check_os()):
    curr_os = check_os()
    list_java_installed = []
    # ? Extraction en fonction de l'os les version de java installée
    match curr_os:
        case "darwin":
            out_res = subprocess.run(["/usr/libexec/java_home", "-V"], capture_output=True, text=True)
            list_java_installed = list(set(re.findall(r'(/Library/Java/JavaVirtualMachines/.*/Contents/Home)', out_res.stdout + out_res.stderr)))
        case "linux":
            path = Path("/usr/lib/jvm")
            list_java_installed = [str(d) for d in path.iterdir() if d.is_dir()] if path.exists() else []
        case "win32" | "nt":
            path = Path(os.environ.get("ProgramFiles", "C:\\Program Files")) / "Java"
            list_java_installed = [str(d) for d in path.iterdir() if d.is_dir()] if path.exists() else []

    # ! [choise] -> par prioritée 
    graal = next((h for h in list_java_installed if "graalvm" in h.lower()), None)
    if graal: 
        return graal
    # Utilisation du Java_home par default si graalvm n'est pas installée
    env_home = os.environ.get("JAVA_HOME")
    if env_home and Path(env_home).exists(): 
        return env_home
    if list_java_installed: 
        return list_java_installed[0]
    return "None"

# ? [INFO] -> recupere java avec un ordre de prioritée sur graalvm pour la performance 
def get_java_home():
    """Récupère JAVA_HOME (priorité GraalVM)."""
    return detect_java_home()

# ? [INFO] -> avoir le chemin du python du virtual env de maniere dynamique pour des fin de portabilitée 
def get_python_executable():
    from pyjava.utils.config import load_env_vars
    # chargement du .env
    env_vars = load_env_vars()
    venv_dir = Path(env_vars.get("VIRTUAL_ENV", "venv"))
    bin_dir = "Scripts" if os.name == "nt" else "bin"
    python_exe = "python.exe" if os.name == "nt" else "python3"
    return venv_dir / bin_dir / python_exe

# ? [INFO] -> verification qu'un port est utilisée dans pour le localhost 
def is_port_in_use(port: int, host: str = "127.0.0.1"):
    import socket
    try:
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
            sock.settimeout(0.5)
            return sock.connect_ex((host, port)) == 0
    except:
        return False
    
# ? [INFO] -> Execution d'un script de maniere detachée en arriere plan notament le server gRPC 
def run_task_background(cmd_args, env=None):
    return subprocess.Popen(
        cmd_args,
        env=env or os.environ.copy(),
        stdout=subprocess.DEVNULL, 
        stderr=subprocess.STDOUT,
        start_new_session=True # mode detachée background 
    )