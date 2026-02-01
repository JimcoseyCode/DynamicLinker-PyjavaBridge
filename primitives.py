from app.decorators import user_func

@user_func
def somme_entiers(args):
    """ Prend deux entiers et renvoie la somme """
    # En Python, args est une liste.
    a = args[0]
    b = args[1]
    print(f"🐍 Python: J'ai reçu {a} (type: {type(a).__name__}) et {b} (type: {type(b).__name__})")
    return a + b

@user_func
def formater_message(args):
    """ Prend un nom (str) et un age (int), renvoie une phrase """
    nom = args[0]
    age = args[1]
    return f"Salut {nom}, tu as {age} ans !"

@user_func
def est_grand(args):
    """ Prend un nombre (float ou int) et renvoie un booléen """
    taille = args[0]
    return taille > 1.80
