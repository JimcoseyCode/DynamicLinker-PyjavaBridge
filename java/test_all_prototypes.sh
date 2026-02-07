#!/bin/bash
# Script de test global situé dans java/

JAVA_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
GRPC_START_SCRIPT="$JAVA_DIR/src/main/java/fr/lirmm/bridge/impl/grpc/start_server.sh"

PYTHON_FILE="custom_test.py"
if [ "$#" -ge 1 ]; then
    PYTHON_FILE="$1"
fi

if [[ "$PYTHON_FILE" != /* && "$PYTHON_FILE" != ../* ]]; then
    TARGET_FILE="../$PYTHON_FILE"
else
    TARGET_FILE="$PYTHON_FILE"
fi

echo "<==================== TEST DES PROTORYPES ====================>"
echo "@ Fichier source Python : $PYTHON_FILE"
echo "<=============================================================>"

cd "$JAVA_DIR"

# 1. Test gRPC
echo -e "\n[1/3] gRPC"
"$GRPC_START_SCRIPT" > /dev/null 2>&1 &
SERVER_PID=$!
sleep 5
./run_prototype.sh grpc "$TARGET_FILE"
kill $SERVER_PID 2>/dev/null

# 2. Test GraalVM
echo -e "\n[2/3] GraalVM"
./run_prototype.sh graal "$TARGET_FILE"

# 3. Test Rep (JEP)
echo -e "\n[3/3] Rep (JEP)"
./run_prototype.sh rep "$TARGET_FILE"

echo -e "\n<==================== FIN DES TESTS ====================>"
