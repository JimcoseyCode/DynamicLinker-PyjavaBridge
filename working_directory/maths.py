from pyjava import user_func
import math

@user_func
def additionner(a: int, b: int) -> int: return a + b

@user_func
def multiplier(a: int, b: int) -> int: return a * b

@user_func
def puissance(base: int, exp: int) -> int: return base ** exp

@user_func
def racine_carree(n: float) -> float: return math.sqrt(n)

@user_func
def est_pair(n: int) -> bool: return n % 2 == 0

@user_func
def factorielle(n: int) -> int: return math.factorial(n)

@user_func
def calculer_tva(prix: float) -> float: return round(prix * 0.20, 2)
