# APS II — Java e orientação a objetos

**Repositório de estudos de Análise e Projeto de Sistemas II, no contexto do curso de ADS do UNIPÊ.**

Este espaço reúne exercícios e materiais de prática com Java, orientação a objetos e organização de projetos. É um registro de aprendizagem, não uma biblioteca pronta para produção.

## Por onde começar

O exemplo mais direto para explorar neste repositório é o **gerenciador de figuras geométricas**, em [`Exercicio4/`](Exercicio4/). Ele usa uma interface comum para trabalhar com triângulos e losangos, calcular a soma das áreas e identificar a maior área cadastrada.

| Caminho | Conteúdo | Como interpretar |
| --- | --- | --- |
| [`Exercicio4/`](Exercicio4/) | Interface, implementações de figuras e programa de demonstração | Exemplo de polimorfismo que pode ser compilado isoladamente. |
| [`aula02/`](aula02/) | Exercícios e estruturas de projetos de aula | Material acadêmico em diferentes estágios; não constitui um build único. |

## Exemplo: gerenciador de figuras

```text
FiguraGeometrica
  ├── Triangulo
  └── Losango
          ↓
GerenteDeFiguras
          ↓
ProgramaDasFiguras
```

- `FiguraGeometrica` define o contrato de cálculo de área e nome da figura.
- `Triangulo` e `Losango` implementam esse contrato.
- `GerenteDeFiguras` mantém uma lista de figuras e realiza operações sobre ela.
- `ProgramaDasFiguras` demonstra o uso das classes.

O exemplo exercita interfaces, implementação de contratos, polimorfismo, composição e coleções com `List`/`ArrayList`.

## Executar o Exercicio4

Requisito: JDK disponível no terminal. O exemplo foi compilado e executado com **OpenJDK 21 em Linux** na revisão da documentação.

Na raiz do repositório:

```sh
javac -encoding UTF-8 -d out Exercicio4/FiguraGeometrica.java Exercicio4/Triangulo.java Exercicio4/Losango.java Exercicio4/GerenteDeFiguras.java Exercicio4/ProgramaDasFiguras.java
java -cp out ProgramaDasFiguras
```

Saída observada:

```text
Figuras cadastradas:
Triângulo
Losango
Losango
Área total: 35.0
Maior área: 20.0
```

O diretório `out/` é saída de compilação e não precisa ser incluído no controle de versão.

## Organização dos estudos

As pastas de `aula02/` preservam a organização utilizada durante as aulas, incluindo configurações de IDE e projetos separados. Para estudar um exercício, abra sua pasta específica; não presuma que todos os arquivos devem ser compilados juntos.

Esta revisão acrescenta documentação sem mover pastas, renomear classes, completar exercícios ou alterar enunciados. Materiais e exemplos de aula preservam a autoria dos respectivos autores; o repositório não reivindica autoria exclusiva sobre todo o material didático.

## Escopo da validação

Em **23/09/2026**, as cinco fontes de `Exercicio4/` foram conferidas por hash contra o conteúdo do GitHub, compiladas e executadas localmente. A saída acima foi verificada.

Isso **não** equivale a validar todos os exercícios, uma suíte automatizada completa, casos extremos ou execução em Windows. As demais pastas foram mantidas como material de estudo.

## Autor do repositório

**Walter Yanko** — estudante de ADS, desenvolvedor de software e IA aplicada, com formação em Engenharia Química e produção musical.

[Perfil no GitHub](https://github.com/walteryanko) · [Portfólio profissional](https://walter-yanko-portfolio.walteryanko.chatgpt.site/)

---

**Licença:** esta revisão não acrescenta licença nem altera a atribuição dos materiais existentes.


## Verificação automatizada do Exercicio4

O workflow `Java verification` compila somente `Exercicio4/`, executa o exemplo e verifica coleção vazia, áreas fracionárias, soma polimórfica e independência da ordem para a maior área. Não cobre os projetos de `aula02/` nem acrescenta validação de dimensões negativas às classes.

```sh
javac -encoding UTF-8 -d out Exercicio4/*.java tests/GeometryChecks.java
java -cp out GeometryChecks
```

Os novos checks passaram com OpenJDK 17 em Linux nesta sessão. O workflow configura Temurin 21; consulte a execução em Actions para seu resultado. Nenhuma licença ou fonte do exercício foi alterada.
