package br.edu.ufla.ppoo.gui;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HeuristicaAEstrelaTest {
    private static final Posicao ORIGEM = new Posicao(6, 1);
    private static final Posicao DESTINO = new Posicao(8, 1);
    private static final Posicao PAREDE = new Posicao(7, 1);
    private static final Posicao FORA_DO_MAPA = new Posicao(-1, 1);
    private static final int[][] PASSOS_ORTOGONAIS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    private static final Mapa MAPA = new Mapa();
    private static final HeuristicaAEstrela BUSCA = new HeuristicaAEstrela(MAPA);

    @BeforeAll
    static void verificarGeometriaDosTestes() {
        assertTrue(MAPA.podePassar(ORIGEM.getX(), ORIGEM.getY()));
        assertTrue(MAPA.podePassar(DESTINO.getX(), DESTINO.getY()));
        assertFalse(MAPA.podePassar(PAREDE.getX(), PAREDE.getY()));
    }

    @Test
    void contornaParedeEmQuatroPassosOrtogonalmente() {
        List<Posicao> percurso = MAPA.encontrarCaminho(ORIGEM, DESTINO);

        verificarPercurso(MAPA, percurso, ORIGEM, DESTINO, 4);
    }

    @Test
    void encontraCustosMinimosEntreAsReferenciasDaCasa() {
        Posicao[] referencias = {
            new Posicao(4, 2), new Posicao(16, 2), new Posicao(4, 6),
            new Posicao(16, 6), new Posicao(4, 10), new Posicao(16, 10),
            new Posicao(4, 14), new Posicao(16, 14), new Posicao(9, 6)
        };

        for (Posicao origem : referencias) {
            int[][] distancias = calcularDistanciasPorBuscaEmLargura(MAPA, origem);
            for (Posicao destino : referencias) {
                int passos = distancias[destino.getX()][destino.getY()];
                assertTrue(passos >= 0, "Referências da casa devem estar conectadas.");
                verificarPercurso(MAPA, BUSCA.encontrarCaminho(origem, destino), origem, destino, passos);
            }
        }
    }

    @Test
    void devolvePercursoVazioQuandoAlgumExtremoNaoETransitavel() {
        assertTrue(BUSCA.encontrarCaminho(PAREDE, DESTINO).isEmpty(), "Origem em parede.");
        assertTrue(BUSCA.encontrarCaminho(FORA_DO_MAPA, DESTINO).isEmpty(), "Origem fora do mapa.");
        assertTrue(BUSCA.encontrarCaminho(ORIGEM, PAREDE).isEmpty(), "Destino em parede.");
        assertTrue(BUSCA.encontrarCaminho(ORIGEM, FORA_DO_MAPA).isEmpty(), "Destino fora do mapa.");
    }

    @Test
    void origemIgualAoDestinoSoFormaPercursoEmCelulaTransitavel() {
        List<Posicao> percurso = BUSCA.encontrarCaminho(ORIGEM, new Posicao(6, 1));

        verificarPercurso(MAPA, percurso, ORIGEM, ORIGEM, 0);
        assertTrue(BUSCA.encontrarCaminho(PAREDE, PAREDE).isEmpty(), "Mesma célula bloqueada.");
        assertTrue(BUSCA.encontrarCaminho(FORA_DO_MAPA, FORA_DO_MAPA).isEmpty(), "Mesma célula fora do mapa.");
    }

    @Test
    void terminaSemPercursoEntreDuasRegioesDesconectadas() {
        Mapa mapaDividido = new Mapa() {
            @Override
            public boolean podePassar(int x, int y) {
                return x != PAREDE.getX() && super.podePassar(x, y);
            }
        };
        assertTrue(mapaDividido.podePassar(ORIGEM.getX(), ORIGEM.getY()));
        assertTrue(mapaDividido.podePassar(DESTINO.getX(), DESTINO.getY()));

        assertTimeoutPreemptively(Duration.ofSeconds(3), new Executable() {
            @Override
            public void execute() {
                assertTrue(mapaDividido.encontrarCaminho(ORIGEM, DESTINO).isEmpty());
            }
        });
    }

    @Test
    void rejeitaMapaOuExtremosNulosComoErroDeChamada() {
        assertThrows(NullPointerException.class, new Executable() {
            @Override
            public void execute() {
                new HeuristicaAEstrela(null);
            }
        });
        assertThrows(NullPointerException.class, new Executable() {
            @Override
            public void execute() {
                BUSCA.encontrarCaminho(null, DESTINO);
            }
        });
        assertThrows(NullPointerException.class, new Executable() {
            @Override
            public void execute() {
                BUSCA.encontrarCaminho(ORIGEM, null);
            }
        });
    }

    private static int[][] calcularDistanciasPorBuscaEmLargura(Mapa mapa, Posicao origem) {
        int[][] distancias = new int[mapa.getLargura()][mapa.getAltura()];
        for (int[] coluna : distancias) {
            Arrays.fill(coluna, -1);
        }
        Queue<Posicao> fila = new ArrayDeque<Posicao>();
        fila.add(origem);
        distancias[origem.getX()][origem.getY()] = 0;

        while (!fila.isEmpty()) {
            Posicao atual = fila.remove();
            int proximaDistancia = distancias[atual.getX()][atual.getY()] + 1;
            for (int[] passo : PASSOS_ORTOGONAIS) {
                int x = atual.getX() + passo[0];
                int y = atual.getY() + passo[1];
                if (mapa.podePassar(x, y) && distancias[x][y] == -1) {
                    distancias[x][y] = proximaDistancia;
                    fila.add(new Posicao(x, y));
                }
            }
        }
        return distancias;
    }

    private static void verificarPercurso(
            Mapa mapa, List<Posicao> percurso, Posicao origem, Posicao destino, int passos) {
        assertEquals(passos + 1, percurso.size(), "Tamanho do percurso de " + origem + " a " + destino);
        assertEquals(origem, percurso.getFirst());
        assertEquals(destino, percurso.getLast());

        Posicao anterior = origem;
        for (Posicao atual : percurso.subList(1, percurso.size())) {
            int deslocamento = Math.abs(atual.getX() - anterior.getX())
                    + Math.abs(atual.getY() - anterior.getY());
            assertEquals(1, deslocamento, "Passo de " + anterior + " a " + atual);
            assertTrue(mapa.podePassar(atual.getX(), atual.getY()), "Célula bloqueada " + atual);
            anterior = atual;
        }
    }
}
