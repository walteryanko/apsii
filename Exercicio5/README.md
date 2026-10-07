# Exercício 05 - Análise e Projeto de Sistemas II

**Aluno:** Walter Yanko de Aragão Brandão  
**Instituição:** Centro Universitário de João Pessoa - UNIPÊ  
**Curso:** Análise e Desenvolvimento de Sistemas  
**Professor:** Raul Andrade  
**Semestre:** 2026.2

Entrega das questões 1 a 8: respostas teóricas, análise do projeto Locadora, código refatorado e testes de regressão.

| Material | Localização |
| --- | --- |
| Respostas das questões 1 a 8 | Este documento |
| Código refatorado da questão 8 | [Locadora/](Locadora/) |
| Justificativas e comparação antes/depois | [REFATORACOES.md](REFATORACOES.md) |
| Código original do professor, para comparação | [original/Locadora/](original/Locadora/) |
| Testes automatizados | [testes/LocadoraTest.java](testes/LocadoraTest.java) |

Os trechos de código das questões teóricas ilustram as propostas de refatoração; o projeto executável entregue está na questão 8.

## Questão 1 - Classe SistemaAcademico com muitas responsabilidades

O principal smell é **Large Class**, pois a classe reúne mais de 3.000 linhas e responsabilidades distintas. Ela também apresenta características de **God Class**: concentra cadastro, avaliação acadêmica, relatórios, comunicação e pagamentos. Essas funções têm motivos diferentes para mudar, indicando baixa coesão e violação do princípio da responsabilidade única. Por exemplo, uma alteração no provedor de e-mail não deveria exigir modificar a mesma classe responsável pelo cálculo de médias.

**Long Method** também pode existir, mas é necessário examinar os métodos: a quantidade de linhas da classe, isoladamente, não prova que seus métodos sejam longos. A centralização pode ainda provocar alterações frequentes na mesma classe por motivos diferentes, característica de **Divergent Change**.

Uma refatoração adequada combina **Extract Method**, para separar trechos com uma finalidade clara, **Extract Class**, para criar classes coesas, e **Move Method/Move Field**, para deslocar os comportamentos e dados às classes responsáveis. Uma possível divisão é:

| Responsabilidade | Classe proposta |
| --- | --- |
| Cadastrar e manter alunos | `CadastroDeAlunos` |
| Calcular médias e resultados | `CalculadoraDeMedias` |
| Montar relatórios acadêmicos | `RelatorioAcademico` |
| Enviar mensagens por e-mail | `NotificadorPorEmail` |
| Executar pagamentos | `ProcessadorDePagamentos` |

`SistemaAcademico` pode permanecer como uma fachada de coordenação, delegando a essas classes. Não é necessário transformar o sistema em microsserviços para resolver o problema: a separação pode ocorrer dentro da mesma aplicação.

A mudança deve ser incremental, com testes de caracterização antes da extração e testes após cada passo. Os benefícios são regras mais fáceis de localizar, testes menores, menor conflito entre desenvolvedores e menor risco de uma alteração em pagamentos afetar o cadastro ou o cálculo de médias. A separação deve seguir responsabilidades reais, evitando criar classes sem uma finalidade definida.

## Questão 2 - Regras de frete duplicadas

Manter a mesma regra em carrinho, cálculo do preço final e relatórios aumenta o custo de manutenção. Uma mudança de tarifa exige localizar e corrigir três implementações. Se apenas uma for atualizada, o cliente pode visualizar um frete no carrinho e receber outro valor no fechamento. Correções de bugs também precisam ser repetidas, e testes semelhantes acabam distribuídos por diferentes módulos. O problema viola o princípio **DRY**, pois um mesmo conhecimento de negócio possui várias representações independentes.

O plano de refatoração é:

1. Mapear as três implementações e confirmar se tratam a mesma regra, incluindo exceções, descontos, arredondamento e entrada de dados.
2. Criar testes com resultados atuais, especialmente nos limites de peso, distância e frete grátis.
3. Aplicar **Extract Method** para isolar o cálculo e **Extract Class/Move Method** para centralizá-lo em `CalculadoraDeFrete`.
4. Definir uma entrada clara, como `DadosFrete`, com os dados necessários. A calculadora não deve depender diretamente das classes de tela ou relatório.
5. Fazer os três módulos delegarem à mesma calculadora e remover as cópias antigas após a verificação.

Se houver regras diferentes por transportadora, uma interface `PoliticaDeFrete` com implementações específicas pode organizar essa variação. Não é necessário introduzir várias estratégias quando há apenas uma regra simples.

O resultado é uma única fonte para o cálculo: a próxima alteração de tarifa ocorre em um lugar e é validada por uma suíte comum de testes. Apenas extrair um método em cada módulo manteria a duplicação entre eles e, portanto, não resolveria o problema completo.

## Questão 3 - Oito parâmetros e vários booleanos

A assinatura apresenta **Long Parameter List**. Os parâmetros `mes` e `ano` formam um agrupamento natural, ou **Data Clump**. Strings livres para formato e opções de domínio também podem indicar **Primitive Obsession**. Os booleanos prejudicam a leitura, porque uma chamada terminada em `true, false, true` não informa claramente quais comportamentos serão ativados.

Além disso, parâmetros do mesmo tipo podem ser trocados sem erro de compilação. Acrescentar uma opção exige alterar a assinatura e os chamadores. Vários flags também podem produzir combinações difíceis de interpretar e testar.

A principal solução é **Introduce Parameter Object**, criando uma `SolicitacaoRelatorio` imutável. O período pode ser representado por `YearMonth`, que une mês e ano e impede meses inválidos. Formato e opções podem usar enums, como `FormatoRelatorio`, `NivelDetalhamento`, `TratamentoImpostos` e `OrdenacaoRelatorio`. Um builder facilita a criação com nomes explícitos:

```java
// Exemplo conceitual, após a implementação dos tipos e do builder.
SolicitacaoRelatorio solicitacao = SolicitacaoRelatorio.builder()
        .periodo(YearMonth.of(2026, 10))
        .usuario(usuario)
        .departamento(departamento)
        .formato(FormatoRelatorio.PDF)
        .detalhamento(NivelDetalhamento.DETALHADO)
        .impostos(TratamentoImpostos.INCLUIR)
        .ordenacao(OrdenacaoRelatorio.DATA)
        .build();

servicoRelatorio.gerarRelatorioFinanceiro(solicitacao);
```

O builder deve validar os campos obrigatórios e combinações incompatíveis, em vez de apenas deslocar oito parâmetros para um construtor igualmente confuso. Para a opção original `ordenarPorData = false`, deve existir uma opção que preserve a ordem usada anteriormente, sem inventar outra ordenação.

Quando um booleano escolhe operações realmente diferentes, **Remove Flag Argument/Replace Parameter with Explicit Methods** é outra alternativa, por exemplo `gerarRelatorioResumido` e `gerarRelatorioDetalhado`. Isso não exige criar um método para cada combinação possível. A escolha deve refletir a API de negócio.

Essas mudanças deixam a intenção da chamada visível, centralizam a validação e tornam novas opções mais fáceis de acrescentar. Não eliminam a necessidade de testar as combinações relevantes, mas permitem descrevê-las com mais clareza.

## Questão 4 - Condicionais aninhadas no cálculo de bônus

O trecho exige acompanhar três níveis de condição para reconhecer uma única regra: o bônus especial é aplicável ao gerente com mais de cinco anos de serviço no departamento financeiro. Isso aumenta a carga de leitura e dificulta localizar o ponto de alteração da regra.

Uma primeira solução é **Consolidate Conditional Expression**, reunindo as condições com `&&`, e **Extract Method/Decompose Conditional**, dando um nome à regra:

```java
private boolean elegivelParaBonusEspecial(Funcionario funcionario) {
    return funcionario.getCargo().equals("Gerente")
            && funcionario.getTempoDeServico() > 5
            && funcionario.getDepartamento().equals("Financeiro");
}

private void aplicarBonusEspecialSeElegivel(Funcionario funcionario) {
    if (!elegivelParaBonusEspecial(funcionario)) {
        return;
    }
    calcularEAplicarBonusEspecial(funcionario);
}
```

O segundo método emprega **Replace Nested Conditional with Guard Clauses**: encerra o caso não elegível e mantém o cálculo no fluxo principal. O retorno antecipado deve ficar no método extraído que cuida do bônus. Se o método original executar outras operações da folha, elas devem continuar sendo executadas normalmente; inserir um `return` no método inteiro poderia alterar o comportamento.

O limite permanece `> 5`, portanto um funcionário com exatamente cinco anos não recebe esse bônus. Também é importante preservar a avaliação em curto-circuito e verificar os casos elegível e não elegível, inclusive cada condição falsa e o limite de cinco anos.

Enums para cargo e departamento podem reduzir erros de digitação em uma evolução posterior. Se surgirem várias políticas de bônus, **Replace Conditional with Polymorphism** ou Strategy pode distribuí-las por implementações específicas. Para uma regra isolada, um predicado bem nomeado e a cláusula de guarda já oferecem uma solução simples e clara.

## Questão 5 - Nomes genéricos de métodos e variáveis

Uma funcionalidade produzir o resultado correto não significa que seja fácil de compreender e modificar. Nomes como `doIt()` e `process()` escondem a intenção da operação. Variáveis como `a`, `x1` e `temp`, sem um contexto que explique seu significado, obrigam o leitor a rastrear o código para descobrir o que representam.

A clareza semântica influencia diretamente a manutenção: o desenvolvedor precisa reconhecer o objetivo de uma operação para decidir onde modificar uma regra e como evitar efeitos colaterais. Ela também reduz a curva de aprendizado de novos integrantes, facilita revisões e permite que a equipe converse usando os mesmos termos do domínio.

As técnicas apropriadas são **Rename Method/Rename Function** e **Rename Variable**. Por exemplo, se `process()` calcula a soma dos itens, o nome `calcularValorTotalDoPedido()` informa seu propósito. Se `temp` armazena o total sem impostos, `subtotalSemImpostos` expressa tanto o conteúdo quanto sua condição. Os nomes precisam corresponder ao comportamento real, não apenas ser mais longos.

Os chamadores devem ser atualizados com o apoio das ferramentas da IDE, e os testes devem confirmar que a mudança preservou o comportamento. Em uma API pública, pode ser necessário manter temporariamente um método delegador com o nome antigo para evitar quebrar consumidores.

Nomes curtos não são sempre inadequados: `i` pode ser claro em um laço pequeno, e `x` pode representar uma coordenada. O problema é a ausência de significado no contexto. Refatorar a nomenclatura diminui o esforço de entendimento e o risco de futuras mudanças incorretas, mesmo quando a implementação atual já funciona.

## Questão 6 - Valor total duplicado em Pedido, CarrinhoDeCompras e RelatorioFinanceiro

### a) Por que a duplicação compromete a manutenção

O mesmo conhecimento de negócio está espalhado por três classes. Uma mudança de desconto, imposto ou arredondamento precisa ser replicada em todas elas. A equipe pode corrigir apenas uma cópia e deixar os totais inconsistentes. A duplicação também dificulta reconhecer qual implementação representa a regra oficial, amplia o trabalho de teste e aumenta o risco de regressões.

### b) Refatorações indicadas e justificativa

**Extract Method** isola o cálculo, mas não basta extrair três métodos idênticos. É necessário combinar essa técnica com **Move Method**, colocando a regra em uma única responsabilidade do domínio, ou **Extract Class**, quando a regra justifica uma classe `CalculadoraTotalPedido`.

Uma organização possível é a calculadora receber os itens e as condições necessárias, sem depender do relatório. `Pedido` expõe `calcularValorTotal()` delegando à regra central. `CarrinhoDeCompras` utiliza a mesma regra para seus itens. Quando o relatório resume pedidos já existentes, ele consulta o valor calculado pelo domínio em vez de reimplementar a fórmula.

Essa organização centraliza a manutenção e respeita a responsabilidade de cada classe: o relatório apresenta dados, enquanto o domínio determina os valores. Se um total histórico precisar permanecer fixo após o fechamento do pedido, o relatório deve usar o valor registrado nessa ocasião, evitando recalculá-lo com regras atuais; esse requisito deve ser confirmado antes da migração.

**Pull Up Method/Extract Superclass** só seria adequado se as classes compartilhassem uma relação de herança legítima. Não faz sentido criar uma superclasse artificial para unir pedido, carrinho e relatório apenas porque há código igual. A composição e a delegação são mais coerentes nesse cenário.

Antes de remover as cópias, devem ser criados testes para pedidos vazios, múltiplos itens e condições de desconto ou imposto existentes. Assim, a reorganização preserva a regra já praticada pelo sistema.

## Questão 7 - Inveja dos dados em RelatorioFinanceiro

### a) Riscos da implementação

**Feature Envy**, ou inveja dos dados, ocorre porque o método de `RelatorioFinanceiro` depende intensamente de informações de `Funcionario` para calcular bônus e descontos. A regra está distante dos dados que utiliza, aumentando o acoplamento e prejudicando a coesão. Alterações nos atributos ou critérios do funcionário podem exigir modificar relatórios, e outros módulos podem acabar copiando o mesmo cálculo.

O uso frequente de getters, isoladamente, não é um erro. O problema é o relatório assumir decisões de negócio sobre remuneração que deveriam pertencer ao domínio. Isso dificulta testar as regras independentemente da apresentação e pode incentivar a exposição de detalhes internos de `Funcionario`.

### b) Refatorações adequadas e benefícios

A principal técnica é **Move Method**: mover os cálculos de bônus e descontos para `Funcionario` quando essas regras forem responsabilidades naturais dessa entidade. O relatório passa a solicitar resultados por métodos de intenção clara, como `calcularBonus()` e `calcularDescontos()`, preservando o encapsulamento e evitando conhecer todos os critérios internos.

Se remuneração envolver várias políticas ou deixar `Funcionario` excessivamente complexo, **Extract Class** pode criar `CalculadoraRemuneracao` ou `PoliticaRemuneracao`. Ela recebe as informações necessárias e reúne as regras. Nesse caso, a entidade e o relatório delegam à responsabilidade extraída, sem devolver a lógica ao relatório.

A escolha deve considerar a coesão: mover uma regra para perto de seus dados não significa concentrar todas as regras possíveis em `Funcionario`. O resultado esperado é uma única implementação de cada cálculo, testes de domínio mais simples, menor dependência entre apresentação e regras e mudanças localizadas quando a política de remuneração evoluir.

## Questão 8 - Projeto Locadora refatorado

### a) Análise e refatorações realizadas

O projeto de origem é [raulandrade/apsii - Locadora](https://github.com/raulandrade/apsii/tree/6caf14dc154dfbe360e17edef6d57b23813a6101/Locadora). As quatro fontes originais estão preservadas em [original/Locadora/](original/Locadora/), permitindo comparar o comportamento antes e depois.

O método original `Cliente.extrato()` monta o texto, calcula preços de todas as categorias e calcula pontos de fidelidade. As regras acessam repetidamente `aluguel.getFita().getCodigoDePreco()` e `aluguel.getDiasAlugada()`. Isso concentra responsabilidades no relatório do cliente e revela inveja dos dados. Há ainda um `switch` para preços e uma condição separada para pontos de lançamento.

Foram realizadas as seguintes refatorações:

| Técnica | Alteração concreta | Justificativa |
| --- | --- | --- |
| **Extract Method** | Extração do cálculo de valor e de pontos para operações nomeadas | Torna as regras testáveis e explicita a intenção de cada chamada. |
| **Move Method** | `Aluguel.calcularValor()` e `Aluguel.calcularPontosDeAlugadorFrequente()` recebem a responsabilidade antes concentrada em `Cliente` e delegam à fita | O aluguel conhece a duração e a fita; o cliente deixa de decidir preços e pontos. |
| **Replace Conditional with Polymorphism** | `PoliticaDePreco` usa constantes de enum com implementações próprias para cada categoria | Reúne as regras de cada categoria e elimina a seleção de regras em `Cliente.extrato()`. |
| **Rename Method**, com compatibilidade | Introdução de `Fita.setCodigoDePreco()` e manutenção de `setcodigoDePreco()` como delegador | Melhora o nome sem quebrar chamadas do projeto original. |

Além dessas refatorações, o extrato utiliza `StringBuilder` e imports explícitos. Os textos, a ordem das linhas, os valores e a pontuação foram preservados. Não foram acrescentadas novas validações de entrada que alterassem as respostas do código original.

As regras mantidas são:

| Categoria | Valor do aluguel | Pontos |
| --- | --- | --- |
| Normal | 2,00; acima de 2 dias, acrescenta 1,50 por dia excedente | 1 |
| Lançamento | 3,00 por dia | 2 quando há mais de 1 dia; caso contrário, 1 |
| Infantil | 1,50; acima de 3 dias, acrescenta 1,50 por dia excedente | 1 |

O original aceita códigos desconhecidos e dias zero ou negativos. Esses resultados foram mantidos e comparados nos testes. A política interna `DESCONHECIDA` preserva o valor zero e um ponto para códigos sem categoria reconhecida. Essas entradas são verificações de compatibilidade; não representam uma recomendação de regra comercial.

Para acrescentar uma categoria válida, é preciso definir seu código público e sua implementação em `PoliticaDePreco`. O extrato não precisa ganhar outro `switch`. Como as categorias são fixas no exemplo, o enum mantém a solução pequena e evita uma hierarquia extensa de classes.

### b) Publicação no GitHub

**Endereço da atividade:** [github.com/walteryanko/apsii/tree/main/Exercicio5](https://github.com/walteryanko/apsii/tree/main/Exercicio5).

O código refatorado está em [Locadora/](Locadora/) e as justificativas detalhadas estão em [REFATORACOES.md](REFATORACOES.md).

## Executar e testar

**Requisito:** JDK 8 ou superior disponível no terminal. Não são necessárias bibliotecas externas, Maven ou banco de dados. As classes originais e refatoradas têm os mesmos nomes e devem ser compiladas em diretórios separados.

No Linux/macOS, a partir da raiz do repositório:

```sh
bash Exercicio5/executar.sh
bash Exercicio5/testar.sh
```

No Windows, no Prompt de Comando, a partir da raiz:

```bat
Exercicio5\executar.bat
Exercicio5\testar.bat
```

No PowerShell, use `& .\Exercicio5\executar.bat` e `& .\Exercicio5\testar.bat`.

Compilação manual, dentro de `Exercicio5/`:

```sh
javac -encoding UTF-8 -d out/original original/Locadora/*.java
javac -encoding UTF-8 -d out/refatorado Locadora/*.java testes/LocadoraTest.java
java -cp out/refatorado Locadora
java -cp out/refatorado LocadoraTest out/original
```

O exemplo imprime o mesmo resultado da versão original:

```text
Registro de Alugueis de Raul
    Matrix    3.5
    Toy Story    3.0
    Oppenheimer    6.0
Valor total devido: 12.5
Você ganhou 4 pontos de alugador frequente
```

Os espaços acima representam as tabulações emitidas pelo programa.

Os testes comparam o extrato completo com as classes originais carregadas em um classloader separado. A regra original não foi reimplementada dentro do comparador. Também existem expectativas explícitas para preços, limites de dias, pontos, cliente sem aluguéis e troca de categoria.

Validação realizada em 07/10/2026 com OpenJDK 17 em Linux:

```text
OK: 579 verificações passaram.
Equivalência com código original: 507 cenários passaram.
```

As fontes refatoradas e os testes também foram compilados com `--release 8 -Xlint:all -Werror`, sem avisos. Isso verifica a compatibilidade de compilação com Java 8; a execução local foi feita em Java 17. Os scripts de Windows foram preparados para o JDK, mas não foram executados em Windows nesta validação. O workflow existente do repositório continua verificando o Exercício 4; os testes do Exercício 5 são executados pelos comandos acima.

## Referências

O código inicial de Locadora é material do professor Raul Andrade. Esta entrega preserva a atribuição da origem e não acrescenta uma licença ao material original.

- [Projeto original de Locadora](https://github.com/raulandrade/apsii/tree/6caf14dc154dfbe360e17edef6d57b23813a6101/Locadora).
- Martin Fowler. [Extract Class](https://refactoring.com/catalog/extractClass.html).
- Martin Fowler. [Move Function / Move Method](https://refactoring.com/catalog/moveFunction.html).
- Martin Fowler. [Introduce Parameter Object](https://refactoring.com/catalog/introduceParameterObject.html).
- Martin Fowler. [Replace Nested Conditional with Guard Clauses](https://refactoring.com/catalog/replaceNestedConditionalWithGuardClauses.html).
- Martin Fowler. [Replace Conditional with Polymorphism](https://refactoring.com/catalog/replaceConditionalWithPolymorphism.html).
