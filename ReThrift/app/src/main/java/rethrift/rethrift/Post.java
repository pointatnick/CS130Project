package rethrift.rethrift;

public class Post {

    private String title;
    private String price;
    private String location;
    private String description;
    private String category;

    public Post(String title, String price, String location, String description, String category) {
        this.title = title;
        this.price = price;
        this.location = location;
        this.description = description;
        this.category = category;
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
}
