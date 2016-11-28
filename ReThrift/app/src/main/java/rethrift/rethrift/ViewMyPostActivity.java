package rethrift.rethrift;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

public class ViewMyPostActivity extends AppCompatActivity {
  private int postId;
  private TextView tvTitle, tvPrice, tvState, tvLocation, tvCategory, tvDescription, tvName, tvUsername;
  private ImageView ivImage;
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
    ivImage = (ImageView) findViewById(R.id.imageView);

    btnEdit = (Button) findViewById(R.id.edit_btn);

    Bundle extras = getIntent().getExtras();
    if (extras != null) {
      postId = extras.getInt("ID");
      tvTitle.setText(extras.getString("TITLE"));
      tvPrice.setText(extras.getString("PRICE"));
      tvState.setText(extras.getString("STATE"));
      tvLocation.setText(extras.getString("LOCATION"));
      tvCategory.setText(extras.getString("CATEGORY"));
      tvDescription.setText(extras.getString("DESCRIPTION"));
      tvName.setText(extras.getString("NAME"));
      tvUsername.setText(extras.getString("USERNAME"));
      if (getIntent().hasExtra("IMAGE")) {
        String path = extras.getString("IMAGE").substring(5);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        ivImage.setImageBitmap(bitmap);
      }
    }
  }

  // edit post
  public void editPost(View view) {
    Intent intent = new Intent(this, EditPostActivity.class);
    intent.putExtra("ID", postId);
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

  // delete post
  public void deletePost(View view) {
    String stringUrl = "http://rethrift-1.herokuapp.com/posts/delete";
    new DeletePostTask().execute(stringUrl);
    finish();
  }

  // AsyncTask that deletes the post
  private class DeletePostTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls) {
      try {
        return deletePost(urls[0]);
      } catch (IOException e) {
        e.printStackTrace();
        return "Unable to delete post.";
      }
    }

    @Override
    protected void onPostExecute(String result) {
      Log.d("DELETE POST", result);
    }

    private String deletePost(String myurl) throws IOException {
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
          return "Unable to delete post";
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
