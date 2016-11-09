package rethrift.rethrift;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

  private EditText username, password;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    username = (EditText) findViewById(R.id.username_field);
    password = (EditText) findViewById(R.id.password_field);
  }

  public void createAcct(View view){
    Intent intent = new Intent(this, CreateAccountActivity.class);
    startActivity(intent);
  }

  public void login(View view) {
    String stringUrl = "http://rethrift-1.herokuapp.com/login";
    // check that they have a connection
    ConnectivityManager cxnMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = cxnMgr.getActiveNetworkInfo();
    if (networkInfo != null && networkInfo.isConnected()) {
      // authenticate
      new LoginTask().execute(stringUrl, username.getText().toString(), password.getText().toString());
    } else {
      new AlertDialog.Builder(this)
              .setTitle("Error")
              .setMessage("No network connection available.")
              .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
                }
              })
              .setIcon(android.R.drawable.ic_dialog_alert)
              .show();
    }

    // go to MainActivity
    Intent intent = new Intent(this, SalesboardActivity.class);
    startActivity(intent);
  }

  private class LoginTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... strings) {
      try {
        return loginUser(strings[0]);
      } catch (IOException e) {
        e.printStackTrace();
        return "Unable to login. Please try again later.";
      }
    }

    @Override
    protected void onPostExecute(String result) {
      Log.d("LOGIN", result);
    }

    private String loginUser(String myurl) throws IOException {
      OutputStream os = null;

      try {
        URL url = new URL(myurl);
        Log.d("URL", "" + url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");

        // Starts the query
        conn.connect();
        os = conn.getOutputStream();

        JSONObject userAcctJson = new JSONObject();
        try {
          userAcctJson.put("username", username.getText().toString())
                      .put("password", password.getText().toString());

          Log.d("JSONOBJECT", userAcctJson.toString(2));
          // Write JSONObject to output stream
          writeIt(os, userAcctJson.toString(2));

          return "successfully created account";
        } catch (JSONException e) {
          e.printStackTrace();
          return "couldn't create account";
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
