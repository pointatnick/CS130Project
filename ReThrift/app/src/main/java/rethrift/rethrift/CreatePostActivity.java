package rethrift.rethrift;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

public class CreatePostActivity extends AppCompatActivity {
    private EditText title, price, category;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.create_post);

        title = (EditText) findViewById(R.id.title_field);
        price = (EditText) findViewById(R.id.price_field);
        category = (EditText) findViewById(R.id.category_field);
    }

    public void addPost(View view){
        Intent intent = new Intent(this, SalesboardActivity.class);
        startActivity(intent);
    }

}
