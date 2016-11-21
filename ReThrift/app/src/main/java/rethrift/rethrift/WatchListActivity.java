package rethrift.rethrift;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.view.View;

/**
 * Created by MC on 11/17/2016.
 */

public class WatchListActivity extends AppCompatActivity {
    private TextView Title, Price, Location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_preview_layout);

        Bundle extras = getIntent().getExtras();

            Title = (TextView) findViewById(R.id.title);
            Price = (TextView) findViewById(R.id.price);
            Location = (TextView) findViewById(R.id.location);

        if (extras != null) {
            Price.setText(extras.getString("PRICE"));
            Location.setText(extras.getString("LOCATION"));
            Title.setText(extras.getString("TITLE"));
        }
    }
}
