package br.edu.ufla.ppoo.gui;

import java.util.List;
/**
 * Responsavel pela simulacao.
 * @author David J. Barnes and Michael Kolling and Luiz Merschmann
 */
public class Simulacao {
    private Jogador jogador;
    private JanelaSimulacao janelaSimulacao;
    private Mapa mapa;
    
    public Simulacao(){ 
        mapa = new Mapa();
        jogador = new Jogador();//Cria jogador
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
        List<Posicao> caminho = mapa.encontrarCaminho(origem, destino);
        if (caminho.isEmpty()) {
            System.out.println("Não foi possível encontrar um caminho para o destino.");
            return;
        }

        jogador.setPosicaoOrigem(origem);
        jogador.setPosicaoDestino(destino);
        mapa.adicionarItem(jogador);
        try {
            janelaSimulacao.executarAcao();
            for (int i = 1; i < caminho.size(); i++) {
                executarUmPasso(caminho.get(i));
                esperar(100);
            }
        } finally {
            mapa.removerItem(jogador);
        }

        if (caminho.size() == 1) {
            System.out.println("O jogador já está no destino.");
        } else {
            System.out.println("Movimentação concluída.");
        }
    }

    private void executarUmPasso(Posicao proximaLocalizacao) {
        mapa.removerItem(jogador);
        jogador.executarAcao(proximaLocalizacao);
        mapa.adicionarItem(jogador);
        janelaSimulacao.executarAcao();
    }
    
    private void esperar(int milisegundos){
        try{
            Thread.sleep(milisegundos);
        }catch(InterruptedException e){
            System.out.println(e.getMessage());
        }
    }
    
}
