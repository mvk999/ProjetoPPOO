package br.edu.ufla.ppoo.zuul;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class SorteadorGabarito {

    private final Random geradorAleatorio;

    public SorteadorGabarito() {
        geradorAleatorio = new Random();
    }

    public ConfiguracaoGabarito sortear(List<String> nomesAmbientes) {

        Objects.requireNonNull(nomesAmbientes,"A lista de ambientes é obrigatória.");

        if (nomesAmbientes.size() < 4) {
            throw new IllegalArgumentException("São necessários pelo menos quatro ambientes.");
        }

        // Cria uma cópia para não modificar a lista original.
        List<String> ambientesSorteados = new ArrayList<String>(nomesAmbientes);

        // Embaralha a ordem dos ambientes.
        Collections.shuffle(ambientesSorteados, geradorAleatorio);

        // Sorteia os ambientes dos itens.
        String ambienteTesouro = ambientesSorteados.get(0);
        String ambienteDica1 = ambientesSorteados.get(1);
        String ambienteDica2 = ambientesSorteados.get(2);
        String ambienteChaveMestra = ambientesSorteados.get(3);

        // Sorteia a durabilidade entre 1 e 10.
        int durabilidadeChaveMestra =
                geradorAleatorio.nextInt(10) + 1;
        
        int min = 10;
        int max = 30;

        int tentativasInicais = geradorAleatorio.nextInt((max - min) + 1) + min;
    

        return new ConfiguracaoGabarito(ambienteTesouro, ambienteDica1, ambienteDica2, ambienteChaveMestra,
                                        durabilidadeChaveMestra, tentativasInicais);
    }
}