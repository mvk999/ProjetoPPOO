package br.edu.ufla.ppoo.gui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PosicaoTest {
    @Test
    void coordenadasIguaisTemIgualdadeSimetricaEOMesmoHash() {
        Posicao primeira = new Posicao(-1, 6);
        Posicao segunda = new Posicao(-1, 6);

        assertEquals(primeira, segunda);
        assertEquals(segunda, primeira);
        assertEquals(primeira.hashCode(), segunda.hashCode());
    }

    @Test
    void distingueMudancasEmCadaEixoENaoIgualaOutrosTipos() {
        Posicao posicao = new Posicao(4, 14);

        assertNotEquals(posicao, new Posicao(5, 14));
        assertNotEquals(posicao, new Posicao(4, 15));
        assertFalse(posicao.equals(null));
        assertFalse(posicao.equals("(4, 14)"));
    }

    @Test
    void conjuntoDeVisitadasReconheceOutraInstanciaDaMesmaCelula() {
        Set<Posicao> visitadas = new HashSet<Posicao>();
        visitadas.add(new Posicao(4, 14));

        assertTrue(visitadas.contains(new Posicao(4, 14)));
        assertFalse(visitadas.add(new Posicao(4, 14)));
        assertTrue(visitadas.add(new Posicao(4, 13)));
        assertEquals(2, visitadas.size());
    }

    @Test
    void mapaDeCustosSubstituiOCustoDaMesmaCelulaSemApagarOVizinho() {
        Map<Posicao, Integer> custos = new HashMap<Posicao, Integer>();
        custos.put(new Posicao(4, 14), 9);
        custos.put(new Posicao(5, 14), 10);

        assertEquals(Integer.valueOf(9), custos.put(new Posicao(4, 14), 6));
        assertEquals(Integer.valueOf(6), custos.get(new Posicao(4, 14)));
        assertEquals(Integer.valueOf(10), custos.get(new Posicao(5, 14)));
        assertEquals(2, custos.size());
    }
}
