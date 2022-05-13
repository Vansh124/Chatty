package com.amol.realapp.chatty;

import android.app.Application;
import com.amol.realapp.chatty.LogSender;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.database.FirebaseDatabase;

public class MyApplication extends Application {

  @Override
  public void onCreate() {
    LogSender.startLogging(this);
    super.onCreate();
    FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    FirebaseApp.initializeApp(this);
    FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
    firebaseAppCheck.installAppCheckProviderFactory(SafetyNetAppCheckProviderFactory.getInstance());
  }
}
