# Stage L3 - Pont Java-Python (fonctions-externes-python)

Ce système implémente plusieurs solutions pour faire appel à des fonctions Python depuis Java de manière transparente à travers différentes technologies : **JeP (Java Embedded Python)**, **gRPC**, et **GraalVM**.

L'architecture utilise un système de **Proxy dynamique** en Java, permettant d'appeler des fonctions Python comme s'il s'agissait de méthodes Java natives, avec une génération automatique d'interface regroupant les fonctions utilisateur mais également l interface de pont dynamique offre plusieurs methode pour faire appel a des fonctions python a travers java avec des prototype variée.

---

## Guide d'installation de pyjava_bridge

Le projet est conçu pour être portable (Windows, Mac, Linux) sans aucune configuration manuelle de variables d'environnement.

Un seul script Python s'occupe de tout : création de l'environnement virtuel (`venv`), installation des dépendances (JeP, gRPC), configuration dynamique de Maven, et compilation du projet Java.

```bash
# À la racine du projet, lancez :
python3 init_env.py
```

*C'est tout ! Le script configure automatiquement les chemins vers les bibliothèques C/C++ de JeP (`.jnilib`, `.so`, `.dll`) dans le `pom.xml` pour que Maven fonctionne directement.*

---
Le script d'initialisation est fondamentale car c'est elle qui apporte la portabilitée en init l'environnement virtuel avec installer des dependances et modification du de la config de maven pour qu'il soit capable de trouver les lib de jep par exemple 

## definition d'une nouvelle fonction utilisateur 

1. Placez vos fichiers Python dans le dossier 
**`python_src_dir`**.
2. Utilisez le décorateur `@user_func` et ajoutez des *Type Hints* (indications de type) pour que le traducteur Java comprenne les paramètres c'est pour faciliter lors de l'analyse syntaxique des fonctions python @user_func avec AST pour avoir direct tout les infos pour generer la signature qui sera utiliser pour le proxy entre les deux languages:

```python
# Fichier: python_src_dir/user_func_file.py
from bridge_api.decorators import user_func

@user_func
def multiplier(a: int, b: int) -> int:
    return a * b
```

3. **Recompilez le projet :**
   ```bash
   cd java/
   mvn clean compile
   ```
   *Lors de la compilation, le script `generate_interface.py` lit l'Arbre Syntaxique (AST) de votre code Python et génère automatiquement la méthode correspondante dans l'interface Java `PythonFunctions.java`.*

---

## Utilisation depuis Java 

Le `PythonConnectorFactory` permet de créer un pont vers Python. L'utilisation du Proxy rend l'appel totalement transparent et gère les conversions de types dynamiquement.

```java
package fr.lirmm.bridge;

import fr.lirmm.bridge.core.PythonBridge;
import fr.lirmm.bridge.core.PythonConnectorFactory;
import fr.lirmm.bridge.user_api.PythonFunctions;
// Prototype des technologie disponible 
 public enum Prototype {
        GRPC, // Serveur grpc implemntée 
        GRAAL, // pas encore implementée 
        JEP // implementée 
}
public class MonClient {
    public static void main(String[] args) {
        System.out.println("Lancement du Pont...");
        
        
        // Création du pont (Ici avec JeP, le plus rapide et direct il ya juste le prototype a changer l utilisationr este transparente 
        try (PythonBridge bridge = PythonConnectorFactory.createBridge(Prototype.JEP, null)) {
            
            // importaiton de la l interface qui contiens tous nos definitons de @user_func 
            PythonFunctions api = bridge.proxyCall(PythonFunctions.class);
            // utilisation transparente 
            int res = api.multiplier(10, 5);
            System.out.println("Résultat de multiplier(10, 5) : " + res);

            // 2. Façon dynamique (sans interface générée)
            Object dynRes = bridge.call("multiplier", 10, 5);
            System.out.println("Appel dynamique : " + dynRes);
    }
}
```

---

## Execution d'un client java pour tester 

L'exécution se fait via `mvn exec:exec` car elle garantit que le processus Java démarre avec le bon environnement (le `PYTHONPATH` pointant sur le `venv`), indispensable pour JeP.

### Lancer le client JeP spécifique :
```bash
cd java/
mvn exec:exec -Dexec.mainClass="fr.lirmm.bridge.Client"
```
> **Note :** Si vous tentez de lancer `mvn exec:exec` sans spécifier `-Dexec.mainClass`, la commande échouera volontairement. Il faut toujours préciser le fichier à exécuter !

---

## Architecture globale 

- `init_env.py` : L'orchestrateur principal, qui configure l'environnement entier et patch Maven dynamiquement pour eviter les problemes de non portabilitée.
- `bridge_api/` : Contient le décorateur `@user_func` utilisé côté Python pour exposer des fonctions dans pyjava_bridge.
- `python_src_dir/` : Répertoire où l'utilisateur definit ses propres fonctions utilisateur .
- `java/src/main/java/fr/lirmm/bridge/core/` : Le cœur du système (Factory, Proxy, Connecteurs JeP/gRPC).
- `java/scripts/generate_interface.py` : Script de "compilation AST" qui traduit les signatures Python en interface Java au moment du `mvn compile`.
