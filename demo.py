# Appel de fonctions Python avec types primitifs depuis Java
from app.decorators import user_func


@user_func
def sum_interval(a, b):
    res = 0
    for val in range(a, b):
        res += val
    return res


@user_func
def ImInStage():
    students = ["raphael", "yacer", "victor", "ylyess"]
    print("voici la liste des etudiants de ce stage")
    for stu in students:
        print(stu)


@user_func
def main(args=None):
    print("🚀 Démarrage de la démo de Janvier...")
    ImInStage()
    print(sum_interval(0, 10))
