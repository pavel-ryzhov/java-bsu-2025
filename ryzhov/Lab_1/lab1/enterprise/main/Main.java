package lab1.enterprise.main;

import lab1.enterprise.model.line.ChemicalLine;
import lab1.enterprise.model.line.ElectronicLine;
import lab1.enterprise.model.line.MechanicalLine;
import lab1.enterprise.model.line.ProductionLine;
import lab1.enterprise.model.product.ChemicalProduct;
import lab1.enterprise.model.product.ElectronicProduct;
import lab1.enterprise.model.product.MechanicalProduct;
import lab1.enterprise.model.product.Product;
import lab1.enterprise.service.ProdAnalyzer;

import java.util.Arrays;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        var analyzer = getProdAnalyzer();

        System.out.println("Линии с эффективностью > 0.9: " + analyzer.getHighEfficiencyLines(0.9));
        System.out.println("Количество продуктов по категориям: " + analyzer.countProductsByCategory());
        System.out.println("Все продукты со всех линий: " + analyzer.getAllProductsFromLines());
        System.out.println("Общее время производства: " + analyzer.calculateTotalProductionTime());

        Optional<ProductionLine<? extends Product>> mostLoaded = analyzer.findMostLoadedLine();
        mostLoaded.ifPresent(line -> System.out.println("Самая загруженная линия: " + line.getLineId()));
    }

    private static ProdAnalyzer getProdAnalyzer() {
        var phone = new ElectronicProduct("E001", "Smartphone", 120);
        var laptop = new ElectronicProduct("E002", "Laptop", 180);
        var engine = new MechanicalProduct("M001", "Car Engine", 300);
        var fertilizer = new ChemicalProduct("C001", "Fertilizer", 90);

        var elLine = new ElectronicLine("EL-01", 0.95);
        var mechLine = new MechanicalLine("MECH-01", 0.88);
        var chemLine = new ChemicalLine("CHEM-01", 0.92);

        elLine.addProduct(phone);
        elLine.addProduct(laptop);
        mechLine.addProduct(engine);
        chemLine.addProduct(fertilizer);

        System.out.println(elLine.canProduce(phone));       //true
        System.out.println(elLine.canProduce(laptop));      //true
        System.out.println(elLine.canProduce(engine));      //false
        System.out.println(elLine.canProduce(fertilizer));  //false

        return new ProdAnalyzer(Arrays.asList(elLine, mechLine, chemLine));
    }
}