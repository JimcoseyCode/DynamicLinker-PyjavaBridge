import functools

class BridgeRegistry:
    def __init__(self):
        self._functions = {}
    def register(self, name, func):
        self._functions[name] = func
    def get(self, name):
        return self._functions.get(name)
    def list_all(self):
        return list(self._functions.keys())

registry = BridgeRegistry()

def user_func(func):
    """Décorateur pour signaler une fonction utilisateur appelable depuis Java."""
    registry.register(func.__name__, func)
    return func

def java_class(cls):
    """Décorateur Legacy - No-op"""
    return cls