package br.edu.ufla.ppoo.zuul;

import java.io.FileWriter;
import java.io.IOException;

public class EscritorGabarito {

    /**
     * Grava as configurações do jogo em um arquivo de texto.
     *
     * @param nomeArquivo nome do arquivo
     * @param configuracao configurações que serão armazenadas
     * @throws IOException caso ocorra um erro durante a gravação
     */
    public void gravar(String nomeArquivo, ConfiguracaoGabarito configuracao) throws IOException {

        try (FileWriter escritor = new FileWriter(nomeArquivo)) {

            escritor.write("tesouro=" + configuracao.getAmbienteTesouro() + "\n");

            escritor.write("dica1=" + configuracao.getAmbienteDica1() + "\n");

            escritor.write("dica2=" + configuracao.getAmbienteDica2() + "\n");

            escritor.write("ambienteNegadoDica1=" + configuracao.getAmbienteNegadoDica1() + "\n");

            escritor.write("ambienteNegadoDica2=" + configuracao.getAmbienteNegadoDica2() + "\n");

            escritor.write("chaveMestra=" + configuracao.getAmbienteChaveMestra() + "\n");

            escritor.write("durabilidadeChaveMestra=" + configuracao.getDurabilidadeChaveMestra() + "\n");

            escritor.write("tentativasIniciais=" + configuracao.getTentativasIniciais() + "\n");
        }
    }
}