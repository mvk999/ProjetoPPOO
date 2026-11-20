package br.edu.ufla.ppoo.gui;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import javax.swing.SwingUtilities;

/**
 * Inicia a demonstração dos percursos no cenário fornecido.
 *
 * @author Luiz Merschmann
 */
public final class Principal {
    private Principal() {
    }

    /**
     * Exibe, na thread principal, um percurso por vez entre ambientes distintos.
     *
     * @param args argumentos do programa, ignorados pela demonstração
     * @throws IllegalStateException se chamada na thread de eventos do Swing
     * @throws InterruptedException se a thread principal for interrompida
     * @throws ExecutionException se a criação da janela ou um percurso falhar
     */
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        if (SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException("Inicie a demonstração fora da thread de eventos do Swing.");
        }
        FutureTask<Simulacao> criacao = new FutureTask<Simulacao>(new Callable<Simulacao>() {
            @Override
            public Simulacao call() {
                return new Simulacao();
            }
        });
        SwingUtilities.invokeLater(criacao);
        try {
            exibirPercursos(criacao.get());
        } catch (InterruptedException | ExecutionException | RuntimeException erro) {
            agendarFechamento(criacao);
            throw erro;
        }
    }

    private static void exibirPercursos(Simulacao simulacao) throws InterruptedException, ExecutionException {
        Map<String, Posicao> referencias = CenarioDemonstracao.referencias();
        for (Map.Entry<String, Posicao> origem : referencias.entrySet()) {
            for (Map.Entry<String, Posicao> destino : referencias.entrySet()) {
                if (!origem.getKey().equals(destino.getKey())) {
                    System.out.println("Origem: " + origem.getKey() + "\tDestino: " + destino.getKey());
                    System.out.println("Procurando o caminho ...");
                    boolean janelaAberta = simulacao.executarSimulacao(origem.getValue(), destino.getValue())
                            .toCompletableFuture().get();
                    if (!janelaAberta) {
                        return;
                    }
                }
            }
        }
        System.out.println("Simulação concluída.");
    }

    private static void agendarFechamento(FutureTask<Simulacao> criacao) {
        // A criação já está na fila do Swing e será processada antes deste evento.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (criacao.state() == Future.State.SUCCESS) {
                    criacao.resultNow().fecharJanela();
                }
            }
        });
    }
}
