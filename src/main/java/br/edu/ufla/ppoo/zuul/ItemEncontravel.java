package br.edu.ufla.ppoo.zuul;

import java.util.Objects;

/**
 * Representa um item que pode ser encontrado em um ambiente.
 */
public abstract class ItemEncontravel {
    private final String descricao;

    /**
     * Cria um item com uma descrição válida.
     *
     * @param descricao descrição apresentada ao jogador
     * @throws NullPointerException se a descrição for nula
     * @throws IllegalArgumentException se a descrição estiver vazia
     */
    protected ItemEncontravel(String descricao) {
        Objects.requireNonNull(descricao, "A descrição é obrigatória.");
        if (descricao.isBlank()) {
            throw new IllegalArgumentException("A descrição não pode estar vazia.");
        }
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
