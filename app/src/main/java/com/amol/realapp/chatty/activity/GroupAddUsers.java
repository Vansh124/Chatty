package com.amol.realapp.chatty.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.amol.realapp.chatty.R;
import com.amol.realapp.chatty.adapter.groupUsersAddedAdapter;
import com.amol.realapp.chatty.adapter.groupUsersAvailableAdapter;
import com.amol.realapp.chatty.model.groupUsersAdded;
import com.amol.realapp.chatty.model.groupUsersAvailable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;

public class GroupAddUsers extends AppCompatActivity {

  private RecyclerView groupUsersAvailableView, groupUsersAddedView;
  private ArrayList<groupUsersAvailable> groupAvailList = new ArrayList<>();
  private FloatingActionButton proceedGroupUsersDetails;

  private groupUsersAvailableAdapter groupUsersAvailAdapter;
  private Toolbar toolbar;
  private groupUsersAddedAdapter groupUserAddAdp;
  private ArrayList<groupUsersAdded> groupNewAddedUsers = new ArrayList<>();
  private SwipeRefreshLayout refreshLayout;

  private String currentUserUid, currentUserName, currentUserProfile;
  private String key;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_group_users);
    init();
    initListener();
  }

  private void init() {

    toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    groupUsersAvailableView = findViewById(R.id.usersAvailableList);
    groupUsersAddedView = findViewById(R.id.usersAddedList);

    refreshLayout = findViewById(R.id.groupAvailableUsersRefresh);

    proceedGroupUsersDetails = findViewById(R.id.groupProceedWithDetails);
  }

  private void initListener() {
    key =
        FirebaseDatabase.getInstance().getReference().push().getKey()
            + FirebaseAuth.getInstance().getUid();

    FirebaseDatabase.getInstance()
        .getReference()
        .child("Groups")
        .child(key)
        .child("Members")
        .addValueEventListener(
            new ValueEventListener() {

              @Override
              public void onDataChange(DataSnapshot p1) {
                if (p1.exists()) {
                  groupNewAddedUsers.clear();

                  for (DataSnapshot dSnapshot : p1.getChildren()) {
                    String imageUrl = dSnapshot.child("userAvailImage").getValue(String.class);
                    String name = dSnapshot.child("userAvailName").getValue(String.class);

                    groupUsersAdded grPUserAdded = dSnapshot.getValue(groupUsersAdded.class);
                    grPUserAdded.setUserImage(imageUrl);
                    grPUserAdded.setUserName(name);

                    groupNewAddedUsers.add(grPUserAdded);
                  }
                  groupUserAddAdp.notifyDataSetChanged();
                }
              }

              @Override
              public void onCancelled(DatabaseError p1) {}
            });

    groupUserAddAdp = new groupUsersAddedAdapter(GroupAddUsers.this, groupNewAddedUsers);
    LinearLayoutManager lm = new LinearLayoutManager(this);
    lm.setOrientation(LinearLayoutManager.HORIZONTAL);
    groupUsersAddedView.setLayoutManager(lm);
    groupUsersAddedView.setAdapter(groupUserAddAdp);

    FirebaseDatabase.getInstance()
        .getReference()
        .child("Users")
        .addValueEventListener(
            new ValueEventListener() {

              @Override
              public void onDataChange(DataSnapshot p1) {
                groupAvailList.clear();
                for (DataSnapshot gSnapShot : p1.getChildren()) {

                  final groupUsersAvailable gAvailUsers =
                      gSnapShot.getValue(groupUsersAvailable.class);
                  String imageUrl = gSnapShot.child("userProfileImage").getValue(String.class);
                  String name = gSnapShot.child("name").getValue(String.class);
                  String uid = gSnapShot.child("uid").getValue(String.class);
                  gAvailUsers.setUserAvailImage(imageUrl);
                  gAvailUsers.setUserAvailName(name);
                  gAvailUsers.setUid(uid);
                  if (!gAvailUsers.getUid().equals(FirebaseAuth.getInstance().getUid())) {
                    groupAvailList.add(gAvailUsers);
                  }
                }
                groupUsersAvailAdapter.notifyDataSetChanged();
              }

              @Override
              public void onCancelled(DatabaseError p1) {}
            });
    groupUsersAvailAdapter =
        new groupUsersAvailableAdapter(GroupAddUsers.this, groupAvailList, key);

    LinearLayoutManager llm = new LinearLayoutManager(this);
    llm.setOrientation(LinearLayoutManager.VERTICAL);
    groupUsersAvailableView.setLayoutManager(llm);
    groupUsersAvailableView.setAdapter(groupUsersAvailAdapter);

    refreshLayout.setOnRefreshListener(
        new SwipeRefreshLayout.OnRefreshListener() {

          @Override
          public void onRefresh() {

            FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .addValueEventListener(
                    new ValueEventListener() {

                      @Override
                      public void onDataChange(DataSnapshot p1) {
                        groupAvailList.clear();
                        for (DataSnapshot gSnapShot : p1.getChildren()) {

                          final groupUsersAvailable gAvailUsers =
                              gSnapShot.getValue(groupUsersAvailable.class);
                          String imageUrl =
                              gSnapShot.child("userProfileImage").getValue(String.class);
                          String name = gSnapShot.child("name").getValue(String.class);
                          String uid = gSnapShot.child("uid").getValue(String.class);

                          gAvailUsers.setUserAvailImage(imageUrl);
                          gAvailUsers.setUserAvailName(name);
                          gAvailUsers.setUid(uid);
                          if (!gAvailUsers.getUid().equals(FirebaseAuth.getInstance().getUid())) {
                            groupAvailList.add(gAvailUsers);
                          }
                        }
                        groupUsersAvailAdapter.notifyDataSetChanged();
                      }

                      @Override
                      public void onCancelled(DatabaseError p1) {}
                    });
            if (refreshLayout.isRefreshing()) {
              refreshLayout.setRefreshing(false);
            }
          }
        });

    proceedGroupUsersDetails.setOnClickListener(
        new View.OnClickListener() {

          @Override
          public void onClick(View view) {
            FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(
                    new ValueEventListener() {

                      @Override
                      public void onDataChange(DataSnapshot p1) {
                        if (p1.exists()) {
                          currentUserUid = p1.child("uid").getValue(String.class);
                          currentUserProfile = p1.child("userProfileImage").getValue(String.class);
                          currentUserName = p1.child("name").getValue(String.class);

                          HashMap<String, Object> currentUserGroup = new HashMap<>();
                          currentUserGroup.put("uid", currentUserUid);
                          currentUserGroup.put("userAvailImage", currentUserProfile);
                          currentUserGroup.put("userAvailName", currentUserName);
                          FirebaseDatabase.getInstance()
                              .getReference()
                              .child("Groups")
                              .child(key)
                              .child("Members")
                              .child(FirebaseAuth.getInstance().getUid())
                              .setValue(currentUserGroup)
                              .addOnCompleteListener(
                                  new OnCompleteListener<Void>() {

                                    @Override
                                    public void onComplete(Task<Void> p1) {
                                      if (p1.isSuccessful()) {
                                        SharedPreferences sPref =
                                            getSharedPreferences(
                                                "pushKeyFile", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor edt = sPref.edit();
                                        edt.putString("dbPushKey", key);
                                        edt.commit();
                                        Intent intent =
                                            new Intent(GroupAddUsers.this, GroupActivity.class);
                                        startActivity(intent);

                                        Toast.makeText(
                                                GroupAddUsers.this,
                                                "Successfully Created Group",
                                                Toast.LENGTH_SHORT)
                                            .show();
                                      }
                                    }
                                  });
                        }
                      }

                      @Override
                      public void onCancelled(DatabaseError p1) {}
                    });
          }
        });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.group_search_users, menu);
    MenuItem searchUsers = menu.findItem(R.id.search);
    final SearchView searchView = (SearchView) searchUsers.getActionView();
    searchView.setOnQueryTextListener(
        new SearchView.OnQueryTextListener() {

          @Override
          public boolean onQueryTextSubmit(String p1) {

            return false;
          }

          @Override
          public boolean onQueryTextChange(String p2) {
            groupUsersAvailAdapter.getFilter().filter(p2);
            return false;
          }
        });
    return super.onCreateOptionsMenu(menu);
  }
}
