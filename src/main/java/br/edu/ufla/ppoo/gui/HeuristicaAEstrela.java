package br.edu.ufla.ppoo.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Encontra percursos mínimos no mapa com busca A* e movimentos ortogonais
 * de custo unitário.
 */
public class HeuristicaAEstrela {
    private static final Comparator<No> POR_CUSTO_TOTAL = new Comparator<No>() {
        @Override
        public int compare(No primeiro, No segundo) {
            return Integer.compare(primeiro.getCustoF(), segundo.getCustoF());
        }
    };

    private final Mapa mapa;

    /**
     * Prepara a busca para consultar as células transitáveis do mapa.
     *
     * @param mapa mapa usado na busca
     * @throws NullPointerException se o mapa for nulo
     */
    public HeuristicaAEstrela(Mapa mapa) {
        this.mapa = Objects.requireNonNull(mapa, "O mapa é obrigatório.");
    }

    /**
     * Busca o menor percurso entre duas posições.
     *
     * @param origem posição inicial
     * @param destino posição final
     * @return percurso incluindo origem e destino, ou uma lista vazia se algum
     *         extremo não for transitável ou se não houver caminho. Quando
     *         origem e destino coincidem em uma célula transitável, devolve
     *         um percurso com apenas essa posição.
     * @throws NullPointerException se a origem ou o destino for nulo
     */
    public List<Posicao> encontrarCaminho(Posicao origem, Posicao destino) {
        Objects.requireNonNull(origem, "A origem é obrigatória.");
        Objects.requireNonNull(destino, "O destino é obrigatório.");
        if (!mapa.podePassar(origem.getX(), origem.getY())
                || !mapa.podePassar(destino.getX(), destino.getY())) {
            return Collections.<Posicao>emptyList();
        }

        PriorityQueue<No> abertos = new PriorityQueue<No>(POR_CUSTO_TOTAL);
        Set<Posicao> fechados = new HashSet<Posicao>();
        Map<Posicao, Integer> melhoresCustos = new HashMap<Posicao, Integer>();

        No noInicial = new No(
            origem,
            null,
            0,
            distanciaManhattan(origem, destino)
        );

        abertos.add(noInicial);
        melhoresCustos.put(origem, 0);
        
        while (!abertos.isEmpty()) {
            No atual = abertos.remove();

            Posicao posicaoAtual = atual.getPosicao();

            // A fila pode conter mais de um nó para a mesma posição.
            if (!fechados.add(posicaoAtual)) {
                continue;
            }
            if (posicaoAtual.equals(destino)) {
                return reconstruirCaminho(atual);
            }

            int novoCustoG = atual.getCustoG() + 1;
            for (Posicao vizinho : obterVizinhos(posicaoAtual)) {

                if (fechados.contains(vizinho)) {
                    continue;
                }

                Integer melhorCusto =
                    melhoresCustos.get(vizinho);

                if (melhorCusto == null ||
                    novoCustoG < melhorCusto) {

                    melhoresCustos.put(
                        vizinho,
                        novoCustoG
                    );

                    int custoH =
                        distanciaManhattan(
                            vizinho,
                            destino
                        );

                    No novoNo = new No(
                        vizinho,
                        atual,
                        novoCustoG,
                        custoH
                    );

                    abertos.add(novoNo);
                }
            }
        }

        return Collections.<Posicao>emptyList();
    }

    private List<Posicao> reconstruirCaminho(No no) {

        List<Posicao> caminho = new ArrayList<Posicao>();

        while (no != null) {
            caminho.add(no.getPosicao());
            no = no.getPai();
        }

        Collections.reverse(caminho);

        return caminho;
    }

    private int distanciaManhattan(
            Posicao a,
            Posicao b) {

        return Math.abs(a.getX() - b.getX())
             + Math.abs(a.getY() - b.getY());
    }

    private List<Posicao> obterVizinhos(
            Posicao posicao) {

        List<Posicao> vizinhos = new ArrayList<Posicao>();

        int x = posicao.getX();
        int y = posicao.getY();

        adicionarSePossivel(vizinhos, x + 1, y);
        adicionarSePossivel(vizinhos, x - 1, y);
        adicionarSePossivel(vizinhos, x, y + 1);
        adicionarSePossivel(vizinhos, x, y - 1);

        return vizinhos;
    }

    private void adicionarSePossivel(
            List<Posicao> vizinhos,
            int x,
            int y) {

        if (mapa.podePassar(x, y)) {
            vizinhos.add(new Posicao(x, y));
        }
    }
}
