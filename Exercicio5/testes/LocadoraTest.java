import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/** Testes de regressão executáveis com Java, sem bibliotecas externas. */
public final class LocadoraTest {
    private static int verificacoes;
    private static int cenariosComparados;

    private LocadoraTest() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException(
                    "Uso: java -cp out/refatorado LocadoraTest out/original");
        }
        verificarPrecosEPontos();
        verificarExtratoDoExemplo();
        verificarClienteSemAlugueis();
        verificarMudancaDeCategoria();
        compararComOriginal(args[0]);
        System.out.println("OK: " + verificacoes + " verificações passaram.");
        System.out.println("Equivalência com código original: "
                + cenariosComparados + " cenários passaram.");
    }

    private static void verificarPrecosEPontos() {
        verificarAluguel(Fita.NORMAL, 1, 2.0, 1);
        verificarAluguel(Fita.NORMAL, 2, 2.0, 1);
        verificarAluguel(Fita.NORMAL, 3, 3.5, 1);
        verificarAluguel(Fita.NORMAL, 5, 6.5, 1);
        verificarAluguel(Fita.INFANTIL, 1, 1.5, 1);
        verificarAluguel(Fita.INFANTIL, 3, 1.5, 1);
        verificarAluguel(Fita.INFANTIL, 4, 3.0, 1);
        verificarAluguel(Fita.LANCAMENTO, 0, 0.0, 1);
        verificarAluguel(Fita.LANCAMENTO, 1, 3.0, 1);
        verificarAluguel(Fita.LANCAMENTO, 2, 6.0, 2);
        verificarAluguel(Fita.LANCAMENTO, 5, 15.0, 2);
        verificarAluguel(99, 3, 0.0, 1);
    }

    private static void verificarAluguel(int codigo, int dias, double valor, int pontos) {
        Fita fita = new Fita("Filme de teste", codigo);
        Aluguel aluguel = new Aluguel(fita, dias);
        verificarNumero(valor, aluguel.calcularValor(), "Preço da categoria " + codigo);
        verificarNumero(pontos, aluguel.calcularPontosDeAlugadorFrequente(),
                "Pontos da categoria " + codigo);
        verificarNumero(dias, aluguel.getDiasAlugada(), "Duração do aluguel");
        verificarNumero(codigo, aluguel.getFita().getCodigoDePreco(), "Código da fita");
        verificarTexto("Filme de teste", aluguel.getFita().getTitulo(), "Título da fita");
    }

    private static void verificarExtratoDoExemplo() {
        Cliente cliente = new Cliente("Raul");
        cliente.adicionaAluguel(new Aluguel(new Fita("Matrix", Fita.NORMAL), 3));
        cliente.adicionaAluguel(new Aluguel(new Fita("Toy Story", Fita.INFANTIL), 4));
        cliente.adicionaAluguel(new Aluguel(new Fita("Oppenheimer", Fita.LANCAMENTO), 2));
        String esperado = "Registro de Alugueis de Raul\n"
                + "\tMatrix\t3.5\n"
                + "\tToy Story\t3.0\n"
                + "\tOppenheimer\t6.0\n"
                + "Valor total devido: 12.5\n"
                + "Você ganhou 4 pontos de alugador frequente";
        verificarTexto(esperado, cliente.extrato(), "Extrato completo e ordem dos aluguéis");
        verificarTexto(esperado, cliente.extrato(), "Extrato repetido sem efeitos colaterais");
        verificarTexto("Raul", cliente.getNome(), "Nome do cliente");
    }

    private static void verificarClienteSemAlugueis() {
        String esperado = "Registro de Alugueis de Yanko\n"
                + "Valor total devido: 0.0\n"
                + "Você ganhou 0 pontos de alugador frequente";
        verificarTexto(esperado, new Cliente("Yanko").extrato(), "Cliente sem aluguéis");
    }

    private static void verificarMudancaDeCategoria() {
        Fita fita = new Fita("Matrix", Fita.NORMAL);
        Aluguel aluguel = new Aluguel(fita, 3);
        verificarNumero(3.5, aluguel.calcularValor(), "Preço antes de trocar categoria");
        fita.setCodigoDePreco(Fita.LANCAMENTO);
        verificarNumero(9.0, aluguel.calcularValor(), "Preço com novo setter");
        verificarNumero(2, aluguel.calcularPontosDeAlugadorFrequente(), "Pontos atualizados");
        fita.setcodigoDePreco(Fita.INFANTIL);
        verificarNumero(1.5, aluguel.calcularValor(), "Compatibilidade com setter original");
        verificarNumero(1, aluguel.calcularPontosDeAlugadorFrequente(), "Pontos infantis");
        fita.setcodigoDePreco(99);
        verificarNumero(99, fita.getCodigoDePreco(), "Código desconhecido preservado");
        verificarNumero(0, aluguel.calcularValor(), "Preço do código desconhecido");
        verificarNumero(1, aluguel.calcularPontosDeAlugadorFrequente(), "Pontos desconhecidos");
    }

    private static void compararComOriginal(String diretorioOriginal) throws Exception {
        int[] codigos = {Fita.NORMAL, Fita.LANCAMENTO, Fita.INFANTIL, -1, 99, Integer.MIN_VALUE};
        int[] duracoes = {Integer.MIN_VALUE, -3, -1, 0, 1, 2, 3, 4, 7, 30,
                715827883, Integer.MAX_VALUE};
        int[] duracoesCombinadas = {0, 1, 2, 3, 4, 7};

        try (Original original = new Original(diretorioOriginal)) {
            for (String nome : new String[] {"Cliente", "", null}) {
                comparar(original.extrato(nome, new String[0], new int[0], new int[0]),
                        new Cliente(nome).extrato(), "Cliente vazio");
            }
            for (int codigo : codigos) {
                for (int dias : duracoes) {
                    String[] titulos = {"Teste"};
                    int[] categorias = {codigo};
                    int[] periodos = {dias};
                    comparar(original.extrato("Cliente", titulos, categorias, periodos),
                            extratoRefatorado("Cliente", titulos, categorias, periodos),
                            "Aluguel isolado: categoria " + codigo + ", dias " + dias);
                }
            }
            for (int diasNormal : duracoesCombinadas) {
                for (int diasInfantil : duracoesCombinadas) {
                    for (int diasLancamento : duracoesCombinadas) {
                        String[] titulos = {"Matrix", "Toy Story", "Oppenheimer"};
                        int[] categorias = {Fita.NORMAL, Fita.INFANTIL, Fita.LANCAMENTO};
                        int[] periodos = {diasNormal, diasInfantil, diasLancamento};
                        comparar(original.extrato("Raul", titulos, categorias, periodos),
                                extratoRefatorado("Raul", titulos, categorias, periodos),
                                "Extrato com as três categorias");
                    }
                }
            }
            for (int codigoInicial : codigos) {
                for (int codigoNovo : codigos) {
                    for (int dias : duracoesCombinadas) {
                        Fita fita = new Fita("Filme compartilhado", codigoInicial);
                        Cliente cliente = new Cliente("Cliente");
                        cliente.adicionaAluguel(new Aluguel(fita, dias));
                        cliente.adicionaAluguel(new Aluguel(fita, dias + 1));
                        fita.setcodigoDePreco(codigoNovo);
                        comparar(original.extratoAposAlterarCategoria(codigoInicial, codigoNovo, dias),
                                cliente.extrato(), "Troca de categoria em fita compartilhada");
                    }
                }
            }
        }
    }

    private static String extratoRefatorado(String nome, String[] titulos,
            int[] codigos, int[] dias) {
        Cliente cliente = new Cliente(nome);
        for (int i = 0; i < titulos.length; i++) {
            cliente.adicionaAluguel(new Aluguel(new Fita(titulos[i], codigos[i]), dias[i]));
        }
        return cliente.extrato();
    }

    private static void comparar(String esperado, String atual, String contexto) {
        verificarTexto(esperado, atual, contexto);
        cenariosComparados++;
    }

    private static void verificarTexto(String esperado, String atual, String contexto) {
        if (!esperado.equals(atual)) {
            throw new AssertionError(contexto + "\nEsperado:\n" + esperado + "\nObtido:\n" + atual);
        }
        verificacoes++;
    }

    private static void verificarNumero(double esperado, double atual, String contexto) {
        if (Double.compare(esperado, atual) != 0) {
            throw new AssertionError(contexto + ": esperado " + esperado + ", obtido " + atual);
        }
        verificacoes++;
    }

    /** Carrega as classes originais separadamente, sem reimplementar suas regras no teste. */
    private static final class Original implements AutoCloseable {
        private final URLClassLoader loader;
        private final Constructor<?> construirCliente;
        private final Constructor<?> construirFita;
        private final Constructor<?> construirAluguel;
        private final Method adicionarAluguel;
        private final Method gerarExtrato;
        private final Method alterarCategoria;

        Original(String diretorio) throws Exception {
            File arquivoCliente = new File(diretorio, "Cliente.class");
            if (!arquivoCliente.isFile()) {
                throw new IllegalArgumentException("Compile primeiro o original em " + diretorio);
            }
            loader = new URLClassLoader(new URL[] {new File(diretorio).toURI().toURL()}, null);
            Class<?> cliente = loader.loadClass("Cliente");
            Class<?> fita = loader.loadClass("Fita");
            Class<?> aluguel = loader.loadClass("Aluguel");
            construirCliente = cliente.getConstructor(String.class);
            construirFita = fita.getConstructor(String.class, int.class);
            construirAluguel = aluguel.getConstructor(fita, int.class);
            adicionarAluguel = cliente.getMethod("adicionaAluguel", aluguel);
            gerarExtrato = cliente.getMethod("extrato");
            alterarCategoria = fita.getMethod("setcodigoDePreco", int.class);
        }

        String extrato(String nome, String[] titulos, int[] codigos, int[] dias) throws Exception {
            Object cliente = construirCliente.newInstance(nome);
            for (int i = 0; i < titulos.length; i++) {
                Object fita = construirFita.newInstance(titulos[i], codigos[i]);
                Object aluguel = construirAluguel.newInstance(fita, dias[i]);
                adicionarAluguel.invoke(cliente, aluguel);
            }
            return (String) gerarExtrato.invoke(cliente);
        }

        String extratoAposAlterarCategoria(int codigoInicial, int codigoNovo, int dias) throws Exception {
            Object cliente = construirCliente.newInstance("Cliente");
            Object fita = construirFita.newInstance("Filme compartilhado", codigoInicial);
            adicionarAluguel.invoke(cliente, construirAluguel.newInstance(fita, dias));
            adicionarAluguel.invoke(cliente, construirAluguel.newInstance(fita, dias + 1));
            alterarCategoria.invoke(fita, codigoNovo);
            return (String) gerarExtrato.invoke(cliente);
        }

        @Override
        public void close() throws java.io.IOException {
            loader.close();
        }
    }
}
