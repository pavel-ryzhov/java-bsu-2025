package lab1.enterprise.model.line;

import lab1.enterprise.model.product.ElectronicProduct;
import lab1.enterprise.model.product.Product;

import java.util.List;

public class ElectronicLine extends ProductionLine<ElectronicProduct>{
    public ElectronicLine(String lineId, List<ElectronicProduct> products, double efficiency) {
        super(lineId, products, efficiency);
    }

    public ElectronicLine(String lineId, double efficiency) {
        super(lineId, efficiency);
    }

    @Override
    public boolean canProduce(Product product) {
        return product instanceof ElectronicProduct;
    }
}
