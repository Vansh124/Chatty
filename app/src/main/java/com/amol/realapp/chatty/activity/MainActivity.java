package com.amol.realapp.chatty.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;
import com.amol.realapp.chatty.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DatabaseReference;
import android.util.Log;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;
import androidx.fragment.app.Fragment;
import com.amol.realapp.chatty.fragment.ChatsFragment;
import com.amol.realapp.chatty.fragment.GroupsFragment;
import com.amol.realapp.chatty.fragment.StatusFragment;
import androidx.fragment.app.FragmentManager;
import com.google.firebase.database.FirebaseDatabase;
import android.content.Intent;
import com.amol.realapp.chatty.model.userStatus;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.FirebaseStorage;
import java.util.Date;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.storage.UploadTask;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnSuccessListener;
import android.net.Uri;
import com.amol.realapp.chatty.model.userProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import java.util.HashMap;
import com.amol.realapp.chatty.model.Status;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.content.Context;
import android.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity {
  private MaterialToolbar tBar;
  private BottomNavigationView btmNavView;
  private FragmentManager fragmentManager;
  private static final int GET_STATUS_IMAGE = 60;

  private userProfile user;

  private String key;
  private SharedPreferences sPref;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    init();
    listener();
    
    
  }

  private void listener() {

    btmNavView.setOnNavigationItemSelectedListener(
        new BottomNavigationView.OnNavigationItemSelectedListener() {

          @Override
          public boolean onNavigationItemSelected(MenuItem p1) {
            Fragment fragment = null;
            switch (p1.getItemId()) {
              case R.id.chats:
                fragment = new ChatsFragment();
                break;
              case R.id.groups:
                fragment = new GroupsFragment();

                break;
              case R.id.status:
                fragment = new ChatsFragment();
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GET_STATUS_IMAGE);
                break;
              default:
                fragment = new ChatsFragment();
            }
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();

            return true;
          }
        });
    btmNavView.setSelectedItemId(R.id.chats);
    FirebaseDatabase.getInstance()
        .getReference()
        .child("Users")
        .child(FirebaseAuth.getInstance().getUid())
        .addValueEventListener(
            new ValueEventListener() {

              @Override
              public void onDataChange(DataSnapshot p1) {
                if (p1.exists()) {
                  user = p1.getValue(userProfile.class);
                }
              }

              @Override
              public void onCancelled(DatabaseError p1) {}
            });
            
            
  }

  private void init() {
    tBar = findViewById(R.id.toolbar);
    setSupportActionBar(tBar);
    fragmentManager = getSupportFragmentManager();
    btmNavView = findViewById(R.id.main_bottomNavigationView);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == GET_STATUS_IMAGE) {

      if (data != null) {
        if (data.getData() != null) {
          final Date date = new Date();
          final StorageReference sRef =
              FirebaseStorage.getInstance()
                  .getReference()
                  .child("status")
                  .child(date.getTime() + "");
          sRef.putFile(data.getData())
              .addOnCompleteListener(
                  new OnCompleteListener<UploadTask.TaskSnapshot>() {

                    @Override
                    public void onComplete(Task<UploadTask.TaskSnapshot> p1) {
                      sRef.getDownloadUrl()
                          .addOnSuccessListener(
                              new OnSuccessListener<Uri>() {

                                @Override
                                public void onSuccess(Uri p1) {
                                  userStatus uStatus = new userStatus();

                                  uStatus.setName(user.getName());
                                  uStatus.setProfileImage(user.getUserProfileImage());
                                  uStatus.setLastUpdated(date.getTime());

                                  HashMap<String, Object> obj = new HashMap<>();
                                  obj.put("name", uStatus.getName());
                                  obj.put("profileImage", uStatus.getProfileImage());
                                  obj.put("lastUpdated", uStatus.getLastUpdated());

                                  String imageUrl = p1.toString();
                                  Status status = new Status(imageUrl, uStatus.getLastUpdated());
                                  
                                  FirebaseDatabase.getInstance()
                                      .getReference()
                                      .child("Stories")
                                      .child(FirebaseAuth.getInstance().getUid())
                                      .updateChildren(obj);

                                  FirebaseDatabase.getInstance()
                                      .getReference()
                                      .child("Stories")
                                      .child(FirebaseAuth.getInstance().getUid())
                                      .child("statuses")
                                      .push()
                                      .setValue(status);
                                }
                              });
                    }
                  });
        }
      }
    }
  }
}




