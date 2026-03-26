import os
import importlib.util
import sys

# Le dossier où se trouvent les scripts de l'utilisateur
# Configuration des chemins pour les imports

if "PROJECT_ROOT_FROM_JAVA" in globals():
    PROJECT_ROOT = PROJECT_ROOT_FROM_JAVA
else:
    CURRENT_DIR = os.path.dirname(os.path.abspath(__file__))
    # On remonte à la racine du projet
    PROJECT_ROOT = os.path.abspath(os.path.join(CURRENT_DIR, "../" * 10))

WORKING_DIR = os.path.join(PROJECT_ROOT, "python_src_dir")
print(f"[PY] WORKING_DIR: {WORKING_DIR}")
sys.path.append(WORKING_DIR)
sys.path.append(PROJECT_ROOT)

def user_func(func):
    """Le décorateur qui marque les fonctions pour Java"""
    # On injecte la fonction dans le module global pour que JeP la trouve
    globals()[func.__name__] = func
    return func

def load_user_scripts():
    """Scanne le dossier et importe les fichiers"""
    if not os.path.exists(WORKING_DIR):
        print(f"⚠️ Dossier {WORKING_DIR} introuvable.")
        return

    from bridge_api.decorators import EXPOSED_FUNCTIONS

    for filename in os.listdir(WORKING_DIR):
        if filename.endswith(".py") and filename != "__init__.py":
            module_name = filename[:-3]
            file_path = os.path.join(WORKING_DIR, filename)
            
            # Importation dynamique du module
            spec = importlib.util.spec_from_file_location(module_name, file_path)
            module = importlib.util.module_from_spec(spec)
            spec.loader.exec_module(module)
            
            print(f"✅ Module chargé : {module_name}")

    # Injection dans le namespace global de l'interpréteur JeP
    # pour que interp.invoke("nom_fonction", ...) fonctionne.
    print(f"[PY] Injecting {len(EXPOSED_FUNCTIONS)} functions into globals: {list(EXPOSED_FUNCTIONS.keys())}")
    for name, func in EXPOSED_FUNCTIONS.items():
        globals()[name] = func

# On lance le scan immédiatement au chargement par JeP
load_user_scripts()