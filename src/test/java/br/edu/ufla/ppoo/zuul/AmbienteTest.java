package br.edu.ufla.ppoo.zuul;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AmbienteTest {
    @Test
    void novoAmbientePreservaDescricaoEComecaSemSaidas() {
        Ambiente entrada = new Ambiente("Entrada");

        assertEquals("Entrada", entrada.getDescricao());
        assertTrue(entrada.listarSaidas().isEmpty());
        assertNull(entrada.consultarSaida("norte"));
    }

    @Test
    void cadastraMaisDeQuatroSaidasSemCriarRetornoAutomatico() {
        Ambiente corredor = new Ambiente("Corredor");
        Ambiente sala = new Ambiente("Sala");
        corredor.definirSaida("norte", sala);
        corredor.definirSaida("leste", sala);
        corredor.definirSaida("sul", sala);
        corredor.definirSaida("oeste", sala);
        corredor.definirSaida("varanda", sala);
        corredor.definirSaida("fundos", sala);

        assertEquals(List.<String>of("norte", "leste", "sul", "oeste", "varanda", "fundos"),
                corredor.listarSaidas());
        assertSame(sala, corredor.consultarSaida("varanda"));
        assertNull(sala.consultarSaida("varanda"));
    }

    @Test
    void substituiERemoveSomenteASaidaIndicada() {
        Ambiente corredor = new Ambiente("Corredor");
        Ambiente sala = new Ambiente("Sala");
        Ambiente cozinha = new Ambiente("Cozinha");
        corredor.definirSaida("norte", sala);
        corredor.definirSaida("sul", cozinha);

        corredor.definirSaida("norte", cozinha);
        assertSame(cozinha, corredor.consultarSaida("norte"));
        assertEquals(List.<String>of("norte", "sul"), corredor.listarSaidas());

        corredor.removerSaida("norte");
        corredor.removerSaida("norte");
        assertNull(corredor.consultarSaida("norte"));
        assertSame(cozinha, corredor.consultarSaida("sul"));
        assertEquals(List.<String>of("sul"), corredor.listarSaidas());
    }

    @Test
    void listaDeSaidasNaoPermiteAlteracoesENaoRefleteMudancasPosteriores() {
        Ambiente corredor = new Ambiente("Corredor");
        Ambiente sala = new Ambiente("Sala");
        corredor.definirSaida("norte", sala);
        List<String> consulta = corredor.listarSaidas();

        assertThrows(UnsupportedOperationException.class, new Executable() {
            @Override
            public void execute() {
                consulta.remove("norte");
            }
        });
        assertSame(sala, corredor.consultarSaida("norte"));

        corredor.removerSaida("norte");
        corredor.definirSaida("leste", sala);
        assertEquals(List.<String>of("norte"), consulta);
        assertEquals(List.<String>of("leste"), corredor.listarSaidas());
    }

    @Test
    void rejeitaCadastroInvalidoSemMudarSaidaValida() {
        Ambiente corredor = new Ambiente("Corredor");
        Ambiente sala = new Ambiente("Sala");
        corredor.definirSaida("norte", sala);

        assertThrows(NullPointerException.class, new Executable() {
            @Override
            public void execute() {
                corredor.definirSaida("norte", null);
            }
        });
        assertThrows(NullPointerException.class, new Executable() {
            @Override
            public void execute() {
                corredor.definirSaida(null, sala);
            }
        });
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                corredor.definirSaida(" \t", sala);
            }
        });
        assertSame(sala, corredor.consultarSaida("norte"));
        assertEquals(List.<String>of("norte"), corredor.listarSaidas());
    }

    @Test
    void rejeitaDescricaoNulaAoCriarAmbiente() {
        assertThrows(NullPointerException.class, new Executable() {
            @Override
            public void execute() {
                new Ambiente(null);
            }
        });
    }
}
