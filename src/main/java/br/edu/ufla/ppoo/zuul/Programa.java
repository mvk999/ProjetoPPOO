package br.edu.ufla.ppoo.zuul;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Ponto de entrada da versão textual do World of Zuul.
 */
public final class Programa {
    private Programa() {
    }

    /**
     * Lê comandos do terminal até o jogador sair ou a entrada terminar.
     *
     * @param args argumentos de execução, ignorados pela versão textual
     */
    public static void main(String[] args) {
        Jogo jogo = new Jogo();
        Analisador analisador = new Analisador();
        // A entrada padrão pertence ao processo e fica aberta.
        BufferedReader entrada = new BufferedReader(new InputStreamReader(System.in));

        jogo.iniciar();
        try {
            executarComandos(entrada, analisador, jogo);
        } catch (IOException erro) {
            System.err.println("Não foi possível ler os comandos do terminal.");
        }
        System.out.println("Obrigado por jogar. Ate mais!");
    }

    private static void executarComandos(BufferedReader entrada, Analisador analisador, Jogo jogo)
            throws IOException {
        while (true) {
            System.out.print("> ");
            System.out.flush();
            String linha = entrada.readLine();
            if (linha == null || jogo.processarComando(analisador.interpretar(linha))) {
                return;
            }
        }
    }
}
