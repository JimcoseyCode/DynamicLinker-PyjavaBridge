# Documentation Utilisateur - Pont Java-Python

## 📋 Résumé des commandes utiles

| Action | Commande |
| :--- | :--- |
| **Générer les classes Java** | `python3 tools/generate_bridge.py` |
| **Lancer la démo Primitifs** | `cd src/bridges/java-bridge && mvn compile exec:java -Dexec.mainClass="fr.lirmm.bridge.DemoPrimitives" -q` |
| **Lancer votre propre App** | `mvn compile exec:java -Dexec.mainClass="fr.lirmm.bridge.MonApp"` |

---

## 1. Préparation du script Python

Votre fichier Python doit contenir deux éléments essentiels :
1.  **L'import du décorateur** : `from app.decorators import user_func`
2.  **L'annotation des fonctions** : `@user_func` devant chaque fonction que vous souhaitez exposer à Java.

### Exemple :
```python
from app.decorators import user_func

@user_func
def multiplier_par_deux(args):
    # args[0] est le premier paramètre envoyé par Java
    valeur = args[0]
    return valeur * 2
```

## 2. Configuration du projet

Le fichier `bridge.config` à la racine du projet est le point central de configuration. Il permet d'éviter l'utilisation de variables d'environnement complexes.

Modifiez-le pour pointer vers votre fichier :
```ini
script = mon_fichier_perso.py
```

## 3. Génération du Pont Java

Chaque fois que vous ajoutez, renommez ou modifiez les paramètres d'une fonction Python, vous devez régénérer les classes Java correspondantes.

**Commande :**
```bash
python3 tools/generate_bridge.py
```
Cette commande crée des fichiers `Func_*.java` dans le dossier `src/bridges/java-bridge/src/main/java/fr/lirmm/bridge/generated/`.

## 4. Utilisation dans Java

### Initialisation
Avant tout appel, vous devez démarrer le service et configurer les "proxies" (les classes générées).

```java
// 1. Démarrer le service gRPC (lance Python en arrière-plan)
BridgeService service = new BridgeService("GRPC", "../python-env");

// 2. Lier les fonctions au service
Func_multiplier_par_deux.setBridge(service);
```

### Appel de fonction
L'appel est synchrone. Java attend que Python ait fini son calcul pour continuer.

```java
// 3. Exécution
Object resultat = Func_multiplier_par_deux.run(50);
System.out.println("Résultat reçu : " + resultat); // Affiche 100
```

## 5. Types de données supportés

Le système convertit automatiquement les types suivants entre les deux langages :

*   **Nombres** : `int`, `long`, `float`, `double` (Java) <-> `int`, `float` (Python)
*   **Texte** : `String` (Java) <-> `str` (Python)
*   **Logique** : `boolean` (Java) <-> `bool` (Python)
*   **Listes** : `List` (Java) <-> `list` (Python)

---

## 💡 Astuces et Bonnes Pratiques

*   **Arguments** : En Python, vos fonctions reçoivent toujours une liste d'arguments nommée `args`. `args[0]` est le premier paramètre, `args[1]` le second, etc.
*   **Performance** : Le premier appel peut être légèrement plus lent car Java doit démarrer le processus Python. Les appels suivants sont très rapides grâce à gRPC.
*   **Arrêt** : Pensez à appeler `service.stop()` à la fin de votre programme Java pour fermer proprement le processus Python.
