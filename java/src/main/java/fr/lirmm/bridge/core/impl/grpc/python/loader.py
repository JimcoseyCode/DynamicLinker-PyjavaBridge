import os
import importlib.util
import sys

def load_modules_from_directory(directory):
    """
    Parcourt le répertoire donné et importe tous les fichiers .py pour enregistrer les @user_func.
    """
    print(f"--- Découverte automatique des fonctions dans : {directory} ---")
    
    # On s'assure que la racine et tools sont dans le path pour les imports relatifs
    if directory not in sys.path:
        sys.path.insert(0, directory)
    
    tools_path = os.path.join(directory, "tools")
    if os.path.exists(tools_path) and tools_path not in sys.path:
        sys.path.insert(0, tools_path)

    count = 0
    # Dossiers à ignorer absolument
    ignored_dirs = {"venv", ".git", "__pycache__", "java", "target", ".vscode", "docs"}

    for root, dirs, files in os.walk(directory):
        # Filtrage intelligent des dossiers
        dirs[:] = [d for d in dirs if d not in ignored_dirs]
        
        for filename in files:
            if filename.endswith(".py") and filename != "loader.py" and filename != "server.py":
                filepath = os.path.join(root, filename)
                
                # On détermine le nom du module (ex: tools.decorators ou user_func)
                rel_path = os.path.relpath(filepath, directory)
                module_name = rel_path.replace(os.sep, ".").replace(".py", "")
                
                try:
                    # On importe le module. Si le module contient @user_func, 
                    # il s'enregistrera tout seul dans le dictionnaire global.
                    spec = importlib.util.spec_from_file_location(module_name, filepath)
                    if spec and spec.loader:
                        module = importlib.util.module_from_spec(spec)
                        # Important pour que les imports internes fonctionnent
                        sys.modules[module_name] = module
                        spec.loader.exec_module(module)
                        print(f"  [+] Chargé : {module_name}")
                        count += 1
                except Exception as e:
                    print(f"  [-] Erreur lors du chargement de {module_name} ({filename}): {e}")
    
    print(f"--- Fin du scan : {count} fichiers chargés ---\n")
