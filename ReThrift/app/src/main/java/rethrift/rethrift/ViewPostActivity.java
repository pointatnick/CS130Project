package rethrift.rethrift;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;


public class ViewPostActivity extends AppCompatActivity {
  private TextView tvTitle, tvPrice, tvState, tvLocation, tvCategory, tvDescription, tvName, tvUsername;
  private Button btnWatchlist, btnContact;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_post);

    tvTitle = (TextView) findViewById(R.id.title);
    tvPrice = (TextView) findViewById(R.id.price);
    tvState = (TextView) findViewById(R.id.state);
    tvLocation = (TextView) findViewById(R.id.location);
    tvCategory = (TextView) findViewById(R.id.category);
    tvDescription = (TextView) findViewById(R.id.description);
    tvName = (TextView) findViewById(R.id.name);
    tvUsername = (TextView) findViewById(R.id.username);

    btnWatchlist = (Button) findViewById(R.id.watchlist_btn);
    btnContact = (Button) findViewById(R.id.contact_btn);

    Bundle extras = getIntent().getExtras();
    if (extras != null) {
      tvTitle.setText(extras.getString("TITLE"));
      tvPrice.setText(extras.getString("PRICE"));
      tvState.setText(extras.getString("STATE"));
      tvLocation.setText(extras.getString("LOCATION"));
      tvCategory.setText(extras.getString("CATEGORY"));
      tvDescription.setText(extras.getString("DESCRIPTION"));
      tvName.setText(extras.getString("NAME"));
      tvUsername.setText(extras.getString("USERNAME"));
    }

    btnWatchlist.setOnClickListener(new View.OnClickListener()
    {

      public void onClick(View v){
        Intent i = new Intent(ViewPostActivity.this, WatchListActivity.class);
        i.putExtra("TITLE", tvTitle.getText().toString());
      }
    });
    //==

    //i.putExtra("Price",  tvPrice );
    //i.putExtra("Location", tvLocation);
    //==
  }



  // TODO: translate lat/long to location
  // TODO: add to watchlist
  // TODO: contacts seller
  public void contactSeller(View view) {

  }

}
