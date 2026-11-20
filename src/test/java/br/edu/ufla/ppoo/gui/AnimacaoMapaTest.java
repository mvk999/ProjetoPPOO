package br.edu.ufla.ppoo.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnimacaoMapaTest {
    private static final Posicao ORIGEM = new Posicao(1, 1);
    private static final Posicao MEIO = new Posicao(2, 1);
    private static final Posicao DESTINO = new Posicao(2, 2);

    @Test
    void exibeOsQuadrosEmOrdemEDeixaAThreadDeEventosProcessarOutrosEventos() throws Exception {
        List<Object> eventos = new ArrayList<Object>();
        CompletableFuture<List<Object>> resultado = new CompletableFuture<List<Object>>();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                AnimacaoMapa animacao = new AnimacaoMapa(new Consumer<Posicao>() {
                    @Override
                    public void accept(Posicao quadro) {
                        assertTrue(SwingUtilities.isEventDispatchThread());
                        eventos.add(quadro);
                    }
                });
                animacao.registrarOuvinteFim(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evento) {
                        assertTrue(SwingUtilities.isEventDispatchThread());
                        assertEquals(animacao, evento.getSource());
                        eventos.add("fim");
                        resultado.complete(List.<Object>copyOf(eventos));
                    }
                });
                List<Posicao> caminho = new ArrayList<Posicao>(List.<Posicao>of(ORIGEM, MEIO, DESTINO));
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        eventos.add("outro evento");
                    }
                });
                animacao.animar(caminho);
                assertEquals(List.<Object>of(ORIGEM), eventos);
                caminho.clear();
            }
        });
        assertEquals(List.<Object>of(ORIGEM, "outro evento", MEIO, DESTINO, "fim"),
                resultado.get(5, TimeUnit.SECONDS));
    }

    @Test
    void rejeitaSobreposicaoEPermiteNovoPercursoAposCancelar() throws Exception {
        List<Posicao> quadros = new ArrayList<Posicao>();
        List<ActionEvent> conclusoes = new ArrayList<ActionEvent>();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                AnimacaoMapa animacao = new AnimacaoMapa(new Consumer<Posicao>() {
                    @Override
                    public void accept(Posicao quadro) {
                        quadros.add(quadro);
                    }
                });
                animacao.registrarOuvinteFim(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evento) {
                        conclusoes.add(evento);
                    }
                });
                animacao.animar(List.<Posicao>of(ORIGEM, MEIO));
                assertThrows(IllegalStateException.class, new Executable() {
                    @Override
                    public void execute() {
                        animacao.animar(List.<Posicao>of(MEIO, DESTINO));
                    }
                });
                animacao.cancelar();
                assertTrue(conclusoes.isEmpty());

                animacao.animar(List.<Posicao>of(DESTINO));
                assertEquals(List.<Posicao>of(ORIGEM, DESTINO), quadros);
                assertEquals(1, conclusoes.size());
            }
        });
    }

    @Test
    void validaTodoOPercursoAntesDeExibirEAceitaUmQuadroSemOuvinte() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                List<Posicao> quadros = new ArrayList<Posicao>();
                AnimacaoMapa animacao = new AnimacaoMapa(new Consumer<Posicao>() {
                    @Override
                    public void accept(Posicao quadro) {
                        quadros.add(quadro);
                    }
                });
                assertThrows(IllegalArgumentException.class, new Executable() {
                    @Override
                    public void execute() {
                        animacao.animar(List.<Posicao>of());
                    }
                });
                assertThrows(NullPointerException.class, new Executable() {
                    @Override
                    public void execute() {
                        animacao.animar(Arrays.<Posicao>asList(ORIGEM, null));
                    }
                });
                assertTrue(quadros.isEmpty());
                animacao.animar(List.<Posicao>of(DESTINO));
                assertEquals(List.<Posicao>of(DESTINO), quadros);
            }
        });
    }
}
