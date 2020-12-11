package Required;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.campusexchange.ChatActivity;
import com.example.campusexchange.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import Useful.ChatBrief;

public class ChatListViewAdapter extends RecyclerView.Adapter<ChatListViewAdapter.ChatListViewHolder> {
    List<ChatBrief> chats;
    Context context;

    public ChatListViewAdapter(List<ChatBrief> cts, Context ctxt) {
        chats = cts;
        context = ctxt;
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_chats, parent, false);
        return new ChatListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatListViewHolder holder, final int position) {
        holder.name.setText(chats.get(position).name);
        holder.message.setText(chats.get(position).message);

        String personID = chats.get(position).UID;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query user = db.collection("Users").whereEqualTo("UID", personID);
        user.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(!task.isSuccessful()) return;
                for(DocumentSnapshot user: task.getResult()) {
                    String profileURI = user.getString("ProfilePic");
                    if(profileURI.contentEquals("null")) return;
                    else Glide.with(context).load(profileURI).centerCrop().into(holder.profilePic);
                    break;
                }
            }
        });

        holder.chatsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(context, ChatActivity.class);
                chatIntent.putExtra("UserName", chats.get(position).name);
                chatIntent.putExtra("UserID", chats.get(position).UID);
                context.startActivity(chatIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class ChatListViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView message;
        ImageView profilePic;
        ConstraintLayout chatsLayout;

        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.chatsName);
            message = itemView.findViewById(R.id.chatsMessage);
            profilePic = itemView.findViewById(R.id.chatsImage);
            chatsLayout = itemView.findViewById(R.id.chatsLayout);
        }
    }
}
