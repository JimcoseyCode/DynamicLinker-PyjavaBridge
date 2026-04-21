import ast, json, subprocess, textwrap, os
from datetime import datetime
from pathlib import Path
"""
    [INFO-CLASS] -> generateur de contract pyjava_bridge
    recherche recursif des focntion utilisateurs decorées avec @user_func
        * Pour chaque fichier python ou il ya des focntions utilisateur -> un fichier java sera generer pour le refleter 
        * mise a jour du fichier config pour avoir tout les informations utilitaire pour appeler pyjava_bridge avec des invoke(module,func_name,args)
 """

class ContractGenerator:
    # ?  Table de correspondance des types Python -> Java
    # ! [info] -> ici on a notre table de de traduction des types primitif de python a java 
    TYPES = {
        "int": "Long", "float": "Double", "str": "String", "bool": "Boolean", 
        "list": "java.util.List<?>", "dict": "java.util.Map<String, Object>", "None": "void"
    }
    # ? [INFO] -> chargement du fichier cfg_read_descriptorig
    def __init__(self, config_path="config.json"):
        # Initialisation des chemins
        self.path_cfg = Path(config_path).resolve()
        self.cfg_read_descriptor = json.loads(self.path_cfg.read_text(encoding="utf-8"))
        # ? Working directory a usage de recherche recursive pour la detection @user_func dynamic
        self.work_dir = Path(self.cfg_read_descriptor.get("working_dir", "./working_directory")).resolve()
        # ! target path pour les fichier module des fonction py pour java 
        self.p_generated_userFunc_contract = Path("target/generated-sources/pyjava/fr/lirmm/pyjava/contract").resolve()
        self.metadata = {}
    # ? [INFO] -> type maping de l'arbre syntaxique du noeud(la focntion) et on matche son type java sinon Object si elle est complexe 
    def _get_type(self, node):
        """Traduit l'annotation AST en type Java lisible."""
        name = node.id if isinstance(node, ast.Name) else "Object"
        return self.TYPES.get(name, "Object")
    # ? [INFO] -> Enregistrement des changement dans le config.json 
    def _save_config(self):
        self.cfg_read_descriptor["modules"] = self.metadata
        self.cfg_read_descriptor["generated_at"] = datetime.now().isoformat()
        self.path_cfg.write_text(json.dumps(self.cfg_read_descriptor, indent=4), encoding="utf-8")
        print(f"[*]({self.path_cfg.name}) -> a été mis a jour ! ")
    # ! [F_UTILITY] -> generation de nos interfaces java et compilation direct de nos module en java 
    def _generate_java(self, mod_name, if_name, funcs):
        f_java_user_func = [
            "package fr.lirmm.pyjava.contract;",
            "",
            f"/**",
            f" * Interface générée pour le module : {mod_name}",
            f" */",
            f"public interface {if_name} {{"
        ]
        for name, info in funcs.items():
            args = ", ".join(f"{t} {n}" for n, t in info['args'].items())
            f_java_user_func.append(f"    {info['return']} {name}({args});")
        f_java_user_func.append("}")
        user_func_lines  = "\n".join(f_java_user_func) + "\n"

        self.p_generated_userFunc_contract.mkdir(parents=True, exist_ok=True)
        (self.p_generated_userFunc_contract / f"{if_name}.java").write_text(user_func_lines, encoding="utf-8")
        print(f"[+] Interface @user_func-file {if_name}.java generée avec succées .")

    def run(self):
        print(f"[*] Scan du répertoire : {self.work_dir}")
        # * recherche recursif pour cherche tout les focntion python du working_dir
        for py_file in self.work_dir.rglob("*.py"):
            try:
                # * arbre syntaxique AST 
                tree = ast.parse(py_file.read_text(encoding="utf-8"))
                all_user_func = {}
                # *  Analyse statique du code pour select les @user_func 
                for node in ast.walk(tree):
                    # * On cherche les fonctions avec le décorateur @user_func
                    if isinstance(node, ast.FunctionDef) and any(
                        isinstance(d, ast.Name) and d.id == 'user_func' for d in node.decorator_list
                    ):
                        args = {a.arg: self._get_type(a.annotation) for a in node.args.args}
                        all_user_func[node.name] = {"args": args, "return": self._get_type(node.returns)}

                if all_user_func:
                    rel_parts = py_file.relative_to(self.work_dir.parent).with_suffix("").parts
                    mod_name = ".".join(rel_parts)
                    
                    # !  Conversion snake_case -> PascalCase (ex: test_fun -> TestFun) car java utilise le PascalCase
                    if_name = "".join(x.capitalize() for x in py_file.stem.split("_"))
                    
                    self.metadata[mod_name] = {
                        "infosmodule": {
                            "full_path": str(py_file.resolve()),
                            "java_interface": if_name,
                            "functions": all_user_func
                        }
                    }
                    self._generate_java(mod_name, if_name, all_user_func)

            except Exception as e:
                print(f"[!] Erreur d'analyse sur {py_file.name} : {e}")

        self._save_config() 
        print("[Pyjava_Bridge] -> compilation terminée .")
if __name__ == "__main__":
    ContractGenerator().run()