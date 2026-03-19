import json
import os
import sys
from concurrent import futures

import grpc
# Configuration des chemins pour les imports
CURRENT_DIR = os.path.dirname(os.path.abspath(__file__))

# On remonte à la racine du projet (qui est 11 dossiers plus haut)
PROJECT_ROOT = os.path.abspath(os.path.join(CURRENT_DIR, "../" * 11))

GENERATED_DIR = os.path.join(CURRENT_DIR, "generated")
PROTO_DIR = os.path.join(PROJECT_ROOT, "java", "src", "main", "java", "fr", "lirmm", "bridge", "core", "impl", "grpc", "proto")

sys.path.extend([PROJECT_ROOT, CURRENT_DIR, GENERATED_DIR])

# Compilation dynamique des fichiers Protobuf
try:
    import grpc_tools.protoc
    os.makedirs(GENERATED_DIR, exist_ok=True)
    proto_file = os.path.join(PROTO_DIR, "bridge.proto")
    
    print("Vérification/Génération du code gRPC Python...")
    grpc_tools.protoc.main((
        '',
        f'-I{PROTO_DIR}',
        f'--python_out={GENERATED_DIR}',
        f'--grpc_python_out={GENERATED_DIR}',
        proto_file,
    ))
except Exception as e:
    print(f"Avertissement : Impossible de regénérer les fichiers Proto: {e}")

# Imports gRPC générés
try:
    import bridge_pb2
    import bridge_pb2_grpc
except ImportError:
    print("Erreur : Fichiers gRPC non trouvés. La génération automatique a échoué.")
    sys.exit(1)

# Import du registre des fonctions
import java.scripts.loader as loader
from bridge_api.decorators import EXPOSED_FUNCTIONS

# Ajout du script de génération d'interface dans le path pour pouvoir l'appeler
SCRIPTS_DIR = os.path.join(PROJECT_ROOT, "java", "scripts")
sys.path.append(SCRIPTS_DIR)
try:
    import generate_interface
except ImportError:
    generate_interface = None
    print("Avertissement: generate_interface non trouvé.")


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
    # ! Scan du working directory pour extraire les fonctions utilisateurs
    user_src_dir = os.path.join(PROJECT_ROOT, "python_src_dir")
    loader.load_modules_from_directory(user_src_dir)

    # Génération automatique de l'interface Java
    if generate_interface:
        print("\n--- Mise à jour de l'interface Java ---")
        generate_interface.generate()

    print("\n [API] @user_func -> ")    
    i = 1 
    for name in EXPOSED_FUNCTIONS:
        print(f"  {i} : {name}")
        i += 1
    print("****** - ******");
    # init du serveur grpc 
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    bridge_pb2_grpc.add_BridgeServiceServicer_to_server(BridgeService(), server)
    print("Le serveur gRPC est en cours d'execution au port 50051\n")
    print("En attente de communcation avec un client java ...\n")
    server.add_insecure_port("[::]:50051")
    server.start()
    server.wait_for_termination()
if __name__ == "__main__":
    serve()

