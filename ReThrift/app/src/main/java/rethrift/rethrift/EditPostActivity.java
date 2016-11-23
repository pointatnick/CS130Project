package rethrift.rethrift;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;


public class EditPostActivity extends AppCompatActivity {
  private TextView tvTitle, tvPrice, tvDescription, tvName, tvUsername;
  private Spinner state, category;
  private Button btnUpdate, btnDelete;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_post);

    tvTitle = (TextView) findViewById(R.id.title);
    tvPrice = (TextView) findViewById(R.id.price);
    tvDescription = (TextView) findViewById(R.id.description);
    tvName = (TextView) findViewById(R.id.name);
    tvUsername = (TextView) findViewById(R.id.username);

    category = (Spinner) findViewById(R.id.category_spinner);
    // Create an ArrayAdapter using the string array and a default spinner layout
    ArrayAdapter<CharSequence> catAdapter = ArrayAdapter.createFromResource(this,
            R.array.category_array, android.R.layout.simple_spinner_item);
    // Specify the layout to use when the list of choices appears
    catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // Apply the adapter to the spinner
    category.setAdapter(catAdapter);

    state = (Spinner) findViewById(R.id.state_spinner);
    // Create an ArrayAdapter using the string array and a default spinner layout
    ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(this,
            R.array.state_array, android.R.layout.simple_spinner_item);
    // Specify the layout to use when the list of choices appears
    stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // Apply the adapter to the spinner
    category.setAdapter(stateAdapter);

    btnUpdate = (Button) findViewById(R.id.update_btn);
    btnDelete = (Button) findViewById(R.id.delete_btn);

    Bundle extras = getIntent().getExtras();
    if (extras != null) {
      tvTitle.setText(extras.getString("TITLE"));
      tvPrice.setText(extras.getString("PRICE"));
      state.setText(extras.getString("STATE"));
      category.setText(extras.getString("CATEGORY"));
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