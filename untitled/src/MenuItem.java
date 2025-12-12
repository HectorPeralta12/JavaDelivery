public abstract class MenuItem {
    protected Long id;
    protected String name;
    protected double price;
    protected DishType type;

    public MenuItem(Long id, String name, double price, DishType type) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public DishType getType() {
        return type;
    }

    public abstract void prepare();
}
