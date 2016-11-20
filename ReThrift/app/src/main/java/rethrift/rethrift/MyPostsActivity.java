package rethrift.rethrift;

/**
 * Created by kexinyu on 11/18/2016.
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import android.app.ListActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Button;

import java.util.ArrayList;

public class MyPostsActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_salesboard);

        // generate dummy post list
        ArrayList<String> posts = new ArrayList<String>();
        posts.add("Selling Queen Size Bed Frame");
        posts.add("Silver iPhone 5s");
        posts.add("Selling Like New White Sectional Couch");

        //instantiate adapter
        MyPostsAdapter adapter = new MyPostsAdapter(posts, this);

        //handle listview and assign adapter
        ListView listView = (ListView)findViewById(R.id.post_list);
        listView.setAdapter(adapter);

    }
}