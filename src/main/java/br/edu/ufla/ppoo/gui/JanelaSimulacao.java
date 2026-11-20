package br.edu.ufla.ppoo.gui;

import javax.swing.JFrame;

/**
 * Exibe a visão do mapa em uma janela da demonstração.
 *
 * @author David J. Barnes and Michael Kolling and Luiz Merschmann
 */
public final class JanelaSimulacao extends JFrame {
    private static final long serialVersionUID = 1L;

    /** Painel que apresenta o mapa e a posição do personagem. */
    private final VisaoMapa visaoMapa;

    /**
     * Cria a janela para a geometria fornecida.
     *
     * @param mapa geometria a ser desenhada
     */
    public JanelaSimulacao(Mapa mapa) {
        super("World of Zuul");
        visaoMapa = new VisaoMapa(mapa);
        add(visaoMapa);
        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    /**
     * Solicita a exibição de um quadro do percurso.
     *
     * @param posicao posição visual do personagem
     * @throws NullPointerException se a posição for nula
     */
    void exibirPosicao(Posicao posicao) {
        visaoMapa.atualizarPosicao(posicao);
    }
}
