package rethrift.rethrift;

/**
 * Created by kexinyu on 11/2/16.
 */

public class Post {

    private String m_title;
    private String m_price;
    private String m_category;

    public Post() {}

    public Post(String title, String price, String category){
        m_title = title;
        m_price = price;
        m_category = category;
    }

    public String getTitle() {
        return m_title;
    }

    public String getPrice() {
        return m_price;
    }

    public String getCategory() {
        return m_category;
    }
}
