package com.univ.universite;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Étape 4 : validation d'un document XML par rapport à un schéma XSD
 * avec SchemaFactory et Validator.
 */
public class XmlValidator {

    /**
     * Valide le fichier XML (ressource du classpath) avec le schéma XSD (ressource du classpath).
     *
     * @return la liste des erreurs de validation (vide si le document est valide)
     */
    public static List<String> valider(String xmlResource, String xsdResource) throws IOException, SAXException {
        List<String> erreurs = new ArrayList<>();

        // 1. Créer une fabrique de schémas pour XML Schema 1.0
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        try (InputStream xsd = ouvrir(xsdResource);
             InputStream xml = ouvrir(xmlResource)) {

            // 2. Charger le schéma XSD
            Schema schema = factory.newSchema(new StreamSource(xsd));

            // 3. Créer le validateur à partir du schéma
            Validator validator = schema.newValidator();

            // 4. Collecter toutes les erreurs au lieu de s'arrêter à la première
            validator.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException e) {
                    erreurs.add("[AVERTISSEMENT] ligne " + e.getLineNumber() + " : " + e.getMessage());
                }

                @Override
                public void error(SAXParseException e) {
                    erreurs.add("[ERREUR] ligne " + e.getLineNumber() + " : " + e.getMessage());
                }

                @Override
                public void fatalError(SAXParseException e) throws SAXException {
                    erreurs.add("[FATAL] ligne " + e.getLineNumber() + " : " + e.getMessage());
                    throw e;
                }
            });

            // 5. Valider le document XML
            try {
                validator.validate(new StreamSource(xml));
            } catch (SAXParseException fatal) {
                // déjà enregistrée par le handler
            }
        }
        return erreurs;
    }

    private static InputStream ouvrir(String ressource) throws IOException {
        InputStream in = XmlValidator.class.getClassLoader().getResourceAsStream(ressource);
        if (in == null) {
            throw new IOException("Ressource introuvable dans le classpath : " + ressource);
        }
        return in;
    }
}
