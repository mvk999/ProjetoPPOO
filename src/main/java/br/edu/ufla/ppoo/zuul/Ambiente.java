package br.edu.ufla.ppoo.zuul;

import java.util.ArrayList;
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
    private final String id;
    private final String nome;
    private boolean tesouroEnterrado;
    private final List<ItemEncontravel> itens = new ArrayList<ItemEncontravel>();
    private final Map<String, Porta> saidas = new LinkedHashMap<String, Porta>();

    /**
     * Cria um ambiente sem saídas cadastradas.
     *
     * @param descricao descrição apresentada ao jogador
     * @throws NullPointerException se a descrição for nula
     */
    public Ambiente(String descricao) {
        Objects.requireNonNull(descricao, "A descrição é obrigatória.");
        if (descricao.isBlank()) {
            throw new IllegalArgumentException("A descrição não pode estar vazia.");
        }
        this.id = descricao;
        this.nome = descricao;
    }

    /**
     * Cria um ambiente identificado no modelo da casa mal-assombrada.
     *
     * @param id identificador estável usado pelas portas e pelo mapa
     * @param nome nome apresentado ao jogador
     * @throws NullPointerException se o identificador ou o nome for nulo
     * @throws IllegalArgumentException se o identificador ou o nome estiver vazio
     */
    public Ambiente(String id, String nome) {
        Objects.requireNonNull(id, "O identificador é obrigatório.");
        Objects.requireNonNull(nome, "O nome é obrigatório.");
        if (id.isBlank()) {
            throw new IllegalArgumentException("O identificador não pode estar vazio.");
        }
        if (nome.isBlank()) {
            throw new IllegalArgumentException("O nome não pode estar vazio.");
        }
        this.id = id;
        this.nome = nome;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void definirTesouroEnterrado(boolean tesouroEnterrado) {
        this.tesouroEnterrado = tesouroEnterrado;
    }

    public boolean getTesouroEnterrado() {
        return tesouroEnterrado;
    }

    public boolean recuperarTesouro() {
        if (!tesouroEnterrado) {
            return false;
        }
        tesouroEnterrado = false;
        return true;
    }

    public void adicionarItem(ItemEncontravel item) {
        itens.add(Objects.requireNonNull(item, "O item é obrigatório."));
    }

    /**
     * Retira todos os itens encontrados neste ambiente.
     *
     * @return cópia imutável dos itens antes da retirada
     */
    public List<ItemEncontravel> retirarItens() {
        List<ItemEncontravel> itensEncontrados = List.<ItemEncontravel>copyOf(itens);
        itens.clear();
        return itensEncontrados;
    }

    /**
     * Consulta a porta associada ao identificador de destino.
     *
     * @param destinoId identificador do ambiente de destino
     * @return primeira porta para o destino na ordem das saídas, ou {@code null} se não houver porta
     */
    public Porta consultarPorta(String destinoId) {
        for (Porta porta : saidas.values()) {
            if (porta.outroLado(this).getId().equals(destinoId)) {
                return porta;
            }
        }
        return null;
    }

    /**
     * Cadastra uma saída ou substitui a porta de uma direção já cadastrada.
     * O ambiente de destino deve cadastrar a mesma porta na direção de retorno.
     *
     * @param direcao direção usada para identificar a saída
     * @param porta porta pela qual se alcança o destino
     * @throws NullPointerException se a direção ou a porta for nula
     * @throws IllegalArgumentException se a direção estiver em branco ou a porta
     *         não ligar este ambiente
     */
    public void definirSaida(String direcao, Porta porta) {
        Objects.requireNonNull(direcao, "A direção é obrigatória.");
        Objects.requireNonNull(porta, "A porta é obrigatória.");
        if (direcao.isBlank()) {
            throw new IllegalArgumentException("A direção deve ter um nome.");
        }
        porta.outroLado(this);
        saidas.put(direcao, porta);
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
        Porta porta = consultarPortaNaDirecao(direcao);
        if (porta == null) {
            return null;
        }
        return porta.outroLado(this);
    }

    Porta consultarPortaNaDirecao(String direcao) {
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
        return nome;
    }
}
