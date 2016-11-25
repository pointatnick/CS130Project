package rethrift.rethrift;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

public class ViewWatchlistPostActivity extends AppCompatActivity {

  private TextView tvTitle, tvPrice, tvState, tvLocation, tvCategory, tvDescription, tvName, tvUsername;
  private String user;
  private int postId;
  private Button btnWatchlist, btnContact;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_watchlist_post);

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
      user = extras.getString("CURRENT USER");
      postId = extras.getInt("ID");
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

  //TODO: remove from watchlist
  public void removeFromWatchlist(View view) {
    String stringUrl = "http://rethrift-1.herokuapp.com/users/" + user + "/unwatch";
    new WatchTask().execute(stringUrl);
    finish();
  }
  // TODO: contacts seller
  public void contactSeller(View view) {

  }

  // AsyncTask that deletes the post
  private class WatchTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls) {
      try {
        return watchPost(urls[0]);
      } catch (IOException e) {
        e.printStackTrace();
        return "Unable to add to watchlist";
      }
    }

    @Override
    protected void onPostExecute(String result) {
      Log.d("WATCH POST", result);
    }

    private String watchPost(String myurl) throws IOException {
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

        try {
          JSONObject postJson = new JSONObject();
          postJson.put("postID", postId);

          // Write JSONObject to output stream
          writeIt(os, postJson.toString(2));
          int response = conn.getResponseCode();
          conn.disconnect();
          return "good";
        } catch (JSONException e) {
          e.printStackTrace();
          return "Unable to add to watchlist";
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
