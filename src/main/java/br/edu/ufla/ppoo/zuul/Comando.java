package br.edu.ufla.ppoo.zuul;

/**
 * Resultado imutável da interpretação de uma linha de comando.
 *
 * @author Michael Kölling and David J. Barnes (traduzido por Julio Cesar Alves)
 * @version 2011.07.31 (2016.02.01)
 */
public final class Comando {
    /** Palavra que solicita deslocamento. */
    static final String IR = "ir";
    /** Palavra que solicita o encerramento da sessão. */
    static final String SAIR = "sair";
    /** Palavra que solicita a lista de comandos. */
    static final String AJUDA = "ajuda";

    private final String palavra;
    private final String argumento;

    /**
     * Cria um comando com sua palavra reconhecida e seu argumento opcional.
     *
     * @param palavra palavra do vocabulário, ou {@code null} se não foi reconhecida
     * @param argumento texto após a primeira palavra, ou {@code null} se estiver ausente
     */
    public Comando(String palavra, String argumento) {
        this.palavra = palavra;
        this.argumento = argumento;
    }

    /**
     * Consulta a palavra que identifica a ação solicitada.
     *
     * @return palavra reconhecida, ou {@code null} para comando desconhecido
     */
    public String getPalavraDeComando() {
        return palavra;
    }

    /**
     * Consulta o conteúdo fornecido após a primeira palavra.
     *
     * @return argumento completo, ou {@code null} se estiver ausente
     */
    public String getArgumento() {
        return argumento;
    }

    /**
     * Indica se a interpretação encontrou uma palavra do vocabulário.
     *
     * @return {@code true} quando a palavra não foi reconhecida
     */
    public boolean ehDesconhecido() {
        return palavra == null;
    }

    /**
     * Indica se a linha continha texto após a primeira palavra.
     *
     * @return {@code true} quando há argumento
     */
    public boolean temArgumento() {
        return argumento != null;
    }
}
