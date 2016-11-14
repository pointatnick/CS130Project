package rethrift.rethrift;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ViewPostActivity extends AppCompatActivity {
  private TextView tvTitle, tvPrice, tvLocation, tvCategory, tvDescription;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_post);

    tvTitle = (TextView) findViewById(R.id.title);

    Bundle extras = getIntent().getExtras();
    if (extras != null) {
      tvTitle.setText(extras.getString("TITLE"));
      tvPrice.setText(extras.getString("PRICE"));
      tvLocation.setText(extras.getString("LOCATION"));
      tvCategory.setText(extras.getString("CATEGORY"));
      tvDescription.setText(extras.getString("DESCRIPTION"));
    }
  }


}
