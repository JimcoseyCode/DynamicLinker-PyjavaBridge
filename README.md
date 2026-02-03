# Pont Java <-> Python (gRPC High Performance)

Ce projet fournit une solution clé en main pour exécuter du code Python depuis une application Java avec une latence extrêmement faible (~0.2ms) et une grande simplicité d'utilisation.

## 🚀 Fonctionnalités Clés

1.  **Découverte Automatique** : Placez vos scripts Python n'importe où dans le projet, ils sont automatiquement détectés.
2.  **Zéro Config** : Ajoutez simplement le décorateur `@user_func` pour exposer une fonction.
3.  **Appel Java Simplifié** : Une classe `PythonBridge` gère toute la complexité.
4.  **Architecture Modulaire** : Le cœur du système est isolé dans `src/prototype/`.

## 📂 Structure du Projet

```text
.
├── main.py                     <-- Vos fonctions Python (exemple)
├── start_server.sh             <-- Script de lancement du serveur
├── run_client_demo.sh          <-- Script de démo Java
├── java/                       <-- Client Java
├── proto/                      <-- Définitions gRPC
└── src/
    └── prototype/
        └── grpc_implementation/
            ├── server.py       <-- Serveur gRPC
            ├── loader.py       <-- Scanner de fonctions
            ├── decorators.py   <-- Décorateur @user_func
            └── generated/      <-- Code gRPC généré
```

## ⚡️ Démarrage Rapide

### 1. Lancer le Serveur Python

```bash
./start_server.sh
```
*Le serveur s'initialise, installe les dépendances dans un environnement virtuel, et scanne le projet à la recherche de fonctions décorées.*

### 2. Lancer la Démo Java

Dans un autre terminal :
```bash
./run_client_demo.sh
```

## 🛠 Ajouter vos propres fonctions

Créez un fichier `.py` **n'importe où** (ex: `mes_algos.py` à la racine).

```python
from decorators import user_func

@user_func
def mon_algo_complexe(data):
    return {"status": "ok", "processed": len(data)}
```

Appelez-le depuis Java :

```java
try (PythonBridge bridge = new PythonBridge()) {
    Map resultat = bridge.execute("mon_algo_complexe", Map.class, "mes données");
}
```