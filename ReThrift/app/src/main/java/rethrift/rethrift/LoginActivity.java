package rethrift.rethrift;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
  }

  public void createAcct(View view){
    Intent intent = new Intent(this, CreateAccountActivity.class);
    startActivity(intent);
  }

}