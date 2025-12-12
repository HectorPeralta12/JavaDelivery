public class BurgerRestaurant extends Restaurant {
    public BurgerRestaurant(Long id, String name) {
        super(id, name, RestaurantType.BURGER);
    }

    @Override
    public void showMenu() {
        System.out.println("Showing burger menu...");
    }
}
