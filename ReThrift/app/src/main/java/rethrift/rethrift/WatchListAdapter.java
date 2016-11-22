package rethrift.rethrift;

/**
 * Created by taegen on 20/11/2016.
 */
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

public class WatchListAdapter extends RecyclerView.Adapter<WatchListAdapter.WatchListHolder>{

    private List<Post> watchListL;

    public WatchListAdapter(List<Post> watchListL) {
        this.watchListL= watchListL;
    }

    @Override
    public int getItemCount() {
        return watchListL.size();
    }

    @Override
    public void onBindViewHolder(WatchListHolder watchListHolder, int i) {
        Post wl = watchListL.get(i);
        watchListHolder.title.setText(wl.getTitle());
        watchListHolder.price.setText(wl.getPrice());
    }

    @Override
    public WatchListHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_preview_layout, viewGroup, false);
        Context context = viewGroup.getContext();
        return new WatchListHolder(itemView, context);
    }

    public static class WatchListHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        protected TextView title, price, location;
        private Context context;

        public WatchListHolder(View view, Context context) {
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

            context.startActivity(intent);
        }
    }
}
