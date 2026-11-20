package br.edu.ufla.ppoo.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Desenha a grade e a posição visual do personagem.
 *
 * @author David J. Barnes and Michael Kolling and Luiz Merschmann
 */
final class VisaoMapa extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int TAMANHO_CELULA = 40;
    private static final Color COR_FUNDO = Color.WHITE;
    private static final Font FONTE_LETREIROS = new Font("Arial", Font.BOLD, 30);

    private final BufferedImage imagemMapa;
    private final BufferedImage imagemPersonagem;
    private volatile Posicao posicaoVisual = null;

    /**
     * Prepara o desenho da geometria. O personagem aparece no primeiro quadro.
     *
     * @param mapa geometria a desenhar
     */
    VisaoMapa(Mapa mapa) {
        Objects.requireNonNull(mapa, "O mapa é obrigatório.");
        imagemMapa = desenharMapa(mapa, carregarImagem("/Imagens/tijolo.png"));
        imagemPersonagem = carregarImagem("/Imagens/pessoa.png");
        setBackground(COR_FUNDO);
        setPreferredSize(new Dimension(imagemMapa.getWidth(), imagemMapa.getHeight()));
    }

    /**
     * Publica a posição do personagem para o próximo redesenho.
     *
     * @param posicao célula do personagem
     * @throws NullPointerException se a posição for nula
     */
    void atualizarPosicao(Posicao posicao) {
        posicaoVisual = Objects.requireNonNull(posicao, "A posição visual é obrigatória.");
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graficos) {
        super.paintComponent(graficos);
        Graphics2D quadro = (Graphics2D) graficos.create();
        try {
            quadro.scale((double) getWidth() / imagemMapa.getWidth(),
                    (double) getHeight() / imagemMapa.getHeight());
            quadro.drawImage(imagemMapa, 0, 0, null);
            Posicao posicao = posicaoVisual;
            if (posicao != null) {
                quadro.setColor(COR_FUNDO);
                quadro.fillRect(posicao.getX() * TAMANHO_CELULA + 1, posicao.getY() * TAMANHO_CELULA + 1,
                        TAMANHO_CELULA - 1, TAMANHO_CELULA - 1);
                desenharNaCelula(quadro, imagemPersonagem, posicao);
            }
        } finally {
            quadro.dispose();
        }
    }

    private static BufferedImage desenharMapa(Mapa mapa, BufferedImage tijolo) {
        int largura = Math.multiplyExact(mapa.getLargura(), TAMANHO_CELULA);
        int altura = Math.multiplyExact(mapa.getAltura(), TAMANHO_CELULA);
        BufferedImage fundo = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_RGB);
        Graphics2D quadro = fundo.createGraphics();
        try {
            quadro.setColor(COR_FUNDO);
            quadro.fillRect(0, 0, largura, altura);
            quadro.setColor(Color.GRAY);
            for (int x = 0; x < largura; x += TAMANHO_CELULA) {
                quadro.drawLine(x, 0, x, altura - 1);
            }
            for (int y = 0; y < altura; y += TAMANHO_CELULA) {
                quadro.drawLine(0, y, largura - 1, y);
            }
            for (Posicao parede : mapa.getParedes()) {
                desenharNaCelula(quadro, tijolo, parede);
            }
            quadro.setColor(Color.BLACK);
            quadro.setFont(FONTE_LETREIROS);
            for (Map.Entry<Posicao, String> letreiro : mapa.getLetreiros().entrySet()) {
                desenharLetreiro(quadro, letreiro.getKey(), letreiro.getValue());
            }
        } finally {
            quadro.dispose();
        }
        return fundo;
    }

    private static void desenharNaCelula(Graphics2D quadro, BufferedImage imagem, Posicao posicao) {
        quadro.drawImage(imagem, posicao.getX() * TAMANHO_CELULA + 1, posicao.getY() * TAMANHO_CELULA + 1,
                TAMANHO_CELULA - 1, TAMANHO_CELULA - 1, null);
    }

    private static void desenharLetreiro(Graphics2D quadro, Posicao posicao, String texto) {
        Graphics celula = quadro.create(posicao.getX() * TAMANHO_CELULA + 1,
                posicao.getY() * TAMANHO_CELULA + 1, TAMANHO_CELULA - 1, TAMANHO_CELULA - 1);
        try {
            celula.drawString(texto, 4, 29);
        } finally {
            celula.dispose();
        }
    }

    private static BufferedImage carregarImagem(String caminho) {
        URL recurso = VisaoMapa.class.getResource(caminho);
        if (recurso == null) {
            throw new IllegalStateException("Recurso gráfico ausente: " + caminho);
        }
        try {
            BufferedImage imagem = ImageIO.read(recurso);
            if (imagem == null) {
                throw new IllegalStateException("Formato de imagem inválido: " + caminho);
            }
            return imagem;
        } catch (IOException erro) {
            throw new UncheckedIOException("Falha ao ler a imagem " + caminho, erro);
        }
    }
}
