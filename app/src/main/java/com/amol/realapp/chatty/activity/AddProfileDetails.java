package com.amol.realapp.chatty.activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.amol.realapp.chatty.R;
import com.amol.realapp.chatty.model.userProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import de.hdodenhof.circleimageview.CircleImageView;

public class AddProfileDetails extends AppCompatActivity {

    private CircleImageView userProfileImage;
    private EditText userName;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private MaterialButton proceedUserDetails;
    private Uri selectedImage;
    private StorageReference storageReference;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        init();
        initListener();
    }

    private void initListener() {              
        userProfileImage.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Intent intent=new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, 45);

                }

            });
        proceedUserDetails.setOnClickListener(new View.OnClickListener() 
            {

                @Override
                public void onClick(View view) {
                    String name=userName.getText().toString();
                    if (name.isEmpty()) {
                        userName.setError("Please enter your username");
                        return;
                    }
                    if (selectedImage != null) {
                        submitDetails(); 
                    }
                    else{
                        submitDetailsElse();
                    }
                }
            });
    }
    private void submitDetails() {
        View v=LayoutInflater.from(AddProfileDetails.this).inflate(R.layout.dialog_updating_profile,null,false);
        final AlertDialog.Builder mDialog=new MaterialAlertDialogBuilder(AddProfileDetails.this);
        mDialog.setView(v);
        mDialog.setCancelable(false);
        
        mDialog.show();
        
        final AlertDialog dialog=mDialog.create();
        
        
        storageReference = storage.getReference().child("Profiles").child(mAuth.getUid());
        storageReference.putFile(selectedImage)
            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>(){

                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> p1) {
                    if (p1.isSuccessful()) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){

                                @Override
                                public void onSuccess(@NonNull Uri p1) {

                                    String uid=mAuth.getUid();
                                    String name=userName.getText().toString();
                                    String phoneNumber=mAuth.getCurrentUser().getPhoneNumber();
                                    String imageUrl=p1.toString();

                                    userProfile profile=new userProfile(uid, name, phoneNumber, imageUrl);
                                    database.getReference().child("Users").child(mAuth.getCurrentUser().getUid()).setValue(profile)
                                        .addOnCompleteListener(new OnCompleteListener<Void>(){

                                            @Override
                                            public void onComplete(@NonNull Task<Void> p1) {
                                                if (p1.isSuccessful()) {
                                                    dialog.dismiss();
                                                    Intent intent=new Intent(AddProfileDetails.this, MainActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    dialog.dismiss();
                                                    Toast.makeText(AddProfileDetails.this, p1.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                                }
                                            }


                                        });


                                }


                            });
                    } else {
                        Toast.makeText(AddProfileDetails.this, p1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }


            });
    }
    private void submitDetailsElse() {
        View v=LayoutInflater.from(AddProfileDetails.this).inflate(R.layout.dialog_updating_profile,null,false);
        final AlertDialog.Builder mDialog=new MaterialAlertDialogBuilder(AddProfileDetails.this);
        mDialog.setView(v);
        mDialog.setCancelable(false);

        mDialog.show();


        
        String uid=mAuth.getUid();
        String name=userName.getText().toString();
        String phoneNumber=mAuth.getCurrentUser().getPhoneNumber();

        userProfile profile=new userProfile(uid, name, phoneNumber, "no image");
        database.getReference().child("Users").child(mAuth.getCurrentUser().getUid()).setValue(profile)
            .addOnCompleteListener(new OnCompleteListener<Void>(){

                @Override
                public void onComplete(@NonNull Task<Void> p1) {
                    if (p1.isSuccessful()) {
                        mDialog.create().dismiss();
                        
                        Intent intent=new Intent(AddProfileDetails.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        mDialog.create().dismiss();
                        
                        Toast.makeText(AddProfileDetails.this, p1.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }


            });
        
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (data.getData() != null) {
                userProfileImage.setImageURI(data.getData());
                selectedImage = data.getData();

            } 
        }
    }                      

    private void init() {
 
        userName = findViewById(R.id.userProfileUsername);
        userProfileImage = findViewById(R.id.profile_image);
        proceedUserDetails = findViewById(R.id.userProceedDetails);
    }



}
