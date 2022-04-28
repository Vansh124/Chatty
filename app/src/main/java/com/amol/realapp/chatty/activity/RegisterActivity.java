package com.amol.realapp.chatty.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.amol.realapp.chatty.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
  private Toolbar tb;
  private FirebaseAuth mAuth;
  private EditText phoneNumber;
  private MaterialButton submitNumber;
  private String txtPhoneNumber;

  private FirebaseUser firebaseUser;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);
    mAuth = FirebaseAuth.getInstance();
    mAuth.setLanguageCode("en");
    init();
    initListener();
  }

  @Override
  protected void onStart() {
    super.onStart();
    firebaseUser = mAuth.getCurrentUser();
    if (firebaseUser != null) {
      startActivity(new Intent(RegisterActivity.this, MainActivity.class));
      finish();
    }
  }

  private void initListener() {

    submitNumber.setOnClickListener(
        new View.OnClickListener() {

          @Override
          public void onClick(View view) {
            txtPhoneNumber = phoneNumber.getText().toString();
            if (TextUtils.isEmpty(txtPhoneNumber) || txtPhoneNumber.length() < 10) {
              phoneNumber.setError("Please enter a valid mobile number ");
              phoneNumber.requestFocus();
              return;
            } else {
              sendUserToVerifyActivity();
            }
          }
        });
  }

  private void init() {
    tb = findViewById(R.id.toolbar);
    setSupportActionBar(tb);

    phoneNumber = findViewById(R.id.userPhoneNumber);
    submitNumber = findViewById(R.id.submitNumber);
  }

  private void sendUserToVerifyActivity() {
    Intent intent = new Intent(RegisterActivity.this, VerifyActivity.class);
    intent.putExtra("userMobileNumber", txtPhoneNumber);
    startActivity(intent);
  }
}
