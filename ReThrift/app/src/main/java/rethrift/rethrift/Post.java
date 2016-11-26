package rethrift.rethrift;

public class Post {

    private int id;
    private String title, price, state, description, category, name, username;
    private double latitude, longitude;
    private String image;

    public Post(int id, String title, String price, String state,
                double latitude, double longitude, String description, String category,
                String name, String username, String image) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.category = category;
        this.name = name;
        this.image = image;
        this.username = username;

    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public String getState() {
        return state;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getImage() {
        return image;
    }

}

