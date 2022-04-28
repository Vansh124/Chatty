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
import java.util.ArrayList;

public class groupUsersAddedAdapter
    extends RecyclerView.Adapter<groupUsersAddedAdapter.groupUserAddedItemHolder> {

  private Context context;
  private ArrayList<groupUsersAdded> groupNewAddedUsers;

  public groupUsersAddedAdapter(Context context, ArrayList<groupUsersAdded> groupNewAddedUsers) {
    this.context = context;
    this.groupNewAddedUsers = groupNewAddedUsers;
  }

  @Override
  public groupUsersAddedAdapter.groupUserAddedItemHolder onCreateViewHolder(ViewGroup p1, int p2) {
    View v = LayoutInflater.from(context).inflate(R.layout.group_users_added, p1, false);

    return new groupUserAddedItemHolder(v);
  }

  @Override
  public void onBindViewHolder(
      final groupUsersAddedAdapter.groupUserAddedItemHolder p1, final int p2) {

    final groupUsersAdded userGroupAdded = groupNewAddedUsers.get(p2);

    Glide.with(context)
        .load(userGroupAdded.getUserImage())
        .centerCrop()
        .thumbnail(0.05f)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .placeholder(R.drawable.ic_profile)
        .into(p1.groupUsersAddedProfileImg);

    p1.groupUsersAddedName.setText(userGroupAdded.getUserName());

    p1.groupUsersCross.setOnClickListener(
        new View.OnClickListener() {

          @Override
          public void onClick(View view) {

            groupNewAddedUsers.remove(p2);
            notifyDataSetChanged();
          }
        });
  }

  @Override
  public int getItemCount() {
    return groupNewAddedUsers.size();
  }

  public class groupUserAddedItemHolder extends RecyclerView.ViewHolder {
    private ImageView groupUsersAddedProfileImg;
    private ImageView groupUsersCross;
    private TextView groupUsersAddedName;

    public groupUserAddedItemHolder(View v) {
      super(v);
      groupUsersAddedProfileImg = v.findViewById(R.id.groupUserAddedProfileImage);
      groupUsersCross = v.findViewById(R.id.groupUserAddedCross);
      groupUsersAddedName = v.findViewById(R.id.groupUserAddedName);
    }
  }
}
