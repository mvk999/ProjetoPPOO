package br.edu.ufla.ppoo.gui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class VisaoMapaTest {
    private static final int COLUNAS = 3;
    private static final int LINHAS = 2;
    private static final int CELULA = 40;
    private static final Posicao PAREDE = new Posicao(0, 0);
    private static final Posicao LETREIRO = new Posicao(1, 0);
    private static final Posicao LIVRE = new Posicao(2, 0);

    @Test
    void personagemMudaDeCelulaERestauraLetreiroSemAlterarParede() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                Mapa mapa = new Mapa(COLUNAS, LINHAS,
                        Set.<Posicao>of(PAREDE), Map.<Posicao, String>of(LETREIRO, "X"));
                VisaoMapa visao = new VisaoMapa(mapa);
                visao.setSize(COLUNAS * CELULA, LINHAS * CELULA);

                BufferedImage semPersonagem = pintar(visao);
                assertFalse(Arrays.equals(pixels(semPersonagem, PAREDE), pixels(semPersonagem, LIVRE)),
                        "O tijolo deve ser desenhado na célula da parede.");
                assertFalse(Arrays.equals(pixels(semPersonagem, LETREIRO), pixels(semPersonagem, LIVRE)),
                        "O letreiro deve ser visível antes da chegada do personagem.");

                visao.atualizarPosicao(LETREIRO);
                BufferedImage primeiroQuadro = pintar(visao);
                assertFalse(Arrays.equals(pixels(semPersonagem, LETREIRO), pixels(primeiroQuadro, LETREIRO)),
                        "O personagem deve ocupar a célula indicada.");
                assertArrayEquals(pixels(semPersonagem, LIVRE), pixels(primeiroQuadro, LIVRE));

                visao.atualizarPosicao(LIVRE);
                BufferedImage segundoQuadro = pintar(visao);
                assertArrayEquals(pixels(semPersonagem, LETREIRO), pixels(segundoQuadro, LETREIRO),
                        "O letreiro deve reaparecer assim que o personagem sair.");
                assertArrayEquals(pixels(semPersonagem, PAREDE), pixels(segundoQuadro, PAREDE));
                assertFalse(Arrays.equals(pixels(semPersonagem, LIVRE), pixels(segundoQuadro, LIVRE)),
                        "O personagem deve aparecer na nova célula.");
            }
        });
    }

    private static BufferedImage pintar(VisaoMapa visao) {
        BufferedImage imagem = new BufferedImage(visao.getWidth(), visao.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D quadro = imagem.createGraphics();
        try {
            visao.paint(quadro);
        } finally {
            quadro.dispose();
        }
        return imagem;
    }

    private static int[] pixels(BufferedImage imagem, Posicao posicao) {
        return imagem.getRGB(posicao.getX() * CELULA, posicao.getY() * CELULA,
                CELULA, CELULA, null, 0, CELULA);
    }
}
