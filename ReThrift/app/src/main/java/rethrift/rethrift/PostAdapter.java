package rethrift.rethrift;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

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
    postHolder.location.setText(ci.getLocation());
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
    protected String state, description, category, name, username;
    private Context context;

    public PostHolder(View view, Context context) {
      super(view);
      title =  (TextView) view.findViewById(R.id.title);
      price = (TextView) view.findViewById(R.id.price);
      location = (TextView) view.findViewById(R.id.location);
      this.context = context;
      view.setOnClickListener(this);
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
  }
}
