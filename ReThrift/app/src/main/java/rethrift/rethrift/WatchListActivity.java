package rethrift.rethrift;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class WatchListActivity extends AppCompatActivity {

    private RecyclerView cardList;
    String user, name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.watchlist);

        // get extras
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user = extras.getString("USERNAME");
            name = extras.getString("FIRSTNAME");
        }

        cardList = (RecyclerView) findViewById(R.id.card_list);
        cardList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        cardList.setLayoutManager(llm);

        retrievePosts(cardList);
    }

    @Override
    public void onResume() {
        retrievePosts(cardList);
        super.onResume();
    }

    public void retrievePosts(RecyclerView recView) {
        try {
            String stringUrl = "http://rethrift-1.herokuapp.com/users/" + user + "/watchlist";
            WatchListAdapter ca = new WatchListAdapter(new GetWatchlistTask().execute(stringUrl).get(), user);
            recView.setAdapter(ca);
        } catch (InterruptedException e) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Unable to load your posts")
                    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } catch (ExecutionException e) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Unable to load your posts")
                    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    // AsyncTask that gets the posts the user created
    private class GetWatchlistTask extends AsyncTask<String, Void, List<Post>> {
        @Override
        protected List<Post> doInBackground(String... urls) {
            try {
                return getWatchlist(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        private List<Post> getWatchlist(String myurl) throws IOException {
            InputStream is = null;
            int len = 5000;

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
                String postArray = readIt(is, len);
                Log.d("RESULT", postArray);

                try {
                    JSONArray postJsonArray = new JSONArray(postArray);
                    List<Post> posts = new ArrayList<>();
                    for (int i = 0; i < postJsonArray.length(); i++) {
                        JSONObject postJson = postJsonArray.getJSONObject(i);
                        Log.d("POST", postJson.toString());
                        posts.add(
                                new Post(postJson.getInt("id"),
                                         postJson.getString("title"),
                                         "$" + postJson.getInt("price"),
                                         postJson.getString("state"),
                                         postJson.getDouble("latitude"),
                                         postJson.getDouble("longitude"),
                                         postJson.getString("description"),
                                         postJson.getString("category"),
                                         name,
                                         user,
                                         postJson.getString("image")));
                    }
                    return posts;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
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
