import os
import sys

# Ajoute le répertoire racine de python-env au sys.path
# Cela permet d'importer 'app' même quand on est à l'intérieur
root_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
if root_dir not in sys.path:
    sys.path.insert(0, root_dir)
