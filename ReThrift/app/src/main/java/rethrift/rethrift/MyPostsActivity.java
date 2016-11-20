package rethrift.rethrift;

/**
 * Created by kexinyu on 11/18/2016.
 */

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class MyPostsActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        // generate dummy post list
        ArrayList<String> posts = new ArrayList<String>();
        posts.add("Selling Queen Size Bed Frame");
        posts.add("Silver iPhone 5s");
        posts.add("Selling Like New White Sectional Couch");

        //instantiate adapter
        MyPostsAdapter adapter = new MyPostsAdapter(posts, this);

        //handle listview and assign adapter
        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(adapter);

    }
}