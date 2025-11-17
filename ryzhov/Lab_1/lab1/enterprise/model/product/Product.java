package lab1.enterprise.model.product;

//Использовать record для Product невозможно, record не может быть abstract
public abstract class Product {

    private final String id;
    private final String name;
    private final int productionTime;

    public abstract String getCategory();

    public Product(String id, String name, int productionTime) {
        this.id = id;
        this.name = name;
        this.productionTime = productionTime;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public int getProductionTime() {
        return productionTime;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", productionTime=" + productionTime +
                '}';
    }
}
