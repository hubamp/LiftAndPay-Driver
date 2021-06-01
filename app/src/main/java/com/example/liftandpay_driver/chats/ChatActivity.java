package com.example.liftandpay_driver.chats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {
    String driverId;
    RecyclerView recyclerView;
    ArrayList<messageModel> messageModels;
    ImageButton sendBtn;
    EditText typedMessage;

    private HashMap<String, Object> chat = new HashMap<>();


    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String theDriverId = mAuth.getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        sendBtn = findViewById(R.id.sendBtn);
        typedMessage = findViewById(R.id.typedMessage);

        recyclerView = findViewById(R.id.chatRecyler);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this,LinearLayoutManager.VERTICAL,true));
        messageModels = new ArrayList<>();

        CollectionReference chatCollection =  db.collection("Chat").document(theDriverId ).collection("eZzRyFSyhmcQgGFvrdo6snNIv8i1");

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!typedMessage.getText().equals("")){

                    chat.put("Message",typedMessage.getText().toString());
                    chat.put("Time","Not clear");
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

                assert value != null;
                for (DocumentChange ds : value.getDocumentChanges()) {

                    String mode  = ds.getDocument().getString("ChatMode");
                    if (mode.equals("1")) {
                        messageModel messageModel = new messageModel(ds.getDocument().getString("Message"), 1);
                        messageModels.add(0, messageModel);
                        recyclerView.setAdapter(new messageAdapter(messageModels));
                    }
                    if (mode.equals("2")) {
                            messageModel messageModel = new messageModel(ds.getDocument().getString("Message"), 2);
                            messageModels.add(0, messageModel);
                            recyclerView.setAdapter(new messageAdapter(messageModels));
                        }
                    if (!mode.equals("2") && !mode.equals("1")) Snackbar.make(ChatActivity.this, recyclerView, "Some messages are missing", 6000).show();


                }
            }
        });






    }
}