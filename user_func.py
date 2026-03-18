from tools.decorators import user_func

# ! Definition de mes fonctions utilisateur decorée

@user_func
def multiply(a: int, b: int) -> int:
    return a * b

@user_func
def reverse_string(s: str) -> str:
    return s[::-1]

@user_func
def sayHello(name: str) -> str:
    return (
        f"Boujour , {name} [Je suis une fonction Python executée depuis java Youpi ]!"
    )

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
def putain(name: str) -> str:
    return f"Putain {name}"
