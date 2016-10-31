package rethrift.rethrift;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;


/**
 * Created by MC on 10/29/2016.
 */

public class SalesboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_salesboard);
    }

    public void createPost(View view){
        Intent intent = new Intent(this, CreatePostActivity.class);
        startActivity(intent);
    }

    // TODO: profile preview (left screen)

    // TODO: search (right screen)

}
