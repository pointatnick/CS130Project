package rethrift.rethrift;

/**
 * Created by taegen on 20/11/2016.
 */

public class WatchList {

    private String title, location, price;


    public WatchList(String title, String price, String state, String location) {
        this.title = title;
        this.price = price;
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public String getLocation() {return location;}
}
