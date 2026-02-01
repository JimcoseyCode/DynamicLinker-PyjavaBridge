# Pont Java-Python (L3 2026)

> **🚀 Aide-mémoire : Commandes Rapides**
> 
> *   **Générer le pont** : `python3 tools/generate_bridge.py`
> *   **Lancer la démo** : `cd src/bridges/java-bridge && mvn compile exec:java -Dexec.mainClass="fr.lirmm.bridge.DemoPrimitives" -q`

Ce projet permet d'appeler des fonctions **Python** nativement depuis une application **Java**.

## 🚀 Fonctionnalités
*   **Simplicité** : Décorez vos fonctions Python avec `@user_func`, elles deviennent appelables en Java.
*   **Transparence** : Les types primitifs (`int`, `String`, `boolean`, `float`) sont convertis automatiquement.
*   **Configuration Facile** : Un simple fichier `bridge.config` à la racine pointe vers votre script.
*   **Robuste** : L'exécution Python se fait dans un processus séparé (isolation des crashs).

---

## 🛠 Pré-requis
*   Java 17+
*   Maven 3+
*   Python 3.9+

---

## ⚡️ Guide de Démarrage Rapide

### 1. Codez en Python
Créez un fichier (ex: `mes_fonctions.py`) à la racine et utilisez le décorateur :

```python
from app.decorators import user_func

@user_func
def addition(args):
    """ Fait une somme """
    return args[0] + args[1]

@user_func
def inverser(args):
    """ Inverse un texte """
    return args[0][::-1]
```

### 2. Configurez
Créez ou modifiez le fichier `bridge.config` à la racine du projet :

```ini
script = mes_fonctions.py
```

### 3. Générez le Pont
Lancez l'outil qui va créer les classes Java correspondantes :

```bash
python3 tools/generate_bridge.py
```
*Cela va créer `Func_addition.java` et `Func_inverser.java`.*

### 4. Appelez depuis Java
Utilisez les classes générées comme des fonctions statiques :

```java
// Initialisation (une seule fois)
BridgeService service = new BridgeService("GRPC", "../python-env");
Func_addition.setBridge(service);

// Appel
Object resultat = Func_addition.run(10, 20);
System.out.println("Résultat : " + resultat); // Affiche 30
```

---

## 🧪 Lancer la Démonstration (Types Primitifs)

Une démo est fournie pour tester les entiers, chaînes et booléens.

1.  Assurez-vous que `bridge.config` contient `script = primitives.py`.
2.  Générez le pont :
    ```bash
    python3 tools/generate_bridge.py
    ```
3.  Lancez la démo Java :
    ```bash
    cd src/bridges/java-bridge
    mvn compile exec:java -Dexec.mainClass="fr.lirmm.bridge.DemoPrimitives" -q
    ```

---

## 📂 Structure du Projet

*   `src/bridges/python-env/` : L'environnement Python (Serveur gRPC).
*   `src/bridges/java-bridge/` : La librairie Java et vos applications.
*   `tools/generate_bridge.py` : Le générateur de code.
*   `bridge.config` : Fichier de configuration du script utilisateur.

## 🔧 Dépannage
Si Java ne trouve pas la fonction :
1.  Vérifiez que votre fonction a bien `@user_func`.
2.  Vérifiez que `bridge.config` pointe vers le bon fichier.
3.  Relancez `python3 tools/generate_bridge.py`.
4.  Assurez-vous d'avoir fait `mvn compile` côté Java.