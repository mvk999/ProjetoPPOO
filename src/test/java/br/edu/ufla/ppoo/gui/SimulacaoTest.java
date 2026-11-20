package br.edu.ufla.ppoo.gui;

import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

@EnabledIfSystemProperty(named = "ppoo.testarGui", matches = "true")
@ResourceLock(Resources.SYSTEM_OUT)
class SimulacaoTest {
    @Test
    void distingueFalhaDePercursoSemDeslocamentoEPermiteMoverEmSeguida() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless(), "O teste Swing exige uma sessão gráfica.");
        Set<Window> janelasAnteriores = new HashSet<Window>(Arrays.<Window>asList(Window.getWindows()));
        PrintStream saidaOriginal = System.out;
        ByteArrayOutputStream mensagens = new ByteArrayOutputStream();

        try (PrintStream captura = new PrintStream(mensagens, true, StandardCharsets.UTF_8)) {
            System.setOut(captura);
            Simulacao simulacao = abrirJanela();
            Posicao origem = new Posicao(6, 1);

            mensagens.reset();
            assertTrue(aguardar(simulacao.executarSimulacao(origem, new Posicao(7, 1))));
            assertEquals("Não foi possível encontrar um caminho para o destino.", lerMensagens(mensagens));

            mensagens.reset();
            assertTrue(aguardar(simulacao.executarSimulacao(new Posicao(-1, 1), origem)));
            assertEquals("Não foi possível encontrar um caminho para o destino.", lerMensagens(mensagens));

            mensagens.reset();
            assertTrue(aguardar(simulacao.executarSimulacao(origem, new Posicao(6, 1))));
            assertEquals("O jogador já está no destino.", lerMensagens(mensagens));

            mensagens.reset();
            assertTrue(aguardar(simulacao.executarSimulacao(origem, new Posicao(5, 1))));
            assertEquals("Movimentação concluída.", lerMensagens(mensagens));
        } finally {
            System.setOut(saidaOriginal);
            liberarJanelas(janelasAnteriores);
        }
    }

    @Test
    void rejeitaUmaSegundaAnimacaoEEncerraAPrimeiraQuandoAJanelaFecha() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless(), "O teste Swing exige uma sessão gráfica.");
        Set<Window> janelasAnteriores = new HashSet<Window>(Arrays.<Window>asList(Window.getWindows()));
        try {
            Simulacao simulacao = abrirJanela();
            Posicao origem = new Posicao(6, 1);
            Posicao destino = new Posicao(1, 1);
            FutureTask<List<CompletionStage<Boolean>>> solicitacoes =
                    new FutureTask<List<CompletionStage<Boolean>>>(new Callable<List<CompletionStage<Boolean>>>() {
                @Override
                public List<CompletionStage<Boolean>> call() {
                    CompletionStage<Boolean> percurso = simulacao.executarSimulacao(origem, destino);
                    CompletionStage<Boolean> sobreposto = simulacao.executarSimulacao(destino, origem);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            for (Window janela : Window.getWindows()) {
                                if (!janelasAnteriores.contains(janela)) {
                                    janela.dispatchEvent(new WindowEvent(janela, WindowEvent.WINDOW_CLOSING));
                                }
                            }
                        }
                    });
                    return List.<CompletionStage<Boolean>>of(percurso, sobreposto);
                }
            });
            SwingUtilities.invokeAndWait(solicitacoes);
            List<CompletionStage<Boolean>> conclusoes = solicitacoes.get(5, TimeUnit.SECONDS);
            ExecutionException erro = assertThrows(ExecutionException.class, new Executable() {
                @Override
                public void execute() throws Exception {
                    aguardar(conclusoes.get(1));
                }
            });
            assertInstanceOf(IllegalStateException.class, erro.getCause());
            assertFalse(aguardar(conclusoes.getFirst()));
            assertFalse(aguardar(simulacao.executarSimulacao(origem, destino)));
        } finally {
            liberarJanelas(janelasAnteriores);
        }
    }

    @Test
    void recusaCriacaoDaJanelaForaDaThreadDeEventos() {
        assertThrows(IllegalStateException.class, new Executable() {
            @Override
            public void execute() {
                new Simulacao();
            }
        });
    }

    private static Simulacao abrirJanela() throws Exception {
        FutureTask<Simulacao> criacao = new FutureTask<Simulacao>(new Callable<Simulacao>() {
            @Override
            public Simulacao call() {
                return new Simulacao();
            }
        });
        SwingUtilities.invokeAndWait(criacao);
        return criacao.get(5, TimeUnit.SECONDS);
    }

    private static boolean aguardar(CompletionStage<Boolean> conclusao) throws Exception {
        return conclusao.toCompletableFuture().get(5, TimeUnit.SECONDS);
    }

    private static void liberarJanelas(Set<Window> janelasAnteriores) throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                for (Window janela : Window.getWindows()) {
                    if (!janelasAnteriores.contains(janela)) {
                        janela.dispose();
                    }
                }
            }
        });
    }

    private static String lerMensagens(ByteArrayOutputStream mensagens) {
        return mensagens.toString(StandardCharsets.UTF_8).strip();
    }
}
