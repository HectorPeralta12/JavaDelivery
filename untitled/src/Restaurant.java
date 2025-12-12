public abstract class Restaurant {
    protected Long id;
    protected String name;
    protected RestaurantType type;

    public Restaurant(Long id, String name, RestaurantType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public RestaurantType getType() {
        return type;
    }

    public abstract void showMenu();
}
