package br.edu.ufla.ppoo.zuul;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ResourceLock(Resources.SYSTEM_OUT)
@ResourceLock("java.lang.System.in")
class JogoNavegacaoTest {
    private static final String FORA = "Voce esta do lado de fora da entrada principal de uma universidade";
    private static final String LABORATORIO = "Voce esta no laboratorio de computacao";
    private static final String SAIDAS_DE_FORA = "Saidas: leste sul oeste";
    private static final String SAIDAS_DO_LABORATORIO = "Saidas: norte leste";
    private static final String ENCERRAMENTO = "Obrigado por jogar. Ate mais!";

    @Test
    void percorreTodasAsPassagensDoCenarioEListaAsSaidasDoAmbienteAtual() {
        List<String> eventos = jogar(
                "ir leste", "ir oeste", "ir oeste", "ir leste",
                "ir sul", "ir leste", "ir oeste", "ir norte", "sair");

        assertEquals(List.<String>of(
                FORA, SAIDAS_DE_FORA,
                "Voce esta no anfiteatro", "Saidas: oeste",
                FORA, SAIDAS_DE_FORA,
                "Voce esta na cantina do campus", "Saidas: leste",
                FORA, SAIDAS_DE_FORA,
                LABORATORIO, SAIDAS_DO_LABORATORIO,
                "Voce esta na sala de administracao dos computadores", "Saidas: oeste",
                LABORATORIO, SAIDAS_DO_LABORATORIO,
                FORA, SAIDAS_DE_FORA,
                ENCERRAMENTO), eventos);
    }

    @Test
    void tentativaSemDirecaoOuSemPassagemPreservaOAmbienteAtual() {
        List<String> eventos = jogar(
                "ir sul", "ir", "ir oeste", "ir diagonal", "ir norte", "sair");

        assertEquals(List.<String>of(
                FORA, SAIDAS_DE_FORA,
                LABORATORIO, SAIDAS_DO_LABORATORIO,
                "Ir pra onde?", "Nao ha passagem!", "Nao ha passagem!",
                FORA, SAIDAS_DE_FORA,
                ENCERRAMENTO), eventos);
    }

    private static List<String> jogar(String... comandos) {
        InputStream entradaOriginal = System.in;
        PrintStream saidaOriginal = System.out;
        byte[] entrada = (String.join("\n", comandos) + "\n").getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream saida = new ByteArrayOutputStream();

        try (PrintStream captura = new PrintStream(saida, true, StandardCharsets.UTF_8)) {
            System.setIn(new ByteArrayInputStream(entrada));
            System.setOut(captura);
            Programa.main(new String[0]);
        } finally {
            System.setIn(entradaOriginal);
            System.setOut(saidaOriginal);
        }

        List<String> eventos = new ArrayList<String>();
        for (String linha : saida.toString(StandardCharsets.UTF_8).split("\\R")) {
            String mensagem = linha.startsWith("> ") ? linha.substring(2) : linha;
            if (mensagem.startsWith("Voce esta ") || mensagem.startsWith("Saidas: ")
                    || mensagem.equals("Ir pra onde?") || mensagem.equals("Nao ha passagem!")
                    || mensagem.equals(ENCERRAMENTO)) {
                eventos.add(mensagem.stripTrailing());
            }
        }
        return eventos;
    }
}
