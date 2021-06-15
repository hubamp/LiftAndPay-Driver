package com.example.liftandpay_driver.chats.model_ChatActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.liftandpay_driver.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class messageAdapter extends RecyclerView.Adapter<messageAdapter.messageHolder> {
    private Context context;
    private int theViewtype;
    private ArrayList<messageModel> messageModels;

    public messageAdapter(ArrayList<messageModel> messageModels) {
        this.messageModels = messageModels;
    }


    @NonNull
    @Override
    public messageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view001, view002, view003;

            view001 = LayoutInflater.from(parent.getContext()).inflate(R.layout.bubble_layout_chatsent, parent, false);
            view002 = LayoutInflater.from(parent.getContext()).inflate(R.layout.bubble_layout_chatreceived,parent,false);





        return viewType == 1? new messageHolder(view001): (viewType == 2?new messageHolder(view002): new messageHolder(view002));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull messageAdapter.messageHolder holder, int position) {
        theViewtype = messageModels.get(position).getvType();

        TextView msgTxt = holder.itemView.findViewById(R.id.MsgTxt);
        msgTxt.setText(messageModels.get(position).getMessage());
    }


    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    public static class messageHolder extends RecyclerView.ViewHolder {
        public messageHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }
}
