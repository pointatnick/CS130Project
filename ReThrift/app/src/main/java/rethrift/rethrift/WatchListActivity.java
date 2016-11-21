package rethrift.rethrift;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.view.View;



public class WatchListActivity extends AppCompatActivity {
    private TextView Title, Price, Location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_preview_layout);

        Bundle extras = getIntent().getExtras();
            Title = (TextView) findViewById(R.id.title);

        if(extras != null){
            Title.setText(extras.getString("TITLE"));
        }
        // Price.setText(extras.getString("PRICE"));
        //Location.setText(extras.getString("LOCATION"));
        //   Price = (TextView) findViewById(R.id.price);
        //   Location = (TextView) findViewById(R.id.location);
    }
}
