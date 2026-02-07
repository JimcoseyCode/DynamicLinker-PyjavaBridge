#!/bin/bash
# Script pour lancer le client avec différents prototypes
# Usage: ./run_prototype.sh [grpc|graal|rep] [fichier_python]

PROTO=${1:-grpc}
FILE=${2:-../main.py}

export JAVA_HOME="/Library/Java/JavaVirtualMachines/graalvm-25.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"

JAVA_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
ROOT_DIR="$JAVA_DIR/.."
GRPC_IMPL="$JAVA_DIR/src/main/java/fr/lirmm/bridge/impl/grpc"
VENV="$GRPC_IMPL/venv/lib/python3.14/site-packages"

export PYTHONPATH="$VENV:$ROOT_DIR:$GRPC_IMPL"

# JVM Flags pour supprimer les warnings de dépréciation et d'accès illégal
# Ces flags ouvrent les modules internes pour Netty (gRPC) et GraalVM/Truffle
JVM_OPTS="--enable-native-access=ALL-UNNAMED \
          --add-opens java.base/jdk.internal.misc=ALL-UNNAMED \
          --add-opens java.base/java.nio=ALL-UNNAMED \
          --add-opens java.base/sun.nio.ch=ALL-UNNAMED \
          --add-opens java.base/java.lang=ALL-UNNAMED \
          -Dsun.misc.Unsafe.ALLOW_OBJECT_FIELD_OFFSET=true \
          -XX:+IgnoreUnrecognizedVMOptions"

echo "==> Prototype: $PROTO | Fichier: $FILE"

cd "$JAVA_DIR" && mvn -q exec:exec -Dexec.executable="java" \
    -Dexec.args="$JVM_OPTS -Djava.library.path=$VENV/jep -cp %classpath fr.lirmm.bridge.Client --prototype $PROTO --file $FILE"
