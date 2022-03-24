package com.LnPay.driver.chats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.LnPay.driver.R;
import com.LnPay.driver.chats.model_ChatActivity.messageAdapter;
import com.LnPay.driver.chats.model_ChatActivity.messageModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {
    private String thePassengerId, thePassengerName, thePassengerProfile;

    private  RecyclerView recyclerView;
    private ArrayList<messageModel> messageModels;
    private ImageButton sendBtn;
    private EditText typedMessage;

    private ImageView pAProfile;
    private TextView pAName;

    private HashMap<String, Object> chat = new HashMap<>();
    private HashMap<String, Object> driverDetail = new HashMap<>();
    int vtype;

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String theDriverId = mAuth.getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private messageModel messageModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        thePassengerId = getIntent().getStringExtra("passengerId");
        thePassengerName = getIntent().getStringExtra("passengerName");
        thePassengerProfile = getIntent().getStringExtra("passengerProfile");
        sendBtn = findViewById(R.id.sendBtn);
        typedMessage = findViewById(R.id.typedMessage);
        pAProfile = findViewById(R.id.pAImage);
        pAName = findViewById(R.id.pAName);

        pAName.setText(thePassengerName);
        if (thePassengerProfile != null)
            Picasso.get().load(thePassengerProfile).into(pAProfile);

        recyclerView = findViewById(R.id.chatRecyler);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this,LinearLayoutManager.VERTICAL,true));
        messageModels = new ArrayList<>();

        if (thePassengerId == null)
        {
            Toast.makeText(ChatActivity.this,"Couldn't fetch chat",Toast.LENGTH_SHORT).show();
            finish();
        }

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
                    chat.put("Time",new Timestamp(new Date()));
                    chat.put("ChatMode","1");

                  chatCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                      @Override
                      public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                         int size = queryDocumentSnapshots.size() + 1;

                          chatCollection.document(size +"d").set(chat)
                                  .addOnSuccessListener(new OnSuccessListener<Void>() {
                                      @Override
                                      public void onSuccess(Void aVoid) {
                                          driverDetail.put("Name", Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName());
                                          db.collection("Chat").document(theDriverId).set(driverDetail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                              @Override
                                              public void onComplete(@NonNull @NotNull Task<Void> task) {

                                              }
                                          });
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

        chatCollection
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                messageModels.clear();

                assert value != null;
                for (DocumentSnapshot ds : value.getDocuments()) {

                    String mode  = ds.getString("ChatMode");

                    assert mode != null;
                    if (mode.equals("1")) {
                        vtype = 2;
                        messageModel = new messageModel(ds.getString("Message"), 2);
                    }
                    if (mode.equals("2")) {
                        vtype = 1;
                        messageModel = new messageModel(ds.getString("Message"), 1);
                        }
                    if (!mode.equals("2") && !mode.equals("1")) Snackbar.make(ChatActivity.this, recyclerView, "Some messages are missing", 6000).show();

                    messageModels.add(0, messageModel);
//                    Toast.makeText(ChatActivity.this,""+vtype,Toast.LENGTH_LONG).show();
                }
                recyclerView.setAdapter(new messageAdapter(messageModels,vtype));

            }
        });

        /*for(int i =0; i<15; i++){
            if ((i%2 )== 0) {
                vtype = 2;
                messageModel = new messageModel("Even", 2);
                messageModels.add(0, messageModel);
            }
            else
            if((i%2 )== 1)  {
                vtype = 1;
                messageModel = new messageModel("Odd", 1);
                messageModels.add(0, messageModel);
            }

        }
        messageAdapter messageAdapter = new messageAdapter(messageModels,vtype);
        recyclerView.setAdapter(messageAdapter);*/

    }



    }
