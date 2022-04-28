package com.amol.realapp.chatty.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.amol.realapp.chatty.R;
import com.amol.realapp.chatty.activity.GroupChatActivity;
import com.amol.realapp.chatty.model.groupProfile;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class groupListAdapter extends RecyclerView.Adapter<groupListAdapter.groupItemHolder> {

  private Context context;
  private ArrayList<groupProfile> groupDetails;
  private String key;

  public groupListAdapter(Context context, ArrayList<groupProfile> groupDetails, String key) {
    this.context = context;
    this.groupDetails = groupDetails;
    this.key = key;
  }

  @Override
  public groupListAdapter.groupItemHolder onCreateViewHolder(ViewGroup p1, int p2) {
    View v =
        LayoutInflater.from(context)
            .inflate(R.layout.group_frg_details_list_item_layout, p1, false);
    return new groupItemHolder(v);
  }

  @Override
  public void onBindViewHolder(final groupListAdapter.groupItemHolder p1, int p2) {
    final groupProfile gProfile = groupDetails.get(p2);

    Picasso.get()
        .load(gProfile.getGroupProfile())
        .fit()
        .centerCrop()
        .placeholder(R.drawable.ic_profile)
        .networkPolicy(NetworkPolicy.OFFLINE)
        .into(
            p1.groupImage,
            new Callback() {

              @Override
              public void onSuccess() {}

              @Override
              public void onError(Exception p2) {
                Picasso.get()
                    .load(gProfile.getGroupProfile())
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.ic_profile)
                    .into(p1.groupImage);
              }
            });
    FirebaseDatabase.getInstance()
        .getReference()
        .child("Group-Chats")
        .child(gProfile.getGroupUid())
        .addValueEventListener(
            new ValueEventListener() {

              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                  SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");

                  String lastMssg =
                      dataSnapshot.child("lstMsg").child("lastMessage").getValue(String.class);
                  long time =
                      dataSnapshot.child("lstMsg").child("lastMessageTime").getValue(long.class);
                  if (lastMssg.length() > 30) {
                    p1.groupChat.setText(lastMssg);

                    p1.groupChat.setSingleLine(false);
                    p1.groupChat.setEllipsize(TextUtils.TruncateAt.END);
                    p1.groupChat.setLines(2);
                  } else {
                    p1.groupChat.setText(lastMssg);
                  }

                  p1.groupTime.setText(dateFormat.format(new Date(time)));

                } else {

                }
              }

              @Override
              public void onCancelled(DatabaseError dataSnapshot) {}
            });

    p1.groupName.setText(gProfile.getGroupName());

    p1.groupImage.setOnTouchListener(
        new View.OnTouchListener() {

          @Override
          public boolean onTouch(View view, MotionEvent event) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
              // TODO click
              View v =
                  LayoutInflater.from(context)
                      .inflate(R.layout.dialog_view_user_profile_lists, null, false);
              final Dialog s = new Dialog(context);
              s.setContentView(v);
              s.show();
              s.setCancelable(true);
              TextView userDialogName = v.findViewById(R.id.userDialogName);
              ImageView userDialogProfileImage = v.findViewById(R.id.userDialogProfileImage);
              ImageView userDialogChat = v.findViewById(R.id.userDialogChat);
              ImageView userDialogInfo = v.findViewById(R.id.userDialogInfo);

              userDialogName.setText(gProfile.getGroupName());
              if (!gProfile.getGroupProfile().equals("no image")) {

                Glide.with(context)
                    .load(gProfile.getGroupProfile())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(userDialogProfileImage);

              } else {
                userDialogProfileImage.setImageResource(R.drawable.ic_profile);
              }
              userDialogChat.setOnClickListener(
                  new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                      s.dismiss();
                      Intent intent = new Intent(context, GroupChatActivity.class);
                      intent.putExtra("groupName", gProfile.getGroupName());
                      intent.putExtra("groupProfile", gProfile.getGroupProfile());
                      intent.putExtra("groupUid", gProfile.getGroupUid());
                      intent.putExtra("mKey", key);

                      context.startActivity(intent);
                    }
                  });
              userDialogInfo.setOnClickListener(
                  new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                      Toast.makeText(
                              context,
                              "Group: " + gProfile.getGroupName() + "-info",
                              Toast.LENGTH_SHORT)
                          .show();
                    }
                  });
            } else if (action == MotionEvent.ACTION_UP) {
              // TODO up
            } else if (action == MotionEvent.ACTION_MOVE) {
              // TODO move
            }
            return true;
          }
        });
    p1.groupContainer.setOnClickListener(
        new View.OnClickListener() {

          @Override
          public void onClick(View view) {
            Intent intent = new Intent(context, GroupChatActivity.class);
            intent.putExtra("groupName", gProfile.getGroupName());
            intent.putExtra("groupProfile", gProfile.getGroupProfile());
            intent.putExtra("groupUid", gProfile.getGroupUid());
            intent.putExtra("mKey", key);
            context.startActivity(intent);
          }
        });
  }

  @Override
  public int getItemCount() {
    return groupDetails.size();
  }

  public class groupItemHolder extends RecyclerView.ViewHolder {
    ShapeableImageView groupImage;
    TextView groupName, groupChat, groupTime;
    MaterialCardView groupContainer;

    public groupItemHolder(View v) {
      super(v);
      groupImage = v.findViewById(R.id.groupImage);
      groupName = v.findViewById(R.id.groupName);
      groupChat = v.findViewById(R.id.groupChatMessage);
      groupTime = v.findViewById(R.id.groupTime);
      groupContainer = v.findViewById(R.id.groupsDetailsContainer);
    }
  }
}
