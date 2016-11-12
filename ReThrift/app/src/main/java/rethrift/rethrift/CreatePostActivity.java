package rethrift.rethrift;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import java.text.NumberFormat;

public class CreatePostActivity extends AppCompatActivity {
    private TextInputEditText title, price;
    private EditText description;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_create_post);
        title = (TextInputEditText) findViewById(R.id.title_field);
        price = (TextInputEditText) findViewById(R.id.price_field);
        price.addTextChangedListener(new CurrencyTextWatcher());
        description = (EditText) findViewById(R.id.description_field);
        description.setHorizontallyScrolling(false);
        description.setMaxLines(Integer.MAX_VALUE);
    }

    public void addPost(View view){
        Intent intent = new Intent(this, SalesboardActivity.class);
        startActivity(intent);
    }

    private class CurrencyTextWatcher implements TextWatcher {
        private String current = "";

        public CurrencyTextWatcher() {}

        public void afterTextChanged(Editable s) {}

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(!s.toString().equals(current)){
                price.removeTextChangedListener(this);

                String cleanString = s.toString().replaceAll("[$,.]", "");

                double parsed = Double.parseDouble(cleanString);
                String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

                current = formatted;
                price.setText(formatted);
                price.setSelection(formatted.length());

                price.addTextChangedListener(this);
            }
        }
    }
}
