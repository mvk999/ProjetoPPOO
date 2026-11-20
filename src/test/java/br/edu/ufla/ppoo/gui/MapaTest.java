package br.edu.ufla.ppoo.gui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MapaTest {
    @Test
    void demonstracaoPreservaDimensoesParedesLetreirosEReferencias() {
        Mapa mapa = CenarioDemonstracao.mapa();

        assertEquals(20, mapa.getLargura());
        assertEquals(17, mapa.getAltura());
        assertEquals(121, mapa.getParedes().size());
        assertEquals(62, mapa.getLetreiros().size());
        assertEquals(9, CenarioDemonstracao.referencias().size());
        assertEquals(new Posicao(4, 14), CenarioDemonstracao.referencias().get("Sala de TV"));
        assertFalse(mapa.podePassar(7, 1));
        assertTrue(mapa.podePassar(7, 2));
        assertEquals("1", mapa.getLetreiros().get(new Posicao(7, 2)));
        assertEquals("Ç", mapa.getLetreiros().get(new Posicao(17, 14)));
        for (Posicao referencia : CenarioDemonstracao.referencias().values()) {
            assertTrue(mapa.podePassar(referencia.getX(), referencia.getY()), referencia.toString());
        }
    }

    @Test
    void mapaPequenoIsolaGeometriaDeColecoesMutaveis() {
        Posicao parede = new Posicao(0, 0);
        Posicao letreiro = new Posicao(1, 1);
        Set<Posicao> paredes = new HashSet<Posicao>();
        Map<Posicao, String> letreiros = new HashMap<Posicao, String>();
        paredes.add(parede);
        letreiros.put(letreiro, "X");
        Mapa mapa = new Mapa(2, 2, paredes, letreiros);

        paredes.clear();
        letreiros.put(letreiro, "Y");
        assertFalse(mapa.podePassar(0, 0));
        assertTrue(mapa.podePassar(1, 1));
        assertEquals("X", mapa.getLetreiros().get(letreiro));
        assertFalse(mapa.podePassar(-1, 0));
        assertFalse(mapa.podePassar(2, 0));
        assertFalse(mapa.podePassar(0, -1));
        assertFalse(mapa.podePassar(0, 2));
        assertThrows(UnsupportedOperationException.class, new Executable() {
            @Override
            public void execute() {
                mapa.getParedes().remove(parede);
            }
        });
        assertThrows(UnsupportedOperationException.class, new Executable() {
            @Override
            public void execute() {
                mapa.getLetreiros().put(letreiro, "Z");
            }
        });
    }

    @ParameterizedTest
    @CsvSource({"0, 2", "2, -1"})
    void rejeitaDimensoesInvalidas(int largura, int altura) {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                new Mapa(largura, altura, Set.<Posicao>of(), Map.<Posicao, String>of());
            }
        });
    }

    @Test
    void rejeitaPosicoesDeParedeOuLetreiroForaDoLimite() {
        Posicao externa = new Posicao(2, 1);
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                new Mapa(2, 2, Set.<Posicao>of(externa), Map.<Posicao, String>of());
            }
        });
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                new Mapa(2, 2, Set.<Posicao>of(), Map.<Posicao, String>of(externa, "X"));
            }
        });
    }

    @Test
    void rejeitaLetreiroSobreParede() {
        Posicao posicao = new Posicao(1, 1);
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                new Mapa(2, 2, Set.<Posicao>of(posicao), Map.<Posicao, String>of(posicao, "X"));
            }
        });
    }
}
