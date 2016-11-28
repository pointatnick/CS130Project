package rethrift.rethrift;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvUser, tvEmail, tvPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvName = (TextView) findViewById(R.id.user_profile_name);
        tvUser = (TextView) findViewById(R.id.username);
        tvEmail = (TextView) findViewById(R.id.email);
        tvPhone = (TextView) findViewById(R.id.phone);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String name = extras.getString("FIRSTNAME") + " " + extras.getString("LASTNAME");
            tvName.setText(name);
            tvUser.setText(extras.getString("USERNAME"));
            tvEmail.setText(extras.getString("EMAIL"));
            tvPhone.setText(extras.getString("PHONE"));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        finish();
    }
}
