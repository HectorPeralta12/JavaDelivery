public class RestaurantFactory {

    public static Restaurant createRestaurant(RestaurantType type, Long id, String name) {
        switch (type) {
            case BURGER:
                return new BurgerRestaurant(id, name);
            case CHINESE:
                return new ChineseRestaurant(id, name);
            case VEGAN:
                return new VeganRestaurant(id, name);
            default:
                throw new IllegalArgumentException("Unknown restaurant type");
        }
    }
}
