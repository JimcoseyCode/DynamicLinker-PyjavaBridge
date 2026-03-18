#!/bin/bash
# Script robuste pour lancer le serveur gRPC Python

SERVER_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
# On cherche la racine du projet (README.md)
ROOT_DIR="$SERVER_DIR"
while [[ "$ROOT_DIR" != "/" && ! -f "$ROOT_DIR/README.md" ]]; do
    ROOT_DIR="$(dirname "$ROOT_DIR")"
done

# Chemins critiques
VENV_PYTHON="$ROOT_DIR/venv/bin/python3"
PROTO_DIR="$ROOT_DIR/java/src/main/proto"

cd "$SERVER_DIR"

echo "<--- Démarrage du serveur gRPC --->"

# Vérification du Python du venv
if [ ! -f "$VENV_PYTHON" ]; then
    echo "Erreur : venv non trouvé à $VENV_PYTHON"
    exit 1
fi

echo "Utilisation de Python : $VENV_PYTHON"

# Génération des fichiers gRPC (toujours s'assurer qu'ils sont à jour)
echo "Vérification/Génération du code gRPC..."
$VENV_PYTHON -m grpc_tools.protoc \
    -I"$PROTO_DIR" \
    --python_out=generated \
    --grpc_python_out=generated \
    "$PROTO_DIR/bridge.proto"

# Nettoyage du port 50051 (au cas où un serveur précédent tourne encore)
if command -v lsof >/dev/null 2>&1; then
    lsof -ti:50051 | xargs kill -9 2>/dev/null || true
fi

# Lancement du serveur (avec -u pour forcer la sortie console immédiate)
exec $VENV_PYTHON -u server.py
