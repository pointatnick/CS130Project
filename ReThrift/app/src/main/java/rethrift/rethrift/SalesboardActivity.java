package rethrift.rethrift;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class SalesboardActivity extends AppCompatActivity {
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private String user, name;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salesboard);

        // for search
        //getIntent and pass to handler
        handleIntent(getIntent());
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) findViewById(R.id.search_bar);
        //sets up a submit button
        searchView.setSubmitButtonEnabled(true);
        //assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        RecyclerView recList = (RecyclerView) findViewById(R.id.card_list);
        recList.setHasFixedSize(true);
        RecyclerView card_list = (RecyclerView) findViewById(R.id.card_list);
        card_list.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        card_list.setLayoutManager(llm);

        PostAdapter ca = new PostAdapter(createList());
        card_list.setAdapter(ca);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user = extras.getString("USERNAME");
            name = extras.getString("FIRSTNAME");
        }

        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        addDrawerItems();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    // profile preview (left screen)
    public void addDrawerItems() {
        String[] items = { name, user, "Profile", "Watchlist", "My Posts"};
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        mDrawerList.setAdapter(mAdapter);
    }

    // sales board (center screen)
    public void createPost(View view){
        Intent intent = new Intent(this, CreatePostActivity.class);
        intent.putExtra("USERNAME", user);
        startActivity(intent);
    }


    private List<Post> createList() {
        List<Post> result = new ArrayList<>();
        Post ci = new Post("Title goes here", "$10", "5678 Alley Drive", "Test description", "Test category");
        Post di = new Post("Another title", "$5", "1234 Park Lane", "This is a test", "Some test");
        result.add(ci);
        result.add(di);
        return result;
    }

    //for search
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent){
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            //obtain the query string from Intent.ACTION_SEARCH
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
    }

    private void doMySearch(String query){
        //TO-DO: pass to back-end here
        //TO-DO: change return type to whatever back-end returns
        //TO-DO: pass to Kexin's adapter? The results of this search
        //       is shown on the Salesboard view.
        //?? So open up Salesboard and pass result to show on the view?
    }

}
