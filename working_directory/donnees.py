from pyjava import user_func

@user_func
def trier_liste(l: list) -> list: return sorted(l)

@user_func
def moyenne(l: list) -> float: return sum(l) / len(l) if l else 0.0

@user_func
def filtrer_positifs(l: list) -> list: return [x for x in l if x > 0]

@user_func
def generer_carres(n: int) -> list: return [i**2 for i in range(n)]

@user_func
def infos_systeme() -> dict: return {"status": "online", "bridge": "PyJava", "version": 2.0}
