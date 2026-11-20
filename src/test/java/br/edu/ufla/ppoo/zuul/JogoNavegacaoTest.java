package br.edu.ufla.ppoo.zuul;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void argumentoExcedenteNaoTransformaComandoInvalidoEmMovimento() {
        List<String> eventos = jogar("ir sul agora", "ir leste", "sair");

        assertEquals(List.<String>of(
                FORA, SAIDAS_DE_FORA,
                "Nao ha passagem!",
                "Voce esta no anfiteatro", "Saidas: oeste",
                ENCERRAMENTO), eventos);
    }

    @Test
    void executaUltimaLinhaSemQuebraEEncerraNoFimDaEntrada() {
        List<String> eventos = jogarTexto("ir sul");

        assertEquals(List.<String>of(
                FORA, SAIDAS_DE_FORA, LABORATORIO, SAIDAS_DO_LABORATORIO, ENCERRAMENTO), eventos);
    }

    @Test
    void encerraNormalmenteComEntradaVazia() {
        assertEquals(List.<String>of(FORA, SAIDAS_DE_FORA, ENCERRAMENTO), jogarTexto(""));
    }

    @Test
    void ajudaExibeOVocabularioEPermiteContinuarNoMesmoAmbiente() {
        List<String> eventos = jogar("ir sul", "ajuda", "ir norte", "sair");

        assertEquals(List.<String>of(
                FORA, SAIDAS_DE_FORA,
                LABORATORIO, SAIDAS_DO_LABORATORIO,
                "Voce esta perdido. Voce esta sozinho. Voce caminha", "pela universidade.",
                "Suas palavras de comando sao:", "   " + new Analisador().consultarAjuda(),
                FORA, SAIDAS_DE_FORA,
                ENCERRAMENTO), eventos);
    }

    @Test
    void rejeitaSaidaComArgumentoEComandoDesconhecidoAntesDeContinuar() {
        List<String> eventos = jogar("sair agora", "comando-inexistente", "ir sul", "sair");

        assertEquals(List.<String>of(
                FORA, SAIDAS_DE_FORA,
                "Sair o que?", "Eu nao entendi o que voce disse...",
                LABORATORIO, SAIDAS_DO_LABORATORIO,
                ENCERRAMENTO), eventos);
    }

    @Test
    void sessaoCriadaDiretamentePreservaOSuporteASaidaPadrao() {
        PrintStream original = System.out;
        ByteArrayOutputStream mensagens = new ByteArrayOutputStream();
        try (PrintStream captura = new PrintStream(mensagens, true, StandardCharsets.UTF_8)) {
            System.setOut(captura);
            Jogo jogo = new Jogo();
            jogo.iniciar();
            Analisador analisador = new Analisador();
            assertFalse(jogo.processarComando(analisador.interpretar("ir sul")));
            assertTrue(jogo.processarComando(analisador.interpretar("sair")));
            assertTrue(mensagens.toString(StandardCharsets.UTF_8).lines().toList().contains(LABORATORIO));
        } finally {
            System.setOut(original);
        }
    }

    @Test
    @ResourceLock(Resources.SYSTEM_ERR)
    void informaFalhaDeLeituraEEncerraASessao() {
        InputStream entradaComFalha = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("Falha simulada na entrada");
            }
        };
        PrintStream erroOriginal = System.err;
        ByteArrayOutputStream erros = new ByteArrayOutputStream();

        try (PrintStream captura = new PrintStream(erros, true, StandardCharsets.UTF_8)) {
            System.setErr(captura);
            String saida = executarPrograma(entradaComFalha);
            assertTrue(saida.stripTrailing().endsWith(ENCERRAMENTO));
            assertEquals("Não foi possível ler os comandos do terminal.",
                    erros.toString(StandardCharsets.UTF_8).strip());
        } finally {
            System.setErr(erroOriginal);
        }
    }

    private static List<String> jogar(String... comandos) {
        return jogarTexto(String.join("\n", comandos) + "\n");
    }

    private static List<String> jogarTexto(String texto) {
        byte[] entrada = texto.getBytes(StandardCharsets.UTF_8);
        String saida = executarPrograma(new ByteArrayInputStream(entrada));

        List<String> eventos = new ArrayList<String>();
        for (String linha : saida.split("\\R")) {
            String mensagem = linha.startsWith("> ") ? linha.substring(2) : linha;
            if (mensagem.startsWith("Voce esta ") || mensagem.startsWith("Saidas: ")
                    || mensagem.equals("Ir pra onde?") || mensagem.equals("Nao ha passagem!")
                    || mensagem.equals("pela universidade.")
                    || mensagem.equals("Suas palavras de comando sao:") || mensagem.startsWith("   ")
                    || mensagem.equals("Sair o que?") || mensagem.equals("Eu nao entendi o que voce disse...")
                    || mensagem.equals(ENCERRAMENTO)) {
                eventos.add(mensagem.stripTrailing());
            }
        }
        return eventos;
    }

    private static String executarPrograma(InputStream entrada) {
        InputStream entradaOriginal = System.in;
        PrintStream saidaOriginal = System.out;
        ByteArrayOutputStream saida = new ByteArrayOutputStream();

        try (PrintStream captura = new PrintStream(saida, true, StandardCharsets.UTF_8)) {
            System.setIn(entrada);
            System.setOut(captura);
            Programa.main(new String[0]);
        } finally {
            System.setIn(entradaOriginal);
            System.setOut(saidaOriginal);
        }

        return saida.toString(StandardCharsets.UTF_8);
    }
}
