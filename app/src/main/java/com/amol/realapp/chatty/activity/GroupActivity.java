package com.amol.realapp.chatty.activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.amol.realapp.chatty.R;
import com.amol.realapp.chatty.model.groupProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.amol.realapp.chatty.model.userProfile;
import android.content.SharedPreferences;
import android.content.Context;
import android.preference.PreferenceManager;

public class GroupActivity extends AppCompatActivity {


    private DatabaseReference groupRef;
    private StorageReference storageRef;
    private Uri selectedImage;
    private CircleImageView image;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private ArrayList<groupProfile> myGroupList;
    private EditText name;
    private String txtName;
    private MaterialButton submiGrpDetails;
    private FirebaseAuth mAuth;
    private  String userUid;
    private String key;
    private SharedPreferences sPref;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        init();
        initListener();
    }

    private void initListener() {
          userProfile up=new userProfile();



        image.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Intent intent=new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, 43);
                }
            });
        submiGrpDetails.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    txtName = name.getText().toString();

                    if (txtName.isEmpty()) {
                        name.setError("Please enter group's name");
                        return;
                    } else {
                        actCreateGroup();  
                    }
                }
            });   
    }

    private void init() {
    
        image = findViewById(R.id.groupProfileImage);
        name = findViewById(R.id.groupProfileName);
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();    
        database = FirebaseDatabase.getInstance();
        submiGrpDetails = findViewById(R.id.submitGroupDetails);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (data.getData() != null) {
                image.setImageURI(data.getData());
                selectedImage = data.getData();
            }    
        }
    }
    private void actCreateGroup() {
        SharedPreferences sPref=getSharedPreferences("pushKeyFile",Context.MODE_PRIVATE);
        key=sPref.getString("dbPushKey","");
        
        groupRef = database.getReference().child("Groups");

        Toast.makeText(GroupActivity.this, key, Toast.LENGTH_SHORT).show();
        if (selectedImage != null) {
            Calendar cad=Calendar.getInstance();
            storageRef = storage.getReference().child("Groups");

            storageRef.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>(){

                    @Override
                    public void onComplete(Task<UploadTask.TaskSnapshot> p1) {
                        if (p1.isSuccessful()) {
                            
                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){

                                    @Override
                                    public void onSuccess(Uri p1) {
                                        String imageUrl=p1.toString();
                                        
                                        groupProfile groupProfile=new groupProfile(key, txtName, imageUrl);

                                        database.getReference().child("Groups").child(key).child("groupInfo").setValue(groupProfile).addOnCompleteListener(new OnCompleteListener<Void>(){

                                                @Override
                                                public void onComplete(Task<Void> p1) {
                                                    if (p1.isSuccessful()) {
                                                        
                                                        Intent intent=new Intent(GroupActivity.this, MainActivity.class);
                                                         startActivity(intent);
                                                        finish();


                                                        
                                                    } else {
                                                        Toast.makeText(GroupActivity.this, p1.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                                    }
                                                }


                                            });
                                    }


                                });
                        }

                    }


                });
        } else {

            groupProfile gProfile=new groupProfile(key, txtName, "no image");
            groupRef.child(key).child("groupInfo").setValue(gProfile).addOnCompleteListener(new OnCompleteListener<Void>(){

                    @Override
                    public void onComplete(Task<Void> p1) {
                        if (p1.isSuccessful()) {
                            String newKey=key.toString();
                            Toast.makeText(GroupActivity.this, newKey, Toast.LENGTH_SHORT).show();

                            Intent intent=new Intent(GroupActivity.this, MainActivity.class);
                            intent.putExtra("myPushKey", newKey);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(GroupActivity.this, p1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }


                });
        }
    }

}
