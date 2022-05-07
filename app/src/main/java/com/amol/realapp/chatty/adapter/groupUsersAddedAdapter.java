package com.amol.realapp.chatty.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.amol.realapp.chatty.R;
import com.amol.realapp.chatty.model.groupUsersAdded;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.HashMap;

public class groupUsersAddedAdapter
    extends RecyclerView.Adapter<groupUsersAddedAdapter.groupUserAddedItemHolder> {

  private Context context;
  private ArrayList<groupUsersAdded> groupNewAddedUsers;
  private DatabaseReference userAddedAvailableReference;
  private String key;

  public groupUsersAddedAdapter(
      Context context, ArrayList<groupUsersAdded> groupNewAddedUsers, String key) {
    this.context = context;
    this.groupNewAddedUsers = groupNewAddedUsers;
    this.key = key;
  }

  @Override
  public groupUsersAddedAdapter.groupUserAddedItemHolder onCreateViewHolder(ViewGroup p1, int p2) {
    View v = LayoutInflater.from(context).inflate(R.layout.group_users_added, p1, false);

    return new groupUserAddedItemHolder(v);
  }

  @Override
  public void onBindViewHolder(groupUsersAddedAdapter.groupUserAddedItemHolder p1, final int p2) {

    userAddedAvailableReference =
        FirebaseDatabase.getInstance().getReference().child("Groups").child(key);

    groupUsersAdded userGroupAdded = groupNewAddedUsers.get(p2);

    Glide.with(context)
        .load(userGroupAdded.getUserImage())
        .centerCrop()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .placeholder(R.drawable.ic_profile)
        .into(p1.groupUsersAddedProfileImg);

    p1.groupUsersAddedName.setText(userGroupAdded.getUserName());

    p1.groupUsersCross.setOnClickListener(
        new View.OnClickListener() {

          @Override
          public void onClick(View view) {
            String uid = userGroupAdded.getUid();
            String imageUrl = userGroupAdded.getUserImage();
            String name = userGroupAdded.getUserName();

            groupNewAddedUsers.remove(p2);

            userAddedAvailableReference
                .child("Members")
                .child(userGroupAdded.getUid())
                .removeValue();

            HashMap<String, Object> userAvailableGroup = new HashMap<>();
            userAvailableGroup.put("userAvailImage", imageUrl);
            userAvailableGroup.put("userAvailName", name);
            userAvailableGroup.put("uid", uid);

            userAddedAvailableReference
                .child("Available_Members")
                .child(userGroupAdded.getUid())
                .setValue(userAvailableGroup);

            notifyDataSetChanged();
          }
        });
  }

  @Override
  public int getItemCount() {
    return groupNewAddedUsers.size();
  }

  public class groupUserAddedItemHolder extends RecyclerView.ViewHolder {
    private ShapeableImageView groupUsersAddedProfileImg;
    private ShapeableImageView groupUsersCross;
    private TextView groupUsersAddedName;

    public groupUserAddedItemHolder(View v) {
      super(v);
      groupUsersAddedProfileImg = v.findViewById(R.id.groupUserAddedProfileImage);
      groupUsersCross = v.findViewById(R.id.groupUserAddedCross);
      groupUsersAddedName = v.findViewById(R.id.groupUserAddedName);
    }
  }
}
