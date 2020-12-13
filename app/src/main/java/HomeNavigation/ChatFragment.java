package HomeNavigation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.campusexchange.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import Required.ChatListViewAdapter;
import Useful.ChatBrief;
import Useful.User;

public class ChatFragment extends Fragment {

    FirebaseFirestore db;
    CollectionReference chats;

    List<ChatBrief> chatBriefs;
    RecyclerView chatsListView;
    ChatListViewAdapter chatListViewAdapter;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        chatsListView = view.findViewById(R.id.chatListView);
        chatBriefs = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        chats = db.collection("Chats");
        Query getChats = chats.whereGreaterThan("ChatNumber", 0); //TODO:Add Order-by here to order by most recent, will have to create field in database


        final ChatBrief temp = new ChatBrief();
        final int pos = 0;
        getChats.get() //Get all chats that exist and have at least one message, hopefully ordered.
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) { //If successful query completion
                        for(QueryDocumentSnapshot chat: queryDocumentSnapshots) { //Going through the results
                            if(chat.getString("User1Name").equals(User.getName())) {
                                temp.name = chat.getString("User2Name");
                                temp.UID = chat.getString("User2ID");
                            }
                            else if(chat.getString("User2Name").equals(User.getName())){
                                temp.name = chat.getString("User1Name");
                                temp.UID = chat.getString("User1ID");
                            }
                            else continue; //One of the two must be us
                            if(chat.getString("RecentFrom").equals(User.getUID())) temp.message = "You:" + chat.getString("RecentText");
                            else temp.message = chat.getString("RecentText");

                            chatBriefs.add(temp);
                            //pos++;
                            //chatListViewAdapter.notifyItemInserted(pos);
                        }
                        chatListViewAdapter = new ChatListViewAdapter(chatBriefs, getContext());
                        chatsListView.setAdapter(chatListViewAdapter);
                        chatsListView.setLayoutManager(new LinearLayoutManager(getContext()));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }
                });
        return view;
    }
}