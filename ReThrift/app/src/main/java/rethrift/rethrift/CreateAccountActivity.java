package rethrift.rethrift;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateAccountActivity extends AppCompatActivity {

  private TextInputEditText firstName, lastName, email, phone, username, password, verifyPassword;
  private TextInputLayout passwordLayout, verifyLayout;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_account);

    // bind EditText fields
    firstName = (TextInputEditText) findViewById(R.id.firstname_field);
    lastName = (TextInputEditText) findViewById(R.id.lastname_field);
    email = (TextInputEditText) findViewById(R.id.email_field);
    phone = (TextInputEditText) findViewById(R.id.phone_field);
    username = (TextInputEditText) findViewById(R.id.username_field);

    password = (TextInputEditText) findViewById(R.id.password_field);
    password.setTypeface(Typeface.DEFAULT);
    password.setTransformationMethod(new PasswordTransformationMethod());

    verifyPassword = (TextInputEditText) findViewById(R.id.verify_password_field);
    verifyPassword.setTypeface(Typeface.DEFAULT);
    verifyPassword.setTransformationMethod(new PasswordTransformationMethod());

    passwordLayout = (TextInputLayout) findViewById(R.id.password_layout);
    passwordLayout.setTypeface(Typeface.DEFAULT);

    verifyLayout = (TextInputLayout) findViewById(R.id.verify_layout);
    verifyLayout.setTypeface(Typeface.DEFAULT);
  }

  // send user account info
  public void createAcct(View view) {
    String res = checkFields();
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
      // check that they have a connection
      ConnectivityManager cxnMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo networkInfo = cxnMgr.getActiveNetworkInfo();

      if (networkInfo != null && networkInfo.isConnected()) {
        try {
          String stringUrl = "http://rethrift-1.herokuapp.com/users/create";
          String createRes = new CreateAccountTask().execute(stringUrl).get();

          if (createRes.equals("good")) {
            // go to SalesboardActivity
            Intent intent = new Intent(this, SalesboardActivity.class);
            intent.putExtra("USERNAME", username.getText().toString());
            intent.putExtra("FIRSTNAME", firstName.getText().toString());
            startActivity(intent);
          } else {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage(createRes)
                    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                      public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                      }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
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
  }

  // check user fields
  public String checkFields() {
    // check name
    if (firstName.getText().toString().equals("") || lastName.getText().toString().equals("")) {
      return "Please enter your name";
    }

    // check email
    Pattern emailPattern = Pattern.compile(".+@.+\\.[a-z]+");
    Matcher emailMatcher = emailPattern.matcher(email.getText().toString());
    if (!emailMatcher.matches()) {
      return "Please enter a valid email";
    }

    // check phone number
    Pattern phonePattern = Pattern.compile("^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$");
    Matcher phoneMatcher = phonePattern.matcher(phone.getText().toString());
    if (!phoneMatcher.matches()) {
      return "Please enter a valid phone number";
    }

    // check username
    Pattern userPattern = Pattern.compile("(\\w+\\d*){3,12}");
    Matcher userMatcher = userPattern.matcher(username.getText().toString());
    if (!userMatcher.matches()) {
      return "Please enter a valid username";
    }
    try {
      String stringUrl = "http://rethrift-1.herokuapp.com/users/" + username.getText().toString();
      String result = new CheckUsernameTask().execute(stringUrl).get();
      Log.d("RESULT OF CHECKUSERNAME", result);
      if (!result.equals("good")) {
        return result;
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }

    // check pw
    Pattern pwPattern = Pattern.compile("[.\\S]{6,18}");
    Matcher pwMatcher = pwPattern.matcher(password.getText().toString());
    if (!pwMatcher.matches()) {
      return "Please enter a valid password";
    }

    // check verify pw
    if (!verifyPassword.getText().toString().equals(password.getText().toString())) {
      return "Please verify your password";
    }

    return "good";
  }


  // AsyncTask that checks if a username already exists.
  private class CheckUsernameTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls) {
      try {
        return checkUsername(urls[0]);
      } catch (IOException e) {
        e.printStackTrace();
        return "Unable to check username. Please try again later.";
      }
    }

    @Override
    protected void onPostExecute(String result) {
      Log.d("CHECK USERNAME", result);
    }

    private String checkUsername(String myurl) throws IOException {
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
        Log.d("HTTP CONTENT", userAcct);
        conn.disconnect();
        return "Username already exists";
      } catch (FileNotFoundException e) {
        return "good";
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

  // AsyncTask which creates the account in the background
  private class CreateAccountTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls) {
      try {
        return createAccountUrl(urls[0]);
      } catch (IOException e) {
        e.printStackTrace();
        return "Unable to create account. Please try again later.";
      }
    }

    @Override
    protected void onPostExecute(String result) {
      Log.d("CREATE ACCOUNT", result);
    }

    private String createAccountUrl(String myurl) throws IOException {
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

        JSONObject userAcctJson = new JSONObject();
        try {
          userAcctJson.put("firstname", firstName.getText().toString())
                      .put("lastname", lastName.getText().toString())
                      .put("email", email.getText().toString())
                      .put("phone", phone.getText().toString())
                      .put("username", username.getText().toString())
                      .put("password", password.getText().toString());

          Log.d("JSONOBJECT", userAcctJson.toString(2));
          // Write JSONObject to output stream
          writeIt(os, userAcctJson.toString(2));
          int response = conn.getResponseCode();
          conn.disconnect();
          return "good";
        } catch (JSONException e) {
          e.printStackTrace();
          return "Unable to create account. Please try again later.";
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
