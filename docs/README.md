# Trabalho pratico de PPOO

## Objetivo desta organizacao

Preparar um projeto Java compilavel para as correcoes das bases fornecidas.
As funcionalidades do jogo e a integracao entre as bases serao etapas posteriores.
As regras e os problemas existentes nas bases ainda nao foram corrigidos.

## Requisitos de desenvolvimento

- JDK 21.
- Apache Maven 3.9 ou superior.
- Arquivos de texto e fontes Java em UTF-8.

O Maven precisa de `JAVA_HOME` apontando para a pasta do JDK, sem o sufixo `bin`.
Se a instalacao local estiver com essa variavel incorreta, no PowerShell e possivel
ajusta-la apenas para a sessao atual a partir do `javac` disponivel no PATH:

```powershell
$env:JAVA_HOME = Split-Path -Parent (Split-Path -Parent (Get-Command javac).Source)
```

## Estrutura

```text
pom.xml
src/
  main/
    java/br/edu/ufla/ppoo/
      zuul/                # Base textual do World of Zuul
      gui/                 # Base de simulacao grafica
    resources/
      Imagens/             # Recursos usados pelas classes atuais
  test/
    java/                  # Espaco para testes futuros
    resources/             # Espaco para dados de testes futuros
docs/
  enunciado/               # PDF original e transcricao em Markdown
  diagramas/               # Diagrama de classes recebido
  bases-originais/         # Copias intactas dos arquivos fornecidos
```

Os diretorios de testes ainda estao vazios. Nao foi adicionada biblioteca de testes.
As bases originais em `docs` sao referencias e nao participam da compilacao Maven.

## Compilar e verificar

Na raiz do projeto:

```shell
mvn clean verify
```

Esse comando compila as duas bases e empacota as classes e as imagens em `target`.
Ainda nao existem testes automatizados permanentes; o sucesso desse comando nao
significa que as falhas funcionais das bases foram corrigidas.

## Executar as bases separadamente

Depois de compilar, executar a base textual:

```shell
java -cp target/classes br.edu.ufla.ppoo.zuul.Programa
```

Executar a demonstracao grafica:

```shell
java -cp target/classes br.edu.ufla.ppoo.gui.Principal
```

A demonstracao grafica percorre automaticamente combinacoes de ambientes e ainda
contem as falhas da base fornecida. Ela nao e a interface final do trabalho.

O JAR gerado ainda nao possui uma classe principal unica configurada, pois as
duas aplicacoes ainda serao integradas.

## Alteracoes estruturais realizadas

- Fontes transferidos para o layout padrao do Maven.
- Pacotes `br.edu.ufla.ppoo.zuul` e `br.edu.ufla.ppoo.gui` declarados nos fontes ativos.
- Imagens transferidas para `src/main/resources/Imagens`, preservando os caminhos
  absolutos de classpath usados atualmente pelo codigo.
- Enunciado e diagrama reunidos em `docs`.
- Copias originais preservadas sem alteracao de conteudo em `docs/bases-originais`.
- Configuracao de compilacao para Java 21 e UTF-8.

Nenhuma regra de jogo, algoritmo ou contrato de metodo foi alterado nesta etapa.
Os pacotes atuais refletem as duas bases de origem; a divisao final de
responsabilidades sera definida durante as correcoes e a integracao.

## Verificacao desta etapa

- `mvn clean verify` concluido com sucesso usando JDK 21.
- Os 16 fontes Java ativos foram compilados.
- As duas imagens foram carregadas pelo classpath e incluidas no JAR.
- Os dois pontos de entrada foram encontrados nos novos pacotes.
- A base textual foi iniciada e encerrada com o comando `sair`.
- Os 18 arquivos das bases originais e os tres documentos movidos foram
  conferidos por SHA-256 durante a organizacao.
- Os fontes ativos foram comparados com os originais: somente a declaracao de
  pacote e sua separacao por linhas foram acrescentadas.

A janela grafica nao foi aberta nesta verificacao. A animacao e as regras
funcionais ainda precisam ser verificadas durante a etapa de correcoes.

## Cuidados para as proximas etapas

- Trabalhar nos fontes de `src/main/java`, mantendo as referencias originais intactas.
- Seguir o enunciado: nao utilizar inferencia de tipo nem expressoes lambda nas
  alteracoes. As construcoes ja presentes na base ainda precisam ser revisadas.
- Manter os creditos dos autores e atualizar o Javadoc ao corrigir as classes.
- Preservar a continuidade com as duas implementacoes fornecidas.
- Atualizar o UML de acordo com o codigo implementado.
- Revisar o formato de entrega do enunciado ao final: a estrutura de desenvolvimento
  nao substitui a pasta de entrega exigida pelo professor.
