package com.amol.realapp.chatty;
import android.app.Application;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.OkHttp3Downloader;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
       Picasso.Builder pBuilder=new Picasso.Builder(MyApplication.this);
      pBuilder.downloader(new OkHttp3Downloader(this,Integer.MAX_VALUE));
      Picasso built=pBuilder.build();
      built.setIndicatorsEnabled(false);
      built.setLoggingEnabled(true);
      built.setSingletonInstance(built);
     }
    
    
    
}
