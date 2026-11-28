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
        assertNull(entrada.consultarPorta("sala"));
    }

    @Test
    void cadastraMaisDeQuatroSaidasSemCriarRetornoAutomatico() {
        Ambiente corredor = new Ambiente("Corredor");
        Ambiente sala = new Ambiente("Sala");
        Porta porta = new Porta("porta-corredor-sala", corredor, sala);
        corredor.definirSaida("norte", porta);
        corredor.definirSaida("leste", porta);
        corredor.definirSaida("sul", porta);
        corredor.definirSaida("oeste", porta);
        corredor.definirSaida("varanda", porta);
        corredor.definirSaida("fundos", porta);

        assertEquals(List.<String>of("norte", "leste", "sul", "oeste", "varanda", "fundos"),
                corredor.listarSaidas());
        assertSame(sala, corredor.consultarSaida("varanda"));
        assertNull(sala.consultarSaida("varanda"));
        assertNull(sala.consultarPorta(corredor.getId()));
    }

    @Test
    void consultaAMesmaPortaNosDoisSentidosPeloIdDoDestino() {
        Ambiente tv = new Ambiente("sala-tv", "Sala de TV");
        Ambiente jantar = new Ambiente("sala-jantar", "Sala de Jantar");
        Porta porta = new Porta("porta-tv-jantar", tv, jantar);

        tv.definirSaida("norte", porta);
        jantar.definirSaida("sul", porta);

        assertSame(porta, tv.consultarPorta("sala-jantar"));
        assertSame(porta, jantar.consultarPorta("sala-tv"));
        assertSame(jantar, tv.consultarSaida("norte"));
        assertSame(tv, jantar.consultarSaida("sul"));
        assertNull(tv.consultarPorta("Sala de Jantar"));
        assertNull(tv.consultarPorta("norte"));
    }

    @Test
    void substituiERemoveSomenteASaidaIndicada() {
        Ambiente corredor = new Ambiente("Corredor");
        Ambiente sala = new Ambiente("Sala");
        Ambiente cozinha = new Ambiente("Cozinha");
        Ambiente quarto = new Ambiente("Quarto");
        Porta portaSala = new Porta("porta-corredor-sala", corredor, sala);
        Porta portaCozinha = new Porta("porta-corredor-cozinha", corredor, cozinha);
        Porta portaQuarto = new Porta("porta-corredor-quarto", corredor, quarto);
        corredor.definirSaida("norte", portaSala);
        corredor.definirSaida("sul", portaCozinha);
        assertSame(portaSala, corredor.consultarPorta(sala.getId()));

        corredor.definirSaida("norte", portaQuarto);
        assertSame(quarto, corredor.consultarSaida("norte"));
        assertSame(portaQuarto, corredor.consultarPorta(quarto.getId()));
        assertNull(corredor.consultarPorta(sala.getId()));
        assertEquals(List.<String>of("norte", "sul"), corredor.listarSaidas());

        corredor.removerSaida("norte");
        corredor.removerSaida("norte");
        assertNull(corredor.consultarSaida("norte"));
        assertNull(corredor.consultarPorta(quarto.getId()));
        assertSame(cozinha, corredor.consultarSaida("sul"));
        assertSame(portaCozinha, corredor.consultarPorta(cozinha.getId()));
        assertEquals(List.<String>of("sul"), corredor.listarSaidas());
    }

    @Test
    void listaDeSaidasNaoPermiteAlteracoesENaoRefleteMudancasPosteriores() {
        Ambiente corredor = new Ambiente("Corredor");
        Ambiente sala = new Ambiente("Sala");
        Porta porta = new Porta("porta-corredor-sala", corredor, sala);
        corredor.definirSaida("norte", porta);
        List<String> consulta = corredor.listarSaidas();

        assertThrows(UnsupportedOperationException.class, new Executable() {
            @Override
            public void execute() {
                consulta.remove("norte");
            }
        });
        assertSame(sala, corredor.consultarSaida("norte"));

        corredor.removerSaida("norte");
        corredor.definirSaida("leste", porta);
        assertEquals(List.<String>of("norte"), consulta);
        assertEquals(List.<String>of("leste"), corredor.listarSaidas());
    }

    @Test
    void rejeitaCadastroInvalidoSemMudarSaidaValida() {
        Ambiente corredor = new Ambiente("Corredor");
        Ambiente sala = new Ambiente("Sala");
        Ambiente cozinha = new Ambiente("Cozinha");
        Porta portaValida = new Porta("porta-corredor-sala", corredor, sala);
        Porta portaAlheia = new Porta("porta-sala-cozinha", sala, cozinha);
        corredor.definirSaida("norte", portaValida);

        assertThrows(NullPointerException.class, new Executable() {
            @Override
            public void execute() {
                corredor.definirSaida("norte", null);
            }
        });
        assertThrows(NullPointerException.class, new Executable() {
            @Override
            public void execute() {
                corredor.definirSaida(null, portaValida);
            }
        });
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                corredor.definirSaida(" \t", portaValida);
            }
        });
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                corredor.definirSaida("norte", portaAlheia);
            }
        });
        assertSame(sala, corredor.consultarSaida("norte"));
        assertSame(portaValida, corredor.consultarPorta(sala.getId()));
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
