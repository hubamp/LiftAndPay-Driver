package com.example.liftandpay_driver.chats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.liftandpay_driver.R;
import com.example.liftandpay_driver.chats.model_ChatActivity.messageAdapter;
import com.example.liftandpay_driver.chats.model_ChatActivity.messageModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {
    private String driverId, thePassengerId;

    private  RecyclerView recyclerView;
    private ArrayList<messageModel> messageModels;
    private ImageButton sendBtn;
    private EditText typedMessage;

    private HashMap<String, Object> chat = new HashMap<>();



    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String theDriverId = mAuth.getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private messageModel messageModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        SharedPreferences sharedPreferences = this.getSharedPreferences("PASS",MODE_PRIVATE);

        thePassengerId = getIntent().getStringExtra("passengerId");
        sendBtn = findViewById(R.id.sendBtn);
        typedMessage = findViewById(R.id.typedMessage);

        recyclerView = findViewById(R.id.chatRecyler);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this,LinearLayoutManager.VERTICAL,true));
        messageModels = new ArrayList<>();

        if (thePassengerId == null)
        {
            Toast.makeText(ChatActivity.this,"Couldn't fetch chat",Toast.LENGTH_SHORT).show();
            finish();
        }

        Toast.makeText(ChatActivity.this,thePassengerId,Toast.LENGTH_SHORT).show();

        CollectionReference chatCollection =  db.collection("Chat").document(theDriverId).collection("Passengers").document(thePassengerId).collection("messages");

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if (!typedMessage.getText().equals("")){

                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    System.out.println(dtf.format(now));

                    chat.put("Message",typedMessage.getText().toString());
                    chat.put("Time",dtf.format(now));
                    chat.put("ChatMode","1");

                  chatCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                      @Override
                      public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                         int size = queryDocumentSnapshots.size() + 1;

                          chatCollection.document(size +"d").set(chat)
                                  .addOnSuccessListener(new OnSuccessListener<Void>() {
                                      @Override
                                      public void onSuccess(Void aVoid) {
                                          Toast.makeText(ChatActivity.this,"Added Successfully",Toast.LENGTH_LONG).show();
                                      }
                                  })
                                  .addOnFailureListener(new OnFailureListener() {
                                      @Override
                                      public void onFailure(@NonNull Exception e) {
                                          Toast.makeText(ChatActivity.this,"Failed to add",Toast.LENGTH_LONG).show();
                                      }
                                  });
                          typedMessage.setText("");
                      }
                  });
                }
            }
        });

        chatCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                messageModels.clear();

                assert value != null;
                for (DocumentSnapshot ds : value.getDocuments()) {

                    String mode  = ds.getString("ChatMode");
                    Toast.makeText(ChatActivity.this,mode,Toast.LENGTH_SHORT).show();

                    assert mode != null;
                    if (mode.equals("1")) {
                        messageModel = new messageModel(ds.getString("Message"), 2);
                    }
                    if (mode.equals("2")) {
                        messageModel = new messageModel(ds.getString("Message"), 1);
                        }
                    if (!mode.equals("2") && !mode.equals("1")) Snackbar.make(ChatActivity.this, recyclerView, "Some messages are missing", 6000).show();

                    messageModels.add(0, messageModel);

                }
                recyclerView.setAdapter(new messageAdapter(messageModels));

            }
        });

    }
}