/** Regras de preço e pontos específicas de cada categoria de fita. */
enum PoliticaDePreco {
    NORMAL(Fita.NORMAL) {
        @Override
        double calcularValor(int diasAlugada) {
            double valor = 2.0;
            if (diasAlugada > 2) {
                valor += (diasAlugada - 2) * 1.5;
            }
            return valor;
        }
    },
    LANCAMENTO(Fita.LANCAMENTO) {
        @Override
        double calcularValor(int diasAlugada) {
            return diasAlugada * 3;
        }

        @Override
        int calcularPontos(int diasAlugada) {
            return diasAlugada > 1 ? 2 : 1;
        }
    },
    INFANTIL(Fita.INFANTIL) {
        @Override
        double calcularValor(int diasAlugada) {
            double valor = 1.5;
            if (diasAlugada > 3) {
                valor += (diasAlugada - 3) * 1.5;
            }
            return valor;
        }
    },
    // O switch original também aceitava códigos desconhecidos: valor 0 e 1 ponto.
    DESCONHECIDA(Integer.MIN_VALUE) {
        @Override
        double calcularValor(int diasAlugada) {
            return 0.0;
        }
    };

    private final int codigo;

    PoliticaDePreco(int codigo) {
        this.codigo = codigo;
    }

    abstract double calcularValor(int diasAlugada);

    int calcularPontos(int diasAlugada) {
        return 1;
    }

    static PoliticaDePreco paraCodigo(int codigo) {
        for (PoliticaDePreco politica : values()) {
            if (politica.codigo == codigo) {
                return politica;
            }
        }
        return DESCONHECIDA;
    }
}
