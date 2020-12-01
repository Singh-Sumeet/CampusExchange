package Required;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusexchange.ChatActivity;
import com.example.campusexchange.R;

import java.util.ArrayList;
import java.util.List;

import Useful.ChatData;

public class ChatViewAdapter extends RecyclerView.Adapter<ChatViewAdapter.ChatViewHolder> {

    Context context;
    List<ChatData> chatData;
    String theirName;
    //TODO:Adding and Removing Chats

    public ChatViewAdapter(Context ctxt, List<ChatData> cd, String thrName) {
        context = ctxt;
        chatData = cd;
        theirName = new String(thrName);
    }

    @NonNull
    @Override
    public ChatViewAdapter.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewAdapter.ChatViewHolder holder, int position) {
        holder.message.setText(chatData.get(position).message);
        holder.date.setText(chatData.get(position).date);
    }

    @Override
    public int getItemCount() {
        return chatData.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView message;
        TextView date;
        ConstraintLayout cardLayout;
        int sentMessageColor;
        int receivedMessageColor;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.chatMessageText);
            date = itemView.findViewById(R.id.chatMessageDate);
            cardLayout = itemView.findViewById(R.id.chatCardLayout);
            sentMessageColor = R.color.messageSent;
            receivedMessageColor = R.color.messageReceived;
        }
    }
}
