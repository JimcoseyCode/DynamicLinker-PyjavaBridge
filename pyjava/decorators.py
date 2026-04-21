# Registre global de tous les @user_func
EXPOSED_FUNCTIONS = {}
# ? [INFO] -> Décorateur pour specifier les @user_func qui seront exposer a Pyjava_bridge de maniere specifique est maitrisé
def user_func(func):
    EXPOSED_FUNCTIONS[func.__name__] = func
    return func