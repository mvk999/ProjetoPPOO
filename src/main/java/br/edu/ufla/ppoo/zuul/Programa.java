package br.edu.ufla.ppoo.zuul;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Objects;

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
        executar(new BufferedReader(new InputStreamReader(System.in)), System.out, System.err);
    }

    /**
     * Executa uma sessão até o comando de saída, o fim da entrada ou uma falha de leitura.
     * Os fluxos pertencem ao chamador e permanecem abertos para reutilização.
     *
     * @param entrada origem das linhas de comando
     * @param saida destino das mensagens da sessão
     * @param erros destino do diagnóstico de leitura
     * @throws NullPointerException se algum dos fluxos for nulo
     */
    public static void executar(BufferedReader entrada, PrintStream saida, PrintStream erros) {
        Objects.requireNonNull(entrada, "A entrada é obrigatória.");
        Objects.requireNonNull(erros, "A saída de erros é obrigatória.");
        Jogo jogo = new Jogo(saida);
        Analisador analisador = new Analisador();

        jogo.iniciar();
        try {
            executarComandos(entrada, analisador, jogo, saida);
        } catch (IOException erro) {
            erros.println("Não foi possível ler os comandos do terminal.");
            erros.flush();
        }
        saida.println("Obrigado por jogar. Ate mais!");
        saida.flush();
    }

    private static void executarComandos(BufferedReader entrada, Analisador analisador, Jogo jogo, PrintStream saida)
            throws IOException {
        while (true) {
            saida.print("> ");
            saida.flush();
            String linha = entrada.readLine();
            if (linha == null) {
                return;
            }

            Comando comando = analisador.interpretar(linha);
            if (Comando.AJUDA.equals(comando.getPalavraDeComando())) {
                imprimirAjuda(saida, analisador.consultarAjuda());
            } else if (jogo.processarComando(comando)) {
                return;
            }
        }
    }

    private static void imprimirAjuda(PrintStream saida, String palavrasAceitas) {
        saida.println("Voce esta perdido. Voce esta sozinho. Voce caminha");
        saida.println("pela universidade.");
        saida.println();
        saida.println("Suas palavras de comando sao:");
        saida.println("   " + palavrasAceitas);
    }
}
