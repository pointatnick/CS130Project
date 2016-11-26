package rethrift.rethrift;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {

  private List<Post> postList;
  private String user;
  private static final String API_KEY = "AIzaSyCpnQeSQBSuxb_hITDW63AcVzCeLuT5hcg";

  public PostAdapter(List<Post> postList, String user) {
    this.postList = postList;
    this.user = user;
  }

  @Override
  public int getItemCount() {
    return postList.size();
  }

  @Override
  public void onBindViewHolder(PostHolder postHolder, int i) {
    Post ci = postList.get(i);
    postHolder.postId = ci.getId();
    postHolder.tvTitle.setText(ci.getTitle());
    postHolder.tvPrice.setText(ci.getPrice());
    postHolder.tvLocation.setText(findAddress(ci.getLatitude(), ci.getLongitude()));
    postHolder.state = ci.getState();
    postHolder.description = ci.getDescription();
    postHolder.category = ci.getCategory();
    postHolder.name = ci.getName();
    postHolder.username = ci.getUsername();
    if (ci.getImage() != null) {
      Uri imageUri = Uri.parse(ci.getImage());
      postHolder.ivImage.setImageURI(imageUri);
      postHolder.image = ci.getImage();
    }
  }

  public String findAddress(double latitude, double longitude) {
    try {
      String location = "" + latitude + "," + longitude;
      String stringUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + location + "&radius=5000&key=" + API_KEY;
      return new FindAddressTask().execute(stringUrl).get();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
    return null;
  }

  class FindAddressTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls) {
      try {
        return findAddress(urls[0]);
      }
      catch(IOException e){
        e.printStackTrace();
        return null;
      }
    }

    private String findAddress(String myURL) throws IOException {
      InputStream is = null;
      int len = 8192;

      try {
        URL url = new URL(myURL);
        Log.d("URL", "" + url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        is = conn.getInputStream();
        String addressArray = readIt(is, len);

        try {
          JSONObject addressJsonArray = new JSONObject(addressArray);
          JSONArray resultsJsonArray = addressJsonArray.getJSONArray("results");
          JSONObject resultJsonObject = resultsJsonArray.getJSONObject(1);
          String vicinity = resultJsonObject.getString("vicinity");
          Log.d("RESULT", resultJsonObject.toString(2));
          return vicinity;
        } catch (JSONException e) {
          e.printStackTrace();
          return "Address unavailable";
        }
      } catch (FileNotFoundException e){
        return "Address unavailable";
      } finally {
        if(is != null){
          is.close();
        }
      }
    }

    // Reads an InputStream and converts it to a String.
    private String readIt(InputStream stream, int len) throws IOException {
      StringBuilder response = new StringBuilder();
      BufferedReader input = new BufferedReader(new InputStreamReader(stream), len);
      String strLine;
      while ((strLine = input.readLine()) != null) {
        response.append(strLine);
      }
      input.close();
      return response.toString();
    }
  }

  @Override
  public PostHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_layout, viewGroup, false);
    Context context = viewGroup.getContext();
    return new PostHolder(itemView, context, user);
  }

  public static class PostHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    protected TextView tvTitle, tvPrice, tvLocation;
    protected ImageView ivImage;
    protected String state, description, category, name, username, user;
    protected String image;
    private Context context;
    private int postId;

    public PostHolder(View view, Context context, String currentUser) {
      super(view);
      tvTitle =  (TextView) view.findViewById(R.id.title);
      tvPrice = (TextView) view.findViewById(R.id.price);
      tvLocation = (TextView) view.findViewById(R.id.location);
      ivImage = (ImageView) view.findViewById(R.id.card_image);
      user = currentUser;
      this.context = context;
      view.setOnClickListener(this);
    }

    // Handles the row being being clicked
    @Override
    public void onClick(View view) {
      Intent intent = new Intent(context, ViewPostActivity.class);
      intent.putExtra("CURRENT USER", user);
      intent.putExtra("ID", postId);
      intent.putExtra("TITLE", tvTitle.getText().toString());
      intent.putExtra("PRICE", tvPrice.getText().toString());
      intent.putExtra("STATE", state);
      intent.putExtra("LOCATION", tvLocation.getText().toString());
      intent.putExtra("DESCRIPTION", description);
      intent.putExtra("CATEGORY", category);
      intent.putExtra("NAME", name);
      intent.putExtra("USERNAME", username);
      intent.putExtra("IMAGE", image);
      context.startActivity(intent);
    }
  }
}
