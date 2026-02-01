# -*- coding: utf-8 -*-
# Ce fichier doit être compatible Python 2.7 (Jython) et Python 3.x (JEP)
try:
    from app.decorators import user_func
except ImportError:
    try:
        from decorators import user_func
    except ImportError:
        from .decorators import user_func

@user_func
def addition(args):
    """
    Signature: (int a, int b) -> int
    Entree: liste [a, b]
    Sortie: a + b
    """
    return args[0] + args[1]

@user_func
def dire_bonjour(args):
    """
    Entree: liste [nom]
    Sortie: "Bonjour " + nom
    """
    return "Bonjour " + str(args[0])

@user_func
def puissance(args):
    """
    Entree: liste [base, exposant]
    Sortie: base ^ exposant
    """
    # En Python 2, pow retourne un int si possible
    return int(pow(args[0], args[1]))

@user_func
def calculer_age(args):
    """
    Entree: liste [annee_naissance]
    Sortie: 2026 - annee_naissance
    """
    # On evite l'import datetime complexe pour Jython si possible
    return 2026 - args[0]
