package fr.lirmm.pyjava;

import fr.lirmm.pyjava.api.PyJavaBridge;
import fr.lirmm.pyjava.contract.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        try (PyJavaBridge bridge = PyJava.load()) {

            System.out.println("[MODE 1] pyFunc[Dynamic-Linker] automatisation de reconnaisance du module ");
            Maths math = bridge.pyFunc(Maths.class);
            Textes text = bridge.pyFunc(Textes.class);
            Donnees data = bridge.pyFunc(Donnees.class);
            System.out.println("1.  Puissance(2,8) : " + math.puissance(2L, 8L));
            System.out.println("2.  Racine(16.0)    : " + math.racine_carree(16.0));
            System.out.println("3.  Multiplier(6,7) : " + math.multiplier(6L, 7L));
            System.out.println("4.  Est Pair(42)    : " + math.est_pair(42L));
            System.out.println("5.  Inverser('Java'): " + text.inverser("Java"));
            System.out.println("6.  Majuscule       : " + text.majuscule("interop"));
            System.out.println("7.  Compte Mots(`pyjava bridge`)     : " + text.compte_mots("pyjava bridge"));
            System.out.println("8.  Concaténer      : " + text.concatener("Hello", "World"));
            System.out.println("9.  Saluer          : " + text.saluer("Raphael"));
            System.out.println("10. Moyenne(10,20)  : " + data.moyenne(List.of(10L, 20L)));
            System.out.println("11. Filtrer Positifs: " + data.filtrer_positifs(List.of(-1L, 5L, -3L, 10L)));
            System.out.println("12. Générer carrés  : " + data.generer_carres(5L));
            System.out.println("13. additionner(10,5): " + math.additionner(10L, 5L));
            System.out.println("14. Palindrome('KAYAK') : " + text.est_palindrome("KAYAK"));
            System.out.println("15. trier_liste([3,1,2]) : " + data.trier_liste(List.of(3L, 1L, 2L)));

            System.out.println("\n[MODE 2] invoke(module,user_func_name,returnType,args");
            Double tva = bridge.invoke("working_directory.maths", "calculer_tva", Double.class, 150.0);
            String email = bridge.invoke("working_directory.textes", "masquer_email", String.class,
                    "raphael.bouchrani@etu.umontpellier.fr");
            System.out.println("TVA   : 150€ -> " + tva + "€");
            System.out.println("Email : " + email);

            System.out.println("\ninvoke(module,user_func_name,args");
            Object resFac = bridge.invoke("working_directory.maths", "factorielle", 5L);
            Object resSys = bridge.invoke("working_directory.donnees", "infos_systeme");
            System.out.println("Factorielle(5) : " + resFac);
            System.out.println("Infos Système  : " + resSys);

        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
        }
    }
}