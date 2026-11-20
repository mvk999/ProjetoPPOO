package br.edu.ufla.ppoo.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javax.swing.Timer;

/**
 * Avança pelos quadros de um percurso na thread de eventos do Swing.
 * O desenho e a reação ao término são fornecidos pelos consumidores.
 * A criação e as chamadas devem ocorrer na thread de eventos.
 */
final class AnimacaoMapa {
    private static final int INTERVALO_ENTRE_QUADROS_MS = 100;

    private final Consumer<Posicao> exibirQuadro;
    private final Timer timer;
    private Iterator<Posicao> quadrosRestantes = Collections.<Posicao>emptyIterator();
    private ActionListener ouvinteFim = null;

    /**
     * Prepara uma animação em repouso.
     *
     * @param exibirQuadro operação que apresenta cada posição
     * @throws NullPointerException se o exibidor for nulo
     */
    AnimacaoMapa(Consumer<Posicao> exibirQuadro) {
        this.exibirQuadro = Objects.requireNonNull(exibirQuadro, "O exibidor de quadros é obrigatório.");
        timer = new Timer(INTERVALO_ENTRE_QUADROS_MS, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                avancar();
            }
        });
    }

    /**
     * Define o ouvinte notificado na thread de eventos após a conclusão.
     *
     * @param ouvinte ouvinte que substitui o registrado anteriormente
     * @throws NullPointerException se o ouvinte for nulo
     */
    void registrarOuvinteFim(ActionListener ouvinte) {
        ouvinteFim = Objects.requireNonNull(ouvinte, "O ouvinte de fim é obrigatório.");
    }

    /**
     * Exibe a origem imediatamente e agenda os quadros restantes.
     * Um percurso com uma única posição termina no próprio evento.
     *
     * @param caminho posições do percurso, em ordem da origem ao destino
     * @throws IllegalArgumentException se o caminho estiver vazio
     * @throws IllegalStateException se já houver uma animação em andamento
     * @throws NullPointerException se o caminho ou alguma posição for nula
     */
    void animar(List<Posicao> caminho) {
        if (timer.isRunning()) {
            throw new IllegalStateException("Já há um percurso em animação.");
        }
        List<Posicao> quadros = List.<Posicao>copyOf(caminho);
        if (quadros.isEmpty()) {
            throw new IllegalArgumentException("O percurso deve conter pelo menos uma posição.");
        }
        quadrosRestantes = quadros.iterator();
        exibirQuadro.accept(quadrosRestantes.next());
        if (quadrosRestantes.hasNext()) {
            timer.start();
        } else {
            notificarFim();
        }
    }

    /**
     * Interrompe a animação na thread de eventos, mantendo o último quadro exibido.
     */
    void cancelar() {
        timer.stop();
        quadrosRestantes = Collections.<Posicao>emptyIterator();
    }

    private void avancar() {
        if (quadrosRestantes.hasNext()) {
            exibirQuadro.accept(quadrosRestantes.next());
        } else {
            // O último quadro fica disponível para pintura durante um intervalo completo.
            cancelar();
            notificarFim();
        }
    }

    private void notificarFim() {
        if (ouvinteFim != null) {
            ouvinteFim.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "fim"));
        }
    }
}
