package rethrift.rethrift;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import android.view.Menu;
import android.view.MenuInflater;

public class SalesboardActivity extends AppCompatActivity {

    private ArrayList<String> postsArray;
    private ArrayAdapter postsAdapter;
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salesboard);

        // mock array for cards
        postsArray = new ArrayList<String>();
        postsArray.add("Selling Queen Size Bed Frame, $50, Furniture");
        postsArray.add("Silver iPhone 5s, $200, Electronics");
        postsArray.add("Selling Like New white sectional couch, $350, Furniture");

        postsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, postsArray);

        ListView listView = (ListView) findViewById(R.id.cardlist);
        listView.setAdapter(postsAdapter);


        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        addDrawerItems();
    }

    // profile preview (left screen)
    public void addDrawerItems() {
        String[] items = { "Name", "Username", "Watchlist", "Profile"};
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        mDrawerList.setAdapter(mAdapter);
    }

    // TODO: add button listeners

    // sales board (center screen)
    public void createPost(View view){
        Intent intent = new Intent(this, CreatePostActivity.class);
        startActivity(intent);
    }

    // TODO: search (right screen)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);

        return true;
    }

}
