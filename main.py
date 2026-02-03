from decorators import user_func


@user_func
def sayHello(user_name):
    return f"Bonjour {user_name}"


@user_func
def puissance(a, b):
    return a**b


@user_func
def fibonacci(n):
    a, b = 0, 1
    result = []
    while len(result) < n:
        result.append(a)
        a, b = b, a + b
    return result


@user_func
def get_user_info(user_id):
    return {
        "id": user_id,
        "username": f"user_{user_id}",
        "Facultées": ["Facultées des Sciences Montpelleir"],
    }
