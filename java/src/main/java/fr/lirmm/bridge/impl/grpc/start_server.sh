#!/bin/bash
# Script pour lancer le serveur gRPC Python
# Situé dans java/src/main/java/fr/lirmm/bridge/impl/grpc/

SERVER_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
# Remonte à la racine du projet (9 niveaux)
ROOT_DIR="$SERVER_DIR/../../../../../../../../.."
# Chemin vers le fichier proto (sous-dossier local)
PROTO_DIR="$SERVER_DIR/proto"

cd "$SERVER_DIR"

echo "<--- Démarrage du serveur gRPC --->"

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
    -I"$PROTO_DIR" \
    --python_out=generated \
    --grpc_python_out=generated \
    "$PROTO_DIR/bridge.proto"

lsof -ti:50051 | xargs kill -9 2>/dev/null || true
python -u server.py