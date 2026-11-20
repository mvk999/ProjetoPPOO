package br.edu.ufla.ppoo.gui;

import java.awt.AWTEvent;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.WindowEvent;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

@ResourceLock(Resources.SYSTEM_OUT)
class PrincipalTest {
    @Test
    void recusaEntrarNaDemonstracaoPelaThreadDeEventos() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                assertThrows(IllegalStateException.class, new Executable() {
                    @Override
                    public void execute() throws Exception {
                        Principal.main(new String[0]);
                    }
                });
            }
        });
    }

    @Test
    @EnabledIfSystemProperty(named = "ppoo.testarGui", matches = "true")
    void interromperAPartidaFechaAJanelaCriadaPeloPontoDeEntrada() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless(), "O teste Swing exige uma sessão gráfica.");
        CompletableFuture<Window> aberta = new CompletableFuture<Window>();
        CompletableFuture<Window> fechada = new CompletableFuture<Window>();
        FutureTask<Void> demonstracao = new FutureTask<Void>(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Principal.main(new String[0]);
                return null;
            }
        });
        Thread demonstrador = new Thread(demonstracao, "teste-principal-swing");
        demonstrador.setDaemon(true);
        AWTEventListener ouvinte = new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent evento) {
                if (evento instanceof WindowEvent eventoJanela) {
                    Window janela = eventoJanela.getWindow();
                    if (evento.getID() == WindowEvent.WINDOW_OPENED && janela instanceof JanelaSimulacao) {
                        aberta.complete(janela);
                        demonstrador.interrupt();
                    } else if (evento.getID() == WindowEvent.WINDOW_CLOSED && janela == aberta.getNow(null)) {
                        fechada.complete(janela);
                    }
                }
            }
        };

        Toolkit ferramentas = Toolkit.getDefaultToolkit();
        ferramentas.addAWTEventListener(ouvinte, AWTEvent.WINDOW_EVENT_MASK);
        try {
            demonstrador.start();
            ExecutionException erro = assertThrows(ExecutionException.class, new Executable() {
                @Override
                public void execute() throws Exception {
                    demonstracao.get(5, TimeUnit.SECONDS);
                }
            });
            assertInstanceOf(InterruptedException.class, erro.getCause());
            assertEquals(aberta.get(5, TimeUnit.SECONDS), fechada.get(5, TimeUnit.SECONDS));
        } finally {
            demonstrador.interrupt();
            ferramentas.removeAWTEventListener(ouvinte);
            Window janela = aberta.getNow(null);
            if (janela != null) {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        janela.dispose();
                    }
                });
            }
        }
    }
}
