#!/bin/bash

# Script pour lancer le serveur gRPC Python Bridge

ROOT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
SERVER_DIR="$ROOT_DIR/src/prototype/grpc_implementation"

cd "$SERVER_DIR"

echo "<--- Démarrage du serveur gRPC --->"

# 1. Vérification de l'environnement virtuel
if [ ! -d "venv" ]; then
    echo "Environnement virtuel non trouvé. Installation..."
    python3 -m venv venv
    source venv/bin/activate
    pip install -r requirements.txt
else
    source venv/bin/activate
fi

echo "Vérification/Génération du code gRPC..."
python -m grpc_tools.protoc \
    -I"$ROOT_DIR/proto" \
    --python_out=generated \
    --grpc_python_out=generated \
    "$ROOT_DIR/proto/bridge.proto"
lsof -ti:50051 | xargs kill -9 2>/dev/null || true
python -u server.py