package com.univ.universite;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UniversiteTest {

    @Test
    void xmlValideEstAccepte() throws Exception {
        assertTrue(XmlValidator.valider("universite.xml", "universite.xsd").isEmpty());
    }

    @Test
    void xmlInvalideEstRejete() throws Exception {
        assertFalse(XmlValidator.valider("universite-invalide.xml", "universite.xsd").isEmpty());
    }

    @Test
    void titresDesCours() throws Exception {
        List<String> titres = new XPathQueries("universite.xml").titresDesCours();
        assertEquals(5, titres.size());
        assertTrue(titres.contains("Programmation Java"));
    }

    @Test
    void etudiantsActifs() throws Exception {
        List<String> actifs = new XPathQueries("universite.xml").etudiantsActifs();
        assertEquals(3, actifs.size());
        assertFalse(actifs.contains("Karim Trabelsi"));
    }

    @Test
    void coursOptionnels() throws Exception {
        List<String> optionnels = new XPathQueries("universite.xml").coursOptionnels();
        assertEquals(List.of("Technologies XML", "Gestion de projet"), optionnels);
    }
}
