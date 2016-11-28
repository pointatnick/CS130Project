package rethrift.rethrift;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class ViewWatchlistPostActivity extends AppCompatActivity {

  private TextView tvTitle, tvPrice, tvState, tvLocation, tvCategory, tvDescription, tvName, tvUsername;
  private ImageView ivImage;
  private String user;
  private int postId;

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
    ivImage = (ImageView) findViewById(R.id.imageView);

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
      if (!extras.getString("IMAGE").equals("null")) {
        Log.d("PATH", extras.getString("IMAGE"));
        String path = extras.getString("IMAGE").substring(5);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        ivImage.setImageBitmap(bitmap);
      }
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    finish();
  }

  // remove from watchlist
  public void removeFromWatchlist(View view) {
    String stringUrl = "http://rethrift-1.herokuapp.com/users/" + user + "/unwatch";
    new UnwatchTask().execute(stringUrl);
    finish();
  }
  // open phone dialer, fill phone with phone number
  public void contactSeller(View view) {
    try {
      String stringUrl = "http://rethrift-1.herokuapp.com/users/" + tvUsername.getText().toString();
      String phoneNumber = new FindNumberTask().execute(stringUrl).get();

      if (phoneNumber.equals("bad")) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(phoneNumber)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                  }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
      } else {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
  }

  // AsyncTask that removes post from watchlist
  private class UnwatchTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls) {
      try {
        return unwatchPost(urls[0]);
      } catch (IOException e) {
        e.printStackTrace();
        return "Unable to remove from watchlist";
      }
    }

    @Override
    protected void onPostExecute(String result) {
      Log.d("UNWATCH POST", result);
    }

    private String unwatchPost(String myurl) throws IOException {
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
          return "Unable to remove from watchlist";
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

  // AsyncTask that grabs seller's phone number
  private class FindNumberTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls) {
      try {
        return findNumber(urls[0]);
      } catch (IOException e) {
        e.printStackTrace();
        return "Could not find phone number";
      }
    }

    private String findNumber(String myUrl) throws IOException {
      InputStream is = null;
      int len = 5000;
      try {
        URL url = new URL(myUrl);
        Log.d("URL", "" + url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        // Starts the query
        conn.connect();
        is = conn.getInputStream();

        // Convert the InputStream into a string
        String userInfo = readIt(is, len);
        Log.d("RESULT", userInfo);
        try {
          JSONObject userInfoJson = new JSONObject(userInfo);
          return userInfoJson.getString("phone");
        } catch (JSONException e) {
          e.printStackTrace();
          return "Could not find phone number";
        }
      } finally {
        // Makes sure that the InputStream is closed after the app is finished using it.
        if (is != null) {
          is.close();
        }
      }
    }

    // Reads an InputStream and converts it to a String.
    private String readIt(InputStream stream, int len) throws IOException {
      Reader reader = new InputStreamReader(stream, "UTF-8");
      char[] buffer = new char[len];
      reader.read(buffer);
      return new String(buffer);
    }
  }
}
