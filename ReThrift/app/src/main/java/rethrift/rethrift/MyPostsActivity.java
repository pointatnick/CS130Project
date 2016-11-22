package rethrift.rethrift;

/**
 * Created by kexinyu on 11/18/2016.
 */

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

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

public class MyPostsActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        // generate dummy post list
        ArrayList<String> posts = new ArrayList<String>();
        posts.add("Selling Queen Size Bed Frame");
        posts.add("Silver iPhone 5s");
        posts.add("Selling Like New White Sectional Couch");

        //instantiate adapter
        MyPostsAdapter adapter = new MyPostsAdapter(posts, this);

        //handle listview and assign adapter
        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(adapter);

    }

    // AsyncTask that gets the posts the user created
    private class GetMyPostsTask extends AsyncTask<String, Void, List<Post>> {
        @Override
        protected List<Post> doInBackground(String... urls) {
            try {
                JSONArray postJsonArray = getPosts(urls[0]);
                JSONArray userJsonArray = findUsers(postJsonArray);
                return constructPosts(postJsonArray, userJsonArray);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        private JSONArray getPosts(String myurl) throws IOException {
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
                    return new JSONArray(postArray);
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

        private JSONArray findUsers(JSONArray postJsonArray) throws IOException {
            try {
                String stringUrl = "http://rethrift-1.herokuapp.com/users/";
                JSONArray userJsonArray = new JSONArray();
                for (int i = 0; i < postJsonArray.length(); i++) {
                    JSONObject postJson = postJsonArray.getJSONObject(i);
                    int userId = postJson.getInt("UserId");
                    InputStream is = null;
                    int len = 5000;
                    try {
                        URL url = new URL(stringUrl + userId);
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
                        JSONObject userInfoJson = new JSONObject(userInfo);
                        userJsonArray.put(userInfoJson);
                    } finally {
                        // Makes sure that the InputStream is closed after the app is finished using it.
                        if (is != null) {
                            is.close();
                        }
                    }
                }
                return userJsonArray;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        // Reads an InputStream and converts it to a String.
        private String readIt(InputStream stream, int len) throws IOException {
            Reader reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }

        private List<Post> constructPosts(JSONArray postJsonArray, JSONArray userJsonArray) {
            try {
                List<Post> posts = new ArrayList<>();
                for (int i = 0; i < postJsonArray.length(); i++) {
                    JSONObject postJson = postJsonArray.getJSONObject(i);
                    Log.d("POST", postJson.toString());
                    JSONObject userJson = userJsonArray.getJSONObject(i);
                    Log.d("USER", userJson.toString());
                    posts.add(
                            new Post(postJson.getString("title"),
                                    // TODO: change to getDouble
                                    "$" + postJson.getString("price"),
                                    postJson.getString("state"),
                                    postJson.getDouble("latitude"),
                                    postJson.getDouble("longitude"),
                                    postJson.getString("description"),
                                    postJson.getString("category"),
                                    userJson.getString("firstname") + userJson.getString("lastname"),
                                    userJson.getString("username")));
                }
                return posts;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}