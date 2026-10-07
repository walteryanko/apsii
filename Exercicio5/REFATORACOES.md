# Questão 8 - Justificativa das refatorações de Locadora

**Aluno:** Walter Yanko de Aragão Brandão  
**Disciplina:** Análise e Projeto de Sistemas II  
**Professor:** Raul Andrade

## Origem e problemas encontrados

Base: [raulandrade/apsii, pasta Locadora](https://github.com/raulandrade/apsii/tree/6caf14dc154dfbe360e17edef6d57b23813a6101/Locadora), commit `6caf14dc154dfbe360e17edef6d57b23813a6101`.

`Cliente.extrato()` concentrava apresentação, preços e pontos. Para aplicar as regras, acessava a categoria da fita e a duração do aluguel. Esses cálculos pertencem ao domínio de locação, enquanto o cliente deve agregar seus aluguéis e apresentar o extrato. A seleção por categoria estava distribuída entre um `switch` de preço e um `if` para os pontos de lançamento.

## 1. Extract Method e Move Method

Os cálculos foram extraídos do extrato e movidos para operações de domínio:

- [Aluguel.calcularValor()](Locadora/Aluguel.java) combina a duração com a fita e delega o cálculo.
- [Aluguel.calcularPontosDeAlugadorFrequente()](Locadora/Aluguel.java) delega a pontuação usando a duração.
- [Fita](Locadora/Fita.java) seleciona sua política de preço e expõe os resultados.

Antes, o cliente precisava interpretar o código de preço e conhecer todas as tarifas. Depois, utiliza:

```java
double valorAluguel = aluguel.calcularValor();
valorTotal += valorAluguel;
pontosDeAlugadorFrequente += aluguel.calcularPontosDeAlugadorFrequente();
```

Essa mudança reduz a inveja dos dados, localiza as regras junto das informações necessárias e permite testar um aluguel sem montar um extrato completo.

## 2. Replace Conditional with Polymorphism

[PoliticaDePreco](Locadora/PoliticaDePreco.java) é um enum com implementações específicas por constante. `NORMAL`, `LANCAMENTO` e `INFANTIL` implementam `calcularValor(int)`. A política de lançamento sobrescreve também `calcularPontos(int)`; as demais recebem a implementação comum de um ponto.

Antes, `Cliente.extrato()` selecionava a fórmula no `switch`. Depois, o despacho do método executa a implementação correspondente à política da fita:

```java
public double calcularValor(int diasAlugada) {
    return politicaDePreco.calcularValor(diasAlugada);
}
```

A conversão de código inteiro para política fica centralizada em `PoliticaDePreco.paraCodigo(int)`. Ela utiliza uma busca pelos códigos; os condicionais que selecionavam regras de preço e pontos foram removidos de `Cliente`.

O enum é adequado ao conjunto pequeno e fechado de categorias do exercício. Permite mudar a categoria da mesma fita atualizando sua política, sem trocar o objeto `Fita` ou invalidar seus aluguéis existentes. Para categorias extensíveis em tempo de execução, uma interface com estratégias independentes seria uma evolução possível.

## 3. Rename Method com preservação da API

Foi introduzido `setCodigoDePreco(int)`, seguindo o padrão camelCase. O método original `setcodigoDePreco(int)` foi mantido e delega ao novo método. Os construtores, constantes de preço e demais métodos públicos originais continuam disponíveis.

Isso melhora a nomenclatura e evita quebrar código que utiliza o nome antigo. Os testes verificam os dois nomes e confirmam que uma mudança de categoria atualiza preço e pontos.

## Ajustes complementares

`Cliente.extrato()` utiliza `StringBuilder` para montar as linhas e mantém a ordem de inclusão dos aluguéis. Os imports foram explicitados. Campos sem operação de alteração receberam `final`; a categoria da fita permanece mutável, como no projeto original.

O exemplo `Locadora.main()` conserva o mesmo cliente, filmes e durações. O total continua sendo **12,5**, com **4 pontos**. O formato textual do extrato, incluindo tabulações, quebras de linha e a ausência de uma quebra adicional no retorno de `extrato()`, foi preservado.

## Preservação do comportamento

A refatoração não introduz novos critérios de cobrança nem validações. Categorias desconhecidas continuam gerando valor zero e um ponto. Dias zero e negativos seguem os resultados antigos. A multiplicação de lançamento mantém o cálculo inteiro do original, inclusive seu comportamento de overflow para entradas extremas; corrigir esse limite seria uma mudança de regra ou de robustez separada.

As quatro fontes em `original/Locadora/` coincidem, byte a byte, com os blobs do professor:

| Arquivo original | SHA do blob Git |
| --- | --- |
| `Aluguel.java` | `12a19051a5a53d2fe321bbd11170a2356925326f` |
| `Cliente.java` | `48d33446423599a3083b2dba9bbe62483e319aa6` |
| `Fita.java` | `c2e584483759cc450eb09cef3730cacb78dbef2a` |
| `Locadora.java` | `1f23ef2640e3c8c14d666279ef486ac95c425083` |

## Testes e reprodução

Execute `bash Exercicio5/testar.sh` na raiz do repositório. No Prompt de Comando do Windows, use `Exercicio5\testar.bat` com o JDK no PATH.

[LocadoraTest.java](testes/LocadoraTest.java) compõe expectativas explícitas e uma comparação com as classes originais, carregadas separadamente:

| Grupo comparado com o original | Cenários |
| --- | ---: |
| Cliente sem aluguéis, incluindo nome vazio e nulo | 3 |
| Aluguel isolado: 6 códigos e 12 durações | 72 |
| Combinação das três categorias: 6 durações para cada uma | 216 |
| Troca de categoria em fita compartilhada por dois aluguéis | 216 |
| **Total de cenários de equivalência** | **507** |

Somando as expectativas explícitas, foram executadas **579 verificações**, todas aprovadas em OpenJDK 17 no Linux em 07/10/2026. A compilação com alvo Java 8 e todos os avisos tratados como erros também passou. Os testes preexistentes de figuras geométricas foram executados e passaram.

Os scripts de Windows não foram executados neste ambiente. A validação não abrange todos os projetos da pasta `aula02/`.
