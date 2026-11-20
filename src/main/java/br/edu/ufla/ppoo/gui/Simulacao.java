package br.edu.ufla.ppoo.gui;

import java.util.List;

/**
 * Coordena a busca do percurso e a exibição de seus quadros.
 *
 * @author David J. Barnes and Michael Kolling and Luiz Merschmann
 */
public class Simulacao {
    private static final int INTERVALO_ENTRE_PASSOS_MS = 100;
    private final HeuristicaAEstrela busca;
    private final JanelaSimulacao janelaSimulacao;

    /**
     * Prepara a busca e a janela com a mesma geometria da demonstração.
     */
    public Simulacao() {
        Mapa mapa = CenarioDemonstracao.mapa();
        busca = new HeuristicaAEstrela(mapa);
        janelaSimulacao = new JanelaSimulacao(mapa);
    }
    
    /**
     * Exibe o percurso encontrado. Se o percurso estiver vazio, informa a
     * falha e preserva o estado da simulação.
     *
     * @param origem posição inicial
     * @param destino posição final
     * @throws NullPointerException se a origem ou o destino for nulo
     */
    public void executarSimulacao(Posicao origem, Posicao destino) {
        List<Posicao> caminho = busca.encontrarCaminho(origem, destino);
        if (caminho.isEmpty()) {
            System.out.println("Não foi possível encontrar um caminho para o destino.");
            return;
        }

        janelaSimulacao.exibirPosicao(caminho.getFirst());
        for (int i = 1; i < caminho.size(); i++) {
            janelaSimulacao.exibirPosicao(caminho.get(i));
            esperar(INTERVALO_ENTRE_PASSOS_MS);
        }

        if (caminho.size() == 1) {
            System.out.println("O jogador já está no destino.");
        } else {
            System.out.println("Movimentação concluída.");
        }
    }

    private void esperar(int milisegundos){
        try{
            Thread.sleep(milisegundos);
        }catch(InterruptedException e){
            System.out.println(e.getMessage());
        }
    }
    
}
