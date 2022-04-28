package com.amol.realapp.chatty.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.amol.realapp.chatty.R;
import com.amol.realapp.chatty.activity.MainActivity;
import com.amol.realapp.chatty.model.Status;
import com.amol.realapp.chatty.model.userStatus;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.devlomi.circularstatusview.CircularStatusView;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.ArrayList;
import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class TopStatusAdapter extends RecyclerView.Adapter<TopStatusAdapter.TopStatusViewHolder> {

  private Context context;
  private ArrayList<userStatus> userStatuses;

  public TopStatusAdapter(Context context, ArrayList<userStatus> userStatuses) {
    this.context = context;
    this.userStatuses = userStatuses;
  }

  @Override
  public TopStatusAdapter.TopStatusViewHolder onCreateViewHolder(ViewGroup p1, int p2) {
    View v = LayoutInflater.from(context).inflate(R.layout.item_status, p1, false);

    return new TopStatusViewHolder(v);
  }

  @Override
  public void onBindViewHolder(final TopStatusAdapter.TopStatusViewHolder p1, int p2) {
    final userStatus uStatus = userStatuses.get(p2);

    final Status lastStatus = uStatus.getStatuses().get(uStatus.getStatuses().size() - 1);

    p1.circleStatusView.setPortionsCount(uStatus.getStatuses().size());

    if (uStatus.getProfileImage().equals("no image")) {
      p1.circleImageView.setImageResource(R.drawable.ic_profile);
    } else {
      Glide.with(context)
          .load(uStatus.getProfileImage())
          .diskCacheStrategy(DiskCacheStrategy.DATA)
          .into(p1.circleImageView);
    }
    p1.circleStatusView.setOnClickListener(
        new View.OnClickListener() {

          @Override
          public void onClick(View view) {
            ArrayList<MyStory> myStories = new ArrayList<>();
            for (Status status : uStatus.getStatuses()) {
              myStories.add(new MyStory(status.getImageUrl()));
            }

            StoryView.Builder builder =
                new StoryView.Builder(((MainActivity) context).getSupportFragmentManager());
            builder.setStoriesList(myStories);
            builder.setStoryDuration(5000);
            builder.setTitleText(uStatus.getName());
            builder.setSubtitleText("");
            builder.setTitleLogoUrl(uStatus.getProfileImage());
            builder.setStoryClickListeners(
                new StoryClickListeners() {

                  @Override
                  public void onDescriptionClickListener(int arg) {
                    // TODO your action

                  }

                  @Override
                  public void onTitleIconClickListener(int arg0) {
                    // TODO your action

                  }
                });
            builder.build();
            builder.show();
          }
        });
  }

  @Override
  public int getItemCount() {
    return userStatuses.size();
  }

  public class TopStatusViewHolder extends RecyclerView.ViewHolder {
    CircleImageView circleImageView;
    CircularStatusView circleStatusView;

    public TopStatusViewHolder(View v) {
      super(v);
      circleImageView = v.findViewById(R.id.circle_image_view);
      circleStatusView = v.findViewById(R.id.circular_status_view);
    }
  }
}
