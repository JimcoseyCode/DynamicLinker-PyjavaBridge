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