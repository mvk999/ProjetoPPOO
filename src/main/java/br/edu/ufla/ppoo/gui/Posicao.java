package br.edu.ufla.ppoo.gui;

/**
 * Representa um par imutável de coordenadas da grade. Os limites da grade
 * são definidos pelo mapa.
 *
 * @author David J. Barnes and Michael Kolling and Luiz Merschmann
 */
public final class Posicao {
    private final int x;
    private final int y;

    /**
     * Cria uma posição com as coordenadas informadas.
     *
     * @param x coordenada horizontal
     * @param y coordenada vertical
     */
    public Posicao(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return a coordenada horizontal
     */
    public int getX() {
        return x;
    }

    /**
     * @return a coordenada vertical
     */
    public int getY() {
        return y;
    }

    /**
     * Compara duas posições pelas coordenadas.
     *
     * @param objeto objeto a comparar
     * @return {@code true} se o objeto for uma posição com as mesmas coordenadas
     */
    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (!(objeto instanceof Posicao)) {
            return false;
        }
        Posicao outra = (Posicao) objeto;
        return x == outra.x && y == outra.y;
    }

    /**
     * @return o código hash calculado a partir das duas coordenadas
     */
    @Override
    public int hashCode() {
        return 31 * x + y;
    }

    /**
     * @return a representação da posição no formato {@code (x, y)}
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
