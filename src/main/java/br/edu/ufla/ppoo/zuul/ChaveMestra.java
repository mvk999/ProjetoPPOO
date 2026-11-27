package br.edu.ufla.ppoo.zuul;

/**
 * Representa a chave mestra e sua durabilidade.
 */
public final class ChaveMestra extends ItemEncontravel {
    private int durabilidade;

    /**
     * Cria uma chave mestra com durabilidade limitada.
     *
     * @param durabilidade número de portas que a chave pode abrir
     * @throws IllegalArgumentException se a durabilidade estiver fora de 1 a 10
     */
    public ChaveMestra(int durabilidade) {
        super("Chave mestra");
        if (durabilidade < 1 || durabilidade > 10) {
            throw new IllegalArgumentException("A durabilidade deve estar entre 1 e 10.");
        }
        this.durabilidade = durabilidade;
    }

    public int getDurabilidade() {
        return durabilidade;
    }

    /**
     * Indica se a chave ainda pode abrir portas.
     *
     * @return {@code true} enquanto houver durabilidade
     */
    public boolean funcionando() {
        return durabilidade > 0;
    }

    /**
     * Usa uma unidade da durabilidade da chave.
     *
     * @throws IllegalStateException se a chave já estiver esgotada
     */
    public void usar() {
        if (!funcionando()) {
            throw new IllegalStateException("A chave mestra está esgotada.");
        }
        durabilidade--;
    }
}
