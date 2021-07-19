package com.example.liftandpay_driver.chats.model_ChatActivity;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.liftandpay_driver.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static androidx.core.content.ContextCompat.getColor;

public class messageAdapter extends RecyclerView.Adapter<messageAdapter.messageHolder> {
    private Context context;
    private ArrayList<messageModel> messageModels;
    private View view;
    int theViewType;

    public messageAdapter(ArrayList<messageModel> messageModels, int theViewType) {
        this.messageModels = messageModels;
        this.theViewType = theViewType;
    }


    @NonNull
    @Override
    public messageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bubble_layout_chatreceived, parent, false);
            return new messageHolder(view);

        }
        else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bubble_layout_chatsent, parent, false);
            return new messageHolder(view);
        }



    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull messageAdapter.messageHolder holder, int position) {

        TextView msgTxt = holder.itemView.findViewById(R.id.MsgTxt);
        msgTxt.setText(messageModels.get(position).getMessage());

    }


    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        theViewType = messageModels.get(position).getvType();

        return theViewType;
    }

    public static class messageHolder extends RecyclerView.ViewHolder {
        public messageHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }
}
