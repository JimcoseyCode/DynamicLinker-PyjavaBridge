import sys
import os
import time
from pathlib import Path

# Racine du projet
ROOT = Path(__file__).resolve().parent
sys.path.insert(0, str(ROOT))

from pyjava.utils.system import exec_cmd, is_port_in_use, get_python_executable, run_task_background
from pyjava.utils.config import load_config, load_env_vars



def compile(python_bin, env):
    # exec_cmd(f"{python_bin} automation/contract.py", env=env)
    exec_cmd("mvn compile -q")

def start_grpc(python_bin, env, port):
    if not is_port_in_use(port):
        print(f"[Pyjava_Bridge] -> Lancement du serveur gRPC ...")
        run_task_background([str(python_bin), "pyjava/grpc/server.py"], env=env)
        for _ in range(10):
            if is_port_in_use(port): break
            time.sleep(0.5)

def run_java_main(env, java_class, args, java_home):
    java_bin = Path(java_home) / "bin" / "java" if java_home and java_home != "None" else "java"
    cp = os.pathsep.join(["target/classes", "target/dependency/*"])
    cmd = f"{java_bin} -cp {cp} -Dpolyglot.engine.WarnInterpreterOnly=false {java_class} {' '.join(args)}"
    exec_cmd(cmd, env=env)

def main():
    if len(sys.argv) < 2:
        print("Usage: python bridge.py [setup | compile | test ]")
        return

    cmd = sys.argv[1]
    env_vars = load_env_vars()
    config = load_config()
    python_bin = get_python_executable()
    
    env = os.environ.copy()
    env["PYTHONPATH"] = os.pathsep.join([str(ROOT), env_vars.get("PYTHON_SITE_PACKAGES", "")])
    if cmd == "setup":
        exec_cmd(f"{sys.executable} automation/setup.py")
    elif cmd == "compile":
        compile(python_bin, env) # Compilation légère quotidienne
    elif cmd == "test" :
        if config.get("bridge_mode") == "grpc":
            start_grpc(python_bin, env, config.get("grpc_port"))
        
        java_class = "fr.lirmm.pyjava.Main"
        run_java_main(env, java_class, sys.argv[2:], env_vars.get("JAVA_HOME"))

if __name__ == "__main__":
    main()