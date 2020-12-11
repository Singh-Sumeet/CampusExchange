package HomeNavigation;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.campusexchange.R;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import Required.FeedViewAdapter;

public class HomeFragment extends Fragment {

    List<String> names;
    List<String> descriptions;
    List<String> categories;
    List<String> prices;
    List<String> imagesUrl;
    List<Date> dates;
    List<Map> users;

    FirebaseFirestore db;
    CollectionReference items;

    RecyclerView feedView;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        feedView = view.findViewById(R.id.feedView);
        names = new ArrayList<>();
        descriptions = new ArrayList<>();
        categories = new ArrayList<>();
        prices = new ArrayList<>();
        imagesUrl = new ArrayList<>();
        dates = new ArrayList<>();
        users = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        items = db.collection("Items");
        Query getItems = items.orderBy("Date", Query.Direction.DESCENDING);

        getItems.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(!task.isSuccessful()) {
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }
                        for(QueryDocumentSnapshot item: task.getResult()) {
                            names.add(item.getString("Name"));
                            descriptions.add(item.getString("Description"));
                            categories.add(item.getString("Category"));
                            prices.add(item.getString("Price"));
                            imagesUrl.add(item.getString("Image"));
                            dates.add(item.getDate("Date"));
                            users.add((Map)item.get("User"));
                        }
                        FeedViewAdapter feedViewAdapter = new FeedViewAdapter(getContext(), names, descriptions, categories, prices, imagesUrl, dates, users);
                        feedView.setAdapter(feedViewAdapter);
                        feedView.setLayoutManager(new LinearLayoutManager(getContext()));
                    }
                });
        return view;
    }
}

