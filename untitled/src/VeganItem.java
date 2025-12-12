public class VeganItem extends MenuItem {
    public VeganItem(Long id, String name, double price) {
        super(id, name, price, DishType.VEGAN);
    }

    @Override
    public void prepare() {
        System.out.println("Preparing a vegan dish...");
    }
}
