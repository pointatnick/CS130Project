package rethrift.rethrift;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class SalesboardActivity extends AppCompatActivity {

    private ArrayList<String> postsArray;
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salesboard);

        postsArray = new ArrayList<String>();

        // mock array
        postsArray.add("Selling Queen Size Bed Frame, $50, Furniture");
        postsArray.add("Silver iPhone 5s, $200, Electronics");
        postsArray.add("Selling Like New white sectional couch, $350, Furniture");

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, postsArray);

        ListView listView = (ListView) findViewById(R.id.cardlist);
        listView.setAdapter(adapter);
    }

    public void createPost(View view){
        Intent intent = new Intent(this, CreatePostActivity.class);
        startActivity(intent);
    }

    // TODO: profile preview (left screen)

    // TODO: search (right screen)

}
