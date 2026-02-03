#!/bin/bash
set -e

echo "=================================================="
echo "   PYTHON <-> JAVA BRIDGE (gRPC)"
echo "=================================================="

# On lance le serveur via le script standard en tâche de fond
./start_server.sh > server_demo.log 2>&1 &
SERVER_PID=$!
echo "Serveur Python démarré (PID: $SERVER_PID). Logs dans server_demo.log"
sleep 5 # On laisse un peu plus de temps pour la génération et le scan

# 4. Execution Client
echo -e "\n[CLIENT] Running Java Client..."
echo "--------------------------------------------------"
cd java
mvn -q clean compile exec:java -Dexec.mainClass="fr.lirmm.bridge.Client"
cd ..
echo "--------------------------------------------------"

# Nettoyage
echo -e "\nStopping Server..."
kill $SERVER_PID
echo "Done."
