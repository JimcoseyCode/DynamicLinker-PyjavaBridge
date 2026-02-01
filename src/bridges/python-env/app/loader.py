import os
import sys
import importlib.util

def load_user_script():
    print("DEBUG: Entrée dans load_user_script", flush=True)
    """
    Charge le script utilisateur défini dans la variable d'environnement BRIDGE_USER_FILE
    ou dans le fichier de configuration 'bridge.config' à la racine.
    """
    user_file = os.environ.get("BRIDGE_USER_FILE")

    # Si pas de variable d'environnement, on cherche bridge.config
    if not user_file:
        # On sait que loader.py est dans src/bridges/python-env/app/
        # Donc la racine est à 4 niveaux au-dessus.
        base_dir = os.path.dirname(os.path.abspath(__file__))
        project_root = os.path.abspath(os.path.join(base_dir, "../../../.."))
        
        config_path = os.path.join(project_root, "bridge.config")
        print(f"DEBUG_FINAL: Checking {config_path} (Exists? {os.path.exists(config_path)})", flush=True)
        
        if os.path.exists(config_path):
            try:
                # IMPORTANT: On change le répertoire de travail vers la racine du projet
                # pour que les imports Python (ex: import primitives) fonctionnent
                project_root = os.path.dirname(config_path)
                if project_root not in sys.path:
                    sys.path.insert(0, project_root)
                
                with open(config_path, "r") as f:
                    for line in f:
                        if line.strip().startswith("script"):
                            parts = line.split("=")
                            if len(parts) == 2:
                                script_name = parts[1].strip()
                                user_file = os.path.join(project_root, script_name)
                                print(f"📄 [Loader] Configuration trouvée dans bridge.config : {script_name}")
                                break
            except Exception as e:
                print(f"⚠️ [Loader] Erreur lecture bridge.config: {e}")

    if not user_file:

        # Fallback sur les primitives par défaut si aucun fichier utilisateur n'est spécifié
        try:
            import app.primitives
            print("📦 [Loader] Chargement par défaut : app.primitives")
        except ImportError:
            pass
        return

    print(f"📂 [Loader] Chargement du script utilisateur : {user_file}")
    
    if not os.path.exists(user_file):
        print(f"❌ [Loader] Fichier introuvable : {user_file}")
        return

    # Chargement dynamique du module
    try:
        module_name = os.path.splitext(os.path.basename(user_file))[0]
        spec = importlib.util.spec_from_file_location(module_name, user_file)
        if spec and spec.loader:
            module = importlib.util.module_from_spec(spec)
            sys.modules[module_name] = module
            spec.loader.exec_module(module)
            print(f"✅ [Loader] Module '{module_name}' chargé avec succès !")
    except Exception as e:
        print(f"❌ [Loader] Erreur lors du chargement de {user_file}: {e}")
