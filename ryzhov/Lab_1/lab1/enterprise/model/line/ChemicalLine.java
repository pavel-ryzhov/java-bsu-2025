package lab1.enterprise.model.line;

import lab1.enterprise.model.product.ChemicalProduct;
import lab1.enterprise.model.product.Product;

import java.util.List;

public class ChemicalLine extends ProductionLine<ChemicalProduct>{
    public ChemicalLine(String lineId, List<ChemicalProduct> products, double efficiency) {
        super(lineId, products, efficiency);
    }

    public ChemicalLine(String lineId, double efficiency) {
        super(lineId, efficiency);
    }

    @Override
    public boolean canProduce(Product product) {
        return product instanceof ChemicalProduct;
    }
}
