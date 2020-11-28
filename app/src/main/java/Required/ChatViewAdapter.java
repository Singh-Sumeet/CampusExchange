package Required;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusexchange.R;

import java.util.ArrayList;
import java.util.List;

public class ChatViewAdapter extends RecyclerView.Adapter<ChatViewAdapter.ChatViewHolder> {

    Context context;
    int noOfMessages;
    List<String> messages;
    List<String> dates;
    List<String> fromUs;
    String theirName;
    //TODO:Adding and Removing Chats

    public ChatViewAdapter(Context ctxt, int noMess, List<String> mess, List<String> dts, List<String> frmUs, String thrName) {
        noOfMessages = noMess;
        messages = new ArrayList<>(mess);
        dates = new ArrayList<>(dts);
        fromUs = new ArrayList<>(frmUs);
        theirName = new String(thrName);
        context = ctxt;
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
        holder.message.setText(messages.get(position));
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(context, holder.cardLayout.getId());

        if(fromUs.get(position).contentEquals("True")) {
            constraintSet.setHorizontalBias(holder.message.getId(), 1);
            constraintSet.applyTo(holder.cardLayout);
            holder.message.setBackgroundColor(holder.sentMessageColor);
        }
        else {
            constraintSet.setHorizontalBias(holder.message.getId(), 0);
            constraintSet.applyTo(holder.cardLayout);
            holder.message.setBackgroundColor(holder.receivedMessageColor);
        }

        holder.date.setText(dates.get(position));
    }

    @Override
    public int getItemCount() {
        return noOfMessages;
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
            receivedMessageColor = R.color.messageRecieved;
        }
    }
}
