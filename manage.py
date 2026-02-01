#!/usr/bin/env python3
import argparse
import os
import subprocess
import sys

# --- Configuration ---
ROOT_DIR = os.path.dirname(os.path.abspath(__file__))
PYTHON_ENV_DIR = os.path.join(ROOT_DIR, "src", "bridges", "python-env")
JAVA_BRIDGE_DIR = os.path.join(ROOT_DIR, "src", "bridges", "java-bridge")
VENV_DIR = os.path.join(PYTHON_ENV_DIR, "venv")
VENV_PYTHON = os.path.join(VENV_DIR, "bin", "python")


def run_cmd(cmd, cwd=ROOT_DIR, env=None):
    """Exécute une commande shell."""
    try:
        subprocess.check_call(cmd, cwd=cwd, env=env, stdout=subprocess.DEVNULL)
    except subprocess.CalledProcessError as e:
        print(f"❌ Erreur technique : {e}")
        sys.exit(e.returncode)


def get_env(user_file=None):
    """Prépare l'environnement."""
    env = os.environ.copy()
    if user_file:
        abs_path = os.path.abspath(user_file)
        env["BRIDGE_USER_FILE"] = abs_path
    return env


def generate_proxies(user_file=None):
    """Génère les proxies Java."""
    script = os.path.join(ROOT_DIR, "tools", "generate_bridge.py")
    run_cmd([VENV_PYTHON, script], env=get_env(user_file))


def execute_runner(
    strategy="GRPC", user_file=None, main_class="fr.lirmm.bridge.UniversalRunner"
):
    """Compile et lance le runner Java."""
    # 1. Génération
    print("⚙️  [1/3] Analyse du code Python...")
    generate_proxies(user_file)

    # 2. Compilation Java
    print("☕ [2/3] Préparation du moteur Java...")
    run_cmd(["mvn", "package", "-DskipTests"], cwd=JAVA_BRIDGE_DIR)

    # 3. Exécution
    print(f"🚀 [3/3] Exécution ({strategy})...")
    print("")

    # On utilise subprocess.call ici pour voir la sortie standard de l'app
    cmd = [
        "mvn",
        "exec:java",
        f"-Dexec.mainClass={main_class}",
        f"-Dexec.args={strategy} {PYTHON_ENV_DIR}",
    ]
    subprocess.call(cmd, cwd=JAVA_BRIDGE_DIR, env=get_env(user_file))


def main():
    parser = argparse.ArgumentParser(description="Python-Java High Performance Bridge")
    subparsers = parser.add_subparsers(dest="command")

    parent_parser = argparse.ArgumentParser(add_help=False)
    parent_parser.add_argument(
        "--file", "-f", required=True, help="Votre script Python"
    )
    parent_parser.add_argument(
        "--strategy",
        default="GRPC",
        choices=["GRPC"],
        help="Strategie d'execution (Uniquement GRPC supporte)",
    )

    # Commande RUN
    parser_run = subparsers.add_parser(
        "run", parents=[parent_parser], help="Exécute le script python atravers Java"
    )

    # Commande BENCHMARK
    parser_bench = subparsers.add_parser(
        "benchmark", parents=[parent_parser], help="Mesure la performance"
    )

    args = parser.parse_args()

    if args.command == "run":
        execute_runner(args.strategy, args.file, "fr.lirmm.bridge.UniversalRunner")
    elif args.command == "benchmark":
        execute_runner(args.strategy, args.file, "fr.lirmm.bridge.Benchmark")
    else:
        parser.print_help()


if __name__ == "__main__":
    main()
