import sys
import os

# Setup path like grpc_server does
sys.path.append(os.path.abspath("src/bridges/python-env"))

from app.loader import load_user_script

print("--- TEST LOADER ---")
load_user_script()
print("--- END TEST ---")
