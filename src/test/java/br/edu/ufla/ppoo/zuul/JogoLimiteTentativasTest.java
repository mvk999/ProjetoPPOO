package br.edu.ufla.ppoo.zuul;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JogoLimiteTentativasTest {
    private static final int TENTATIVAS_INICIAIS = 10;

    @TempDir
    Path diretorioTemporario;

    private final ByteArrayOutputStream mensagens = new ByteArrayOutputStream();
    private final Analisador analisador = new Analisador();

    @Test
    void ultimaPortaEmperradaEncerraAPartidaEBloqueiaCavar() {
        Jogo jogo = iniciarJogo(new SorteioPortas(true));

        for (int tentativa = 1; tentativa < TENTATIVAS_INICIAIS; tentativa++) {
            assertFalse(processar(jogo, "ir norte"));
        }

        assertTrue(processar(jogo, "ir norte"));
        String mensagemDaDerrota = mensagens.toString(StandardCharsets.UTF_8);
        assertTrue(mensagemDaDerrota.contains("A porta está emperrada"));
        assertTrue(mensagemDaDerrota.contains("Game over."));

        assertTrue(processar(jogo, "cavar"));
        assertEquals(mensagemDaDerrota, mensagens.toString(StandardCharsets.UTF_8));
    }

    @Test
    void ultimaPortaAbertaPermiteAChegadaEEncerraAPartida() {
        Jogo jogo = iniciarJogo(new SorteioPortas(false));

        for (int tentativa = 1; tentativa < TENTATIVAS_INICIAIS; tentativa++) {
            assertFalse(processar(jogo, tentativa % 2 == 1 ? "ir norte" : "ir sul"));
        }
        mensagens.reset();

        assertTrue(processar(jogo, "ir sul"));
        String mensagemDaDerrota = mensagens.toString(StandardCharsets.UTF_8);
        assertTrue(mensagemDaDerrota.contains("Voce esta em Sala de TV"));
        assertTrue(mensagemDaDerrota.contains("0 tentativas restantes"));
        assertTrue(mensagemDaDerrota.contains("Game over."));
    }

    @Test
    void chaveAbrePortaEmperradaSemGastarAUltimaTentativa() {
        SorteioPortas sorteio = new SorteioPortas(false);
        Jogo jogo = iniciarJogo(sorteio);
        sorteio.emperrar = true;

        // A porta abre com o estado inicial. O sorteio seguinte emperra todas as portas.
        assertFalse(processar(jogo, "ir norte"));
        for (int tentativa = 2; tentativa < TENTATIVAS_INICIAIS; tentativa++) {
            assertFalse(processar(jogo, "ir sul"));
        }
        mensagens.reset();

        assertFalse(processar(jogo, "chave sul"));
        String mensagemDaChegada = mensagens.toString(StandardCharsets.UTF_8);
        assertTrue(mensagemDaChegada.contains("Durabilidade restante: 0."));
        assertTrue(mensagemDaChegada.contains("Voce esta em Sala de TV"));
        assertTrue(mensagemDaChegada.contains("1 tentativas restantes"));
        assertTrue(processar(jogo, "ir norte"));
    }

    private Jogo iniciarJogo(Random sorteio) {
        SorteadorGabarito sorteadorGabarito = new SorteadorGabarito() {
            @Override
            public ConfiguracaoGabarito sortear(List<String> nomesAmbientes) {
                return new ConfiguracaoGabarito("Sala de TV", "Quarto 1", "Quarto 2",
                        "WC 1", "Cozinha", "Sala de Jantar", 1, TENTATIVAS_INICIAIS);
            }
        };
        PrintStream saida = new PrintStream(mensagens, true, StandardCharsets.UTF_8);
        Jogo jogo = new Jogo(saida, sorteio, sorteadorGabarito, diretorioTemporario.resolve("gabarito.txt").toString());
        jogo.iniciar();
        return jogo;
    }

    private boolean processar(Jogo jogo, String comando) {
        return jogo.processarComando(analisador.interpretar(comando));
    }

    private static final class SorteioPortas extends Random {
        private boolean emperrar;

        SorteioPortas(boolean emperrar) {
            this.emperrar = emperrar;
        }

        @Override
        public int nextInt(int limite) {
            return emperrar ? 0 : 1;
        }
    }
}
