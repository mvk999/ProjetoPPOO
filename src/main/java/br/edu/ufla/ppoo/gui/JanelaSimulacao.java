package br.edu.ufla.ppoo.gui;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * Exibe a visão do mapa em uma janela da demonstração.
 *
 * @author David J. Barnes and Michael Kolling and Luiz Merschmann
 */
public final class JanelaSimulacao extends JFrame {
    private static final long serialVersionUID = 1L;

    /** Painel que apresenta o mapa e a posição do personagem. */
    private final VisaoMapa visaoMapa;

    /* Rótulos da GUI */
    private JLabel rotuloNumTentativasRestantes;
   // private JLabel numTentativasRestantes;
    private JLabel rotuloDurabilidadeChaveMestra;
   // private JLabel durabilidadeChaveMestra;
    private JLabel rotuloDicasEncontradas;
    private JTextArea areaDicas;
    private JTextArea areaMensagens;

    /**
     * Configura a janela na thread de eventos do Swing.
     *
     * @param mapa geometria a ser desenhada
     */
    public JanelaSimulacao(Mapa mapa) {
        super("World of Zuul");
        setSize(1000, 700);
        setLayout(new BorderLayout());

        /* Painel da Esquerda */
        JPanel painelEsq = new JPanel();
        painelEsq.setLayout(new BoxLayout(painelEsq, BoxLayout.Y_AXIS));
        painelEsq.add(rotuloNumTentativasRestantes = new JLabel("Nº de Tentativas Restantes:"));
        rotuloNumTentativasRestantes.setFont(new Font("Arial", Font.PLAIN, 24));
        painelEsq.add(rotuloDurabilidadeChaveMestra = new JLabel("Durabilidade da Chave Mestra:"));
        rotuloDurabilidadeChaveMestra.setFont(new Font("Arial", Font.PLAIN, 24));
        add(painelEsq, BorderLayout.WEST);

        /* Painel do Centro */
        JPanel painelCentral = new JPanel();
        painelCentral.add(visaoMapa = new VisaoMapa(mapa));
        add(painelCentral, BorderLayout.CENTER);
 
        /* Painel da Direita */
        JPanel painelDir = new JPanel();
        painelDir.setLayout(new BoxLayout(painelDir, BoxLayout.Y_AXIS));
        painelDir.add(rotuloDicasEncontradas = new JLabel("Dicas Encontradas:"));
        rotuloDicasEncontradas.setFont(new Font("Arial", Font.PLAIN, 24));
        painelDir.add(areaDicas = new JTextArea());
        areaDicas.setEditable(false); //não permite que o usuario edite a área
        areaDicas.setLineWrap(true); //permite quebra de linha
        areaDicas.setWrapStyleWord(true); //quebra de linha se chegar no limite

        add(painelDir, BorderLayout.EAST);

        /* Painel Inferior */
        JPanel painelInf = new JPanel();

        add(painelInf, BorderLayout.SOUTH);
        

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
    }

    /**
     * Solicita a exibição de um quadro do percurso na thread de eventos.
     *
     * @param posicao posição visual do personagem
     * @throws NullPointerException se a posição for nula
     */
    void exibirPosicao(Posicao posicao) {
        visaoMapa.atualizarPosicao(posicao);
    }
}
