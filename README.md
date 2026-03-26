# Stage L3 - Pont Java-Python (fonctions-externes-python)

Ce système implémente plusieurs solutions pour faire appel à des fonctions Python depuis Java de manière transparente à travers différentes technologies : **gRPC**, **GraalVM**, et **Jep**.

L'architecture utilise un système de **Proxy dynamique** en Java, permettant d'appeler des fonctions Python comme s'il s'agissait de méthodes Java natives, avec une génération automatique d'interface regroupant les fonctions user dans une seule interface.


## Installation et Configuration
### 1. Préparation de l'environnement Python
Le projet utilise un environnement virtuel (`venv`) pour isoler les dépendances gRPC et par la suite les autres technologie .

```bash
python3 init_env.py
```
### 2. Compilation du projet Java
La compilation Maven gère automatiquement :
1. La génération du code Java à partir du fichier `.proto`.
2. Le scan des fichiers Python dans `python_src_dir/` pour générer l'interface `PythonFunctions.java`.

```bash
cd java/
mvn clean compile
```

---

## [] -> Guide d'Utilisation (gRPC)

### 1. Écrire le code Python
Placez vos fichiers dans le dossier **`python_src_dir`**. Utilisez le décorateur `@user_func` et les *Type Hints* pour une meilleure intégration :

```python
# Fichier: python_src_dir/mes_calculs.py
from bridge_api.decorators import user_func

@user_func
def multiplier(a: int, b: int) -> int:
    """Multiplie deux nombres depuis Python."""
    return a * b
```

### 2. Lancer le serveur gRPC 
Le serveur doit être actif pour que Java puisse communiquer avec Python ou sinon le client java a travers le connecteur si cela n'est pas fais il le fera a votre place 

```bash
# À la racine du projet
python3 start_persistent_server_grpc.py
```
*Le serveur re-génère automatiquement l'interface Java à chaque démarrage si de nouvelles fonctions sont détectées.*

### 3. Utilisation depuis Java
Le `PythonConnectorFactory` permet de créer un pont vers Python. L'utilisation du Proxy rend l'appel totalement transparent et simple pour l utilisateur final.

```java
import fr.lirmm.bridge.core.PythonBridge;
import fr.lirmm.bridge.core.PythonConnectorFactory;
import fr.lirmm.bridge.user_api.PythonFunctions;

public class Main {
    public static void main(String[] args) {
        // Création du pont avec [typePrototype]
        try (PythonBridge bridge = PythonConnectorFactory.createBridge(PythonConnectorFactory.Prototype.GRPC, null)) {
            
            // Récupération de l interface proxy 
            PythonFunctions user_func = bridge.proxyCall(PythonFunctions.class);
            
            // Appel transparent de la fonction Python
            int res = api.multiplier(10, 5);
            System.out.println("Résultat de multiplier(10, 5) : " + res);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

---

## Architecture du Projet

- `bridge_api/` : Contient le décorateur `@user_func` utilisé côté Python.
- `python_src_dir/` : Répertoire où l'utilisateur dépose ses scripts Python.
- `java/src/main/java/fr/lirmm/bridge/core/` : Cœur du système (Factory, Connecteurs gRPC/GraalVM).
- `java/scripts/generate_interface.py` : Script de "compilation" qui traduit les signatures Python en interface Java.
- `start_persistent_server_grpc.py` : Script de lancement rapide du serveur gRPC.

## Tests
Pour lancer le client de test :
```bash
cd java/
mvn exec:java -Dexec.mainClass="fr.lirmm.bridge.Client"
```


