public final class GeometryChecks {
    private static void equal(double expected, double actual) {
        if (!Double.isFinite(actual) || Math.abs(expected - actual) > 1e-10) {
            throw new AssertionError("Expected " + expected + ", got " + actual);
        }
    }
    public static void main(String[] args) {
        GerenteDeFiguras empty = new GerenteDeFiguras();
        equal(0, empty.calculaAreaTotalDeFiguras());
        equal(0, empty.getMaiorAreaDeFigura());
        FiguraGeometrica triangle = new Triangulo(2.5, 3);
        FiguraGeometrica diamond = new Losango(3, 1.5);
        equal(3.75, triangle.calculaArea());
        equal(2.25, diamond.calculaArea());
        for (boolean reverse : new boolean[] {false, true}) {
            GerenteDeFiguras manager = new GerenteDeFiguras();
            manager.adicionaFigura(reverse ? diamond : triangle);
            manager.adicionaFigura(reverse ? triangle : diamond);
            equal(6, manager.calculaAreaTotalDeFiguras());
            equal(3.75, manager.getMaiorAreaDeFigura());
            if (manager.getFiguras().size() != 2) throw new AssertionError("Lost a figure");
        }
        System.out.println("Geometry checks passed: empty, fractional, polymorphic and order-independent cases.");
    }
}
