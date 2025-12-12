public class NonVegItem extends MenuItem {
    public NonVegItem(Long id, String name, double price) {
        super(id, name, price, DishType.NON_VEG);
    }

    @Override
    public void prepare() {
        System.out.println("Preparing a non-vegetarian dish...");
    }
}
