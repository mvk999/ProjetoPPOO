package br.edu.ufla.ppoo.gui;

import java.util.Map;

/**
 * Inicia a demonstração dos percursos no cenário fornecido.
 *
 * @author Luiz Merschmann
 */
public final class Principal {
    private Principal() {
    }

    /**
     * Exibe um percurso entre cada par de ambientes distintos.
     *
     * @param args argumentos do programa, ignorados pela demonstração
     */
    public static void main(String[] args) {
        Map<String, Posicao> referencias = CenarioDemonstracao.referencias();
        Simulacao simulacao = new Simulacao();

        for (Map.Entry<String, Posicao> origem : referencias.entrySet()) {
            for (Map.Entry<String, Posicao> destino : referencias.entrySet()) {
                if (!origem.getKey().equals(destino.getKey())) {
                    System.out.println("Origem: " + origem.getKey() + "\tDestino: " + destino.getKey());
                    System.out.println("Procurando o caminho ...");
                    simulacao.executarSimulacao(origem.getValue(), destino.getValue());
                }
            }
        }
        System.out.println("Simulação concluída.");
    }
}
