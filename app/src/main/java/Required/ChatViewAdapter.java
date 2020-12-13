package Required;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusexchange.R;

import java.util.List;

import Useful.ChatData;

public class ChatViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<ChatData> chatData;
    String theirName;
    final int SEND_TYPE = 1;
    final int REC_TYPE = 0;

    public ChatViewAdapter(Context ctxt, List<ChatData> cd, String thrName) {
        context = ctxt;
        chatData = cd;
        theirName = new String(thrName);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        RecyclerView.ViewHolder chatViewHolder;
        if(viewType == SEND_TYPE){
            view = inflater.inflate(R.layout.card_chat_sender, parent, false);
            chatViewHolder = new ChatSendViewHolder(view);
        }
        else {
            view = inflater.inflate(R.layout.card_chat_receiver, parent, false);
            chatViewHolder = new ChatRecViewHolder(view);
        }
        return chatViewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if(chatData.get(position).fromUs.contentEquals("True")) return SEND_TYPE;
        return REC_TYPE;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(chatData.get(position).fromUs.contentEquals("True")) {
            ((ChatSendViewHolder)holder).message.setText(chatData.get(position).message);
            ((ChatSendViewHolder)holder).date.setText(chatData.get(position).date);
        }
        else {
            ((ChatRecViewHolder)holder).message.setText(chatData.get(position).message);
            ((ChatRecViewHolder)holder).date.setText(chatData.get(position).date);
        }
    }

    @Override
    public int getItemCount() {
        return chatData.size();
    }

    public class ChatSendViewHolder extends RecyclerView.ViewHolder {
        TextView message;
        TextView date;


        public ChatSendViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.chatMessageSendText);
            date = itemView.findViewById(R.id.chatMessageSendDate);
        }
    }

    public class ChatRecViewHolder extends RecyclerView.ViewHolder {
        TextView message;
        TextView date;

        public ChatRecViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.chatMessageRecText);
            date = itemView.findViewById(R.id.chatMessageRecDate);
        }
    }
}
