package rethrift.rethrift;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class EditPostActivity extends AppCompatActivity {
  private TextView tvTitle, tvPrice, tvState, tvCategory, tvDescription, tvName, tvUsername;
  private Button btnUpdate, btnDelete;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_post);

    tvTitle = (TextView) findViewById(R.id.title);
    tvPrice = (TextView) findViewById(R.id.price);
    tvState = (TextView) findViewById(R.id.state);
    tvCategory = (TextView) findViewById(R.id.category);
    tvDescription = (TextView) findViewById(R.id.description);
    tvName = (TextView) findViewById(R.id.name);
    tvUsername = (TextView) findViewById(R.id.username);

    btnUpdate = (Button) findViewById(R.id.update_btn);
    btnDelete = (Button) findViewById(R.id.delete_btn);

    Bundle extras = getIntent().getExtras();
    if (extras != null) {
      tvTitle.setText(extras.getString("TITLE"));
      tvPrice.setText(extras.getString("PRICE"));
      tvState.setText(extras.getString("STATE"));
      tvCategory.setText(extras.getString("CATEGORY"));
      tvDescription.setText(extras.getString("DESCRIPTION"));
      tvName.setText(extras.getString("NAME"));
      tvUsername.setText(extras.getString("USERNAME"));
    }
  }

  public void updatePost(View view) {


    //finish();
  }

  // TODO: delete post
  public void deletePost(View view) {

  }
}
