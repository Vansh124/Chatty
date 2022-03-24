package com.amol.realapp.chatty.fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.amol.realapp.chatty.R;
import com.amol.realapp.chatty.activity.GroupActivity;
import com.amol.realapp.chatty.model.groupProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.ArrayList;
import com.amol.realapp.chatty.activity.GroupAddUsers;
import androidx.recyclerview.widget.RecyclerView;
import com.amol.realapp.chatty.adapter.groupListAdapter;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.auth.FirebaseAuth;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.firebase.auth.FirebaseUser;
import java.util.HashMap;
import com.amol.realapp.chatty.model.groupUsersAvailable;
import android.content.SharedPreferences;
import android.content.Context;
import com.amol.realapp.chatty.model.usersUid;
public class GroupsFragment extends Fragment {

    private View v;


    private RecyclerView groupRecView;
    private groupListAdapter gAdapter;
    private ArrayList<groupProfile> groupList=new ArrayList<>();

    private DatabaseReference dref,groupDetailsRef;

    private String key;

    public GroupsFragment() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_groups, container, false);
        init();
        initListener();
        return v;
    }

    private void init() {
        SharedPreferences sPref=getActivity().getSharedPreferences("pushKeyFile", Context.MODE_PRIVATE);
        key = sPref.getString("dbPushKey", "");

        dref = FirebaseDatabase.getInstance().getReference().child("Groups");


        groupRecView = v.findViewById(R.id.groupsList);
        groupDetailsRef = dref.child(key);


    }

    private void initListener() {
        addGroupForUser();
        gAdapter = new groupListAdapter(getActivity(), groupList,key);
        LinearLayoutManager llm=new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        groupRecView.setLayoutManager(llm);
        groupRecView.setAdapter(gAdapter);
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.group_item, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.createGroup:
                startActivity(new Intent(getActivity(), GroupAddUsers.class));

                break;
        }
        return super.onOptionsItemSelected(item);
    }




    public boolean checkUserExists(DataSnapshot dSnapshot) {

        return FirebaseAuth.getInstance().getUid().equals(dSnapshot.child("Members").child(FirebaseAuth.getInstance().getUid()).child("groupUid").getValue(String.class));
    }     
    public void addGroupForUser() {
        dref.addValueEventListener(new ValueEventListener(){

                @Override
                public void onDataChange(DataSnapshot p1) {
                    if(p1.exists()){
                        
                    
                        groupList.clear();
                        for(DataSnapshot mSnap:p1.getChildren()){
                           
                              
                           
                            String mUid=mSnap.child("groupInfo").child("groupUid").getValue(String.class);
                            String groupName=mSnap.child("groupInfo").child("groupName").getValue(String.class);
                            String groupProfile=mSnap.child("groupInfo").child("groupProfile").getValue(String.class);
                            String uid=mSnap.child("Members").child(FirebaseAuth.getInstance().getUid()).child("uid").getValue(String.class);
                   
                           groupProfile gDetails=mSnap.getValue(groupProfile.class);
                            gDetails.setGroupUid(mUid);
                            gDetails.setGroupName(groupName);
                            gDetails.setGroupProfile(groupProfile);
                           if(FirebaseAuth.getInstance().getUid().equals(uid)){
                               
                           
                            groupList.add(gDetails);
                        }
                        else{
                                    }
                        }
                        gAdapter.notifyDataSetChanged();
                      
                       }
                    }
                

                @Override
                public void onCancelled(DatabaseError p1) {
                }


            });
    }
    
}
