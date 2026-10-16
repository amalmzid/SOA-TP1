package com.univ.universite;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Main {

    private static final String XML = "universite.xml";
    private static final String XML_INVALIDE = "universite-invalide.xml";
    private static final String XSD = "universite.xsd";

    public static void main(String[] args) throws Exception {
        // Affichage correct des accents dans la console
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        // ===== Étape 4 : validation XML / XSD =====
        System.out.println("=== VALIDATION XML / XSD ===");

        List<String> erreurs = XmlValidator.valider(XML, XSD);
        if (erreurs.isEmpty()) {
            System.out.println("[OK] " + XML + " est valide par rapport à " + XSD);
        } else {
            System.out.println("[KO] " + XML + " est invalide :");
            erreurs.forEach(e -> System.out.println("   " + e));
        }

        System.out.println();
        List<String> erreursInvalide = XmlValidator.valider(XML_INVALIDE, XSD);
        if (erreursInvalide.isEmpty()) {
            System.out.println("[OK] " + XML_INVALIDE + " est valide");
        } else {
            System.out.println("[KO] " + XML_INVALIDE + " est invalide (attendu) : "
                    + erreursInvalide.size() + " erreur(s)");
            erreursInvalide.forEach(e -> System.out.println("   " + e));
        }

        // Si le document de référence n'est pas valide, on n'interroge pas
        if (!erreurs.isEmpty()) {
            return;
        }

        // ===== Étape 5 : requêtes XPath =====
        System.out.println();
        System.out.println("=== REQUÊTES XPATH ===");
        XPathQueries q = new XPathQueries(XML);

        afficher("Tous les titres de cours", "/universite/cours/cours-item/titre", q.titresDesCours());
        afficher("Étudiants actifs", "/universite/etudiants/etudiant[@actif='true']", q.etudiantsActifs());
        afficher("Cours optionnels", "/universite/cours/cours-item[@optionnel='true']/titre", q.coursOptionnels());

        System.out.println();
        System.out.println("--- Requêtes bonus ---");
        afficher("Cours du département Informatique",
                "/universite/cours/cours-item[departement='Informatique']/titre", q.coursInformatique());
        afficher("Étudiants avec moyenne >= 14",
                "/universite/etudiants/etudiant[moyenne >= 14]/nom", q.etudiantsMention());
        afficher("Cours enseignés par E001",
                "/universite/cours/cours-item[enseignantRef='E001']/titre", q.coursDeEnseignant("E001"));
        System.out.println();
        System.out.println("Nombre d'étudiants : " + q.nombreEtudiants());
        System.out.printf("Moyenne générale   : %.2f%n", q.moyenneGenerale());
        System.out.println("Total des crédits  : " + q.totalCredits());
    }

    private static void afficher(String titre, String xpath, List<String> resultats) {
        System.out.println();
        System.out.println(titre + "  ->  " + xpath);
        resultats.forEach(r -> System.out.println("   - " + r));
    }
}
