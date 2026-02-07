import json
import os
import sys
from concurrent import futures

import grpc

# Configuration des chemins pour les imports
CURRENT_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.abspath(os.path.join(CURRENT_DIR, "../../../../../../../../../"))
GENERATED_DIR = os.path.join(CURRENT_DIR, "generated")

# Ajout des chemins au système pour que Python trouve les modules
sys.path.append(PROJECT_ROOT)
sys.path.append(CURRENT_DIR)
sys.path.append(GENERATED_DIR)

# Imports gRPC depuis le dossier generated
import bridge_pb2
import bridge_pb2_grpc

# Imports locaux
import loader
from decorators import EXPOSED_FUNCTIONS


class BridgeService(bridge_pb2_grpc.BridgeServiceServicer):
    def Execute(self, request, context):
        func_name = request.function_name
        try:
            if func_name not in EXPOSED_FUNCTIONS:
                return bridge_pb2.FunctionResult(
                    success=False,
                    error_message=f"La fonction '{func_name}' n'a pas été exposée dans le pont pour etre accesible ou il est inexistant ",
                )

            target_func = EXPOSED_FUNCTIONS[func_name]
            args = json.loads(request.args_json)

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
                error_message=f"Erreur Lors de l'éxecution de la fonction : {str(e)}",
            )


def serve():

    loader.load_modules_from_directory(PROJECT_ROOT)

    # 2. Affichage des fonctions disponibles
    print("Fonctions exposées via gRPC :")
    for name in EXPOSED_FUNCTIONS:
        print(f"  - {name}")

    # 3. Démarrage serveur
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    bridge_pb2_grpc.add_BridgeServiceServicer_to_server(BridgeService(), server)
    server.add_insecure_port("[::]:50051")
    print("\nLe serveur gRPC Python est en cours d'execution port 50051")
    server.start()
    server.wait_for_termination()


if __name__ == "__main__":
    serve()
