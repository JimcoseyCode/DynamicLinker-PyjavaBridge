# Architecture et Fonctionnement

Ce document détaille le fonctionnement interne du Pont Java-Python.

## 1. Vue d'ensemble

Le système utilise une architecture **Client-Serveur** locale via **gRPC**.
*   **Java** agit comme le Client.
*   **Python** agit comme le Serveur.

L'objectif est de masquer totalement la couche réseau pour le développeur.

## 2. Le Cycle de Vie

### A. Démarrage
1.  L'application Java démarre et instancie `BridgeService`.
2.  `BridgeService` lance un sous-processus : `python grpc_server.py`.
3.  Le serveur Python démarre.
    *   Il cherche le fichier `bridge.config` à la racine du projet.
    *   Il lit la ligne `script = ...`.
    *   Il charge dynamiquement ce script utilisateur.
    *   Il enregistre toutes les fonctions décorées avec `@user_func`.
4.  Java attend que le port 50051 soit ouvert.

### B. Appel de Fonction
1.  L'utilisateur Java appelle `Func_maFonction.run(arg1, arg2)`.
2.  La classe `Func_maFonction` (générée) sérialise les arguments.
3.  Elle envoie une requête gRPC `Execute` contenant le nom "maFonction" et les arguments.
4.  Python reçoit la requête, trouve la fonction dans son registre, l'exécute, et renvoie le résultat.
5.  Java désérialise le résultat et le retourne à l'utilisateur.

## 3. Gestion de la Configuration (`bridge.config`)

Pour éviter de coder en dur les chemins, le système utilise un mécanisme de découverte automatique.

*   **Le Loader (`loader.py`)** : Situé côté Python, il est responsable de trouver le script utilisateur.
*   **Algorithme de recherche** :
    1.  Il cherche `bridge.config` à la racine du projet (en remontant l'arborescence depuis son propre emplacement).
    2.  S'il le trouve, il lit le chemin du script cible.
    3.  Il ajoute le dossier du script au `PYTHONPATH` (pour permettre les imports relatifs).
    4.  Il charge le module.

## 4. Génération de Code (`generate_bridge.py`)

Cet outil est essentiel pour assurer le typage et la facilité d'utilisation côté Java.

1.  Il utilise le même mécanisme que le serveur pour charger le script utilisateur (via `bridge.config`).
2.  Il inspecte le registre des fonctions (`FUNCTION_REGISTRY`).
3.  Pour chaque fonction, il génère un fichier `.java` dans `src/bridges/java-bridge/src/main/java/fr/lirmm/bridge/generated/`.
4.  Ces fichiers contiennent le code "boilerplate" pour appeler le `BridgeService`.

## 5. Types de Données Supportés

Le pont gère automatiquement la conversion des types primitifs suivants :

| Java | Python |
| :--- | :--- |
| `int` / `Integer` | `int` |
| `String` | `str` |
| `boolean` / `Boolean` | `bool` |
| `double` / `Float` | `float` |
| `List<Object>` | `list` |

Pour les objets complexes, ils sont pour l'instant transmis sous forme de `Map` ou de JSON selon l'implémentation.