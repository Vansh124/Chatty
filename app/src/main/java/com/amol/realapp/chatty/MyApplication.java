package com.amol.realapp.chatty;

import android.app.Application;
import com.google.firebase.database.FirebaseDatabase;
import com.itsaky.androidide.logsender.LogSender;

public class MyApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    LogSender.startLogging(this);
    FirebaseDatabase.getInstance().setPersistenceEnabled(true);
  }
}
