#!/bin/bash


ROOT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$ROOT_DIR/java"
mvn -q compile exec:java -Dexec.mainClass="fr.lirmm.bridge.Client"
