public class Main {
    public static void main(String[] args) {


        System.out.println("Testing Restaurant Factory:");
        Restaurant burgerRes = RestaurantFactory.createRestaurant(RestaurantType.BURGER, 1L, "Burger Palace");
        Restaurant chineseRes = RestaurantFactory.createRestaurant(RestaurantType.CHINESE, 2L, "Dragon Express");

        System.out.println("Created restaurant: " + burgerRes.getName() + " (" + burgerRes.getType() + ")");
        System.out.println("Created restaurant: " + chineseRes.getName() + " (" + chineseRes.getType() + ")");

        burgerRes.showMenu();
        chineseRes.showMenu();



        System.out.println("\nTesting MenuItem Factory:");
        MenuItem vegItem = MenuItemFactory.createMenuItem(DishType.VEG, 10L, "Vegetable Pizza", 12.99);
        MenuItem nonVegItem = MenuItemFactory.createMenuItem(DishType.NON_VEG, 11L, "Chicken Burger", 9.99);
        MenuItem veganItem = MenuItemFactory.createMenuItem(DishType.VEGAN, 12L, "Vegan Bowl", 11.50);

        System.out.println("Created item: " + vegItem.getName() + " (" + vegItem.getType() + ")");
        System.out.println("Created item: " + nonVegItem.getName() + " (" + nonVegItem.getType() + ")");
        System.out.println("Created item: " + veganItem.getName() + " (" + veganItem.getType() + ")");

        vegItem.prepare();
        nonVegItem.prepare();
        veganItem.prepare();
    }
}