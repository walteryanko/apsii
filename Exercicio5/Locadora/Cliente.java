import java.util.ArrayList;
import java.util.List;

public class Cliente {
    private final String nome;
    private final List<Aluguel> alugueis = new ArrayList<>();

    public Cliente(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void adicionaAluguel(Aluguel aluguel) {
        alugueis.add(aluguel);
    }

    public String extrato() {
        double valorTotal = 0;
        int pontosDeAlugadorFrequente = 0;
        StringBuilder resultado = new StringBuilder("Registro de Alugueis de ")
                .append(getNome()).append('\n');

        for (Aluguel aluguel : alugueis) {
            double valorAluguel = aluguel.calcularValor();
            valorTotal += valorAluguel;
            pontosDeAlugadorFrequente += aluguel.calcularPontosDeAlugadorFrequente();

            resultado.append('\t').append(aluguel.getFita().getTitulo())
                    .append('\t').append(valorAluguel).append('\n');
        }

        resultado.append("Valor total devido: ").append(valorTotal).append('\n');
        resultado.append("Você ganhou ").append(pontosDeAlugadorFrequente)
                .append(" pontos de alugador frequente");
        return resultado.toString();
    }
}
