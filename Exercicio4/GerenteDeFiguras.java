import java.util.ArrayList;
import java.util.List;

public class GerenteDeFiguras {
    private List<FiguraGeometrica> figuras;

    public GerenteDeFiguras() {
        figuras = new ArrayList<FiguraGeometrica>();
    }

    public void adicionaFigura(FiguraGeometrica fig) {
        figuras.add(fig);
    }

    public double calculaAreaTotalDeFiguras() {
        double areaTotal = 0.0;

        for (FiguraGeometrica figura : figuras) {
            areaTotal += figura.calculaArea();
        }

        return areaTotal;
    }

    public List<FiguraGeometrica> getFiguras() {
        return figuras;
    }

    public void imprimeFiguras() {
        for (FiguraGeometrica figura : figuras) {
            System.out.println(figura.getNomeFigura());
        }
    }

    public double getMaiorAreaDeFigura() {
        double maiorArea = 0.0;

        for (FiguraGeometrica figura : figuras) {
            double area = figura.calculaArea();

            if (area > maiorArea) {
                maiorArea = area;
            }
        }

        return maiorArea;
    }
}
