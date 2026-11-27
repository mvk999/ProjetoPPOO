package br.edu.ufla.ppoo.zuul;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Representa o jogador da casa mal-assombrada.
 */
public final class Jogador {
    private boolean cargaDisponivel;
    private Ambiente ambienteAtual;
    private ChaveMestra chave;
    private final List<String> dicasEncontradas = new ArrayList<String>();

    /**
     * Cria um jogador com uma única carga explosiva.
     */
    public Jogador(Ambiente ambienteInicial) {
        ambienteAtual = Objects.requireNonNull(ambienteInicial, "O ambiente inicial é obrigatório.");
        cargaDisponivel = true;
        chave = null;
        coletarItens(ambienteAtual.retirarItens());
    }

    public Ambiente getAmbienteAtual() {
        return ambienteAtual;
    }

    /**
     * Move o jogador para o ambiente informado.
     *
     * @param destino ambiente de destino
     * @return itens encontrados ao entrar no ambiente
     * @throws NullPointerException se o destino for nulo
     */
    public List<ItemEncontravel> moverPara(Ambiente destino) {
        Ambiente ambienteDestino = Objects.requireNonNull(destino, "O ambiente de destino é obrigatório.");
        List<ItemEncontravel> itensEncontrados = ambienteDestino.retirarItens();
        coletarItens(itensEncontrados);
        ambienteAtual = ambienteDestino;
        return itensEncontrados;
    }

    public boolean getCargaDisponivel() {
        return cargaDisponivel;
    }

    /**
     * Consome a única carga explosiva do jogador.
     *
     * @throws IllegalStateException se a carga já tiver sido usada
     */
    public boolean consumirCarga() {
        if (!cargaDisponivel) {
            return false;
        }
        cargaDisponivel = false;
        if(getAmbienteAtual().recuperarTesouro()) {
            return true;
        } 
        return false;
    }

    /**
     * Recebe a chave mestra encontrada pelo jogador.
     *
     * @param chave chave encontrada
     * @throws NullPointerException se a chave for nula
     */
    public void receberChave(ChaveMestra chave) {
        this.chave = Objects.requireNonNull(chave, "A chave mestra não pode ser nula.");
    }

    public ChaveMestra getChave() {
        return chave;
    }

    /**
     * Usa uma unidade de durabilidade da chave mestra.
     *
     * @throws IllegalStateException se o jogador não tiver uma chave utilizável
     */
    public void usarChave() {
        if (chave == null || !chave.funcionando()) {
            throw new IllegalStateException("O jogador não possui uma chave mestra utilizável.");
        }
        chave.usar();
    }

    public void registrarDica(String texto) {
        Objects.requireNonNull(texto, "A dica não pode ser vazia.");
        if (texto.isBlank()) {
            throw new IllegalArgumentException("A dica não pode estar vazia.");
        }
        dicasEncontradas.add(texto);
    }

    public List<String> getDicasEncontradas() {
        return List.<String>copyOf(dicasEncontradas);
    }

    /**
     * Aplica ao jogador os itens encontrados em um ambiente.
     *
     * @param itens itens encontrados
     * @throws NullPointerException se a lista ou algum item for nulo
     */
    public void coletarItens(List<ItemEncontravel> itens) {
        Objects.requireNonNull(itens, "Os itens encontrados são obrigatórios.");
        for (ItemEncontravel item : itens) {
            Objects.requireNonNull(item, "O item encontrado não pode ser nulo.");
            if (item instanceof Dica) {
                Dica dica = (Dica) item;
                registrarDica(dica.getDescricao());
            } else if (item instanceof ChaveMestra) {
                receberChave((ChaveMestra) item);
            }
        }
    }
}
