#!/bin/bash
# Demo client Java situé dans java/

JAVA_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$JAVA_DIR"

mvn -q compile exec:java -Dexec.mainClass="fr.lirmm.bridge.Client"