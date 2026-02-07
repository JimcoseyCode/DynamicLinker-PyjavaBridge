from decorators import user_func


@user_func
def multiply(a, b):
    return a * b


@user_func
def reverse_string(s):
    return s[::-1]


@user_func
def sayHello(name):
    return f"Hello from custom file, {name}!"


@user_func
def puissance(a, b):
    return a**b + 1  # Variation to show it's custom


@user_func
def fibonacci(n):
    a, b = 0, 1
    result = []
    while len(result) < n:
        result.append(a)
        a, b = b, a + b
    # Variation: return tuple instead of list, or just same logic
    return result


@user_func
def get_user_info(user_id):
    return {
        "id": user_id,
        "username": f"custom_user_{user_id}",
        "source": "custom_test.py",
    }
