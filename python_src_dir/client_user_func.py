from bridge_api.decorators import user_func

# ! Definition de mes fonctions utilisateur  
# * Pour plus de faciliter utiliser les indications de type en python 
# * pour que l arbre syntaxique de python soit toalement complete et nous permete d'avoir le bon type et conversion en java 
# * A travers une table de mapping 
@user_func
def multiply(a: int, b: int) -> int:
    return a * b

@user_func
def reverse_string(s: str) -> str:
    return s[::-1]

@user_func
def sayHello(name: str) -> str:
    return f"Boujour , {name} [Je suis une fonction Python executée depuis java Youpi ]!"

@user_func
def puissance(a: int, b: int) -> int:
    return a**b

@user_func
def fibonacci(n: int) -> list:
    a, b = 0, 1
    result = []
    while len(result) < n:
        result.append(a)
        a, b = b, a + b
    return result



@user_func
def LOL(name):
    return "ouais LOL im coding "
def carre(x: float) -> float:
    """La fonction mathématique f(x) = x^2 que l'on veut intégrer."""
    return x ** 2
@user_func  
def calculer_integrale(a: float, b: float, n: int = 1000) -> float:
    """
    Calcule l'intégrale de 'carre' entre les bornes a et b.
    'n' est le nombre de trapèzes (plus il est grand, plus c'est précis).
    """
    h = (b - a) / n
    somme = 0.5 * (carre(a) + carre(b))
    
    for i in range(1, n):
        somme += carre(a + i * h)
        
    return somme * h
