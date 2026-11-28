package br.edu.ufla.ppoo.zuul;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JogoNavegacaoTest {
    private static final int PORTAS_DA_CASA = 8;
    private static final String SALA_TV = "Voce esta em Sala de TV";
    private static final String SALA_JANTAR = "Voce esta em Sala de Jantar";
    private static final String CORREDOR = "Voce esta em Corredor";
    private static final String COZINHA = "Voce esta em Cozinha";
    private static final String SAIDAS_TV = "Saidas: norte";
    private static final String SAIDAS_JANTAR = "Saidas: sul leste";
    private static final String SAIDAS_CORREDOR = "Saidas: noroeste nordeste oeste leste sudoeste sudeste";
    private static final String SAIDAS_COZINHA = "Saidas: sul oeste";
    private static final String SEM_PASSAGEM = "Nao ha passagem!";
    private static final String ENCERRAMENTO = "Obrigado por explorar a casa mal-assombrada. Ate mais!";

    @TempDir
    Path diretorioTemporario;

    private final ByteArrayOutputStream mensagens = new ByteArrayOutputStream();
    private final ByteArrayOutputStream diagnosticos = new ByteArrayOutputStream();
    private Random sorteioPortas = new Random() {
        @Override
        public int nextInt(int limite) {
            return 1;
        }
    };

    @Test
    void percorreAsOitoPortasNosDoisSentidosEListaAsSaidasDeCadaComodo() throws IOException {
        List<String> eventos = jogar(
                "ir norte", "ir leste",
                "ir noroeste", "ir leste", "ir nordeste", "ir oeste",
                "ir oeste", "ir leste", "ir leste", "ir oeste",
                "ir sudeste", "ir sul", "ir norte", "ir oeste",
                "ir sudoeste", "ir sul", "sair");

        assertEquals(List.<String>of(
                SALA_TV, SAIDAS_TV, SALA_JANTAR, SAIDAS_JANTAR,
                CORREDOR, SAIDAS_CORREDOR,
                "Voce esta em Quarto 1", "Saidas: leste", CORREDOR, SAIDAS_CORREDOR,
                "Voce esta em Quarto 2", "Saidas: oeste", CORREDOR, SAIDAS_CORREDOR,
                "Voce esta em WC 1", "Saidas: leste", CORREDOR, SAIDAS_CORREDOR,
                "Voce esta em WC 2", "Saidas: oeste", CORREDOR, SAIDAS_CORREDOR,
                COZINHA, SAIDAS_COZINHA, "Voce esta em Área de Serviço", "Saidas: norte",
                COZINHA, SAIDAS_COZINHA, CORREDOR, SAIDAS_CORREDOR,
                SALA_JANTAR, SAIDAS_JANTAR, SALA_TV, SAIDAS_TV, ENCERRAMENTO), eventos);
        List<String> contadores = consultarContadores();
        assertEquals(17, contadores.size());
        assertEquals("Atualmente voce possui 14 tentativas restantes.", contadores.getLast());
    }

    @Test
    void tentativaSemDirecaoOuSemPassagemPreservaOAmbienteEAsTentativas() throws IOException {
        List<String> eventos = jogar("ir", "ir leste", "ir diagonal", "ir norte", "ir norte", "sair");

        assertEquals(List.<String>of(
                SALA_TV, SAIDAS_TV, "Ir pra onde?", SEM_PASSAGEM, SEM_PASSAGEM,
                SALA_JANTAR, SAIDAS_JANTAR, SEM_PASSAGEM, ENCERRAMENTO), eventos);
        assertEquals(List.<String>of(
                "Atualmente voce possui 30 tentativas restantes.",
                "Atualmente voce possui 29 tentativas restantes."), consultarContadores());
    }

    @Test
    void portaDaSalaDeTvEmperraNaVoltaEAceitaNovaTentativaAposSorteio() throws IOException {
        sorteioPortas = new Random() {
            private int sorteios;

            @Override
            public int nextInt(int limite) {
                // Emperra só a primeira porta (TV–Jantar) no sorteio após a ida.
                return sorteios++ == PORTAS_DA_CASA ? 0 : 1;
            }
        };

        assertEquals(List.<String>of(SALA_TV, SAIDAS_TV, SALA_JANTAR, SAIDAS_JANTAR,
                "A porta está emperrada. Não foi possível abrir. 28 tentativas restantes.",
                SALA_TV, SAIDAS_TV, ENCERRAMENTO), jogar("ir norte", "ir sul", "ir sul", "sair"));
        assertEquals("Atualmente voce possui 27 tentativas restantes.", consultarContadores().getLast());
    }

    @Test
    void argumentoExcedenteNaoTransformaComandoInvalidoEmMovimento() throws IOException {
        assertEquals(List.<String>of(SALA_TV, SAIDAS_TV, SEM_PASSAGEM, SALA_JANTAR, SAIDAS_JANTAR, ENCERRAMENTO),
                jogar("ir norte agora", "ir norte", "sair"));
    }

    @Test
    void executaUltimaLinhaSemQuebraEEncerraNoFimDaEntrada() throws IOException {
        assertEquals(List.<String>of(SALA_TV, SAIDAS_TV, SALA_JANTAR, SAIDAS_JANTAR, ENCERRAMENTO),
                jogarTexto("ir norte"));
    }

    @Test
    void encerraNormalmenteComEntradaVazia() throws IOException {
        assertEquals(List.<String>of(SALA_TV, SAIDAS_TV, ENCERRAMENTO), jogarTexto(""));
    }

    @Test
    void ajudaExibeOVocabularioEPermiteContinuarNoMesmoAmbiente() throws IOException {
        assertEquals(List.<String>of(
                SALA_TV, SAIDAS_TV, SALA_JANTAR, SAIDAS_JANTAR,
                "Suas palavras de comando sao:", "   ir chave sair ajuda cavar",
                SALA_TV, SAIDAS_TV, ENCERRAMENTO), jogar("ir norte", "ajuda", "ir sul", "sair"));
    }

    @Test
    void rejeitaSaidaComArgumentoEComandoDesconhecidoAntesDeContinuar() throws IOException {
        assertEquals(List.<String>of(
                SALA_TV, SAIDAS_TV, "Sair o que?", "Eu nao entendi o que voce disse...",
                SALA_JANTAR, SAIDAS_JANTAR, ENCERRAMENTO), jogar("sair agora", "comando-inexistente", "ir norte", "sair"));
    }

    @Test
    @ResourceLock(Resources.SYSTEM_OUT)
    void sessaoCriadaDiretamentePreservaOSuporteASaidaPadrao() {
        PrintStream original = System.out;
        try (PrintStream captura = new PrintStream(mensagens, true, StandardCharsets.UTF_8)) {
            System.setOut(captura);
            Jogo jogo = new Jogo();
            jogo.iniciar();
            Analisador analisador = new Analisador();
            assertFalse(jogo.processarComando(analisador.interpretar("ir sul")));
            assertTrue(jogo.processarComando(analisador.interpretar("sair")));
            List<String> linhas = mensagens.toString(StandardCharsets.UTF_8).lines().toList();
            assertTrue(linhas.contains(SALA_TV));
            assertTrue(linhas.contains(SEM_PASSAGEM));
        } finally {
            System.setOut(original);
        }
    }

    @Test
    void informaFalhaDeLeituraEEncerraASessao() throws IOException {
        try (BufferedReader entrada = new BufferedReader(new StringReader("")) {
            @Override
            public String readLine() throws IOException {
                throw new IOException("Falha simulada na entrada");
            }
        }) {
            executarPrograma(entrada);
        }
        assertTrue(mensagens.toString(StandardCharsets.UTF_8).stripTrailing().endsWith(ENCERRAMENTO));
        assertEquals("Não foi possível ler os comandos do terminal.",
                diagnosticos.toString(StandardCharsets.UTF_8).strip());
    }

    private List<String> jogar(String... comandos) throws IOException {
        return jogarTexto(String.join("\n", comandos) + "\n");
    }

    private List<String> jogarTexto(String texto) throws IOException {
        try (BufferedReader entrada = new BufferedReader(new StringReader(texto))) {
            executarPrograma(entrada);
        }
        assertEquals(0, diagnosticos.size());
        List<String> eventos = new ArrayList<String>();
        for (String linha : mensagens.toString(StandardCharsets.UTF_8).lines().toList()) {
            String mensagem = linha.startsWith("> ") ? linha.substring(2) : linha;
            if (mensagem.startsWith("Voce esta ") || mensagem.startsWith("Saidas: ")
                    || mensagem.equals("Ir pra onde?") || mensagem.equals(SEM_PASSAGEM)
                    || mensagem.startsWith("A porta está emperrada.")
                    || mensagem.equals("Suas palavras de comando sao:") || mensagem.startsWith("   ")
                    || mensagem.equals("Sair o que?") || mensagem.equals("Eu nao entendi o que voce disse...")
                    || mensagem.equals(ENCERRAMENTO)) {
                eventos.add(mensagem);
            }
        }
        return eventos;
    }

    private List<String> consultarContadores() {
        List<String> contadores = new ArrayList<String>();
        for (String linha : mensagens.toString(StandardCharsets.UTF_8).lines().toList()) {
            if (linha.startsWith("Atualmente voce possui ")) {
                contadores.add(linha);
            }
        }
        return contadores;
    }

    private void executarPrograma(BufferedReader entrada) {
        SorteadorGabarito gabaritoFixo = new SorteadorGabarito() {
            @Override
            public ConfiguracaoGabarito sortear(List<String> nomesAmbientes) {
                return new ConfiguracaoGabarito("Quarto 1", "Quarto 2", "Área de Serviço",
                        "Corredor", "Cozinha", "Sala de Jantar", 1, 30);
            }
        };
        try (PrintStream saida = new PrintStream(mensagens, true, StandardCharsets.UTF_8);
                PrintStream erros = new PrintStream(diagnosticos, true, StandardCharsets.UTF_8)) {
            Jogo jogo = new Jogo(saida, sorteioPortas, gabaritoFixo, diretorioTemporario.resolve("gabarito.txt").toString());
            Programa.executar(entrada, saida, erros, jogo);
        }
    }
}
