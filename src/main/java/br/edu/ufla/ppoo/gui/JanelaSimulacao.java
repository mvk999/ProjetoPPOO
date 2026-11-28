package br.edu.ufla.ppoo.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

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
    private JLabel numTentativasRestantes;
    private JLabel rotuloDurabilidadeChaveMestra;
    private JLabel durabilidadeChaveMestra;
    private JLabel rotuloDicasEncontradas;
    private JTextArea areaDicas;
    private JTextArea areaMensagens;
    private JTextField campoComando;

    /**
     * Configura a janela na thread de eventos do Swing.
     *
     * @param mapa geometria a ser desenhada
     */
    public JanelaSimulacao(Mapa mapa) {
        super("World of Zuul");

        setLayout(new BorderLayout(5, 5));

        /* =========================================================
        * Painel da Esquerda */
        JPanel painelEsq = new JPanel();
        painelEsq.setLayout(new BoxLayout(painelEsq, BoxLayout.Y_AXIS));
        

        rotuloNumTentativasRestantes = new JLabel("Nº de Tentativas Restantes:");
        numTentativasRestantes = new JLabel("30");

        rotuloDurabilidadeChaveMestra = new JLabel("Durabilidade da Chave Mestra:");
        durabilidadeChaveMestra = new JLabel("10");

        rotuloNumTentativasRestantes.setFont(new Font("Arial", Font.PLAIN, 16));
        numTentativasRestantes.setFont(new Font("Arial", Font.PLAIN, 16));
        rotuloDurabilidadeChaveMestra.setFont(new Font("Arial", Font.PLAIN, 16));
        durabilidadeChaveMestra.setFont(new Font("Arial", Font.PLAIN, 16));

        painelEsq.add(rotuloNumTentativasRestantes);
        painelEsq.add(numTentativasRestantes);
        painelEsq.add(javax.swing.Box.createVerticalStrut(20)); 
        painelEsq.add(rotuloDurabilidadeChaveMestra);
        painelEsq.add(durabilidadeChaveMestra);

        add(painelEsq, BorderLayout.WEST);

        /* =========================================================
        * Painel Central */
        JPanel painelCentral = new JPanel(new GridBagLayout());

        visaoMapa = new VisaoMapa(mapa);

        painelCentral.add(visaoMapa);

        add(painelCentral, BorderLayout.CENTER);

        /* =========================================================
        * Painel da Direita */
        JPanel painelDir = new JPanel(new BorderLayout(5, 5));

        painelDir.setBorder(BorderFactory.createTitledBorder("Dicas Encontradas"));

        painelDir.setPreferredSize(new Dimension(220, 0));

        areaDicas = new JTextArea();
        areaDicas.setEditable(false);
        areaDicas.setLineWrap(true);
        areaDicas.setWrapStyleWord(true);
        areaDicas.setFont(new Font("Arial", Font.PLAIN, 16));

        painelDir.add(new JScrollPane(areaDicas), BorderLayout.CENTER);

        add(painelDir, BorderLayout.EAST);

        /* =========================================================
        * Painel Inferior */
        JPanel painelInf = new JPanel(new BorderLayout(5, 5));

        painelInf.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        painelInf.setPreferredSize(new Dimension(0, 160));

        areaMensagens = new JTextArea();
        areaMensagens.setEditable(false);
        areaMensagens.setLineWrap(true);
        areaMensagens.setWrapStyleWord(true);
        areaMensagens.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane rolagemMensagens = new JScrollPane(areaMensagens);

        painelInf.add(rolagemMensagens, BorderLayout.CENTER);

        JPanel painelComando = new JPanel(new BorderLayout(5, 5));

        JLabel indicadorComando = new JLabel(">");

        campoComando = new JTextField();

        painelComando.add(indicadorComando, BorderLayout.WEST);
        painelComando.add(campoComando, BorderLayout.CENTER);

        painelInf.add(painelComando, BorderLayout.SOUTH);

        add(painelInf, BorderLayout.SOUTH);


        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        pack();
        setLocationRelativeTo(null);
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
