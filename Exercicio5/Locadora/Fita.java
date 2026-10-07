public class Fita {
    public static final int NORMAL = 0;
    public static final int LANCAMENTO = 1;
    public static final int INFANTIL = 2;

    private final String titulo;
    private int codigoDePreco;
    private PoliticaDePreco politicaDePreco;

    public Fita(String titulo, int codigoDePreco) {
        this.titulo = titulo;
        setCodigoDePreco(codigoDePreco);
    }

    public String getTitulo() {
        return titulo;
    }

    public int getCodigoDePreco() {
        return codigoDePreco;
    }

    public void setCodigoDePreco(int codigoDePreco) {
        this.codigoDePreco = codigoDePreco;
        this.politicaDePreco = PoliticaDePreco.paraCodigo(codigoDePreco);
    }

    /** Mantém compatibilidade com o nome do método no projeto original. */
    public void setcodigoDePreco(int codigoDePreco) {
        setCodigoDePreco(codigoDePreco);
    }

    public double calcularValor(int diasAlugada) {
        return politicaDePreco.calcularValor(diasAlugada);
    }

    public int calcularPontosDeAlugadorFrequente(int diasAlugada) {
        return politicaDePreco.calcularPontos(diasAlugada);
    }
}
