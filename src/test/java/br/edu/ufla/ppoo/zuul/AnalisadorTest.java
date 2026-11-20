package br.edu.ufla.ppoo.zuul;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnalisadorTest {
    private final Analisador analisador = new Analisador();

    @Test
    void preservaNomeCompostoComoUmUnicoArgumento() {
        Comando comando = analisador.interpretar("ir Sala de Jantar");

        assertFalse(comando.ehDesconhecido());
        assertEquals("ir", comando.getPalavraDeComando());
        assertTrue(comando.temArgumento());
        assertEquals("Sala de Jantar", comando.getArgumento());
    }

    @Test
    void removeSeparadoresExternosEPreservaEspacosInternosDoArgumento() {
        Comando comando = analisador.interpretar("\t ir\u2003\tÁrea  de Serviço  \t");

        assertEquals("ir", comando.getPalavraDeComando());
        assertEquals("Área  de Serviço", comando.getArgumento());
    }

    @Test
    void reconheceAjudaSemConfundirEspacosComArgumento() {
        Comando comando = analisador.interpretar("  ajuda \t");

        assertFalse(comando.ehDesconhecido());
        assertEquals("ajuda", comando.getPalavraDeComando());
        assertFalse(comando.temArgumento());
        assertNull(comando.getArgumento());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " \t\u2003 "})
    void entradaVaziaOuSoComEspacosResultaEmComandoDesconhecido(String texto) {
        Comando comando = analisador.interpretar(texto);

        assertTrue(comando.ehDesconhecido());
        assertNull(comando.getPalavraDeComando());
        assertFalse(comando.temArgumento());
    }

    @Test
    void palavraForaDoVocabularioNaoViraAcaoMesmoComArgumentoValido() {
        Comando comando = analisador.interpretar("IR Sala de Jantar");

        assertTrue(comando.ehDesconhecido());
        assertNull(comando.getPalavraDeComando());
        assertEquals("Sala de Jantar", comando.getArgumento());
    }

    @Test
    void argumentoNaoReapareceNoComandoSeguinte() {
        Comando movimento = analisador.interpretar("ir sul");
        Comando encerramento = analisador.interpretar("sair");

        assertEquals("sul", movimento.getArgumento());
        assertEquals("sair", encerramento.getPalavraDeComando());
        assertFalse(encerramento.temArgumento());
    }

    @Test
    void rejeitaTextoNuloComoErroDoChamador() {
        assertThrows(NullPointerException.class, new Executable() {
            @Override
            public void execute() {
                analisador.interpretar(null);
            }
        });
    }
}
