package br.edu.ufla.ppoo.zuul;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Interpreta uma linha de texto usando o vocabulário do jogo.
 *
 * @author Michael Kölling and David J. Barnes (traduzido por Julio Cesar Alves)
 * @version 2011.07.31 (2016.02.01)
 */
public final class Analisador {
    private static final Pattern SEPARADOR = Pattern.compile("\\p{javaWhitespace}+");

    private final PalavrasComando palavrasDeComando;

    /**
     * Cria um analisador com o vocabulário de comandos da base.
     */
    public Analisador() {
        palavrasDeComando = new PalavrasComando();
    }

    /**
     * Separa a primeira palavra do restante do texto. Ignora o espaço em branco
     * nas extremidades e entre a palavra e o argumento. Preserva o argumento
     * inteiro, incluindo o espaço em branco entre suas palavras. O vocabulário
     * distingue maiúsculas de minúsculas.
     *
     * @param texto linha de texto a interpretar
     * @return comando reconhecido ou marcado como desconhecido para texto vazio
     *         ou palavra fora do vocabulário
     * @throws NullPointerException se o texto for nulo
     */
    public Comando interpretar(String texto) {
        String linha = Objects.requireNonNull(texto, "O texto do comando é obrigatório.").strip();
        String[] partes = SEPARADOR.split(linha, 2);
        String palavra = partes[0];
        String argumento = partes.length == 2 ? partes[1] : null;
        if (!palavrasDeComando.ehComando(palavra)) {
            palavra = null;
        }
        return new Comando(palavra, argumento);
    }
}
