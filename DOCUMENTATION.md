# Documentation du Pont Java-Python Haute Performance

Ce document détaille l'architecture, le fonctionnement et l'utilisation du système permettant d'exécuter des fonctions Python depuis Java avec une latence minimale.

---

## 1. Vue d'Ensemble
Le système repose sur une architecture client-serveur utilisant **gRPC** pour la communication. Il permet d'exposer des fonctions Python simplement via un décorateur et de les appeler depuis Java de manière transparente (via des Proxies).

### Points forts :
- **Transparence totale** : Appels Python comme s'il s'agissait de méthodes Java natives.
- **Haute Performance** : Latence ~0.2ms par appel individuel.
- **Multi-Techno** : Supporte gRPC (distant), GraalVM (natif) et JEP (natif).
- **Découverte Automatique** : Scan récursif de l'espace de travail pour trouver les fonctions exposées.

---

## 2. Architecture Technique

### Côté Python (Commun & Outils)
1. **`tools/decorators.py`** : Définit `@user_func`, qui enregistre les fonctions dans un dictionnaire global. Ce dossier est ajouté au `PYTHONPATH` par le système pour permettre un import sans restriction.

### Côté Python (Serveur gRPC)
1. **`loader.py`** : Parcourt le projet, importe dynamiquement les modules `.py` et remplit le registre. Il assure la disponibilité du décorateur partout.
2. **`server.py`** : Implémente le service gRPC et exécute les fonctions cibles.

### Côté Java (Client)
1. **`IPythonConnector`** : Interface commune pour tous les types de ponts.
2. **`PythonProxy`** : Transforme une interface Java en appels Python dynamiques.
3. **`PythonBridge`** : Classe de haut niveau gérant automatiquement le cycle de vie du serveur et la création des proxies.

---

## 3. Guide d'Installation

### Étape 1 : Prérequis Python
Assurez-vous d'avoir Python 3.9+ installé. Le système gère son propre environnement virtuel lors du premier lancement.

### Étape 2 : Compilation Java
```bash
cd java
mvn clean compile
```

### Étape 3 : Lancement du Serveur (Optionnel)
Le serveur est lancé automatiquement par `PythonBridge` en Java, mais vous pouvez le lancer manuellement :
```bash
python3 java/start_grpc_server.py
```

---

## 4. Utilisation du Système

### Exposer une fonction Python
Importez le décorateur depuis n'importe quel fichier de votre espace de travail :
```python
from decorators import user_func

@user_func
def addition(a, b):
    return a + b
```

### Appeler depuis Java (Mode Ultra-Simple)
Utilisez `PythonBridge` pour une transparence maximale :

```java
// 1. Définissez votre interface
public interface MyFunctions {
    Integer addition(Integer a, Integer b);
}

// 2. Utilisez le bridge
try (PythonBridge bridge = new PythonBridge()) {
    MyFunctions api = bridge.getApi(MyFunctions.class);
    int res = api.addition(10, 20);
    System.out.println("Résultat : " + res);
}
```

---

## 5. Résolution des problèmes courants

### Port 50051 déjà utilisé
Le serveur gRPC utilise par défaut le port 50051. Si vous avez une erreur "Address already in use", le système tente de le libérer automatiquement au démarrage. Sinon :
- Linux/Mac : `lsof -ti:50051 | xargs kill -9`
- Windows : `taskkill /F /PID (netstat -ano | findstr :50051)`
