public class Aluguel {
    private final int diasAlugada;
    private final Fita fita;

    public Aluguel(Fita fita, int diasAlugada) {
        this.fita = fita;
        this.diasAlugada = diasAlugada;
    }

    public Fita getFita() {
        return fita;
    }

    public int getDiasAlugada() {
        return diasAlugada;
    }

    public double calcularValor() {
        return fita.calcularValor(diasAlugada);
    }

    public int calcularPontosDeAlugadorFrequente() {
        return fita.calcularPontosDeAlugadorFrequente(diasAlugada);
    }
}
