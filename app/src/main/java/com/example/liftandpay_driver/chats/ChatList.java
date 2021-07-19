package com.example.liftandpay_driver.chats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.liftandpay_driver.R;
import com.example.liftandpay_driver.chats.model_ChaltList.chatListAdapter;
import com.example.liftandpay_driver.chats.model_ChaltList.chatListModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;

public class ChatList extends AppCompatActivity {

    private ArrayList<chatListModel> chatListModels = new ArrayList<>();
    private chatListModel chatListModel;
    private RecyclerView recyclerView;

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String mUid = mAuth.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        recyclerView = findViewById(R.id.chatListRecycler);


        db.collection("Chat").document(mUid).collection("Passengers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {

//                Toast.makeText(ChatList.this, "For here"+ task.getResult().getDocuments().size(), Toast.LENGTH_SHORT).show();


                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
//                    Toast.makeText(ChatList.this, documentSnapshot.getId(), Toast.LENGTH_SHORT).show();

                    if (task.isSuccessful()) {

                        chatListModel = new chatListModel("Hubert", "online", "hello how are you", documentSnapshot.getId());
                        chatListModels.add(chatListModel);

                        recyclerView.setLayoutManager(new LinearLayoutManager(ChatList.this, LinearLayoutManager.VERTICAL, false));
                        recyclerView.setAdapter(new chatListAdapter(ChatList.this, chatListModels));
                    } else {
                        Toast.makeText(ChatList.this, "Hello World", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}