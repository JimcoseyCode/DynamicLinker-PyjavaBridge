from pyjava import user_func

@user_func
def inverser(s: str) -> str: return s[::-1]

@user_func
def majuscule(s: str) -> str: return s.upper()

@user_func
def compte_mots(s: str) -> int: return len(s.split())

@user_func
def est_palindrome(s: str) -> bool: return s.lower() == s[::-1].lower()

@user_func
def concatener(s1: str, s2: str) -> str: return f"{s1} {s2}"

@user_func
def masquer_email(email: str) -> str:
    parts = email.split("@")
    return f"{parts[0][0]}***@{parts[1]}"

@user_func
def saluer(nom: str) -> str: return f"Salut {nom} !"
