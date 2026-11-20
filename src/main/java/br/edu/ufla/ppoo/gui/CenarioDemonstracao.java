package br.edu.ufla.ppoo.gui;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Reúne os dados do cenário fornecido com a demonstração gráfica.
 *
 * @author David J. Barnes and Michael Kolling and Luiz Merschmann
 */
final class CenarioDemonstracao {
    private static final char PAREDE = '#';
    private static final char CELULA_LIVRE = '.';
    private static final List<String> PLANTA = List.<String>of(
            "####################",
            "#......#....#......#",
            "#QUARTO1.C..QUARTO2#",
            "#......#.O..#......#",
            "########.R..########",
            "#......#.R..#......#",
            "#.WC1....E....WC2..#",
            "#......#.D..#......#",
            "########.O..########",
            "#.SALA.#.R..#......#",
            "#JANTAR.....COZINHA#",
            "#......#....#......#",
            "##.....######.....##",
            "#........#....AREA.#",
            "#SALA.TV.#..SERVIÇO#",
            "#........#.........#",
            "####################");
    private static final Mapa MAPA = criarMapa();
    private static final Map<String, Posicao> REFERENCIAS = criarReferencias();

    private CenarioDemonstracao() {
    }

    /**
     * Fornece o mapa fixo usado na demonstração.
     *
     * @return geometria do cenário
     */
    static Mapa mapa() {
        return MAPA;
    }

    /**
     * Fornece as células usadas como origem e destino na demonstração.
     *
     * @return mapa imutável dos nomes para suas posições
     */
    static Map<String, Posicao> referencias() {
        return REFERENCIAS;
    }

    private static Mapa criarMapa() {
        Set<Posicao> paredes = new HashSet<Posicao>();
        Map<Posicao, String> letreiros = new HashMap<Posicao, String>();
        for (int y = 0; y < PLANTA.size(); y++) {
            String linha = PLANTA.get(y);
            for (int x = 0; x < linha.length(); x++) {
                char celula = linha.charAt(x);
                if (celula == PAREDE) {
                    paredes.add(new Posicao(x, y));
                } else if (celula != CELULA_LIVRE) {
                    letreiros.put(new Posicao(x, y), String.valueOf(celula));
                }
            }
        }
        return new Mapa(PLANTA.getFirst().length(), PLANTA.size(), paredes, letreiros);
    }

    private static Map<String, Posicao> criarReferencias() {
        Map<String, Posicao> referencias = new LinkedHashMap<String, Posicao>();
        referencias.put("Quarto 1", new Posicao(4, 2));
        referencias.put("Quarto 2", new Posicao(16, 2));
        referencias.put("WC 1", new Posicao(4, 6));
        referencias.put("WC 2", new Posicao(16, 6));
        referencias.put("Sala de Jantar", new Posicao(4, 10));
        referencias.put("Cozinha", new Posicao(16, 10));
        referencias.put("Sala de TV", new Posicao(4, 14));
        referencias.put("Área de Serviço", new Posicao(16, 14));
        referencias.put("Corredor", new Posicao(9, 6));
        return Collections.<String, Posicao>unmodifiableMap(referencias);
    }
}
