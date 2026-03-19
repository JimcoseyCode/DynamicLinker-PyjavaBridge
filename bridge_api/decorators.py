# Registre des fonctions exposées
EXPOSED_FUNCTIONS = {}

# * [decorateur](func) -> 
def user_func(func):
    """
    Décorateur pour specifier precisement quel fonctions est une focntions utilisateur lors de la face de compilation cela soit mise en cache.
    """
    EXPOSED_FUNCTIONS[func.__name__] = func
    return func
