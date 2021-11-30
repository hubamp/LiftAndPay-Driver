package com.example.liftandpay_driver.chats.model_ChaltList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class chatListAdapter extends RecyclerView.Adapter<chatListAdapter.chatViewHolder> {

    Context context;
    ArrayList<chatListModel> chatListModels;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private String passengerProfile;



    public chatListAdapter(Context context, ArrayList<chatListModel> chatListModels) {
        this.context = context;
        this.chatListModels = chatListModels;
    }

    @NonNull
    @NotNull
    @Override
    public chatViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
          View v=  LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item,parent,false);
        return new chatViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull chatViewHolder holder, int position) {

        holder.name.setText(chatListModels.get(position).getNameOfPassenger());
        holder.message.setText(chatListModels.get(position).getMessage());
        holder.passengerId = chatListModels.get(position).getPassengerId();
        holder.time.setText(new SimpleDateFormat("K:mm a").format(chatListModels.get(position).getTime().toDate()));


        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passengerId = chatListModels.get(holder.getAdapterPosition()).getPassengerId();
                String passengerName = chatListModels.get(holder.getAdapterPosition()).getNameOfPassenger();
                storage.getReference().
                        child("Passenger").
                        child(passengerId).
                        child("profile.png").
                        getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        passengerProfile = task.getResult().toString();
                        Picasso.get().load( passengerProfile).into(holder.image);

                    }
                });


//                Toast.makeText(context,passengerId,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("passengerId",passengerId);
                intent.putExtra("passengerName",passengerName);
                intent.putExtra("passengerProfile",passengerProfile);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return chatListModels.size();
    }

    public static class chatViewHolder extends RecyclerView.ViewHolder {

        TextView name, status, message,time;
        ImageView image;
        private LinearLayout layout;
        private String passengerId;

        public chatViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.layout_id);
            name = itemView.findViewById(R.id.pAnameId);
            status = itemView.findViewById(R.id.pAstatusId);
            message = itemView.findViewById(R.id.pALastMessageId);
            time = itemView.findViewById(R.id.timeModelId);

            image = itemView.findViewById(R.id.pAImageId);
            image.setDrawingCacheEnabled(true);
            image.buildDrawingCache();
        }
    }
}
