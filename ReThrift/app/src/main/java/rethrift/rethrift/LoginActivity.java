package rethrift.rethrift;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {

  private EditText username, password;
  private String user, firstname, lastname, email, phoneNumber;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    username = (EditText) findViewById(R.id.username_field);
    password = (EditText) findViewById(R.id.password_field);
    password.setTypeface(Typeface.DEFAULT);
    password.setTransformationMethod(new PasswordTransformationMethod());
  }

  @Override
  public void onPause() {
    super.onPause();
    finish();
  }

  public void createAcct(View view){
    Intent intent = new Intent(this, CreateAccountActivity.class);
    startActivity(intent);
  }

  public void login(View view) {
    user = username.getText().toString();
    String stringUrl = "http://rethrift-1.herokuapp.com/users/" + user;
    // check that they have a connection
    ConnectivityManager cxnMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = cxnMgr.getActiveNetworkInfo();
    if (networkInfo != null && networkInfo.isConnected()) {
      // authenticate
      try {
        String res = new LoginTask().execute(stringUrl).get();

        if (!res.equals("good")) {
          new AlertDialog.Builder(this)
                  .setTitle("Error")
                  .setMessage(res)
                  .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                      dialog.dismiss();
                    }
                  })
                  .setIcon(android.R.drawable.ic_dialog_alert)
                  .show();
        } else {
          // go to MainActivity
          Intent intent = new Intent(this, SalesboardActivity.class);
          intent.putExtra("USERNAME", user);
          intent.putExtra("FIRSTNAME", firstname);
          intent.putExtra("LASTNAME", lastname);
          intent.putExtra("EMAIL", email);
          intent.putExtra("PHONE", phoneNumber);
          startActivity(intent);
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (ExecutionException e) {
        e.printStackTrace();
      }
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

  // AsyncTask that checks if the password is correct and logs in the user
  private class LoginTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls) {
      try {
        return checkLogin(urls[0]);
      } catch (IOException e) {
        e.printStackTrace();
        return "Unable to check username. Please try again later.";
      }
    }

    @Override
    protected void onPostExecute(String result) {
      Log.d("CHECK USERNAME", result);
    }

    private String checkLogin(String myurl) throws IOException {
      InputStream is = null;
      int len = 500;

      try {
        URL url = new URL(myurl);
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
        String userAcct = readIt(is, len);

        // check password
        try {
          JSONObject userAcctJson = new JSONObject(userAcct);
          if (!userAcctJson.getString("password").equals(password.getText().toString())) {
            return "Invalid password";
          } else {
            firstname = userAcctJson.getString("firstname");
            lastname = userAcctJson.getString("lastname");
            email = userAcctJson.getString("email");
            phoneNumber = userAcctJson.getString("phone");
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }
        return "good";
      } catch (FileNotFoundException e) {
        return "Username does not exist";
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
