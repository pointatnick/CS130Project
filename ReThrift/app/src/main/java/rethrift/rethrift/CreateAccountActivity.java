package rethrift.rethrift;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CreateAccountActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_account);
  }

  public void createAcct(View view) {
    // send the user account info

    // http://localhost:3000/users/post
    try {
      URL url = new URL("http://localhost:3000/users");
      HttpURLConnection cxn = (HttpURLConnection) url.openConnection();
      cxn.setDoOutput(true);
      cxn.setRequestMethod("POST");
      cxn.setRequestProperty("Content-Type", "application/json");

      String input = "{\"qty\":100,\"name\":\"iPad 4\"}";

      OutputStream os = cxn.getOutputStream();
      os.write(input.getBytes());
      os.flush();

      if (cxn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
        throw new RuntimeException("Failed -- HTTP error code: " + cxn.getResponseCode());
      }

      BufferedReader br = new BufferedReader(new InputStreamReader(cxn.getInputStream()));

      String output;
      System.out.println("Output from server .... \n");
      while ((output = br.readLine()) != null) {
        System.out.println(output);
      }

      cxn.disconnect();
      Log.d("POST", "Successfully sent JSON post request");

    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
