import json
import os
import sys
from concurrent import futures

import grpc

# Configuration des chemins pour les imports
CURRENT_DIR = os.path.dirname(os.path.abspath(__file__))


# Recherche robuste de la racine du projet (cherche README.md)
def find_project_root(start_path):
    current = start_path
    while current != os.path.dirname(current):
        if os.path.exists(os.path.join(current, "README.md")):
            return current
        current = os.path.dirname(current)
    return None


PROJECT_ROOT = find_project_root(CURRENT_DIR)
if not PROJECT_ROOT:
    # Fallback si README non trouvé
    PROJECT_ROOT = os.path.abspath(os.path.join(CURRENT_DIR, "../" * 11))

GENERATED_DIR = os.path.join(CURRENT_DIR, "generated")
TOOLS_DIR = os.path.join(PROJECT_ROOT, "tools")

sys.path.extend([PROJECT_ROOT, CURRENT_DIR, GENERATED_DIR, TOOLS_DIR])

# Imports gRPC générés
try:
    import bridge_pb2
    import bridge_pb2_grpc
except ImportError:
    print("Erreur : Fichiers gRPC non trouvés. Lancez start_server.sh.")
    sys.exit(1)

# Import du registre des fonctions
import loader
from tools.decorators import EXPOSED_FUNCTIONS


class BridgeService(bridge_pb2_grpc.BridgeServiceServicer):
    """Implémentation du service gRPC."""

    def Execute(self, request, context):
        func_name = request.function_name
        try:
            if func_name not in EXPOSED_FUNCTIONS:
                return bridge_pb2.FunctionResult(
                    success=False,
                    error_message=f"La fonction '{func_name}' n'est pas exposée via @user_func.",
                )

            target_func = EXPOSED_FUNCTIONS[func_name]
            args = json.loads(request.args_json)

            # Exécution dynamique (gère listes, dicts ou arguments simples)
            if isinstance(args, list):
                result = target_func(*args)
            elif isinstance(args, dict):
                result = target_func(**args)
            else:
                result = target_func(args)

            return bridge_pb2.FunctionResult(
                success=True, result_json=json.dumps(result)
            )

        except Exception as e:
            return bridge_pb2.FunctionResult(
                success=False,
                error_message=f"Erreur Python lors de l'exécution de '{func_name}': {str(e)}",
            )


def serve():
    # Scan automatique des fichiers Python du projet
    loader.load_modules_from_directory(PROJECT_ROOT)

    print("\n--- Fonctions Python prêtes pour Java ---")
    for name in EXPOSED_FUNCTIONS:
        print(f"  [API] {name}")

    # Création du serveur gRPC
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    bridge_pb2_grpc.add_BridgeServiceServicer_to_server(BridgeService(), server)

    server.add_insecure_port("[::]:50051")
    print("\nServeur gRPC standard lancé sur le port 50051.")
    print("Prêt pour les appels Java.")

    server.start()
    server.wait_for_termination()


if __name__ == "__main__":
    serve()
if __name__ == "__main__":
    serve()
if __name__ == "__main__":
    serve()
