package br.edu.ufla.ppoo.zuul;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class SorteadorGabarito {
    private static final String AMBIENTE_INICIAL = "Sala de TV";

    private final Random geradorAleatorio;

    public SorteadorGabarito() {
        geradorAleatorio = new Random();
    }

    public ConfiguracaoGabarito sortear(List<String> nomesAmbientes) {

        Objects.requireNonNull(nomesAmbientes,"A lista de ambientes é obrigatória.");

        if (nomesAmbientes.size() < 4) {
            throw new IllegalArgumentException("São necessários pelo menos quatro ambientes.");
        }

        if (!nomesAmbientes.contains(AMBIENTE_INICIAL)) {
            throw new IllegalArgumentException("A lista deve incluir o ambiente inicial: " + AMBIENTE_INICIAL);
        }

        List<String> ambientesSorteados = new ArrayList<String>(nomesAmbientes);
        Collections.shuffle(ambientesSorteados, geradorAleatorio);
        String ambienteTesouro = ambientesSorteados.get(0);

        List<String> ambientesDeItens = new ArrayList<String>(nomesAmbientes);
        ambientesDeItens.remove(AMBIENTE_INICIAL);
        ambientesDeItens.remove(ambienteTesouro);
        if (ambientesDeItens.size() < 3) {
            throw new IllegalArgumentException(
                    "São necessários três ambientes além do inicial e do tesouro para posicionar os itens.");
        }
        Collections.shuffle(ambientesDeItens, geradorAleatorio);
        String ambienteDica1 = ambientesDeItens.get(0);
        String ambienteDica2 = ambientesDeItens.get(1);
        String ambienteChaveMestra = ambientesDeItens.get(2);

        List<String> ambientesNegados = new ArrayList<String>(nomesAmbientes);
        ambientesNegados.remove(ambienteTesouro);
        Collections.shuffle(ambientesNegados, geradorAleatorio);
        String ambienteNegadoDica1 = ambientesNegados.get(0);
        String ambienteNegadoDica2 = ambientesNegados.get(1);

        // Sorteia a durabilidade entre 1 e 10.
        int durabilidadeChaveMestra =
                geradorAleatorio.nextInt(10) + 1;
        
        int min = 10;
        int max = 30;

        int tentativasInicais = geradorAleatorio.nextInt((max - min) + 1) + min;
    

        return new ConfiguracaoGabarito(ambienteTesouro, ambienteDica1, ambienteDica2,
                                        ambienteNegadoDica1, ambienteNegadoDica2, ambienteChaveMestra,
                                        durabilidadeChaveMestra, tentativasInicais);
    }
}