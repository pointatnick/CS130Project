package rethrift.rethrift;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {

  private List<Post> postList;

  public PostAdapter(List<Post> contactList) {
    this.postList = contactList;
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
    postHolder.location.setText(ci.getLocation());
  }

  @Override
  public PostHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_layout, viewGroup, false);
    return new PostHolder(itemView);
  }

  public static class PostHolder extends RecyclerView.ViewHolder {
    protected TextView title;
    protected TextView price;
    protected TextView location;

    public PostHolder(View v) {
      super(v);
      title =  (TextView) v.findViewById(R.id.title);
      price = (TextView) v.findViewById(R.id.price);
      location = (TextView) v.findViewById(R.id.location);
    }
  }
}
