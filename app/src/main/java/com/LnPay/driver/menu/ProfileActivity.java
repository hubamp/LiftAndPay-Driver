package com.LnPay.driver.menu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.LnPay.driver.R;
import com.LnPay.driver.carBrand.CarBrandsSelection;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class ProfileActivity extends AppCompatActivity {

    private EditText name, email, phone, about, carPlate, carColor,numberOfSeats;
    private TextView mainName,carBrand;
    private String mUID = FirebaseAuth.getInstance().getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private ImageView profileImage;
    private ImageButton backBtn;
    private ImageView mainCarImage,sideCarImage;

    private Button editBtn;

    private Spinner carSpinner;

    private StorageReference profileImageRef = storage.getReference().child("Driver").child(mUID).child("profile.png");
    private StorageReference mainCarImageRef = storage.getReference().child("Driver").child(mUID).child("main.png");
    private StorageReference otherCarImageRef = storage.getReference().child("Driver").child(mUID).child("otherImage.png");
    private final int BRAND_SEARCH_CODE =419;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2);

        backBtn = findViewById(R.id.backButton);
        name = findViewById(R.id.driverName);
        mainName = findViewById(R.id.mainNameId);
        email = findViewById(R.id.driverEmail);
        phone = findViewById(R.id.driverNumber);
        about = findViewById(R.id.about);
        profileImage = findViewById(R.id.imageProfileId);
        carBrand = findViewById(R.id.carBrand);
        carPlate = findViewById(R.id.carLicense);
        carColor = findViewById(R.id.carColor);
        numberOfSeats = findViewById(R.id.numberOfSeatsId);

        editBtn = findViewById(R.id.editBtnId);

        mainCarImage = findViewById(R.id.mainCarId);
        sideCarImage = findViewById(R.id.sideCarId);
        carSpinner = findViewById(R.id.carSpinner);

        profileImage.setDrawingCacheEnabled(true);
        profileImage.buildDrawingCache();
        mainCarImage.setDrawingCacheEnabled(true);
        mainCarImage.buildDrawingCache();
        sideCarImage.setDrawingCacheEnabled(true);
        sideCarImage.buildDrawingCache();


        HashMap<String,Object> updateDetails = new HashMap<>();

        toggleEdit(true);


     /*   ArrayAdapter<CharSequence> adapter= ArrayAdapter.createFromResource(this, R.array.carBrands, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        carSpinner.setAdapter(adapter);

        Toast.makeText(this, adapter.getCount()+"", Toast.LENGTH_SHORT).show();

        Toast.makeText(this, adapter.getItem(6), Toast.LENGTH_SHORT).show();

        for (int i=0;i<adapter.getCount();i++)
        {
            Map<String, String> carMap = new HashMap<>();
            carMap.put("Car",adapter.getItem(i).toString());
            db.collection("CarBrands").document(adapter.getItem(i).toString()).set(carMap);
        }

        carSpinner.setPrompt("Car Brands");*/

        carSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                carBrand.setText(adapterView.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        carBrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, CarBrandsSelection.class);
                startActivityIfNeeded(intent, BRAND_SEARCH_CODE );
//                carSpinner.performClick();
            }
        });


        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!name.isEnabled()) {
                    Toast.makeText(ProfileActivity.this, "Edit Enabled", Toast.LENGTH_SHORT).show();
                    editBtn.setText("Save");
                    toggleEdit(true);
                } else {
                    editBtn.setText("Edit");

                    updateDetails.put("Name",name.getText().toString() );
                    updateDetails.put("Email",email.getText().toString());
                    updateDetails.put("Number of Seats", numberOfSeats.getText().toString() );
                    updateDetails.put("About",about.getText().toString());
                    updateDetails.put("Car Model", carBrand.getText().toString());
                    updateDetails.put("Car color", carColor.getText().toString());
                    updateDetails.put("Numberplate", carPlate.getText().toString());

                    db.collection("Driver").document(mUID).update(updateDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(ProfileActivity.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                            toggleEdit(false);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, "Couldn't save", Toast.LENGTH_SHORT).show();
                            toggleEdit(true);
                        }
                    });


                }


            }
        });


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        db.collection("Driver").document(mUID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {


                name.setText(documentSnapshot.getString("Name"));
                mainName.setText(documentSnapshot.getString("Name"));

                if (documentSnapshot.contains("Email"))
                    email.setText(documentSnapshot.getString("Email"));


                phone.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
                );

                if (documentSnapshot.contains("About")) about.setText(documentSnapshot.getString("About"));
                if (documentSnapshot.contains("Car Model"))  carBrand.setText(documentSnapshot.getString("Car Model"));
                if (documentSnapshot.contains("Car color")) carColor.setText(documentSnapshot.getString("Car color"));
                if (documentSnapshot.contains("Numberplate")) carPlate.setText(documentSnapshot.getString("Numberplate"));
                if (documentSnapshot.contains("Number of Seats")) numberOfSeats.setText(documentSnapshot.getString("Number of Seats"));


            }
        });


        storage.getReference().child("Driver").child(mUID).child("profile.png").getDownloadUrl().addOnCompleteListener(
                new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Uri> task) {

                        if (task.isSuccessful()) {
                            Picasso.get().load(task.getResult().toString()).into(profileImage);
                        }

                    }
                }
        );

        storage.getReference().child("Driver").child(mUID).child("main.png").getDownloadUrl().addOnCompleteListener(
                new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Uri> task) {

                        if (task.isSuccessful()) {
                            Picasso.get().load(task.getResult().toString()).into(mainCarImage);
                        }

                    }
                }
        );

        storage.getReference().child("Driver").child(mUID).child("otherImage.png").getDownloadUrl().addOnCompleteListener(
                new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Uri> task) {

                        if (task.isSuccessful()) {
                            Picasso.get().load(task.getResult().toString()).into(sideCarImage);
                        }

                    }
                }
        );



        mainCarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityIfNeeded(intent, 123);
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityIfNeeded(intent, 666);
            }
        });

        sideCarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityIfNeeded(intent, 321);
            }
        });
    }


    void toggleEdit(boolean editable) {


        name.setEnabled(editable);
        email.setEnabled(editable);
        about.setEnabled(editable);
        name.setEnabled(editable);
//        carBrand.setEnabled(editable);
        carColor.setEnabled(editable);
        numberOfSeats.setEnabled(editable);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123) {

            if (data != null) {
                mainCarImage.setImageURI(data.getData());

                Bitmap bitmap = ((BitmapDrawable) mainCarImage.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] datas = baos.toByteArray();


                UploadTask uploadTask = mainCarImageRef.putBytes(datas);
                String d = Arrays.toString(datas);



                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Timber.e(exception);
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Timber.e("Profile Upload Succesful");
                    }
                });
//                driverInfoPreferences.edit().putString("TheMainCarImageString", Arrays.toString(datas)).apply();

            }
        }

        if (requestCode == 666) {

            if (data != null) {
                profileImage.setImageURI(data.getData());

                Bitmap bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] datas = baos.toByteArray();

                UploadTask uploadTask = profileImageRef.putBytes(datas);
                String d = Arrays.toString(datas);



                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Timber.e(exception);
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Timber.e("Profile Upload Succesful");
                    }
                });
//                driverInfoPreferences.edit().putString("TheMainCarImageString", Arrays.toString(datas)).apply();

            }
        }

        if (requestCode == 321) {

            if (data != null) {
                sideCarImage.setImageURI(data.getData());

                Bitmap bitmap = ((BitmapDrawable) sideCarImage.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] datas = baos.toByteArray();


                UploadTask uploadTask = otherCarImageRef.putBytes(datas);
                String d = Arrays.toString(datas);



                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Timber.e(exception);
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Timber.e("Profile Upload Succesful");
                    }
                });
              /*  driverInfoPreferences.edit().putString("TheSideImageString", Arrays.toString(datas)).apply();*/

            }
        }


        if (requestCode == 419) {
            if (data!=null){
                carBrand.setText(data.getStringExtra("theSelectedBrand"));
            }
        }
    }

}



