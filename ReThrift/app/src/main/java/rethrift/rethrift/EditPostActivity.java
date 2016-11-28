package rethrift.rethrift;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;


public class EditPostActivity extends AppCompatActivity {
  private TextView tvTitle, tvPrice, tvDescription;
  private Spinner state, category;
  private int postId;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_post);

    tvTitle = (TextView) findViewById(R.id.title_field);
    tvPrice = (TextView) findViewById(R.id.price_field);
    tvDescription = (TextView) findViewById(R.id.description_field);

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
    state.setAdapter(stateAdapter);

    Bundle extras = getIntent().getExtras();
    if (extras != null) {
      postId = extras.getInt("ID");
      tvTitle.setText(extras.getString("TITLE"));
      String[] parts = extras.getString("PRICE").split("[$]");
      tvPrice.setText(parts[1]);
      tvDescription.setText(extras.getString("DESCRIPTION"));
    }
  }

  // edit post
  public void updatePost(View view) {
    String stringUrl = "http://rethrift-1.herokuapp.com/posts/update";
    new UpdatePostTask().execute(stringUrl);
    Intent intent = new Intent(this, MyPostsActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    startActivity(intent);
  }

  private class UpdatePostTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls) {
      try {
        return updatePost(urls[0]);
      } catch (IOException e) {
        e.printStackTrace();
        return "Unable to update post";
      }
    }

    @Override
    protected void onPostExecute(String result) {
      Log.d("CREATE ACCOUNT", result);
    }

    private String updatePost(String myurl) throws IOException {
      OutputStream os = null;

      try {
        URL url = new URL(myurl);
        Log.d("URL", "" + url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Starts the query
        conn.connect();
        os = conn.getOutputStream();

        JSONObject postInfoJson = new JSONObject();
        JSONObject updateVals = new JSONObject();
        try {
          updateVals.put("title", tvTitle.getText().toString())
                    .put("description", tvDescription.getText().toString())
                    .put("price", Double.parseDouble(tvPrice.getText().toString()))
                    .put("category", category.getSelectedItem().toString())
                    .put("state", state.getSelectedItem().toString());

          postInfoJson.put("postID", postId)
                      .put("updateVals", updateVals);

          Log.d("JSONOBJECT", postInfoJson.toString(2));
          // Write JSONObject to output stream
          writeIt(os, postInfoJson.toString(2));
          int response = conn.getResponseCode();
          conn.disconnect();
          return "good";
        } catch (JSONException e) {
          e.printStackTrace();
          return "Unable to update post";
        }
        // Makes sure that the OutputStream is closed after the app is finished using it.
      } finally {
        if (os != null) {
          os.close();
        }
      }
    }

    // Writes an OutputStream
    private void writeIt(OutputStream stream, String msg) throws IOException {
      Writer writer = new OutputStreamWriter(stream, "UTF-8");
      writer.write(msg);
      writer.flush();
      writer.close();
    }
  }
}
