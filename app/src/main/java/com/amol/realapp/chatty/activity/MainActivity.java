package com.amol.realapp.chatty.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.amol.realapp.chatty.R;
import com.amol.realapp.chatty.databinding.ActivityMainBinding;
import com.amol.realapp.chatty.fragment.ChatsFragment;
import com.amol.realapp.chatty.fragment.GroupsFragment;
import com.amol.realapp.chatty.model.Status;
import com.amol.realapp.chatty.model.userProfile;
import com.amol.realapp.chatty.model.userStatus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.internal.NavigationMenuItemView;
import com.google.android.material.internal.NavigationMenuView;
import com.google.android.material.navigation.NavigationBarItemView;
import com.google.android.material.navigation.NavigationBarMenu;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itsaky.androidide.logsender.LogSender;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
  private MaterialToolbar tBar;
  private BottomNavigationView btmNavView;
  private FragmentManager fragmentManager;

  private userProfile user;

  private Date date;

  private ActivityMainBinding binding;
  private FirebaseAuth auth;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    LogSender.startLogging(this);
    super.onCreate(savedInstanceState);
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    init();
    initListener();
  }

  private void init() {
    auth = FirebaseAuth.getInstance();
    tBar = binding.toolbar;
    btmNavView = binding.mainBottomNavigationView;

    setSupportActionBar(tBar);
    fragmentManager = getSupportFragmentManager();
  }

  private void initListener() {
    btmNavView.setOnItemSelectedListener(
        p1 -> {
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
              launchStatusImage();
              break;
            default:
              fragment = new ChatsFragment();
          }

          fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();

          return true;
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
              public void onCancelled(DatabaseError p2) {
                Log.d("MainActivity.java", p2.getMessage());
              }
            });
  }

  public void launchStatusImage() {
    statusImageRetriever.launch("image/*");
  }

  ActivityResultLauncher<String> statusImageRetriever =
      registerForActivityResult(
          new ActivityResultContracts.GetContent(),
          new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
              if (uri != null) {
                getUriFromActivityLauncher(uri);
              }
            }
          });

  public void getUriFromActivityLauncher(Uri uri) {
    date = new Date();
    StorageReference sRef =
        FirebaseStorage.getInstance().getReference().child("status").child(date.getTime() + "");
    sRef.putFile(uri)
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

  @Override
  protected void onStart() {
    super.onStart();
    if (auth.getCurrentUser() == null) {
      startActivity(new Intent(this, RegisterActivity.class));
      finish();
    }
  }
}
