# Registre des fonctions exposées
EXPOSED_FUNCTIONS = {}


def user_func(func):
    """
    Décorateur pour exposer une fonction Python via gRPC.
    """
    EXPOSED_FUNCTIONS[func.__name__] = func
    return func
