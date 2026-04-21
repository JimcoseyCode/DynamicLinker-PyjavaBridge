import json
from pathlib import Path
# ? [INFO] -> chargement du fichier config.json de maniere centralisée 
def load_config(path: str = "config.json"):
    CONFIG_FILE = Path(path)
    if CONFIG_FILE.exists():
        return json.loads(CONFIG_FILE.read_text(encoding="utf-8"))
    return {"bridge_mode": "jep", "grpc_port": 50051, "working_dir": "working_directory"}
# ? [INFO] -> chargement des variables d'environnement FILE[".env"]
def load_env_vars(path_dot_env_str: str = ".env"):
    def __read_env(ENV_FILE:Path):
        readed_env = []
        for l in ENV_FILE.read_text(encoding="utf-8").splitlines():
            # Éviter les commentaires et les lignes vides
            if l.strip() and not l.startswith("#"):
                # ajout de toutes les lignes contenant les env_vars
                readed_env.append(l)
        return readed_env
    ENV_FILE = Path(path_dot_env_str)
    environnement_vars = {} # Liste de tous les environnement de variables 
    if not  ENV_FILE.exists() :
        print("Le fichier .env n'existe pas veuillez executée init.py pour initialiser ton environnement !!")
        return {}
    for l in __read_env(ENV_FILE):
        env_var_name, value = l.split("=", 1)
        environnement_vars[env_var_name.strip()] = value.strip()
    
    return environnement_vars
# ?[INFO] -> enregistrement(write) du fichier json avec les données de config personalisée 
def save_config(config_data: dict, path: str = "config.json"):
    Path(path).write_text(json.dumps(config_data, indent=4), encoding="utf-8")
    print("** CONFIGURATION[config.json] **  a bien été effectuer.")