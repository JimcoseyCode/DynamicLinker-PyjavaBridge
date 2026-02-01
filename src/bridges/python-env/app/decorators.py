try:
    from bridge_lib import registry
except ImportError:
    registry = None

# Registre global des fonctions utilisateurs (pour Arrow Flight)
FUNCTION_REGISTRY = {}

# Ce decorateur est un marqueur pour le compilateur/transpilateur
# Il enregistre aussi la fonction pour l'exécution via Arrow Flight et JEP
def user_func(func):
    # Enregistrement pour Arrow Flight
    FUNCTION_REGISTRY[func.__name__] = func
    
    # Enregistrement pour JEP (si bridge_lib est dispo)
    if registry:
        registry.register(func.__name__, func)
        
    return func
