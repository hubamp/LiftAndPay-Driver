package com.example.liftandpay_driver.chats.model_ChaltList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.liftandpay_driver.R;
import com.example.liftandpay_driver.chats.ChatActivity;
import com.example.liftandpay_driver.chats.ChatList;
import com.example.liftandpay_driver.uploadedRide.UploadedRidesAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class chatListAdapter extends RecyclerView.Adapter<chatListAdapter.chatViewHolder> {

    Context context;
    ArrayList<chatListModel> chatListModels;


    public chatListAdapter(Context context, ArrayList<chatListModel> chatListModels) {
        this.context = context;
        this.chatListModels = chatListModels;
    }

    @NonNull
    @NotNull
    @Override
    public chatViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
          View v=  LayoutInflater.from(parent.getContext()).inflate(R.layout.model_chatlist,parent,false);
        return new chatViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull chatViewHolder holder, int position) {

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passengerId = chatListModels.get(position).getPassengerId();
                Toast.makeText(context,passengerId,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("passengerId",passengerId);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return chatListModels.size();
    }

    public static class chatViewHolder extends RecyclerView.ViewHolder {

        TextView name, status, message;
        private LinearLayout layout;
        private String passengerId;

        public chatViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.layout_id);
            name = itemView.findViewById(R.id.pAnameId);
            status = itemView.findViewById(R.id.pAstatusId);
            message = itemView.findViewById(R.id.pALastMessageId);
        }
    }
}
