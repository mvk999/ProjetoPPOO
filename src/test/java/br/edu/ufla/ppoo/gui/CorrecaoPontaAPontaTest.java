package br.edu.ufla.ppoo.gui;

import br.edu.ufla.ppoo.zuul.Programa;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CorrecaoPontaAPontaTest {
    private static final Posicao ORIGEM = new Posicao(0, 1);
    private static final Posicao DESTINO = new Posicao(2, 1);
    private static final Posicao PAREDE = new Posicao(1, 1);

    @Test
    void validaNavegacaoTextualEBuscaAteOQuadroFinalNaMesmaExecucao() throws Exception {
        verificarSessaoTextual();

        Set<Posicao> paredes = new HashSet<Posicao>(Set.<Posicao>of(PAREDE, new Posicao(1, 0)));
        Map<Posicao, String> letreiros = new HashMap<Posicao, String>(Map.<Posicao, String>of(ORIGEM, "A"));
        Mapa mapa = new Mapa(3, 3, paredes, letreiros);
        paredes.clear();
        letreiros.clear();
        assertTrue(mapa.getParedes().contains(new Posicao(1, 1)));
        assertEquals("A", mapa.getLetreiros().get(new Posicao(0, 1)));

        HeuristicaAEstrela busca = new HeuristicaAEstrela(mapa);
        assertTrue(busca.encontrarCaminho(ORIGEM, new Posicao(1, 1)).isEmpty());
        assertEquals(List.<Posicao>of(ORIGEM), busca.encontrarCaminho(ORIGEM, new Posicao(0, 1)));
        List<Posicao> percurso = busca.encontrarCaminho(ORIGEM, DESTINO);
        List<Posicao> desvioEsperado = List.<Posicao>of(
                ORIGEM, new Posicao(0, 2), new Posicao(1, 2), new Posicao(2, 2), DESTINO);
        assertEquals(desvioEsperado, percurso);

        CompletableFuture<List<Posicao>> termino = new CompletableFuture<List<Posicao>>();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                VisaoMapa visao = new VisaoMapa(mapa);
                visao.setSize(visao.getPreferredSize());
                BufferedImage inicial = pintar(visao);
                List<Posicao> exibidas = new ArrayList<Posicao>();
                AnimacaoMapa animacao = new AnimacaoMapa(new Consumer<Posicao>() {
                    @Override
                    public void accept(Posicao posicao) {
                        visao.atualizarPosicao(posicao);
                        exibidas.add(posicao);
                    }
                });
                animacao.registrarOuvinteFim(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evento) {
                        try {
                            BufferedImage ultimoQuadro = pintar(visao);
                            assertArrayEquals(pixels(inicial, ORIGEM), pixels(ultimoQuadro, ORIGEM));
                            assertArrayEquals(pixels(inicial, PAREDE), pixels(ultimoQuadro, PAREDE));
                            assertFalse(Arrays.equals(pixels(inicial, DESTINO), pixels(ultimoQuadro, DESTINO)),
                                    "O destino deve exibir o personagem ao terminar a animação.");
                            termino.complete(List.<Posicao>copyOf(exibidas));
                        } catch (AssertionError | RuntimeException erro) {
                            termino.completeExceptionally(erro);
                        }
                    }
                });
                animacao.animar(percurso);
                assertFalse(Arrays.equals(pixels(inicial, ORIGEM), pixels(pintar(visao), ORIGEM)),
                        "O primeiro quadro deve exibir o personagem na origem.");
            }
        });
        assertEquals(desvioEsperado, termino.get(5, TimeUnit.SECONDS));
    }

    private static void verificarSessaoTextual() throws Exception {
        String comandos = String.join("\n",
                "  ajuda  ", "ir\tnorte agora", "ir   leste", "sair agora", "sair", "ir norte");
        ByteArrayOutputStream mensagens = new ByteArrayOutputStream();
        ByteArrayOutputStream diagnosticos = new ByteArrayOutputStream();
        try (BufferedReader entrada = new BufferedReader(new StringReader(comandos));
                PrintStream saida = new PrintStream(mensagens, true, StandardCharsets.UTF_8);
                PrintStream erros = new PrintStream(diagnosticos, true, StandardCharsets.UTF_8)) {
            Programa.executar(entrada, saida, erros);
            List<String> eventos = new ArrayList<String>();
            for (String linha : mensagens.toString(StandardCharsets.UTF_8).lines().toList()) {
                if (!linha.startsWith("Você possui ") && !linha.startsWith("Atualmente voce possui ")) {
                    eventos.add(linha);
                }
            }
            assertEquals(List.<String>of(
                    "",
                    "Bem-vindo à casa mal-assombrada!",
                    "Você é um caçador de tesouros procurando por uma fortuna enterrada.",
                    "Digite 'ajuda' se voce precisar de ajuda.",
                    "",
                    "Voce esta em Sala de TV",
                    "Saidas: norte",
                    "> Você está em uma casa abandonada e mal-assombrada.",
                    "Explore os ambientes em busca do tesouro.",
                    "",
                    "Suas palavras de comando sao:",
                    "   ir chave sair ajuda cavar",
                    "> Nao ha passagem!",
                    "> Nao ha passagem!",
                    "> Sair o que?",
                    "> Obrigado por explorar a casa mal-assombrada. Ate mais!"), eventos);
            assertEquals(0, diagnosticos.size());
            assertEquals("ir norte", entrada.readLine());
            saida.print("fluxo aberto");
            erros.print("fluxo aberto");
            assertTrue(mensagens.toString(StandardCharsets.UTF_8).endsWith("fluxo aberto"));
            assertEquals("fluxo aberto", diagnosticos.toString(StandardCharsets.UTF_8));
        }
    }

    private static BufferedImage pintar(VisaoMapa visao) {
        BufferedImage imagem = new BufferedImage(visao.getWidth(), visao.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graficos = imagem.createGraphics();
        try {
            visao.paint(graficos);
        } finally {
            graficos.dispose();
        }
        return imagem;
    }

    private static int[] pixels(BufferedImage quadro, Posicao celula) {
        int tamanho = quadro.getWidth() / 3;
        return quadro.getRGB(celula.getX() * tamanho, celula.getY() * tamanho, tamanho, tamanho, null, 0, tamanho);
    }
}
