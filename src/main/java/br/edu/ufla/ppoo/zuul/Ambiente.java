package br.edu.ufla.ppoo.zuul;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Representa um local do cenário e mantém suas conexões de saída.
 *
 * @author Michael Kölling and David J. Barnes (traduzido por Julio Cesar Alves)
 * @version 2011.07.31 (2016.02.01)
 */
public class Ambiente {
    private final String descricao;
    private final Map<String, Ambiente> saidas = new LinkedHashMap<String, Ambiente>();

    /**
     * Cria um ambiente sem saídas cadastradas.
     *
     * @param descricao descrição apresentada ao jogador
     * @throws NullPointerException se a descrição for nula
     */
    public Ambiente(String descricao) {
        this.descricao = Objects.requireNonNull(descricao, "A descrição é obrigatória.");
    }

    /**
     * Cadastra uma saída ou substitui o destino de uma direção já cadastrada.
     * Uma conexão de retorno deve ser cadastrada no ambiente de destino.
     *
     * @param direcao direção usada para identificar a saída
     * @param destino ambiente alcançado pela saída
     * @throws NullPointerException se a direção ou o destino for nulo
     * @throws IllegalArgumentException se a direção for vazia ou só contiver espaços em branco
     */
    public void definirSaida(String direcao, Ambiente destino) {
        Objects.requireNonNull(direcao, "A direção é obrigatória.");
        Objects.requireNonNull(destino, "O destino é obrigatório.");
        if (direcao.isBlank()) {
            throw new IllegalArgumentException("A direção deve ter um nome.");
        }
        saidas.put(direcao, destino);
    }

    /**
     * Remove a saída da direção informada, caso esteja cadastrada.
     *
     * @param direcao direção a remover
     */
    public void removerSaida(String direcao) {
        saidas.remove(direcao);
    }

    /**
     * Consulta o destino associado à direção informada.
     *
     * @param direcao direção a consultar
     * @return ambiente de destino, ou {@code null} se não houver saída nessa direção
     */
    public Ambiente consultarSaida(String direcao) {
        return saidas.get(direcao);
    }

    /**
     * Consulta as direções disponíveis neste ambiente.
     *
     * @return cópia imutável das direções cadastradas, na ordem de cadastro
     */
    public List<String> listarSaidas() {
        return List.<String>copyOf(saidas.keySet());
    }

    /**
     * Fornece o texto exibido ao entrar neste ambiente.
     *
     * @return descrição definida na criação do ambiente
     */
    public String getDescricao() {
        return descricao;
    }
}
