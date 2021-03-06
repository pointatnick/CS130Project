package rethrift.rethrift;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;


public class SalesboardActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private String user, firstname, lastname, email, phone;
    private Spinner categorySpinner;
    private Spinner priceSpinner;
    private RecyclerView cardList;
    private PostAdapter pa;

    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    private double mLatitude, mLongitude;
    private final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 1;

    // /*
    //for search input (mc)
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private SearchView searchView = null;
    private int check = 0;
    private int checkPrice = 0;
    private boolean doingSearch = false;
    //1: keyterms, 2: category, 3: price
    private int whichSearch = 0;

    private int hit = 0;


    //for watchlist status update
    private String prevDateTimeString;

    private Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //check for updates
            try{
                //get time here
                //DateFormat df = DateFormat.getDateTimeInstance();
                //df.setTimeZone(TimeZone.getTimeZone("GMT+0"));
                //String prevDateTimeString = df.format(new Date());
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                //2016-11-28T02:57:15.977Z
                Log.d("NOTIF", "ENTERED");
                isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Log.d("NOTIF", "ENTERED2");
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.SECOND, -20);
                //cal.add(Calendar.HOUR, 8);
                prevDateTimeString = isoFormat.format(cal.getTime());
                Log.d("NOTIF STRING", prevDateTimeString);

                //prevDateTimeString = DateFormat.getDateInstance(DateFormat.LONG, Locale.UK).format(new Date());
                String stringUrl = "http://rethrift-1.herokuapp.com/users/" + user + "/notifications/?timestamp=";
                stringUrl = stringUrl + prevDateTimeString;
                String watchListUpdates = new GetWListUpdateTask().execute(stringUrl).get();
                //prevDateTimeString = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK).format(new Date());

                if(watchListUpdates != null){
                    hit = hit + 1;
                    NotificationCompat.Builder builder =
                            new NotificationCompat.Builder(SalesboardActivity.this)
                                    .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                                    .setContentTitle("Notification!")
                                    .setContentText("Post in Watchlist has been updated!");

                    Log.d("WATCHLIST_UPDATE: ", watchListUpdates);
                    Intent resultIntent = new Intent(SalesboardActivity.this, ViewUpdatedPosts.class);

                    //add user name to the front of string to be passed to viewUpdatedPosts activity
                    watchListUpdates = user.length() + user + watchListUpdates;
                    resultIntent.putExtra("UPDATED_POSTS", watchListUpdates);
                    PendingIntent pIntent = PendingIntent.getActivity(
                            SalesboardActivity.this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT
                    );
                    //send to UpdatedPosts when user clicks on notification
                    builder.setContentIntent(pIntent);
                    Log.d("NUM HIT", "" + hit);
                    // Sets an ID for the notification
                    int mNotificationId = 001;
                    // Gets an instance of the NotificationManager service
                    NotificationManager mNotifyMgr =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    // Builds the notification and issues it.
                    hit = hit + 1;
                    mNotifyMgr.notify(mNotificationId, builder.build());
                    Log.d("NUM HIT", "" + hit);
                }
                else
                    Log.d("NO_WUPDATES: ", "No updates OR no posts in watchlist!");

            } catch(InterruptedException ie){
                //TODO ...
            } catch(ExecutionException e){
                //TODO ...
            }
            //runs every 5 minutes
            //handler.postDelayed(this, 300000);

            //for testing purposes
            handler.postDelayed(this, 20000);
        }
    };

    private class GetWListUpdateTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... url) {
            try{
                return getUpdatedPosts(url[0]);
            } catch(IOException ie){
                ie.printStackTrace();
                return null;
            }
        }

        private String getUpdatedPosts(String myURL) throws IOException{
            InputStream is = null;
            int len = 50000;
            try {
                URL url = new URL(myURL);


                Log.d("URL", "" + url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();


                Log.d("GET RESPONSE:", "Response Code : " + conn.getResponseMessage());
                is = conn.getInputStream();
                //queryPostsArray is [] if there are no updates OR if there are no posts in watchlist
                String queryPostsArray = readIt(is, len);
                Log.d("RESULT", queryPostsArray);
                JSONArray queryPostsArrayJson = new JSONArray(queryPostsArray);

                //if search yields no results
                if(queryPostsArrayJson.length() <= 0){
                    return null;
                }
                return queryPostsArray;
            } catch(JSONException je){
                je.printStackTrace();
                return null;
            } finally{
                if(is!=null){
                    is.close();
                }
            }

        }

        private String readIt(InputStream stream, int len) throws IOException {
            Reader reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            Log.d("READ_STREAM: ", new String(buffer));
            return new String(buffer);
        }
    }
//*/


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salesboard);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user = extras.getString("USERNAME");
            firstname = extras.getString("FIRSTNAME");
            lastname = extras.getString("LASTNAME");
            email = extras.getString("EMAIL");
            phone = extras.getString("PHONE");
        }

        pa = new PostAdapter();

        cardList = (RecyclerView) findViewById(R.id.card_list);
        cardList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        cardList.setLayoutManager(llm);

        // retrieve posts
        retrievePosts(cardList);

        // populating category spinner for search
        categorySpinner = (Spinner) findViewById(R.id.category_spinner2);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setPrompt("Select a category");

        // Apply the adapter to the spinner
        categorySpinner.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        adapter,
                        R.layout.contact_spinner_row_nothing_selected,
                        // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                        this));


        // populating price spinner for search
        priceSpinner = (Spinner) findViewById(R.id.price_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> priceAdapter = ArrayAdapter.createFromResource(this,
                R.array.price_search_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        priceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        priceSpinner.setPrompt("Pick a price option");
        // Apply the adapter to the spinner
        priceSpinner.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        priceAdapter,
                        R.layout.contact_spinner_row_nothing_selected,
                        // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                        this));


        //setting up a listener for spinner2 to send category selected as search filter
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                check = check + 1;
                if (check > 1){
                    Object item = parent.getItemAtPosition(pos);
                    whichSearch = 2;
                    doMySearch(item.toString());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //setting up a listener for priceSpinner to send category selected as search filter
        priceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                checkPrice = checkPrice + 1;
                if (checkPrice > 1){
                    Object item = parent.getItemAtPosition(pos);
                    Log.d("CALLING SEARCH", item.toString());
                    whichSearch = 3;
                    doMySearch(item.toString());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        // for search
        // getIntent and pass to handler
        handleIntent(getIntent());
        Log.d("Search here?", "SEARCH");
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) findViewById(R.id.search_bar);
        //sets up a submit button
        searchView.setSubmitButtonEnabled(true);
        //assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        // setting up location services
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        // Initialize Navigation View
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Checking if the item is in checked state or not, if not make it in checked state
                if(menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()){
                    // For rest of the options we just show a toast on click
                    case R.id.profile:
                        Intent profileIntent = new Intent(SalesboardActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("USERNAME", user);
                        profileIntent.putExtra("FIRSTNAME", firstname);
                        profileIntent.putExtra("LASTNAME", lastname);
                        profileIntent.putExtra("EMAIL", email);
                        profileIntent.putExtra("PHONE", phone);
                        startActivity(profileIntent);
                        return true;
                    case R.id.watch_list:
                        Intent watchListIntent = new Intent(SalesboardActivity.this, WatchListActivity.class);
                        watchListIntent.putExtra("USERNAME", user);
                        watchListIntent.putExtra("FIRSTNAME", firstname);
                        startActivity(watchListIntent);
                        return true;
                    case R.id.my_posts:
                        Intent myPostsIntent = new Intent(SalesboardActivity.this, MyPostsActivity.class);
                        myPostsIntent.putExtra("USERNAME", user);
                        myPostsIntent.putExtra("FIRSTNAME", firstname);
                        myPostsIntent.putExtra("LASTNAME", lastname);
                        startActivity(myPostsIntent);
                        return true;

                    default:
                        Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_SHORT).show();
                        return true;
                }
            }
        });


        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout, R.string.openDrawer, R.string.closeDrawer){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };
        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();


        //check for watchlist status
        handler.postDelayed(runnable, 100);
    }



    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    public void onPause() {
        pa.clear();
        super.onPause();
    }

    protected void onResume() {
        if(!doingSearch)
            retrievePosts(cardList);
        else
            doingSearch = false;
        super.onResume();
    }


    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                if (!Geocoder.isPresent()) {
                    Toast.makeText(this, R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
                    return;
                }
                // It is possible that the user presses the button to get the address before the
                // GoogleApiClient object successfully connects. In such a case, mAddressRequested
                // is set to true, but no attempt is made to fetch the address (see
                // fetchAddressButtonHandler()) . Instead, we start the intent service here if the
                // user has requested an address, since we now have a connection to GoogleApiClient.
                mLatitude = mLastLocation.getLatitude();
                mLongitude = mLastLocation.getLongitude();
                Log.d("LATITUDE", "" + mLatitude);
                Log.d("LONGITUDE", "" + mLongitude);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if (mLastLocation != null) {
                        mLatitude = mLastLocation.getLatitude();
                        mLongitude = mLastLocation.getLongitude();
                    }
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int requestCode) {
        //mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("CONNECTION", connectionResult.toString());
    }


    public void retrievePosts(RecyclerView recView) {
        try {
            String stringUrl = "http://rethrift-1.herokuapp.com/posts/all";
            //pa = new PostAdapter(new GetPostsTask().execute(stringUrl).get(), user);
            pa.setInfo(new GetPostsTask().execute(stringUrl).get(), user);
            recView.setAdapter(pa);
        } catch (InterruptedException e) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Unable to load Salesboard")
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
                    .setMessage("Unable to load Salesboard")
                    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    // sales board (center screen)
    public void createPost(View view){
        Intent intent = new Intent(this, CreatePostActivity.class);
        intent.putExtra("USERNAME", user);
        intent.putExtra("LATITUDE", mLatitude);
        intent.putExtra("LONGITUDE", mLongitude);
        startActivity(intent);
    }

    //for search
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent){
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            //obtain the query string from Intent.ACTION_SEARCH
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d("Search key word", query);
            whichSearch = 1;
            doMySearch(query);

            //clear search entry bar
            searchView.setQuery("", false);
            //hides keyboard
            searchView.clearFocus();
        }
    }

    private void doMySearch(String query){
        //set search flag to true
        doingSearch = true;

        //check connection existence
        ConnectivityManager cxnMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cxnMgr.getActiveNetworkInfo();
        cardList = (RecyclerView) findViewById(R.id.card_list);
        cardList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        cardList.setLayoutManager(llm);

        if (networkInfo != null && networkInfo.isConnected()) {
            try {
                String stringUrl = "http://rethrift-1.herokuapp.com/posts/search/?searchterms=";
                //String stringUrl = "http://rethrift-1.herokuapp.com/posts/all";

                if(new CreateSearchFilterTask().execute(stringUrl, query).get() == null){
                    Log.d("NO SEARCH RESULTS", "NO RESULTS");
                    Toast.makeText(getApplicationContext(),"Search yields no results!",Toast.LENGTH_SHORT).show();
                    retrievePosts(cardList);
                }
                else {
                    //pa = new PostAdapter(new CreateSearchFilterTask().execute(stringUrl, query).get(), user);
                    pa.setInfo(new CreateSearchFilterTask().execute(stringUrl, query).get(), user);
                    cardList.setAdapter(pa);
                }
            } catch(ExecutionException e) {
                //TODO
                Log.d("ERROR:", "SEARCH CANNOT BE LOADED");
            } catch(InterruptedException ie){
                //TODO
                Log.d("ERROR:", "SEARCH CANNOT BE LOADED");
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

        //then close nav drawer
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        LinearLayout drawerLinear = (LinearLayout) findViewById(R.id.right_drawer);
        drawerLayout.closeDrawer(drawerLinear);

        //reset search
        whichSearch = 0;
    }

    private class CreateSearchFilterTask extends AsyncTask<String, Void, List<Post>> {
        @Override
        protected List<Post> doInBackground(String... params) {
            //params[0]: url string, params[1]=query
            //TO-THINK: passing in a list of search queries
            try {
                return getSearchPosts(params[0], params[1]);
            }
            catch(IOException e){
                e.printStackTrace();
                return null;
            }
        }



        private List<Post> getSearchPosts(String myURL, String query) throws IOException {
            InputStream is = null;
            int len = 500000;

            try {

                //TO-THINK: support for seach query with multiple filters
                //List<Pair<String, String>> params = new ArrayList<>();
                //params.add(new Pair<>("search", query));
                //to get values: params.get(i).first, params.get(i).second

                //TO-THINK: use loop for supporting multiple search queries

                Log.d("QUERY", query);

                String json = "";
                String json2 = "{" +
                        "longitude:" + mLongitude + "," +
                        "latitude:" + mLatitude + "," +
                        "distance:" + 5 +
                        "}";

                //key term search
                if (whichSearch == 1) {
                    json =
                            "{" +
                                "where: {" +
                                    "$or:[" +
                                        "{" +
                                            "title: {" +
                                            "$iLike: %" + query + '%' +
                                            "}" +
                                        "}," +
                                        "{" +
                                            "description: {" +
                                            "$iLike: %" + query + '%' +
                                            "}" +
                                        "}" +
                                    "]" +
                                "}" +
                            "}";
                }
                //category search
                else if(whichSearch == 2){
                    json =
                            "{where: {category: {$like: %" + query + "%}}}";
                }
                //price search
                else if(whichSearch == 3) {
                    if (query.equals("Price High to Low")) {
                        Log.d("Price search", query);
                        json =
                                "{" +
                                        "order:[[price, DESC]]" +
                                        "}";
                    } else if (query.equals("Price Low to High")) {
                        Log.d("Price search", query);
                        json =
                                "{" +
                                        "order:[[price, ASC]]" +
                                        "}";
                    }
                }

                if(json.equals("")){
                    Log.d("SEARCH JSON", "NOT SET");
                    return null;
                }

                try {
                    JSONObject jQuery = new JSONObject(json);
                    JSONObject jQuery2 = new JSONObject(json2);
                    myURL = myURL + URLEncoder.encode(jQuery.toString(), "utf-8") + "&locationterm=" + URLEncoder.encode(jQuery2.toString(), "utf-8");
                    URL url = new URL(myURL);
                    Log.d("URL", "" + url);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.connect();

                    Log.d("GET RESPONSE", "Response Code: " + conn.getResponseMessage());
                    //get query results back
                    is = conn.getInputStream();
                    String queryPostsArray = readIt(is, len);
                    Log.d("SEARCH_RESULT", queryPostsArray);


                    JSONArray queryPostsArrayJson = new JSONArray(queryPostsArray);
                    //if search yields no results
                    if(queryPostsArrayJson.length() <= 0){
                        return null;
                    }
                    JSONArray queryUsersArrayJson = findUsers(queryPostsArrayJson);
                    List<Post> postList = new ArrayList<>();
                    for (int i = 0; i < queryPostsArrayJson.length(); i++) {
                        JSONObject postJson = queryPostsArrayJson.getJSONObject(i);
                        JSONObject userJson = queryUsersArrayJson.getJSONObject(i);
                        Log.d("SEARCH_POST", postJson.toString());
                        Log.d("SEARCH_USER", userJson.toString());
                        postList.add(
                                new Post(postJson.getInt("id"),
                                         postJson.getString("title"),
                                         "$" + postJson.getInt("price"),
                                         postJson.getString("state"),
                                         postJson.getDouble("latitude"),
                                         postJson.getDouble("longitude"),
                                         postJson.getString("description"),
                                         postJson.getString("category"),
                                         userJson.getString("firstname") + " " + userJson.getString("lastname"),
                                         userJson.getString("username"),
                                         postJson.getString("image")));
                    }
                    return postList;
                } catch (JSONException e){
                    e.printStackTrace();
                    return null;
                }
            } catch (FileNotFoundException e){
                return null;
            } finally {
                if(is != null){
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
    }

    // AsyncTask that gets posts
    private class GetPostsTask extends AsyncTask<String, Void, List<Post>> {
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
                            new Post(postJson.getInt("id"),
                                     postJson.getString("title"),
                                     "$" + postJson.getInt("price"),
                                     postJson.getString("state"),
                                     postJson.getDouble("latitude"),
                                     postJson.getDouble("longitude"),
                                     postJson.getString("description"),
                                     postJson.getString("category"),
                                     userJson.getString("firstname") + " " + userJson.getString("lastname"),
                                     userJson.getString("username"),
                                     postJson.getString("image")));
                }
                return posts;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

}

