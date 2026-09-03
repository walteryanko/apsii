public class Triangulo implements FiguraGeometrica {
    private double base;
    private double altura;

    public Triangulo(double base, double altura) {
        this.base = base;
        this.altura = altura;
    }

    @Override
    public double calculaArea() {
        return (this.base * this.altura) / 2;
    }

    @Override
    public String getNomeFigura() {
        return "Triângulo";
    }

    @Override
    public String toString() {
        return getNomeFigura() + " - área: " + calculaArea();
    }
}
