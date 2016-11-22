package rethrift.rethrift;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.view.View;



public class WatchListActivity extends AppCompatActivity {
    private TextView Title, Price, Location;
    private RecyclerView cardList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.watchlist);

        cardList = (RecyclerView) findViewById(R.id.card_list);
        cardList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        cardList.setLayoutManager(llm);


        // Price.setText(extras.getString("PRICE"));
        //Location.setText(extras.getString("LOCATION"));
        //   Price = (TextView) findViewById(R.id.price);
        //   Location = (TextView) findViewById(R.id.location);
    }
}
