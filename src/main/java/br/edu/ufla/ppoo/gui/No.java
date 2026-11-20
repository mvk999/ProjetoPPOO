package br.edu.ufla.ppoo.gui;

/**
 * Registra uma posição descoberta pelo A* e os dados usados para ordenar a busca e reconstruir o percurso.
 */
public class No {

    private Posicao posicao;
    private No pai;

    private int custoG;
    private int custoH;

    /**
     * Cria um nó da busca com seu predecessor e seus custos.
     *
     * @param posicao coordenadas representadas pelo nó
     * @param pai predecessor, ou {@code null} para a origem
     * @param custoG custo acumulado desde a origem
     * @param custoH estimativa do custo restante até o destino
     */
    public No(Posicao posicao, No pai, int custoG, int custoH) {
        this.posicao = posicao;
        this.pai = pai;
        this.custoG = custoG;
        this.custoH = custoH;
    }

    /**
     * Consulta as coordenadas do nó.
     *
     * @return posição representada pelo nó
     */
    public Posicao getPosicao() {
        return posicao;
    }

    /**
     * Consulta o nó anterior no percurso.
     *
     * @return predecessor, ou {@code null} para a origem
     */
    public No getPai() {
        return pai;
    }

    /**
     * Consulta o custo do caminho percorrido.
     *
     * @return custo acumulado desde a origem
     */
    public int getCustoG() {
        return custoG;
    }

    /**
     * Consulta a estimativa heurística da busca.
     *
     * @return estimativa do custo restante até o destino
     */
    public int getCustoH() {
        return custoH;
    }

    /**
     * Calcula a prioridade do nó na fila do A*.
     *
     * @return soma do custo acumulado com a estimativa de custo restante
     */
    public int getCustoF() {
        return custoG + custoH;
    }
}
