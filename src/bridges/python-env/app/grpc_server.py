import grpc
from concurrent import futures
import time
import json
import inspect
import sys
import os

# Ensure we can import generated proto files
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

import bridge_pb2
import bridge_pb2_grpc

from app.decorators import FUNCTION_REGISTRY
from app.loader import load_user_script

# Load user functions dynamically
# Tentative de détection de bridge.config depuis la racine du projet
# grpc_server.py est dans src/bridges/python-env/app/
# La racine est à 4 niveaux au-dessus : ../../../../
current_dir = os.path.dirname(os.path.abspath(__file__))
project_root = os.path.abspath(os.path.join(current_dir, "../../../.."))
config_file = os.path.join(project_root, "bridge.config")

if os.path.exists(config_file):
    print(f"🐍 [gRPC] Found config at {config_file}", flush=True)
    with open(config_file, 'r') as f:
        for line in f:
            if line.strip().startswith("script"):
                parts = line.split("=")
                if len(parts) == 2:
                    script_rel = parts[1].strip()
                    script_abs = os.path.join(project_root, script_rel)
                    os.environ["BRIDGE_USER_FILE"] = script_abs
                    print(f"🐍 [gRPC] Setting BRIDGE_USER_FILE to {script_abs}", flush=True)
                    # IMPORTANT: Add project root to sys.path so the script can import local modules
                    if project_root not in sys.path:
                        sys.path.insert(0, project_root)
                    break

load_user_script()

class BridgeService(bridge_pb2_grpc.BridgeServiceServicer):
    def Execute(self, request, context):
        func_name = request.function_name
        args_json = request.args_json
        
        print(f"🐍 [gRPC] Request: {func_name}")
        
        if func_name not in FUNCTION_REGISTRY:
            return bridge_pb2.FunctionResponse(
                success=False, 
                error_message=f"Function {func_name} not found"
            )
        
        try:
            func = FUNCTION_REGISTRY[func_name]
            args = json.loads(args_json)
            
            # Smart invocation (handle legacy 'args' list vs *args)
            sig = inspect.signature(func)
            params = list(sig.parameters.keys())
            
            if len(params) == 1 and params[0] == 'args':
                # Legacy mode: pass the list as is
                result = func(args)
            else:
                # Modern mode: unpack arguments
                if isinstance(args, list):
                    result = func(*args)
                elif isinstance(args, dict):
                    result = func(**args)
                else:
                    # Single argument case?
                    result = func(args)
            
            return bridge_pb2.FunctionResponse(
                success=True,
                result_json=json.dumps(result)
            )
        except Exception as e:
            import traceback
            traceback.print_exc()
            return bridge_pb2.FunctionResponse(
                success=False,
                error_message=str(e)
            )

    def ListFunctions(self, request, context):
        return bridge_pb2.FunctionList(
            function_names=list(FUNCTION_REGISTRY.keys())
        )

def serve():
    port = '50051'
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    bridge_pb2_grpc.add_BridgeServiceServicer_to_server(BridgeService(), server)
    server.add_insecure_port('[::]:' + port)
    print(f"🚀 Python gRPC Server started on port {port}")
    server.start()
    try:
        while True:
            time.sleep(86400)
    except KeyboardInterrupt:
        server.stop(0)

if __name__ == '__main__':
    serve()
