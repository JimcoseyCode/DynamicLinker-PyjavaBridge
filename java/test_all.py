import os
import subprocess
import time
import sys
import signal

def run_command(cmd, cwd=None, env=None, background=False):
    if background:
        return subprocess.Popen(cmd, cwd=cwd, env=env, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    return subprocess.run(cmd, cwd=cwd, env=env)

def main():
    java_dir = os.path.dirname(os.path.abspath(__file__))
    start_server_script = os.path.join(java_dir, "src/main/java/fr/lirmm/bridge/impl/grpc/python/start_server.sh")
    run_proto_py = os.path.join(java_dir, "run_prototype.py")

    print("--- [1/4] Compilation Java ---")
    run_command(["mvn", "clean", "compile"], cwd=java_dir)

    print("\n--- [2/4] Test Prototype gRPC ---")
    # Lancement du serveur en arrière-plan
    server_proc = subprocess.Popen(["bash", start_server_script], cwd=os.path.dirname(start_server_script))
    print("Attente du démarrage du serveur (5s)...")
    time.sleep(5)
    
    try:
        run_command([sys.executable, run_proto_py, "grpc"], cwd=java_dir)
    finally:
        print("Arrêt du serveur gRPC...")
        # Nettoyage propre
        if os.name == 'nt':
            server_proc.terminate()
        else:
            try:
                # Tuer le groupe de processus pour être sûr de stopper python et le shell
                os.killpg(os.getpgid(server_proc.pid), signal.SIGTERM)
            except:
                server_proc.terminate()
        
        # Sécurité port
        run_command(["lsof", "-ti:50051", "|", "xargs", "kill", "-9"], background=True)
        time.sleep(2)

    print("\n--- [3/4] Test Prototype GraalVM ---")
    run_command([sys.executable, run_proto_py, "graal"], cwd=java_dir)

    print("\n--- [4/4] Test Prototype Rep (JEP) ---")
    run_command([sys.executable, run_proto_py, "rep"], cwd=java_dir)

    print("\n<--- TOUS LES TESTS SONT TERMINÉS --->")

if __name__ == "__main__":
    main()
