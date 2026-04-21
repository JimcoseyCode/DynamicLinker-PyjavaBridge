import grpc
from concurrent import futures
import json
import sys
from pathlib import Path

# ? [INFO] -> ajout du server gRPC dans le Sys.path pour une execution non dependant du directory lors des import de mes fonctions utilitaire
root_dir = Path(__file__).resolve().parent.parent.parent
if str(root_dir) not in sys.path:
    sys.path.insert(0, str(root_dir))

from pyjava.utils.config import load_config
from pyjava.module_loader import load_module_by_name
from pyjava.decorators import EXPOSED_FUNCTIONS
from pyjava.grpc import bridge_pb2 as pb2
from pyjava.grpc import bridge_pb2_grpc as pb2_grpc

class BridgeServicer(pb2_grpc.BridgeServiceServicer):
    """[INFO] -> server gRPC utile le principe de chargement differrée pour ne pas alourdir 
     le premier demarage et n'appeler la fonction que lorsqu'elle est appeler et mise cela nous permet
     d'avoir un demarage tres rapide 
    """
    def Execute(self, request, context):
        try:
            # ? Vérifier si user_func specifique est chargée dans dans les fonctions exposée dans le bridge
            user_func = EXPOSED_FUNCTIONS.get(request.function_name)
            # Si non trouvée, tenter un chargement dynamique à la demande
            if not user_func and "." in request.function_name:
                module_name = request.function_name.rsplit('.', 1)[0]
                # chargment du module par son module name 
                load_module_by_name(module_name)
                user_func = EXPOSED_FUNCTIONS.get(request.function_name)
            
            if not user_func:
                return pb2.FunctionResult(success=False, error_message=f"Fonction '{request.function_name}' non trouvée (même après tentative de chargement).")

            args = json.loads(request.args_json)
            result = user_func(*args)
            
            return pb2.FunctionResult(success=True, result_json=json.dumps(result))
        except Exception as e:
            return pb2.FunctionResult(success=False, error_message=str(e))

def gRPC_server():
    # ? [CONFIG] -> chargement du config.json <Pyjava_Bridge>
    config_bridge = load_config()
    port_bridge_gRpc_server = config_bridge.get("grpc_port")

    # ? Le serveur ne charge pas les user_func au démarrage (Lazy Loading) -> que quand c'est necessaire 
    server_gRPC = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    pb2_grpc.add_BridgeServiceServicer_to_server(BridgeServicer(), server_gRPC)
    server_gRPC.add_insecure_port(f'[::]:{port_bridge_gRpc_server}')
    server_gRPC.start()
    print(f"[gRPC] Serveur prêt sur le port {port_bridge_gRpc_server} (Lazy Loading -> optimization)")
    server_gRPC.wait_for_termination()


if __name__ == "__main__":
    gRPC_server()