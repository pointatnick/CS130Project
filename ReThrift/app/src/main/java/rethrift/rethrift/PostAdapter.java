package rethrift.rethrift;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.List;

import static android.content.ContentValues.TAG;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {

  private List<Post> postList;

  public PostAdapter(List<Post> postList) {
    this.postList = postList;
  }

  @Override
  public int getItemCount() {
    return postList.size();
  }

  @Override
  public void onBindViewHolder(PostHolder postHolder, int i) {
    Post ci = postList.get(i);
    postHolder.title.setText(ci.getTitle());
    postHolder.price.setText(ci.getPrice());
    postHolder.state = ci.getState();
    postHolder.latitude = ci.getLatitude();
    postHolder.longitude = ci.getLongitude();
    postHolder.description = ci.getDescription();
    postHolder.category = ci.getCategory();
    postHolder.name = ci.getName();
    postHolder.username = ci.getUsername();
  }

  @Override
  public PostHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_layout, viewGroup, false);
    Context context = viewGroup.getContext();
    return new PostHolder(itemView, context);
  }

  public static class PostHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    protected TextView title, price, location;
    protected double latitude, longitude;
    protected String state, description, category, name, username;
    private Context context;
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    protected boolean mAddressRequested;
    protected String mAddressOutput;
    private AddressResultReceiver mResultReceiver;

    public PostHolder(View view, Context context) {
      super(view);
      title =  (TextView) view.findViewById(R.id.title);
      price = (TextView) view.findViewById(R.id.price);
      location = (TextView) view.findViewById(R.id.location);
      this.context = context;
      view.setOnClickListener(this);
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startIntentService() {
      // Create an intent for passing to the intent service responsible for fetching the address.
      Intent intent = new Intent(context, FetchAddressIntentService.class);

      // Pass the result receiver as an extra to the service.
      intent.putExtra(Constants.RECEIVER, mResultReceiver);

      // Pass the location data as an extra to the service.
      intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);

      // Start the service. If the service isn't already running, it is instantiated and started
      // (creating a process for it if needed); if it is running then it remains running. The
      // service kills itself automatically once all intents are processed.
      context.startService(intent);
    }

    // Handles the row being being clicked
    @Override
    public void onClick(View view) {
      Intent intent = new Intent(context, ViewPostActivity.class);
      intent.putExtra("TITLE", title.getText().toString());
      intent.putExtra("PRICE", price.getText().toString());
      intent.putExtra("STATE", state);
      intent.putExtra("LOCATION", location.getText().toString());
      intent.putExtra("DESCRIPTION", description);
      intent.putExtra("CATEGORY", category);
      intent.putExtra("NAME", name);
      intent.putExtra("USERNAME", username);
      context.startActivity(intent);
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {
      public AddressResultReceiver(Handler handler) {
        super(handler);
      }

      /**
       *  Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
       */
      @Override
      protected void onReceiveResult(int resultCode, Bundle resultData) {

        // Display the address string or an error message sent from the intent service.
        mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
        location.setText(mAddressOutput);
      }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
      // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
      // onConnectionFailed.
      Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
      // The connection to Google Play services was lost for some reason. We call connect() to
      // attempt to re-establish the connection.
      Log.i(TAG, "Connection suspended");
      mGoogleApiClient.connect();
    }
  }
}
