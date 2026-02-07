import os
import importlib.util
import sys

def load_modules_from_directory(directory):
    """
    Parcourt récursivement le répertoire donné et importe tous les fichiers .py.
    """
    print(f"--- Découverte automatique des fonctions dans : {directory} ---")
    
    # Le dossier où se trouve loader.py (src/prototype/grpc_implementation)
    core_dir = os.path.dirname(os.path.abspath(__file__))
    
    # On ajoute ce dossier au path pour que tout le monde puisse importer 'decorators'
    if core_dir not in sys.path:
        sys.path.append(core_dir)

    # On ajoute aussi le dossier scanné (la racine)
    if directory not in sys.path:
        sys.path.append(directory)

    count = 0
    for root, dirs, files in os.walk(directory):
        # Filtrage
        dirs[:] = [d for d in dirs if d not in ["venv", "__pycache__", ".git", "java", "target", "src"]]
        
        # On ne scanne pas notre propre dossier source pour éviter les doubles imports
        if os.path.abspath(root).startswith(core_dir):
            continue

        for filename in files:
            if filename.endswith(".py"):
                filepath = os.path.join(root, filename)
                
                # Nom de module unique
                rel_path = os.path.relpath(filepath, directory)
                module_name = rel_path.replace(os.sep, ".").replace(".py", "")
                
                try:
                    spec = importlib.util.spec_from_file_location(module_name, filepath)
                    if spec and spec.loader:
                        module = importlib.util.module_from_spec(spec)
                        sys.modules[module_name] = module
                        spec.loader.exec_module(module)
                        print(f"  [+] Chargé : {filename}")
                        count += 1
                except Exception as e:
                    print(f"  [-] Erreur lors du chargement de {filename}: {e}")
    
    print(f"--- Fin du scan : {count} fichiers chargés ---\\n")