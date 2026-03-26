import subprocess, os, sys, glob, re

def check_os():
    return os.name

def find_site_packages(venv_path):
    # Cherche le dossier site-packages de manière dynamique
    pattern = os.path.join(venv_path, "**", "site-packages")
    results = glob.glob(pattern, recursive=True)
    return results[0] if results else None

def main():
    print("**** Initialisation de pyjava_bridge ****")
    # Initialsier l'environnement virtuel  
    if not os.path.exists("venv"):
        print("Création de l'environnement virtuel...")
        subprocess.run([sys.executable, "-m", "venv", "venv"], check=True)

    # Installation des dépendances de pyjava_bridge
    bin_dir = "Scripts" if os.name == "nt" else "bin"
    pip_exe = os.path.join("venv", bin_dir, "pip")
    print("Installation de JeP et des dépendances inter-prototype")
    subprocess.run([pip_exe, "install", "--upgrade", "pip"], check=True)
    subprocess.run([pip_exe, "install", "jep==4.3.1", "grpcio", "grpcio-tools"], check=True)

    # Localisation dynamique pour Maven
    site_pkgs = find_site_packages("venv")
    if not site_pkgs:
        print("Erreur : site-packages introuvable.")
        return
    
    """
        ! Jep est a assez delicat en fonction du systeme d'exploitation maven a besoin de savoir exactement dans le virtuel env
        ou se trouve le lib de jep qui se trouve dans le dossier ven qui difference du systeme 
        le code ci dessous injecte apres anaylse dynamique et toalement portable rajoute dans le fichier 
        pom.xml pour specifier non seulement les environnement de varaible que jep a besoin car il utilise des library c qui maven n'est pas capable 
        d'aller les trouver recursivement seul dans son aborescence ajout des environnement de variable et du chemin de jep library de facon dynamique car il 
        chage de nom en focntion du steme d'exploitation unix (linux /macos ) ou windows 


    """    
    site_pkgs_abs = os.path.abspath(site_pkgs)
    lib_name = "libjep.jnilib" if sys.platform == "darwin" else ("jep.dll" if os.name == "nt" else "libjep.so")
    jep_lib_file = os.path.join(site_pkgs_abs, "jep", lib_name)

    # Patch dynamique du pom.xml
    print("Configuration du pom.xml...")
    pom_path = os.path.join("java", "pom.xml")
    if os.path.exists(pom_path):
        with open(pom_path, "r") as f:
            pom_content = f.read()
        exec_config = f"""
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>exec-maven-plugin</artifactId>
                <version>3.6.3</version>
                <configuration>
                    <executable>java</executable>
                    <environmentVariables>
                        <PYTHONPATH>{site_pkgs_abs}</PYTHONPATH>
                    </environmentVariables>
                    <arguments>
                        <argument>-Djep.library.path={jep_lib_file}</argument>
                        <argument>-cp</argument>
                        <classpath/>
                        <argument>${{exec.mainClass}}</argument>
                    </arguments>
                </configuration>
                <executions>"""
        
        pattern = r"<plugin>\s*<groupId>org\.codehaus\.mojo</groupId>\s*<artifactId>exec-maven-plugin</artifactId>.*?<executions>"
        new_content = re.sub(pattern, exec_config, pom_content, flags=re.DOTALL)
        with open(pom_path, "w") as f:
            f.write(new_content)
        print("POM.XML configurée")
    # Compilation du code soruce java 
    print("Compilation de pyjava_bridge")
    subprocess.run("mvn clean compile", shell=True, check=True, cwd="java")
    print("\n Configuration terminée !!")
if __name__ == "__main__":
    main()
