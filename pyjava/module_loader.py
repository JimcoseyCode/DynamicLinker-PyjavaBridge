import sys
import importlib.util
import json
from pathlib import Path
from pyjava.decorators import EXPOSED_FUNCTIONS
# ? [INFO] -> importation dynamique d'un fichier python en tant que module pour avoir acces a ses fonctions
''' 
    chargement d'un module precis a traversle fichier python cible import dynamique
    de nos fichier python car on connais pas a l'avance le noms de chaque fichier contenant des user_func qu'on veut utiliser 
'''
def _import_module_from_file(module_name: str, file_path: Path):
    # localise le fichier python a travers le path du fichier passée en parametre 
    spec_notice_f = importlib.util.spec_from_file_location(module_name, file_path) 
    if spec_notice_f and spec_notice_f.loader:
        module = importlib.util.module_from_spec(spec_notice_f)
        spec_notice_f.loader.exec_module(module)
        return module 
    return None
# ? [INFO] -> Enregistrement dans EXPOSED_FUNCTIONS une fonction d'un module cible 
def _register_exposed_functions(module, module_name: str, function_names: list) -> int:
    nbr_registered_user_func_from_module = 0
    for func_name in function_names:
        user_func = getattr(module, func_name, None)
        if not user_func:
            continue
        # * Enregistrement du nom complet module.user_func pour eviter les conflicts de focntion lorsque il ya des doublons 
        f_user_func_name = f"{module_name}.{func_name}"
        EXPOSED_FUNCTIONS[f_user_func_name] = user_func
        # * Enregistrement du nom cours plus facile en cas de non colision pour un appel bridge.invoke("user_func",args)
        if func_name not in EXPOSED_FUNCTIONS:
            EXPOSED_FUNCTIONS[func_name] = user_func
        nbr_registered_user_func_from_module += 1
    return nbr_registered_user_func_from_module
# ![Lazy-Loading::chargement dynamique] -> charge un module en precisant son nom a la demande evite de charger tout les modules en memoire 
def load_module_by_name(module_name: str, config_path: str = "config.json"):
    # selection le champ module du fichier json
    def __helper_get_modules(content_config:str):
        return json.loads(content_config).get("modules",{})
    
    # ? verifier si le module n'est pas deja chargée en memoire dans les Exposed functions
    if any(k.startswith(f"{module_name}.") for k in EXPOSED_FUNCTIONS.keys()):
        return True # deja en memoire 

    config = Path(config_path)
    if not config.exists():
        return False
    try:
        content_config_readed = config.read_text(encoding="utf-8")
    except Exception:
        return False

    modules_user_func = __helper_get_modules(content_config_readed);
    if module_name not in modules_user_func:
        return False
    # * Recuperation des meta-data du module 
    meta_data_module = modules_user_func[module_name].get("infosmodule", {})
    f_module_user_func = Path(meta_data_module.get('full_path', ''))
    
    # * recuperer les clef les noms des user_func
    user_func_names = list(meta_data_module.get('functions', {}).keys())

    if not f_module_user_func.exists():
        return False

    # * chargmeent dynamique de la fonction
    try:
        # verifie si le dossier ou j execute ce fichier n'est pas dans le path je l'ajoute 
        if str(Path.cwd()) not in sys.path: 
            sys.path.insert(0, str(Path.cwd()))
        
        md = _import_module_from_file(module_name, f_module_user_func)
        if md:
            _register_exposed_functions(md, module_name, user_func_names)
            print(f"[Lazy-Loader] Module '{module_name}' chargé en memoire disponible dans les fonctions exposée.")
            return True
    except Exception as e:
        print(f"[Lazy-Loader] Erreur : {e}")
    return False





# ! [INFO] -> cahrgement de tout les @user_func en memoire demarage lent mais efficacitée d'utilisation direct
def load_modules_from_config(config_path: str = "config.json"):
    p_cfg = Path(config_path)
    if not p_cfg.exists(): return
    with p_cfg.open("r", encoding="utf-8") as f:
        all_md = json.load(f)
    for mod_name in all_md.get("modules", {}).keys():
        load_module_by_name(mod_name, config_path)
