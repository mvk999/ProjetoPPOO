package br.edu.ufla.ppoo.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;

/**
 * Coordena a busca e a animação dos percursos da demonstração.
 *
 * @author David J. Barnes and Michael Kolling and Luiz Merschmann
 */
public final class Simulacao {
    private final HeuristicaAEstrela busca;
    private final JanelaSimulacao janelaSimulacao;
    private final AnimacaoMapa animacao;
    private CompletableFuture<Boolean> percursoEmAndamento = null;

    /**
     * Prepara e abre a demonstração na thread de eventos do Swing.
     *
     * @throws IllegalStateException se chamada fora da thread de eventos
     */
    public Simulacao() {
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException("A simulação deve ser criada na thread de eventos do Swing.");
        }
        Mapa mapa = CenarioDemonstracao.mapa();
        busca = new HeuristicaAEstrela(mapa);
        janelaSimulacao = new JanelaSimulacao(mapa);
        animacao = new AnimacaoMapa(new Consumer<Posicao>() {
            @Override
            public void accept(Posicao posicao) {
                janelaSimulacao.exibirPosicao(posicao);
            }
        });
        janelaSimulacao.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent evento) {
                animacao.cancelar();
                if (percursoEmAndamento != null) {
                    concluirPercurso(false);
                }
            }
        });
        janelaSimulacao.setVisible(true);
    }

    /**
     * Busca o percurso na thread chamadora e agenda sua exibição no Swing.
     * Se a busca devolver um percurso vazio, informa a falha e preserva a posição visual.
     * Pedidos recebidos durante outra animação terminam com {@link IllegalStateException}.
     *
     * @param origem posição inicial
     * @param destino posição final
     * @return conclusão com {@code true} após apresentar o resultado, ou {@code false}
     *         se a janela fechar antes da conclusão ou já estiver fechada
     * @throws NullPointerException se a origem ou o destino for nulo
     */
    public CompletionStage<Boolean> executarSimulacao(Posicao origem, Posicao destino) {
        List<Posicao> caminho = busca.encontrarCaminho(origem, destino);
        CompletableFuture<Boolean> conclusao = new CompletableFuture<Boolean>();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                iniciarPercurso(caminho, conclusao);
            }
        });
        return conclusao.minimalCompletionStage();
    }

    private void iniciarPercurso(List<Posicao> caminho, CompletableFuture<Boolean> conclusao) {
        if (!janelaSimulacao.isDisplayable()) {
            conclusao.complete(false);
            return;
        }
        if (percursoEmAndamento != null) {
            conclusao.completeExceptionally(new IllegalStateException("Já há um percurso em andamento."));
            return;
        }
        if (caminho.isEmpty()) {
            System.out.println("Não foi possível encontrar um caminho para o destino.");
            conclusao.complete(true);
            return;
        }
        percursoEmAndamento = conclusao;
        String mensagem = caminho.size() == 1 ? "O jogador já está no destino." : "Movimentação concluída.";
        animacao.registrarOuvinteFim(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                System.out.println(mensagem);
                concluirPercurso(true);
            }
        });
        animacao.animar(caminho);
    }

    private void concluirPercurso(boolean janelaAberta) {
        CompletableFuture<Boolean> conclusao = percursoEmAndamento;
        percursoEmAndamento = null;
        conclusao.complete(janelaAberta);
    }
}
