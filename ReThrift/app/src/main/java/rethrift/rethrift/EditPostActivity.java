package rethrift.rethrift;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class EditPostActivity extends AppCompatActivity {
  private TextView tvTitle, tvPrice, tvState, tvLocation, tvCategory, tvDescription, tvName, tvUsername;
  private Button btnUpdate, btnDelete;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_post);

    btnUpdate = (Button) findViewById(R.id.update_btn);
    btnDelete = (Button) findViewById(R.id.delete_btn);
  }

  public void updatePost(View view) {


    //finish();
  }

  // TODO: delete post
  public void deletePost(View view) {

  }
}
