public class MenuItemFactory {

    public static MenuItem createMenuItem(DishType type, Long id, String name, double price) {
        switch (type) {
            case VEG:
                return new VegItem(id, name, price);
            case NON_VEG:
                return new NonVegItem(id, name, price);
            case VEGAN:
                return new VeganItem(id, name, price);
            default:
                throw new IllegalArgumentException("Unknown dish type");
        }
    }
}
