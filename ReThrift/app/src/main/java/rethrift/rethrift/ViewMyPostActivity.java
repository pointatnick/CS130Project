package rethrift.rethrift;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ViewMyPostActivity extends AppCompatActivity {
  private TextView tvTitle, tvPrice, tvState, tvLocation, tvCategory, tvDescription, tvName, tvUsername;
  private Button btnEdit;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_my_post);

    tvTitle = (TextView) findViewById(R.id.title);
    tvPrice = (TextView) findViewById(R.id.price);
    tvState = (TextView) findViewById(R.id.state);
    tvLocation = (TextView) findViewById(R.id.location);
    tvCategory = (TextView) findViewById(R.id.category);
    tvDescription = (TextView) findViewById(R.id.description);
    tvName = (TextView) findViewById(R.id.name);
    tvUsername = (TextView) findViewById(R.id.username);

    btnEdit = (Button) findViewById(R.id.edit_btn);

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
  }

  // edit post
  public void editPost(View view) {
    Intent intent = new Intent(this, EditPostActivity.class);
    intent.putExtra("TITLE", tvTitle.getText().toString());
    intent.putExtra("PRICE", tvPrice.getText().toString());
    intent.putExtra("STATE", tvState.getText().toString());
    intent.putExtra("LOCATION", tvLocation.getText().toString());
    intent.putExtra("CATEGORY", tvCategory.getText().toString());
    intent.putExtra("DESCRIPTION", tvDescription.getText().toString());
    intent.putExtra("NAME", tvName.getText().toString());
    intent.putExtra("USERNAME", tvUsername.getText().toString());
    startActivity(intent);
  }
}