package rethrift.rethrift;

public class Post {

    private String title, price, location, description, category, name, username;

    public Post(String title, String price, String location, String description, String category, String name, String username) {
        this.title = title;
        this.price = price;
        this.location = location;
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

    public String getLocation() {
        return location;
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
