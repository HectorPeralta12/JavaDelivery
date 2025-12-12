public class ChineseRestaurant extends Restaurant {
    public ChineseRestaurant(Long id, String name) {
        super(id, name, RestaurantType.CHINESE);
    }

    @Override
    public void showMenu() {
        System.out.println("Showing Chinese menu...");
    }
}
