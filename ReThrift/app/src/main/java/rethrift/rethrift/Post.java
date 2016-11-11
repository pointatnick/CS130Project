package rethrift.rethrift;

public class Post {

    private String m_title;
    private String m_price;
    private String m_location;

    public Post(String title, String price, String location) {
        m_title = title;
        m_price = price;
        m_location = location;
    }

    public String getTitle() {
        return m_title;
    }

    public String getPrice() {
        return m_price;
    }

    public String getLocation() {
        return m_location;
    }
}
