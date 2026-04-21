from pyjava import user_func

@user_func
def additionner(a: int, b: int) -> int:
    return a + b

@user_func
def soustraire(a: int, b: int) -> int:
    return a - b
