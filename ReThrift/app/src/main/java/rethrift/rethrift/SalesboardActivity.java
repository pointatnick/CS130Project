package rethrift.rethrift;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.ArrayAdapter;


/**
 * Created by MC on 10/29/2016.
 */

public class SalesboardActivity extends AppCompatActivity {

    //added
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    //@@@@@@

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_salesboard);

      //  mDrawerList = (ListView)findViewById(R.id.navList);
     //   addDrawerItems();
    }
     //added
     //  private void addDrawerItems() {
     //    String[] items = { "Name", "E-Mail", "Phone Number", "Profile"};
     //   mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
     //   mDrawerList.setAdapter(mAdapter);
     //}
    //@@@@@

    public void createPost(View view){
        Intent intent = new Intent(this, CreatePostActivity.class);
        startActivity(intent);
    }


    // TODO: search (right screen)

}
