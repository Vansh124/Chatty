package com.amol.realapp.chatty.adapter;
import com.amol.realapp.chatty.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.amol.realapp.chatty.model.userStatus;
import com.devlomi.circularstatusview.CircularStatusView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.ArrayList;
import android.content.Intent;
import com.amol.realapp.chatty.activity.StatusViewActivity;
import com.amol.realapp.chatty.model.Status;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.Glide;
public class TopStatusAdapter extends RecyclerView.Adapter<TopStatusAdapter.TopStatusViewHolder> {


    private Context context;
    private ArrayList<userStatus> userStatuses;
    
    public TopStatusAdapter(Context context, ArrayList<userStatus> userStatuses) {
        this.context = context;
        this.userStatuses = userStatuses;
    }
    
    @Override
    public TopStatusAdapter.TopStatusViewHolder onCreateViewHolder(ViewGroup p1, int p2) {
        View v=LayoutInflater.from(context).inflate(R.layout.item_status,p1,false);
        
        return new TopStatusViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final TopStatusAdapter.TopStatusViewHolder p1, int p2) {
    final userStatus uStatus=userStatuses.get(p2);
    
    final Status lastStatus=uStatus.getStatuses().get( uStatus.getStatuses().size()-1);
        
    p1.circleStatusView.setPortionsCount(uStatus.getStatuses().size());
   
    if(uStatus.getProfileImage().equals("no image")){
    p1.circleImageView.setImageResource(R.drawable.ic_profile);   
    }
    else{
    Glide.with(context).load(uStatus.getProfileImage()).diskCacheStrategy(DiskCacheStrategy.DATA).into(p1.circleImageView);
      
        
    }
    p1.circleStatusView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(context,StatusViewActivity.class);
                    
                    context.startActivity(intent);
                    
                }
            });
       
        
    }
    
   

    @Override
    public int getItemCount() {
        return userStatuses.size();
    }

    public class TopStatusViewHolder extends RecyclerView.ViewHolder{
       CircleImageView circleImageView;
       CircularStatusView circleStatusView;
        public TopStatusViewHolder(View v){
        super(v); 
        circleImageView=v.findViewById(R.id.circle_image_view);
        circleStatusView=v.findViewById(R.id.circular_status_view);
       
        }
    }
    
    
    
}
