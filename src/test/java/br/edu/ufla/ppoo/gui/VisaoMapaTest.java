package br.edu.ufla.ppoo.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.UncheckedIOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    void distingueImagemAusenteFormatoInvalidoEFalhaDeLeitura(@TempDir Path diretorio) throws Exception {
        assertEquals("Recurso gráfico ausente: /Imagens/tijolo.png",
                assertInstanceOf(IllegalStateException.class, erroAoCarregarTijolo(null)).getMessage());

        Path invalida = Files.writeString(diretorio.resolve("imagem-invalida.png"), "conteúdo sem formato de imagem");
        assertEquals("Formato de imagem inválido: /Imagens/tijolo.png",
                assertInstanceOf(IllegalStateException.class,
                        erroAoCarregarTijolo(invalida.toUri().toURL())).getMessage());

        URL inacessivel = diretorio.resolve("imagem-inexistente.png").toUri().toURL();
        assertEquals("Falha ao ler a imagem /Imagens/tijolo.png",
                assertInstanceOf(UncheckedIOException.class, erroAoCarregarTijolo(inacessivel)).getMessage());
    }

    private static Throwable erroAoCarregarTijolo(URL tijolo) throws Exception {
        URL classes = VisaoMapa.class.getProtectionDomain().getCodeSource().getLocation();
        // Isola a classe para variar o recurso sem mexer nos arquivos usados pela aplicação.
        try (URLClassLoader carregador = new URLClassLoader(
                new URL[] {classes}, ClassLoader.getPlatformClassLoader()) {
            @Override
            public URL findResource(String nome) {
                if (nome.equals("Imagens/tijolo.png")) {
                    return tijolo;
                }
                return super.findResource(nome);
            }
        }) {
            Class<?> tipoMapa = carregador.loadClass(Mapa.class.getName());
            Object mapa = tipoMapa.getConstructor(int.class, int.class, Set.class, Map.class)
                    .newInstance(1, 1, Set.<Posicao>of(), Map.<Posicao, String>of());
            Class<?> tipoVisao = carregador.loadClass(VisaoMapa.class.getName());
            Constructor<?> construtor = tipoVisao.getDeclaredConstructor(tipoMapa);
            construtor.setAccessible(true);
            FutureTask<Throwable> criacao = new FutureTask<Throwable>(new Callable<Throwable>() {
                @Override
                public Throwable call() {
                    InvocationTargetException erro = assertThrows(InvocationTargetException.class, new Executable() {
                        @Override
                        public void execute() throws Exception {
                            construtor.newInstance(mapa);
                        }
                    });
                    return erro.getCause();
                }
            });
            SwingUtilities.invokeAndWait(criacao);
            return criacao.get(5, TimeUnit.SECONDS);
        }
    }

    @Test
    void centralizaOMapaRedimensionadoSemAlterarOsGraficosDoSwing() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                Mapa mapa = new Mapa(COLUNAS, LINHAS,
                        Set.<Posicao>of(PAREDE), Map.<Posicao, String>of(LETREIRO, "X"));
                VisaoMapa visao = new VisaoMapa(mapa);
                visao.atualizarPosicao(LIVRE);
                visao.setSize(2 * COLUNAS * CELULA, LINHAS * CELULA);
                BufferedImage imagem = new BufferedImage(visao.getWidth(), visao.getHeight(),
                        BufferedImage.TYPE_INT_RGB);
                Graphics2D quadro = imagem.createGraphics();
                try {
                    AffineTransform transformacao = quadro.getTransform();
                    visao.paint(quadro);
                    assertEquals(transformacao, quadro.getTransform());
                    int margemX = COLUNAS * CELULA / 2;
                    assertEquals(Color.WHITE.getRGB(), imagem.getRGB(margemX - 1, 0));
                    assertEquals(Color.GRAY.getRGB(), imagem.getRGB(margemX, 0));
                    assertEquals(Color.WHITE.getRGB(), imagem.getRGB(margemX + COLUNAS * CELULA, 0));
                    int[] personagem = imagem.getRGB(margemX + LIVRE.getX() * CELULA, 0,
                            CELULA, CELULA, null, 0, CELULA);

                    visao.setSize(COLUNAS * CELULA, LINHAS * CELULA);
                    BufferedImage semMargem = pintar(visao);
                    assertArrayEquals(personagem, pixels(semMargem, LIVRE));
                    assertEquals(Color.GRAY.getRGB(), semMargem.getRGB(0, 0));
                } finally {
                    quadro.dispose();
                }
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
