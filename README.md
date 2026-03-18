# Pont Java <-> Python (Architecture gRPC)

Ce projet fournit une solution clé en main pour exécuter du code Python depuis une application Java (comme la plateforme Integraal) avec une grande simplicité d'utilisation et une faible latence. 

Le système repose sur un mécanisme de **découverte automatique** : il suffit de décorer une fonction Python pour qu'elle devienne appelable instantanément et de façon transparente depuis Java.

## ✨ Fonctionnalités Clés

1.  **Découverte Automatique** : Placez vos scripts Python n'importe où dans le projet, le système scanne l'espace de travail au démarrage et trouve vos fonctions.
2.  **Zéro Configuration (Python)** : Importez et ajoutez simplement le décorateur `@user_func` sur les fonctions que vous souhaitez exposer.
3.  **Appels Java Transparents (Proxy)** : Appelez vos fonctions Python via une simple interface Java, comme s'il s'agissait de méthodes Java natives.
4.  **Gestion Automatique du Serveur** : Le cycle de vie du serveur Python (gRPC) est géré automatiquement par le client Java au démarrage.

## 📂 Structure du Projet

```text
.
├── java/
│   ├── Client.java               <-- Démonstration de l'appel Java
│   ├── pom.xml                   <-- Configuration Maven
│   ├── start_grpc_server.py      <-- Lanceur manuel du serveur
│   └── src/main/java/fr/lirmm/bridge/
│       └── core/
│           ├── PythonBridge.java            <-- Point d'entrée principal (Java)
│           ├── PythonProxy.java             <-- Générateur de Proxy dynamique
│           ├── PythonFunctions.java         <-- Interface listant les fonctions Python
│           └── impl/grpc/python/
│               ├── server.py                <-- Serveur gRPC (exécute le code Python)
│               └── loader.py                <-- Scanner de découverte automatique
├── tools/
│   ├── decorators.py             <-- Contient le décorateur @user_func
│   └── generate_interface.py     <-- (Optionnel) Générateur d'interface Java
└── user_func.py                  <-- (Exemple) Vos fonctions Python
```

## 🚀 Démarrage Rapide

### 1. Prérequis
- **Python 3.9+** (le système créera son propre environnement virtuel pour gRPC).
- **Java 11+** et **Maven** (pour compiler le client).

### 2. Compiler le projet Java
Placez-vous dans le dossier `java` et compilez le projet avec Maven :
```bash
cd java
mvn clean compile
```

### 3. Exécuter la démonstration
Vous pouvez exécuter le client Java de démonstration, qui va démarrer automatiquement le serveur Python en arrière-plan et appeler les fonctions de `user_func.py`.
```bash
mvn exec:java -Dexec.mainClass="fr.lirmm.bridge.Client"
```

## 🛠 Guide d'Utilisation : Exposer vos propres fonctions

### Étape 1 : Créer la fonction en Python

Créez un fichier `.py` **n'importe où** dans le projet (par exemple à la racine) et utilisez le décorateur `@user_func` :

```python
# Fichier: mes_calculs.py
from tools.decorators import user_func

@user_func
def ma_super_fonction(a, b):
    return f"Le résultat est : {a + b}"
```

### Étape 2 : L'appeler depuis Java

Vous avez deux façons d'appeler cette nouvelle fonction en Java via la classe `PythonBridge`.

#### Option A : L'appel via Proxy Transparent (Recommandé)

Ajoutez la signature de votre fonction dans l'interface `PythonFunctions.java` :
```java
// Dans: fr.lirmm.bridge.core.PythonFunctions.java
public interface PythonFunctions {
    // ... autres fonctions ...
    String ma_super_fonction(int a, int b);
}
```

Puis appelez-la simplement :
```java
try (PythonBridge bridge = new PythonBridge()) {
    PythonFunctions api = bridge.getApi(PythonFunctions.class);
    
    // Appel magique vers Python !
    String resultat = api.ma_super_fonction(10, 20);
    System.out.println(resultat);
} catch (Exception e) {
    e.printStackTrace();
}
```

#### Option B : L'appel Direct (Sans Interface)
Idéal pour tester très rapidement sans modifier l'interface Java :

```java
try (PythonBridge bridge = new PythonBridge()) {
    String resultat = bridge.execute("ma_super_fonction", String.class, 10, 20);
    System.out.println(resultat);
}
```

## ⚙️ Architecture Sous-Jacente

Le cœur du système utilise **gRPC**, un framework RPC haute performance développé par Google.
- Lorsque le `PythonBridge` s'instancie en Java, il exécute un script shell qui démarre `server.py` dans un processus séparé.
- `server.py` lance un scanner (`loader.py`) qui parcourt récursivement votre projet pour importer tous les fichiers `.py`.
- Le décorateur `@user_func` intercepte les fonctions lors de l'import et les enregistre dans un registre global.
- Le client Java communique avec le serveur Python via le port `50051`.
- Les données (arguments et valeurs de retour) sont sérialisées en JSON de manière transparente.
