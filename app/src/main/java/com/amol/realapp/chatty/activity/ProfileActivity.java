package com.amol.realapp.chatty.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.amol.realapp.chatty.R;
import com.amol.realapp.chatty.model.userProfile;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
  private FirebaseAuth mAuth;
  private FirebaseUser firebaseUser;
  private CircleImageView profileImage;
  private EditText username, phoneNumber;
  private MaterialButton edit;
  private boolean editStatus = false;
  private FirebaseDatabase database;
  private int oneTime;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_profile);

    mAuth = FirebaseAuth.getInstance();
    firebaseUser = mAuth.getCurrentUser();
    database = FirebaseDatabase.getInstance();
    init();
    initListener();
  }

  private void initListener() {
    oneTime = 0;
    edit.setOnClickListener(
        new View.OnClickListener() {

          @Override
          public void onClick(View view) {
            if (editStatus == true) {
              editStatus = false;
              username.setEnabled(false);
              phoneNumber.setEnabled(false);

            } else {
              editStatus = true;
              username.setEnabled(true);
              phoneNumber.setEnabled(true);
            }
          }
        });
  }

  private void init() {

    profileImage = findViewById(R.id.profileImage);
    username = findViewById(R.id.userName);
    phoneNumber = findViewById(R.id.phoneNumber);
    edit = findViewById(R.id.editProfile);
    profileImage.setOnClickListener(
        new View.OnClickListener() {

          @Override
          public void onClick(View view) {}
        });
    database
        .getReference("Users")
        .child(firebaseUser.getUid())
        .addListenerForSingleValueEvent(
            new ValueEventListener() {

              @Override
              public void onDataChange(DataSnapshot p1) {
                userProfile userProfile = p1.getValue(userProfile.class);
                Picasso.get()
                    .load(userProfile.getUserProfileImage())
                    .placeholder(R.drawable.ic_profile)
                    .into(profileImage);
                username.setText(userProfile.getName());
                phoneNumber.setText(userProfile.getPhoneNumber());
              }

              @Override
              public void onCancelled(DatabaseError p1) {
                Toast.makeText(ProfileActivity.this, p1.getMessage().toString(), Toast.LENGTH_SHORT)
                    .show();
              }
            });

    profileImage.setOnClickListener(
        new View.OnClickListener() {

          @Override
          public void onClick(View view) {}
        });
  }
}
