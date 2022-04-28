package com.amol.realapp.chatty.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.amol.realapp.chatty.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;

public class VerifyActivity extends AppCompatActivity {
  private Toolbar tb;
  private FirebaseAuth mAuth;
  private String verificationId;
  private EditText inputOtp;
  private MaterialButton verifyOtp;
  private CoordinatorLayout cl;
  private View v;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_verify);
    mAuth = FirebaseAuth.getInstance();
    init();
    initListener();
  }

  private void initListener() {
    Intent intent = getIntent();
    String mobileNumber = intent.getStringExtra("userMobileNumber");
    sendVerificationCode(mobileNumber);
    verifyOtp.setOnClickListener(
        new View.OnClickListener() {

          @Override
          public void onClick(View view) {
            String code = inputOtp.getText().toString();
            if (code.isEmpty() || code.length() < 6) {
              inputOtp.setError("Enter a valid code");
              inputOtp.requestFocus();
              return;
            } else {
              verifyVerificationCode(code);
            }
          }
        });
  }

  private void sendVerificationCode(String mobileNumber) {
    v = LayoutInflater.from(VerifyActivity.this).inflate(R.layout.dialog_custom_layout, cl, false);
    final Dialog dialog = new Dialog(VerifyActivity.this);
    dialog.setContentView(v);

    PhoneAuthOptions phoneOptions =
        PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber("+91" + mobileNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(VerifyActivity.this)
            .setCallbacks(
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                  @Override
                  public void onVerificationCompleted(PhoneAuthCredential p1) {

                    String code = p1.getSmsCode();
                    if (code != null) {
                      dialog.dismiss();

                      inputOtp.setText(code);

                      verifyVerificationCode(code);
                    }
                  }

                  @Override
                  public void onVerificationFailed(FirebaseException p1) {
                    dialog.dismiss();

                    Toast.makeText(VerifyActivity.this, p1.getMessage(), Toast.LENGTH_SHORT).show();
                  }

                  @Override
                  public void onCodeSent(
                      String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    dialog.dismiss();
                    // storing the verification id that is sent to the user
                    verificationId = s;
                  }
                })
            .build();
    dialog.show();

    PhoneAuthProvider.verifyPhoneNumber(phoneOptions);
  }

  private void verifyVerificationCode(String code) {
    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
    mAuth
        .signInWithCredential(credential)
        .addOnCompleteListener(
            new OnCompleteListener<AuthResult>() {

              @Override
              public void onComplete(Task<AuthResult> p1) {
                if (p1.isSuccessful()) {
                  Intent intent = new Intent(VerifyActivity.this, AddProfileDetails.class);
                  intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                  startActivity(intent);
                  finish();
                } else {
                  String message = "Something is wrong,we will fix it soon";
                  if (p1.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    message = "Invalid code entered....";
                  }
                  final Snackbar snack = Snackbar.make(cl, message, Snackbar.LENGTH_LONG);
                  snack.setAction(
                      "Dismiss",
                      new View.OnClickListener() {

                        @Override
                        public void onClick(View p1) {
                          snack.dismiss();
                        }
                      });
                  snack.show();
                }
              }
            });
  }

  private void init() {

    tb = findViewById(R.id.toolbar);
    setSupportActionBar(tb);

    inputOtp = findViewById(R.id.inputOtp);
    verifyOtp = findViewById(R.id.submitOtp);
    cl = findViewById(R.id.activity_coordinatorLayout);
  }
}
