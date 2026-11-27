package br.edu.ufla.ppoo.zuul;

import java.util.Objects;
import java.util.Random;

/**
 * Representa uma porta entre dois ambientes.
 */
public final class Porta {
    private final String id;
    private final Ambiente ladoA;
    private final Ambiente ladoB;
    private boolean emperrada;

    /**
     * Cria uma porta entre dois ambientes.
     *
     * @param id identificador da porta
     * @param ladoA ambiente em um lado da porta
     * @param ladoB ambiente no outro lado da porta
     * @throws NullPointerException se algum argumento for nulo
     * @throws IllegalArgumentException se o identificador estiver vazio
     */
    public Porta(String id, Ambiente ladoA, Ambiente ladoB) {
        Objects.requireNonNull(id, "O identificador é obrigatório.");
        Objects.requireNonNull(ladoA, "O primeiro ambiente é obrigatório.");
        Objects.requireNonNull(ladoB, "O segundo ambiente é obrigatório.");
        if (id.isBlank()) {
            throw new IllegalArgumentException("O identificador não pode estar vazio.");
        }
        if (ladoA == ladoB) {
            throw new IllegalArgumentException("Os lados da porta devem ser ambientes diferentes.");
        }
        this.id = id;
        this.ladoA = ladoA;
        this.ladoB = ladoB;
    }

    public String getId() {
        return id;
    }
    
    /**
     * Verifica se esta porta abre numa tentativa normal.
     *
     * @return {@code true} quando não está emperrada
     */
    public boolean abreNormalmente() {
        return !emperrada;
    }

    /**
     * Sorteia novamente o estado desta porta.
     *
     * @param sorteio gerador usado para definir o novo estado
     * @throws NullPointerException se o sorteio for nulo
     */
    public void sortearEstado(Random sorteio) {
        Objects.requireNonNull(sorteio, "O gerador de sorteio é obrigatório.");
        emperrada = sorteio.nextBoolean();
    }

    /**
     * Descobre o ambiente oposto ao ambiente informado.
     *
     * @param origem ambiente de onde o jogador pretende passar
     * @return ambiente do outro lado
     * @throws NullPointerException se a origem for nula
     * @throws IllegalArgumentException se a origem não estiver ligada a esta porta
     */
    public Ambiente outroLado(Ambiente origem) {
        Objects.requireNonNull(origem, "O ambiente de origem é obrigatório.");
        if (origem == ladoA) {
            return ladoB;
        }
        if (origem == ladoB) {
            return ladoA;
        }
        throw new IllegalArgumentException("O ambiente não está ligado a esta porta.");
    }
}
