package br.edu.ufla.ppoo.zuul;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * Mantém o cenário e executa os comandos já interpretados do World of Zuul.
 *
 * @author Michael Kölling and David J. Barnes (traduzido por Julio Cesar Alves)
 * @version 2011.07.31 (2016.02.01)
 */
public class Jogo {
    private final PrintStream saida;
    private final Jogador jogador;
    private final Random sorteio;
    private final List<Porta> portas = new ArrayList<Porta>();
    private ConfiguracaoGabarito configuracaoGabarito;
    private int tentativasRestantes;
    private EstadoPartida estadoPartida;

    /**
     * Cria o cenário e posiciona o jogador no ambiente inicial.
     * As mensagens são direcionadas à saída padrão atual.
     */
    public Jogo() {
        this(System.out);
    }

    /**
     * Cria uma sessão textual com a saída fornecida pelo ponto de entrada.
     *
     * @param saida destino das mensagens da sessão
     * @throws NullPointerException se a saída for nula
     */
    Jogo(PrintStream saida) {
        this(saida, new Random(), new SorteadorGabarito(), "gabarito.txt");
    }

    /**
     * Prepara uma sessão com os sorteios e o destino do gabarito informados.
     * @param saida destino das mensagens da sessão
     * @param sorteio gerador do estado das portas
     * @param sorteadorGabarito gerador da configuração inicial da partida
     * @param arquivoGabarito caminho em que o gabarito é gravado
     */
    Jogo(PrintStream saida, Random sorteio, SorteadorGabarito sorteadorGabarito, String arquivoGabarito) {
        this.saida = Objects.requireNonNull(saida, "A saída é obrigatória.");
        this.sorteio = Objects.requireNonNull(sorteio, "O sorteio de portas é obrigatório.");
        jogador = new Jogador(criarAmbientes(sorteadorGabarito, arquivoGabarito));
        tentativasRestantes = configuracaoGabarito.getTentativasIniciais();
        estadoPartida = EstadoPartida.PREPARADA;
    }

    private Ambiente criarAmbientes(SorteadorGabarito sorteadorGabarito, String arquivoGabarito) {
        Ambiente quarto1 = new Ambiente("quarto-1", "Quarto 1");
        Ambiente quarto2 = new Ambiente("quarto-2", "Quarto 2");
        Ambiente wc1 = new Ambiente("lavabo", "WC 1");
        Ambiente wc2 = new Ambiente("banheiro", "WC 2");
        Ambiente salaJantar = new Ambiente("sala-jantar", "Sala de Jantar");
        Ambiente cozinha = new Ambiente("cozinha", "Cozinha");
        Ambiente salaTv = new Ambiente("sala-tv", "Sala de TV");
        Ambiente areaServico = new Ambiente("area-servico", "Área de Serviço");
        Ambiente corredor = new Ambiente("corredor", "Corredor");

        Map<String, Ambiente> ambientes = new LinkedHashMap<String, Ambiente>();
        ambientes.put(quarto1.getNome(), quarto1);
        ambientes.put(quarto2.getNome(), quarto2);
        ambientes.put(wc1.getNome(), wc1);
        ambientes.put(wc2.getNome(), wc2);
        ambientes.put(salaJantar.getNome(), salaJantar);
        ambientes.put(cozinha.getNome(), cozinha);
        ambientes.put(salaTv.getNome(), salaTv);
        ambientes.put(areaServico.getNome(), areaServico);
        ambientes.put(corredor.getNome(), corredor);

        List<String> nomesAmbientes = new ArrayList<String>(ambientes.keySet());
        configuracaoGabarito = sorteadorGabarito.sortear(nomesAmbientes);
        try {
            new EscritorGabarito().gravar(arquivoGabarito, configuracaoGabarito);
        } catch (IOException erro) {
            throw new UncheckedIOException("Não foi possível gravar o arquivo de gabarito.", erro);
        }

        conectarAmbientes(salaTv, "norte", salaJantar, "sul", "porta-tv-jantar");
        conectarAmbientes(cozinha, "sul", areaServico, "norte", "porta-cozinha-servico");
        conectarAmbientes(corredor, "noroeste", quarto1, "leste", "porta-quarto1-corredor");
        conectarAmbientes(corredor, "nordeste", quarto2, "oeste", "porta-quarto2-corredor");
        conectarAmbientes(corredor, "oeste", wc1, "leste", "porta-lavabo-corredor");
        conectarAmbientes(corredor, "leste", wc2, "oeste", "porta-banheiro-corredor");
        conectarAmbientes(corredor, "sudoeste", salaJantar, "leste", "porta-jantar-corredor");
        conectarAmbientes(corredor, "sudeste", cozinha, "oeste", "porta-cozinha-corredor");

        Ambiente ambienteTesouro = ambientes.get(configuracaoGabarito.getAmbienteTesouro());
        if (ambienteTesouro == null) {
            throw new IllegalStateException("O gabarito indica um ambiente de tesouro inexistente.");
        }
        ambienteTesouro.definirTesouroEnterrado(true);

        Ambiente ambienteChave = ambientes.get(configuracaoGabarito.getAmbienteChaveMestra());
        if (ambienteChave == null) {
            throw new IllegalStateException("O gabarito indica um ambiente de chave inexistente.");
        }
        ambienteChave.adicionarItem(new ChaveMestra(configuracaoGabarito.getDurabilidadeChaveMestra()));

        Ambiente ambienteDica1 = ambientes.get(configuracaoGabarito.getAmbienteDica1());
        if (ambienteDica1 == null) {
            throw new IllegalStateException("O gabarito indica um ambiente para a primeira dica inexistente.");
        }
        ambienteDica1.adicionarItem(new Dica("O tesouro não está no(a) "
                + configuracaoGabarito.getAmbienteNegadoDica1() + "."));

        Ambiente ambienteDica2 = ambientes.get(configuracaoGabarito.getAmbienteDica2());
        if (ambienteDica2 == null) {
            throw new IllegalStateException("O gabarito indica um ambiente para a segunda dica inexistente.");
        }
        ambienteDica2.adicionarItem(new Dica("O tesouro não está no(a) "
                + configuracaoGabarito.getAmbienteNegadoDica2() + "."));

        return salaTv;
    }

    /**
     * Apresenta a mensagem de boas-vindas e o ambiente inicial.
     */
    void iniciar() {
        estadoPartida = EstadoPartida.EM_ANDAMENTO;
        saida.println();
        saida.println("Bem-vindo à casa mal-assombrada!");
        saida.println("Você é um caçador de tesouros procurando por uma fortuna enterrada.");
        saida.println("Você possui " + tentativasRestantes + " tentativas.");
        saida.println("Digite '" + Comando.AJUDA + "' se voce precisar de ajuda.");
        saida.println();
        imprimirAmbienteAtual();
    }

    private void imprimirAmbienteAtual() {
        Ambiente ambienteAtual = jogador.getAmbienteAtual();
        saida.println("Voce esta em " + ambienteAtual.getDescricao());
        saida.println("Atualmente voce possui " + tentativasRestantes + " tentativas restantes.");
        saida.println("Saidas: " + String.join(" ", ambienteAtual.listarSaidas()));
    }

    /**
     * Executa um comando do jogo já interpretado. A apresentação da ajuda
     * cabe ao ponto de entrada.
     *
     * @param comando ação recebida do analisador
     * @return {@code true} se o comando encerra a sessão ou se a partida já terminou
     */
    boolean processarComando(Comando comando) {
        if (partidaTerminou()) {
            return true;
        }
        String palavra = comando.getPalavraDeComando();

        if (Comando.SAIR.equals(palavra)) {
            return sair(comando);
        }
        if (Comando.CAVAR.equals(palavra)) {
            cavar(comando);
            return partidaTerminou();
        }
        if (Comando.CHAVE.equals(palavra)) {
            irParaAmbiente(comando, true);
            return partidaTerminou();
        }
        if (Comando.IR.equals(palavra)) {
            irParaAmbiente(comando, false);
            return partidaTerminou();
        }

        saida.println("Eu nao entendi o que voce disse...");
        return false;
    }

    private void irParaAmbiente(Comando comando, boolean usarChave) {
        if (!comando.temArgumento()) {
            saida.println(usarChave ? "Usar a chave para onde?" : "Ir pra onde?");
            return;
        }

        String direcao = comando.getArgumento();
        Ambiente proximoAmbiente = jogador.getAmbienteAtual().consultarSaida(direcao);
        if (proximoAmbiente == null) {
            saida.println("Nao ha passagem!");
            return;
        }

        Ambiente ambienteAtual = jogador.getAmbienteAtual();
        Porta porta = ambienteAtual.consultarPorta(proximoAmbiente.getId());
        if (porta == null) {
            saida.println("A passagem não possui uma porta.");
            return;
        }

        boolean abriu;
        if (usarChave) {
            ChaveMestra chave = jogador.getChave();
            if (chave == null || !chave.funcionando()) {
                saida.println("Você não possui uma chave mestra utilizável.");
                return;
            }
            jogador.usarChave();
            abriu = true;
            saida.println("Você usou a chave mestra. Durabilidade restante: "
                    + chave.getDurabilidade() + ".");
        } else {
            abriu = porta.abreNormalmente();
            tentativasRestantes--;
        }
        sortearTodasAsPortas();

        if (abriu) {
            List<ItemEncontravel> itensEncontrados = jogador.moverPara(proximoAmbiente);
            imprimirItensEncontrados(itensEncontrados);
            imprimirAmbienteAtual();
        } else {
            saida.println("A porta está emperrada. Não foi possível abrir. " + tentativasRestantes + " tentativas restantes.");
        }

        if (tentativasRestantes == 0) {
            estadoPartida = EstadoPartida.DERROTA;
            saida.println("Você não possui tentativas restantes. Game over.");
        }
    }

    private void imprimirItensEncontrados(List<ItemEncontravel> itensEncontrados) {
        for (ItemEncontravel item : itensEncontrados) {
            if (item instanceof Dica) {
                saida.println("Voce encontrou uma dica: " + item.getDescricao());
            } else if (item instanceof ChaveMestra) {
                ChaveMestra chave = (ChaveMestra) item;
                saida.println("Voce encontrou uma chave mestra com durabilidade "
                        + chave.getDurabilidade() + ".");
            }
        }
    }

    private void conectarAmbientes(Ambiente ambienteA, String direcaoA,
                                          Ambiente ambienteB, String direcaoB, String idPorta) {
        ambienteA.definirSaida(direcaoA, ambienteB);
        ambienteB.definirSaida(direcaoB, ambienteA);

        Porta porta = new Porta(idPorta, ambienteA, ambienteB);
        
        portas.add(porta);
        porta.sortearEstado(sorteio);

        ambienteA.definirPorta(ambienteB.getId(), porta);
        ambienteB.definirPorta(ambienteA.getId(), porta);
    }

    private void sortearTodasAsPortas() {
        for (Porta porta : portas) {
            porta.sortearEstado(sorteio);
        }
    }

    /** 
     * "Sair" foi digitado. Verifica o resto do comando pra ver
     * se nos queremos realmente sair do jogo.
     * @return true, se este comando sai do jogo, false, caso contrario
     */
    private boolean sair(Comando comando) 
    {
        if(comando.temArgumento()) {
            saida.println("Sair o que?");
            return false;
        }
        else {
            return true;  // sinaliza que nos queremos sair
        }
    }

    private void cavar(Comando comando) {
        if(!comando.temArgumento()) {
            if(jogador.consumirCarga()) {
                estadoPartida = EstadoPartida.VITORIA;
                saida.println("Você encontrou o tesouro. Parabéns!");
            } else {
                estadoPartida = EstadoPartida.DERROTA;
                saida.println("Você cavou e não encontrou nada. Game Over.");
                }
        } else {
            saida.println("Cavar o que?"); 
            }
    }

    private boolean partidaTerminou() {
        return estadoPartida == EstadoPartida.VITORIA || estadoPartida == EstadoPartida.DERROTA;
    }
}
