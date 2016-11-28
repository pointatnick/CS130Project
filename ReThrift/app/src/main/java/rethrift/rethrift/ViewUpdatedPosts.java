package rethrift.rethrift;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

/**
 * Created by MC on 11/24/2016.
 */

public class ViewUpdatedPosts extends AppCompatActivity {
    private String updatedPosts;
    private RecyclerView updatedCardRecList;
    private String user;
    private int userLength;
    private PostAdapter pa;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updated_posts);

        // Obtain updated watchlist posts from previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            updatedPosts = extras.getString("UPDATED_POSTS");

            //parse user from string passed in
            userLength = Character.getNumericValue(updatedPosts.charAt(0));
            user = updatedPosts.substring(1,userLength + 1);

            //get updates post list
            updatedPosts = updatedPosts.substring(userLength + 1, updatedPosts.length());

            Log.d("updated list", updatedPosts);
            //display posts
            updatedCardRecList = (RecyclerView) findViewById(R.id.updated_card_list);
            updatedCardRecList.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(this);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            updatedCardRecList.setLayoutManager(llm);

            try {
                //pa = new PostAdapter(new getPosts().execute(updatedPosts).get(), user);
                pa.setInfo(new getPosts().execute(updatedPosts).get(), user);
                updatedCardRecList.setAdapter(pa);
            } catch(ExecutionException e){
                e.printStackTrace();
            } catch(InterruptedException ie){
                ie.printStackTrace();
            }

        }
        else{
            Log.d("EMPTY_LIST", "Notifications is not working correctly...");
        }

        pa = new PostAdapter();
    }

    private class getPosts extends AsyncTask<String, Void, List<Post>>{
        @Override
        protected List<Post> doInBackground(String... params) {
                return retrievePosts(params[0]);
        }
    }

    public List<Post> retrievePosts(String uPosts){
        try{
            JSONArray JSONresultList = new JSONArray(uPosts);
            JSONArray userJsonArray = findUsers(JSONresultList);
            List<Post> resultList = new ArrayList<>();
            for(int i=0; i < JSONresultList.length(); i++){
                JSONObject joPost = JSONresultList.getJSONObject(i);
                Log.d("POST", JSONresultList.toString());
                JSONObject userJson = userJsonArray.getJSONObject(i);
                Log.d("USER", userJson.toString());
                resultList.add(
                        new Post(joPost.getInt("id"),
                                joPost.getString("title"),
                                "$" + joPost.getInt("price"),
                                joPost.getString("state"),
                                joPost.getDouble("latitude"),
                                joPost.getDouble("longitude"),
                                joPost.getString("description"),
                                joPost.getString("category"),
                                userJson.getString("firstname") + userJson.getString("lastname"),
                                userJson.getString("username"),
                                joPost.getString("image")));
            }
            return resultList;
        }catch(Exception e){
            Log.d("PRINT_POSTS:", "FAILED");
            return null;
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
                    //conn.setReadTimeout(10000 /* milliseconds */);
                    //conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    // Starts the query
                    Log.d("CONNECT NOW", "ENTERING CONNECT");
                    conn.connect();
                    Log.d("GET RESPONSE", "Response Code: " + conn.getResponseMessage()); //? not reaching here?
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
}
