package com.amol.realapp.chatty.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.amol.realapp.chatty.R;

public class SplashActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);
    Thread thread =
        new Thread() {
          public void run() {
            try {
              sleep(1500);
            } catch (Exception e) {
              e.printStackTrace();
            } finally {
              startActivity(new Intent(SplashActivity.this, RegisterActivity.class));
              finish();
            }
          }
        };
    thread.start();
  }
}
