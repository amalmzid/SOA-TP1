package com.univ.universite;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Étape 5 : interrogation du document XML avec XPathFactory et XPathExpression.
 */
public class XPathQueries {

    private final Document document;
    private final XPath xpath;

    public XPathQueries(String xmlResource) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(xmlResource)) {
            if (in == null) {
                throw new IOException("Ressource introuvable : " + xmlResource);
            }
            this.document = builder.parse(in);
        }
        this.xpath = XPathFactory.newInstance().newXPath();
    }

    /** Exécute une expression XPath et renvoie le texte de chaque noeud trouvé. */
    public List<String> evaluer(String expression) throws XPathExpressionException {
        XPathExpression expr = xpath.compile(expression);
        NodeList noeuds = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
        List<String> resultats = new ArrayList<>();
        for (int i = 0; i < noeuds.getLength(); i++) {
            Node n = noeuds.item(i);
            resultats.add(n.getTextContent().trim());
        }
        return resultats;
    }

    /** Exécute une expression XPath qui renvoie un nombre (count, sum, avg...). */
    public double evaluerNombre(String expression) throws XPathExpressionException {
        XPathExpression expr = xpath.compile(expression);
        return (Double) expr.evaluate(document, XPathConstants.NUMBER);
    }

    // ---------- Requêtes principales ----------

    /** Trouver tous les titres de cours. */
    public List<String> titresDesCours() throws XPathExpressionException {
        return evaluer("/universite/cours/cours-item/titre");
    }

    /** Extraire les étudiants actifs (prénom + nom). */
    public List<String> etudiantsActifs() throws XPathExpressionException {
        List<String> noms = evaluer("/universite/etudiants/etudiant[@actif='true']/nom");
        List<String> prenoms = evaluer("/universite/etudiants/etudiant[@actif='true']/prenom");
        List<String> complets = new ArrayList<>();
        for (int i = 0; i < noms.size(); i++) {
            complets.add(prenoms.get(i) + " " + noms.get(i));
        }
        return complets;
    }

    /** Lister les cours optionnels. */
    public List<String> coursOptionnels() throws XPathExpressionException {
        return evaluer("/universite/cours/cours-item[@optionnel='true']/titre");
    }

    // ---------- Requêtes supplémentaires (bonus) ----------

    /** Cours du département Informatique. */
    public List<String> coursInformatique() throws XPathExpressionException {
        return evaluer("/universite/cours/cours-item[departement='Informatique']/titre");
    }

    /** Étudiants dont la moyenne est supérieure ou égale à 14. */
    public List<String> etudiantsMention() throws XPathExpressionException {
        return evaluer("/universite/etudiants/etudiant[moyenne >= 14]/nom");
    }

    /** Cours enseignés par un enseignant donné (jointure ID/IDREF). */
    public List<String> coursDeEnseignant(String idEnseignant) throws XPathExpressionException {
        return evaluer("/universite/cours/cours-item[enseignantRef='" + idEnseignant + "']/titre");
    }

    /** Nombre total d'étudiants. */
    public int nombreEtudiants() throws XPathExpressionException {
        return (int) evaluerNombre("count(/universite/etudiants/etudiant)");
    }

    /** Moyenne générale des étudiants. */
    public double moyenneGenerale() throws XPathExpressionException {
        return evaluerNombre("sum(/universite/etudiants/etudiant/moyenne) div count(/universite/etudiants/etudiant)");
    }

    /** Total des crédits de tous les cours. */
    public int totalCredits() throws XPathExpressionException {
        return (int) evaluerNombre("sum(/universite/cours/cours-item/credits)");
    }
}
