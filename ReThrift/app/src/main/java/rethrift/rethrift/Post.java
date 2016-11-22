package rethrift.rethrift;

public class Post {

    private String title, price, state, description, category, name, username;
    private double latitude, longitude;

    public Post(String title, String price, String state,
                double latitude, double longitude, String description, String category,
                String name, String username) {
        this.title = title;
        this.price = price;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.category = category;
        this.name = name;
        this.username = username;

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

}

