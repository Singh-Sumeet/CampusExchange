package Required;

import android.content.Context;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.campusexchange.ItemActivity;
import com.example.campusexchange.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class FeedViewAdapter extends RecyclerView.Adapter<FeedViewAdapter.FeedViewHolder> {
    List<String> names;
    List<String> descriptions;
    List<String> categories;
    List<String> prices;
    List<String> imagesUrl;
    List<Date> dates;
    List<Map> users;

    Context context;

    public FeedViewAdapter(Context ctxt, List<String> nms, List<String> desc, List<String> cat, List<String> prcs, List<String> imgUrl, List<Date> dts, List<Map> usrs) {
        context = ctxt;
        names = new ArrayList<>(nms);
        descriptions = new ArrayList<>(desc);
        categories = new ArrayList<>(cat);
        prices = new ArrayList<>(prcs);
        imagesUrl = new ArrayList<>(imgUrl);
        dates = new ArrayList<>(dts);
        users = new ArrayList<>(usrs);
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_feed, parent, false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FeedViewHolder holder, final int position) {
        holder.name.setText(names.get(position));
        holder.description.setText(descriptions.get(position));
        holder.category.setText(categories.get(position));
        holder.price.setText(prices.get(position));
        Glide.with(context).load(imagesUrl.get(position)).centerCrop().into(holder.image);

        holder.feedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itemIntent = new Intent(context, ItemActivity.class);
                itemIntent.putExtra("Name", names.get(position));
                itemIntent.putExtra("Description", descriptions.get(position));
                itemIntent.putExtra("Category", categories.get(position));
                itemIntent.putExtra("Price", prices.get(position));
                itemIntent.putExtra("Image", imagesUrl.get(position));
                itemIntent.putExtra("Date", dates.get(position).toString());
                itemIntent.putExtra("PersonName", users.get(position).get("Name").toString());
                itemIntent.putExtra("PersonUID", users.get(position).get("UID").toString());
                context.startActivity(itemIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public class FeedViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView description;
        TextView category;
        TextView price;
        ImageView image;
        ConstraintLayout feedLayout;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.feedName);
            description = itemView.findViewById(R.id.feedDescription);
            category = itemView.findViewById(R.id.feedCategory);
            price = itemView.findViewById(R.id.feedPrice);
            image = itemView.findViewById(R.id.feedImage);
            feedLayout = itemView.findViewById(R.id.feedLayout);
        }
    }
}
