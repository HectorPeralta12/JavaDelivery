public class VegItem extends MenuItem {
    public VegItem(Long id, String name, double price) {
        super(id, name, price, DishType.VEG);
    }

    @Override
    public void prepare() {
        System.out.println("Preparing a vegetarian dish...");
    }
}
