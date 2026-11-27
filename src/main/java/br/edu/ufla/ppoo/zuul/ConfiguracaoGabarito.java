package br.edu.ufla.ppoo.zuul;

public class ConfiguracaoGabarito {

    private final String ambienteTesouro;
    private final String ambienteDica1;
    private final String ambienteDica2;
    private final String ambienteNegadoDica1;
    private final String ambienteNegadoDica2;
    private final String ambienteChaveMestra;
    private final int durabilidadeChaveMestra;
    private final int tentativasInicais;

    public ConfiguracaoGabarito(String ambienteTesouro, String ambienteDica1, String ambienteDica2,
                                String ambienteNegadoDica1, String ambienteNegadoDica2,
                                String ambienteChaveMestra, int durabilidadeChaveMestra, int tentativasInicais) {

        this.ambienteTesouro = ambienteTesouro;
        this.ambienteDica1 = ambienteDica1;
        this.ambienteDica2 = ambienteDica2;
        this.ambienteNegadoDica1 = ambienteNegadoDica1;
        this.ambienteNegadoDica2 = ambienteNegadoDica2;
        this.ambienteChaveMestra = ambienteChaveMestra;
        this.durabilidadeChaveMestra = durabilidadeChaveMestra;
        this.tentativasInicais = tentativasInicais;
    }

    public String getAmbienteTesouro() {
        return ambienteTesouro;
    }

    public String getAmbienteDica1() {
        return ambienteDica1;
    }

    public String getAmbienteDica2() {
        return ambienteDica2;
    }

    public String getAmbienteNegadoDica1() {
        return ambienteNegadoDica1;
    }

    public String getAmbienteNegadoDica2() {
        return ambienteNegadoDica2;
    }

    public String getAmbienteChaveMestra() {
        return ambienteChaveMestra;
    }

    public int getDurabilidadeChaveMestra() {
        return durabilidadeChaveMestra;
    }

    public int getTentativasIniciais(){
        return tentativasInicais;
    }
}