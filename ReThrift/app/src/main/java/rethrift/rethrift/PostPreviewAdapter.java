package rethrift.rethrift;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class PostPreviewAdapter extends RecyclerView.Adapter<PostPreviewAdapter.PostPreviewHolder> {

  private List<Post> postList;

  public PostPreviewAdapter(List<Post> contactList) {
    this.postList = contactList;
  }

  @Override
  public int getItemCount() {
    return postList.size();
  }

  @Override
  public void onBindViewHolder(PostPreviewHolder postHolder, int i) {
    Post ci = postList.get(i);
    postHolder.title.setText(ci.getTitle());
    postHolder.price.setText(ci.getPrice());
  }

  @Override
  public PostPreviewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_preview_layout, viewGroup, false);
    return new PostPreviewHolder(itemView);
  }

  public static class PostPreviewHolder extends RecyclerView.ViewHolder {
    protected TextView title;
    protected TextView price;

    public PostPreviewHolder(View v) {
      super(v);
      title =  (TextView) v.findViewById(R.id.title);
      price = (TextView) v.findViewById(R.id.price);
    }
  }
}