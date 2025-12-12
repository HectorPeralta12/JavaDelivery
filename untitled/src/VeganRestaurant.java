public class VeganRestaurant extends Restaurant {
    public VeganRestaurant(Long id, String name) {
        super(id, name, RestaurantType.VEGAN);
    }

    @Override
    public void showMenu() {
        System.out.println("Showing vegan menu...");
    }
}
