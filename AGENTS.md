# Correções do projeto PPOO

O objetivo desta etapa é corrigir as bases Java em `src/main/java`. O enunciado em `docs/enunciado/Trabalho Pratico.pdf` define os requisitos. Use o SVG em `docs/diagrama-uml-corrigido-v6.txt` como referência da arquitetura final. As bases em `docs/bases-originais` servem para comparação. A extensão das regras e a integração final são etapas do grupo.

Em cada alteração, revise coesão, acoplamento, encapsulamento, inicialização dos campos, invariantes e uso de herança. Dê a cada classe uma responsabilidade clara. Use herança quando houver uma relação de subtipo e um contrato que a subclasse possa cumprir. Inicialize campos obrigatórios na construção e use `final` para estado que não muda. Documente os contratos públicos com Javadoc e preserve os créditos do código base. Use tipos explícitos, inclusive nas construções genéricas, e evite expressões lambda conforme o enunciado. Atualize os consumidores ao mudar contratos e verifique cada passo antes de avançar.

## Sequência de trabalho

1. Corrigir `Posicao` e verificar igualdade em `HashSet` e `HashMap`.
2. Corrigir os contratos do A* e o tratamento de percurso vazio.
3. Encapsular as saídas de `Ambiente` e ajustar seus usos em `Jogo`.
4. Separar a interpretação dos comandos da leitura do terminal.
5. Eliminar a duplicação da ajuda e da descrição do ambiente.
6. Separar a geometria do mapa, os recursos gráficos e a posição visual.
7. Corrigir a pintura e a animação no Swing.
8. Ajustar os pontos de entrada e documentar os contratos para o grupo.
