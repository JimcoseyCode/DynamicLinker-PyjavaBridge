import os
import subprocess
import sys
import platform
import time

def run_prototype(proto="grpc", python_file="../custom_test.py"):
    # 1. Chemins de base
    java_dir = os.path.dirname(os.path.abspath(__file__))
    root_dir = os.path.dirname(java_dir)
    grpc_impl_dir = os.path.join(java_dir, "src/main/java/fr/lirmm/bridge/impl/grpc/python")
    
    print(f"==> Prototype: {proto} | Fichier: {python_file}")

    # 2. Détection du venv et site-packages pour JEP/GraalVM
    venv_dir = os.path.join(grpc_impl_dir, "venv")
    site_packages = ""
    jep_path = ""
    
    if os.path.exists(venv_dir):
        python_exe = os.path.join(venv_dir, "Scripts", "python.exe") if platform.system() == "Windows" else os.path.join(venv_dir, "bin", "python3")
        try:
            # Récupérer le site-packages du venv
            site_packages = subprocess.check_output([python_exe, "-c", "import site; print(site.getsitepackages()[0])"], text=True).strip()
            jep_path = os.path.join(site_packages, "jep")
        except Exception:
            pass

    # Fallback si venv non trouvé (ou si check_output a échoué)
    if not jep_path:
        # Recherche manuelle récursive pour libjep (plus robuste)
        for root, dirs, files in os.walk(venv_dir):
            if any(f.startswith("libjep") or f.endswith(".jnilib") or f.endswith(".so") or f.endswith(".dll") for f in files):
                if "jep" in root:
                    jep_path = root
                    break
        
        if not jep_path:
            site_packages = os.path.join(venv_dir, "lib", "python3.14", "site-packages")
            jep_path = os.path.join(site_packages, "jep")

    # 3. Configuration de l'environnement
    env = os.environ.copy()
    current_pythonpath = env.get("PYTHONPATH", "")
    new_paths = [site_packages, root_dir, grpc_impl_dir]
    env["PYTHONPATH"] = os.pathsep.join(filter(None, [current_pythonpath] + new_paths))

    # 4. Options JVM
    jvm_opts = [
        "--enable-native-access=ALL-UNNAMED",
        "--add-opens", "java.base/jdk.internal.misc=ALL-UNNAMED",
        "--add-opens", "java.base/java.nio=ALL-UNNAMED",
        "--add-opens", "java.base/sun.nio.ch=ALL-UNNAMED",
        "--add-opens", "java.base/java.lang=ALL-UNNAMED",
        "-Dsun.misc.Unsafe.ALLOW_OBJECT_FIELD_OFFSET=true",
        "-XX:+IgnoreUnrecognizedVMOptions"
    ]
    
    # Ajout du path JEP pour les technos natives
    jvm_opts.append(f"-Djava.library.path={jep_path}")

    # 5. Lancement de Maven
    # On utilise mvn exec:exec pour passer proprement les arguments à la JVM
    mvn_args = " ".join(jvm_opts) + " -cp %classpath fr.lirmm.bridge.Client --prototype " + proto + " --file " + python_file
    
    cmd = ["mvn", "-q", "exec:exec", "-Dexec.executable=java", f"-Dexec.args={mvn_args}"]
    
    try:
        subprocess.run(cmd, cwd=java_dir, env=env, check=True)
    except subprocess.CalledProcessError as e:
        print(f"Erreur lors de l'exécution : {e}")
        sys.exit(1)

if __name__ == "__main__":
    proto = sys.argv[1] if len(sys.argv) > 1 else "grpc"
    python_file = sys.argv[2] if len(sys.argv) > 2 else "../custom_test.py"
    run_prototype(proto, python_file)
