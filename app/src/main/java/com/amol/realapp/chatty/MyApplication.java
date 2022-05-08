package com.amol.realapp.chatty;

import android.app.Application;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.database.FirebaseDatabase;
import com.itsaky.androidide.logsender.LogSender;

public class MyApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    LogSender.startLogging(this);
    FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    FirebaseApp.initializeApp(this);
    FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
    firebaseAppCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance());
  }
}
