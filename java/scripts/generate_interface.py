import ast
import os
import sys
# * [pseudo - COMPILATEUR] -> genere automatiquement une classe java qui sera utilisée en tant qu'interface contenant les methodes
# *                  scnaner dans le dossier directory de tout les fonctions user trouvée et serons disponible directement en java a travers le systeme 
# *                  de proxy pour de la pure transparence entre les focntions python et java 
# Table de correspondance des types Python -> Java
# ! [info] -> ici on a notre table de de traduction des types primitif de python a java 
TYPE_MAP = {
    'int': 'Integer',
    'str': 'String',
    'float': 'Double',
    'bool': 'Boolean',
    'list': 'java.util.List<Object>',
    'dict': 'java.util.Map<String, Object>',
    'None': 'void'
}

def extract_info_from_py(filepath):
    """Analyse un fichier Python et extrait les fonctions @user_func avec leurs types."""
    with open(filepath, "r", encoding="utf-8") as f:
        tree = ast.parse(f.read())

    functions = []
    for node in ast.walk(tree):
        if isinstance(node, ast.FunctionDef):
            # On vérifie si la fonction a le décorateur @user_func
            has_decorator = any(
                (isinstance(d, ast.Name) and d.id == "user_func") or
                (isinstance(d, ast.Attribute) and d.attr == "user_func")
                for d in node.decorator_list
            )
            
            if has_decorator:
                # Extraction du type de retour
                ret_type = "Object"
                if node.returns and isinstance(node.returns, ast.Name):
                    ret_type = TYPE_MAP.get(node.returns.id, "Object")
                
                # Extraction des arguments
                args = []
                for arg in node.args.args:
                    if arg.arg == "self": continue
                    arg_name = arg.arg
                    arg_type = "Object"
                    if arg.annotation and isinstance(arg.annotation, ast.Name):
                        arg_type = TYPE_MAP.get(arg.annotation.id, "Object")
                    args.append(f"{arg_type} {arg_name}")
                
                functions.append({
                    'name': node.name,
                    'ret': ret_type,
                    'args': ", ".join(args)
                })
    return functions

def generate():
    project_root = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
    print(project_root)
    output_dir = os.path.join(project_root, "java/src/main/java/fr/lirmm/bridge/user_api")
    os.makedirs(output_dir, exist_ok=True)
    output_file = os.path.join(output_dir, "PythonFunctions.java")
    user_src_dir = os.path.join(project_root, "python_src_dir")
    
    all_funcs = []
    # On ne scanne QUE le dossier python_src_dir
    for root, _, files in os.walk(user_src_dir):
        for f in files:
            if f.endswith(".py"):
                all_funcs.extend(extract_info_from_py(os.path.join(root, f)))

    # Génération du code Java
    methods = []
    for f in sorted(all_funcs, key=lambda x: x['name']):
        methods.append(f"    {f['ret']} {f['name']}({f['args']});")

    content = f"""package fr.lirmm.bridge.user_api;

/**
 * Interface Generée automatique /scrips/generate_interface.py
 * Basée sur les décorateurs @user_func et les Type Hints Python ou sans typagee en python mais qui seront du type object en java qui
    qui est un type heritée par toute les .
 */
public interface PythonFunctions {{
{chr(10).join(methods)}
}}
"""
    with open(output_file, "w", encoding="utf-8") as f:
        f.write(content)
    print(f"Interface Java mise à jour avec {len(methods)} ")

if __name__ == "__main__":
    generate()
