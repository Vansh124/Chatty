package com.amol.realapp.chatty.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.amol.realapp.chatty.R;
import com.amol.realapp.chatty.model.groupUsersAvailable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.FirebaseDatabase;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.ArrayList;

public class groupUsersAvailableAdapter
    extends RecyclerView.Adapter<groupUsersAvailableAdapter.groupUserAvailItemHolder>
    implements Filterable {

  private Context context;
  private ArrayList<groupUsersAvailable> groupAvailList;
  private ArrayList<groupUsersAvailable> groupAvailListFull;
  private String key;

  public groupUsersAvailableAdapter(
      Context context, ArrayList<groupUsersAvailable> groupAvailList, String key) {
    this.context = context;
    this.groupAvailList = groupAvailList;

    this.groupAvailListFull = groupAvailList;
    this.key = key;
  }

  @Override
  public groupUsersAvailableAdapter.groupUserAvailItemHolder onCreateViewHolder(
      ViewGroup p1, int p2) {
    View v =
        LayoutInflater.from(context).inflate(R.layout.users_available_group_item_layout, p1, false);

    return new groupUserAvailItemHolder(v);
  }

  @Override
  public void onBindViewHolder(
      final groupUsersAvailableAdapter.groupUserAvailItemHolder p1, final int p2) {
    final groupUsersAvailable userGroupAvail = groupAvailList.get(p2);

    Glide.with(context)
        .load(userGroupAvail.getUserAvailImage())
        .thumbnail(0.05f)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .placeholder(R.drawable.ic_profile)
        .into(p1.groupUserAvailImage);

    p1.groupUserAvailName.setText(userGroupAvail.getUserAvailName());
    p1.groupUserPhoneNumber.setText(userGroupAvail.getUid());

    p1.detailsContainer.setOnClickListener(
        new View.OnClickListener() {

          @Override
          public void onClick(View view) {

            groupUsersAvailable grpUsers = groupAvailList.get(p2);
            groupAvailList.remove(p2);

            FirebaseDatabase.getInstance()
                .getReference()
                .child("Groups")
                .child(key)
                .child("Members")
                .child(grpUsers.getUid())
                .setValue(grpUsers);
            notifyDataSetChanged();
          }
        });
  }

  @Override
  public Filter getFilter() {
    return new Filter() {

      @Override
      protected FilterResults performFiltering(CharSequence p1) {
        String charString = p1.toString();
        if (charString.isEmpty()) {
          groupAvailListFull = groupAvailList;
        } else {
          ArrayList<groupUsersAvailable> filteredList = new ArrayList<>();
          for (groupUsersAvailable gProfile : groupAvailList) {

            if (gProfile.getUserAvailName().toLowerCase().contains(charString.toLowerCase())) {
              filteredList.add(gProfile);
            }
          }
          groupAvailListFull = filteredList;
        }
        FilterResults fResults = new FilterResults();
        fResults.values = groupAvailListFull;
        return fResults;
      }

      @Override
      protected void publishResults(CharSequence p1, FilterResults p2) {
        groupAvailListFull = (ArrayList<groupUsersAvailable>) p2.values;
        notifyDataSetChanged();
      }
    };
  }

  @Override
  public int getItemCount() {
    return groupAvailListFull.size();
  }

  public class groupUserAvailItemHolder extends RecyclerView.ViewHolder {
    private CircleImageView groupUserAvailImage;
    private TextView groupUserAvailName, groupUserPhoneNumber;
    private LinearLayout detailsContainer;

    public groupUserAvailItemHolder(View v) {
      super(v);
      groupUserAvailImage = v.findViewById(R.id.groupUserAvailImage);
      groupUserAvailName = v.findViewById(R.id.groupUserAvailName);
      groupUserPhoneNumber = v.findViewById(R.id.groupUserAvailPhoneNumber);
      detailsContainer = v.findViewById(R.id.detailsContainer);
    }
  }
}
