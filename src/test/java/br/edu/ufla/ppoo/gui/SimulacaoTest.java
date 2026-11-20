package br.edu.ufla.ppoo.gui;

import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
            FutureTask<Simulacao> criacao = new FutureTask<Simulacao>(new Callable<Simulacao>() {
                @Override
                public Simulacao call() {
                    return new Simulacao();
                }
            });
            SwingUtilities.invokeAndWait(criacao);
            Simulacao simulacao = criacao.get();
            Posicao origem = new Posicao(6, 1);

            mensagens.reset();
            simulacao.executarSimulacao(origem, new Posicao(7, 1));
            assertEquals("Não foi possível encontrar um caminho para o destino.", lerMensagens(mensagens));

            mensagens.reset();
            simulacao.executarSimulacao(new Posicao(-1, 1), origem);
            assertEquals("Não foi possível encontrar um caminho para o destino.", lerMensagens(mensagens));

            mensagens.reset();
            simulacao.executarSimulacao(origem, new Posicao(6, 1));
            assertEquals("O jogador já está no destino.", lerMensagens(mensagens));

            mensagens.reset();
            simulacao.executarSimulacao(origem, new Posicao(5, 1));
            assertEquals("Movimentação concluída.", lerMensagens(mensagens));
        } finally {
            System.setOut(saidaOriginal);
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
    }

    private static String lerMensagens(ByteArrayOutputStream mensagens) {
        return mensagens.toString(StandardCharsets.UTF_8).strip();
    }
}
