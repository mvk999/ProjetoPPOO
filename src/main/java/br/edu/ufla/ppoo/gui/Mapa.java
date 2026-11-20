package br.edu.ufla.ppoo.gui;

import java.util.Map;
import java.util.Set;

/**
 * Representa os dados fixos da grade, usados pela busca e pela visualização.
 *
 * @author David J. Barnes and Michael Kolling and Luiz Merschmann
 */
public final class Mapa {
    private final int largura;
    private final int altura;
    private final Set<Posicao> paredes;
    private final Map<Posicao, String> letreiros;

    /**
     * Cria uma grade com cópias imutáveis dos dados fornecidos.
     *
     * @param largura número de colunas
     * @param altura número de linhas
     * @param paredes células bloqueadas por paredes
     * @param letreiros textos nas células transitáveis
     * @throws IllegalArgumentException se as dimensões não forem positivas,
     *         alguma posição estiver fora da grade ou um letreiro estiver em uma parede
     * @throws NullPointerException se alguma coleção, posição ou texto for nulo
     */
    public Mapa(int largura, int altura, Set<Posicao> paredes, Map<Posicao, String> letreiros) {
        if (largura <= 0 || altura <= 0) {
            throw new IllegalArgumentException("As dimensões do mapa devem ser positivas.");
        }
        this.largura = largura;
        this.altura = altura;
        this.paredes = Set.<Posicao>copyOf(paredes);
        this.letreiros = Map.<Posicao, String>copyOf(letreiros);

        for (Posicao parede : this.paredes) {
            validarPosicao(parede);
        }
        for (Posicao letreiro : this.letreiros.keySet()) {
            validarPosicao(letreiro);
            if (this.paredes.contains(letreiro)) {
                throw new IllegalArgumentException("Letreiro sobre uma parede em " + letreiro);
            }
        }
    }

    private void validarPosicao(Posicao posicao) {
        if (!estaDentro(posicao.getX(), posicao.getY())) {
            throw new IllegalArgumentException("Posição fora do mapa: " + posicao);
        }
    }

    /**
     * Informa a extensão horizontal da grade.
     *
     * @return número de colunas
     */
    public int getLargura() {
        return largura;
    }

    /**
     * Informa a extensão vertical da grade.
     *
     * @return número de linhas
     */
    public int getAltura() {
        return altura;
    }

    /**
     * Fornece as células bloqueadas por paredes.
     *
     * @return conjunto imutável de posições de parede
     */
    public Set<Posicao> getParedes() {
        return paredes;
    }

    /**
     * Fornece os textos a apresentar na grade.
     *
     * @return mapa imutável de posição para texto
     */
    public Map<Posicao, String> getLetreiros() {
        return letreiros;
    }

    /**
     * Verifica se uma célula pode ser utilizada em um percurso.
     *
     * @param x coluna da célula
     * @param y linha da célula
     * @return {@code true} se a célula estiver dentro da grade e não tiver parede
     */
    public boolean podePassar(int x, int y) {
        return estaDentro(x, y) && !paredes.contains(new Posicao(x, y));
    }

    /**
     * Verifica se as coordenadas pertencem à grade.
     *
     * @param x coluna da célula
     * @param y linha da célula
     * @return {@code true} se as coordenadas estiverem dentro dos limites
     */
    public boolean estaDentro(int x, int y) {
        return x >= 0 && x < largura && y >= 0 && y < altura;
    }
}
