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
            if (linha == null) {
                return;
            }

            Comando comando = analisador.interpretar(linha);
            if (Comando.AJUDA.equals(comando.getPalavraDeComando())) {
                imprimirAjuda(analisador.consultarAjuda());
            } else if (jogo.processarComando(comando)) {
                return;
            }
        }
    }

    private static void imprimirAjuda(String palavrasAceitas) {
        System.out.println("Voce esta perdido. Voce esta sozinho. Voce caminha");
        System.out.println("pela universidade.");
        System.out.println();
        System.out.println("Suas palavras de comando sao:");
        System.out.println("   " + palavrasAceitas);
    }
}
