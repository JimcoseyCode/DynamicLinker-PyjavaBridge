# Stage L3 - Plateforme Modulaire d'Interopérabilité Java-Python (PyJava Bridge)
***Pyjava_Bridge*** -> **Bridge inter-language d'appel de fonctions extra-java. 

Ce projet implémente une infrastructure professionnelle pour l'invocation de fonctions Python depuis Java. Il se distingue par une architecture multi-modules découplée, permettant d'intégrer le pont comme une véritable bibliothèque logicielle a travers cette modularitée des composants metier les plus essentiel pour etre un plug&play dans le sens ou elle pourra etre importer comme une librairie lambda pour pallier a cette problematique.



## Configuration recommandé

Pour garantir une stabilité et une performance optimales, les versions suivantes sont privilégiées :

*   **JDK** : **GraalVM pour JDK 21** (ou supérieur). C'est indispensable pour le mode `graalvm` et cela garantit la compatibilité avec les interfaces compilées.
*   **Python** : **Python 3.10** ou **3.11**. Ces versions offrent le meilleur compromis de compatibilité entre JEP (accès CPython) et GraalPy (environnement polyglotte).
*   **Maven** : Version **3.8** ou supérieure.

---


---
## Guide d'installation de pyjava_bridge: bridge.py
Le projet est conçu pour être portable (Windows, Mac, Linux) sans aucune configuration manuelle de variables d'environnement.Centralisation des fonctions utilitaire dans `bridge.py` dans lequel elle prend des args pour specifier quel script lancer.
L'ensemble du cycle de vie de pyjava_bridge est piloté par un point d'entrée unique à la racine : `bridge.py`.

### Installation et Configuration
```bash
python3 bridge.py setup
```
Crée l'environnement virtuel, installe les dépendances Python, détecte le JDK (priorité GraalVM) pour sa rapiditée pour passer un stress-code c'est a dire l'execution de fonction des millions de fois. Ce script installe les bibliothèques Java dans votre dépôt Maven local.

## CONFIG.JSON
Un fichier de configuration sera generée lors du setup de pyjava_bridge avec des informations de base dans lequel le working directory poura etre modifiée et le bridge_mode = {**JEP**,**GRAALVM**,**gRPC**} a tout moment cela simplifie le cycle de developpement qui ne serons que implementer ses user_func et lancer python3 bridge.py compile et les fonctions serons reconnu directement dans java.
### Synchronisation & compilation 
```bash
python3 bridge.py compile
```
Analyse vos scripts Python dans `working_directory/` et génère une interface Java dédiée pour chaque fichier. Cette commande assure une synchronisation incrémentale ultra-rapide car elle ne necessite pas une compilation complete juste le necessaire notamment nos les fonctions utilisateur nouvellement implementée en python avec le decorateur @user_func.

## definition d'une nouvelle fonction utilisateur 

1. Placez vos fichiers Python dans le dossier 
**`working_dir`**. ou celui que vous aurez defini dans le config.json 
2. Utilisez le décorateur `@user_func` et ajoutez des *Type Hints* (indications de type) pour que le traducteur Java comprenne les paramètres c'est pour faciliter lors de l'analyse syntaxique des fonctions python @user_func avec AST pour avoir direct tout les infos pour generer la signature qui sera utiliser pour le proxy ou faciliter la generation des fichier java pour chaque fichier qui ce trovuera dans l'aborescence du **`working_dir`** :

```python
# Fichier: python_src_dir/user_func_file.py
from pyjava import user_func

@user_func
def multiplier(a: int, b: int) -> int:
    return a * b
```

3. **generation du contract :**
   ```bash
   
   python3 bridge.py compile
   ```
   *Lors de la compilation, le script `automation/contract.py` lit l'Arbre Syntaxique (AST) de chaque fichier python trouvée dans l'aborescence du `working _dir` pour chaque fichier python son fichier java et generer avec les memes nom de fonction en java `user_func_file.java` dans le dossier **`target/generated-sources/pyjava/fr/lirmm/pyjava/contract`**.*

---
```java
package fr.lirmm.pyjava;
import fr.lirmm.pyjava.api.PyJavaBridge;
public class Main {
    public static void main(String[] args) throws Exception {
        try (PyJavaBridge bridge = PyJava.load()) {
            Object resInvoke_soustraire = bridge.invoke("working_directory.calculs", "soustraire", 20L, 8L);
            System.out.println("Soustraction : 20 - 8 = " + resInvoke_soustraire); -> 12 

        } catch (Exception e) {
            System.err.println("erreur pyajva_bridge");
            e.printStackTrace();
        }
    }
}
```

### Test du systeme 
```bash
python3 bridge.py test
```
Lance l'application principale pour valider les appels multi-modules avec execution avec un invoker a la integraal et une avec Dynamic-Linker qui automatise de maniere tres elegante l'execution de fonction python depuis java pour plus tard si une automatisation complete devrai etre privilegiée ce module s'en chargera dynamiquement.

## `Fonctionnalités Clés `

### Liaison Dynamique (pyFunc) avec Dynamic-Linker  
Le moteur exploite les métadonnées pour lier automatiquement une interface Java à son implémentation Python sans configuration manuelle.
plus tard le but sera d'initialiser que le dynamic linker et plus besoin de specifier le path du fichier .class cible pour faire un reflect a la volée.
```java
Calculs calc = bridge.pyFunc(Calculs.class);
Long res = calc.additionner(10L, 20L);
```

### Chargement des @user_func de maniere differée :-> chargment que a l'éxecution
Les modules Python ne sont chargés en mémoire qu'au moment de leur premier appel réel, garantissant un démarrage instantané du système car chargée tout les user_func d'un coup au demarage du bridge etait lourd alors que il faudrais juste utiliser que la fonctions qu'on veut executée qui a été passée en parametre au bridge. 

### Génération Multi-Interfaces 
Chaque fichier `.py` devient une interface `.java` distincte, permettant une organisation propre et évitant les collisions de noms dans les grands projets.


## Architecture du Projet

Le projet est divisé en composants indépendants :
cela garanti une modularitée acru et un deboguage assez localisée. 

1.  **pyjava-dynamic-linker** : Bibliothèque Java autonome gérant la réflexion et la création de Proxys dynamiques via le contrat `PythonInvoker`.
2.  **pyjava-bridge-core** : Cœur du système contenant les connecteurs (JEP, gRPC, GraalVM), la logique de chargement differée et les définitions Protobuf pour le connecteur gRPC.
3.  **App (Projet principal)** : Votre code métier Java situé dans `java/` qui utilise le bridge comme une dépendance standard.
4.  **automation/** : Scripts techniques regroupés pour le setup et la génération automatique de contrats pour faire le link entre java et python.
