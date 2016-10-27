package rethrift.rethrift;

import android.content.Context;
import android.content.DialogInterface;
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

public class CreateAccountActivity extends AppCompatActivity {

  private EditText firstName, lastName, email, phoneNo, username, password, verifyPassword;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_account);

    // bind EditText fields
    firstName = (EditText) findViewById(R.id.firstname_field);
    lastName = (EditText) findViewById(R.id.lastname_field);
    email = (EditText) findViewById(R.id.email_field);
    phoneNo = (EditText) findViewById(R.id.phone_field);
    username = (EditText) findViewById(R.id.username_field);
    password = (EditText) findViewById(R.id.password_field);
    verifyPassword = (EditText) findViewById(R.id.verify_password_field);
  }

  // send user account info
  public void createAcct(View view) {

    // TODO: add function that checks all fields

    // TODO: change to Heroku address
    String stringUrl = "http://192.168.0.22:3000/users";
    // check that they have a connection
    ConnectivityManager cxnMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = cxnMgr.getActiveNetworkInfo();
    if (networkInfo != null && networkInfo.isConnected()) {
      new CreateAccountTask().execute(stringUrl);
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
  }


  private class CreateAccountTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls) {
      try {
        return createAccountUrl(urls[0]);
      } catch (IOException e) {
        return "Unable to create account. Please try again later.";
      }
    }

    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    private String createAccountUrl(String myurl) throws IOException {
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
        int response = conn.getResponseCode();
        Log.d("DEBUG HTTP EXAMPLE", "The response is: " + response);
        os = conn.getOutputStream();

        JSONObject userAcctJson = new JSONObject();
        try {
          userAcctJson.put("first name", firstName.getText().toString())
                      .put("last name", lastName.getText().toString())
                      .put("email", email.getText().toString())
                      .put("phone number", phoneNo.getText().toString())
                      .put("username", username.getText().toString())
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
      writer.close();
    }


    /* saving for later
    // Reads an InputStream and converts it to a String.
    private String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
      Reader reader = null;
      reader = new InputStreamReader(stream, "UTF-8");
      char[] buffer = new char[len];
      reader.read(buffer);
      return new String(buffer);
    }
    */
  }
}
