package com.amol.realapp.chatty.adapter;

import com.amol.realapp.chatty.R;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.amol.realapp.chatty.model.Message;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import android.content.Intent;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.net.Uri;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.amol.realapp.chatty.model.groupUsersAvailable;
import de.hdodenhof.circleimageview.CircleImageView;

public class groupMessageAdapter extends RecyclerView.Adapter {

  

    private Context context;
    private ArrayList<Message> messages;

    final int ITEM_SENT=1;
    final int ITEM_RECIEVE=2;
    private String mainRoom;
    public groupMessageAdapter(Context context, ArrayList<Message> messages,String mainRoom) {
        this.context = context;
        this.messages = messages;
        this.mainRoom=mainRoom;
    }
    

    @Override
    public int getItemViewType(int position) {
        Message message=messages.get(position);
        if(FirebaseAuth.getInstance().getUid().equals(message.getSenderId())){
            return ITEM_SENT;   
        }
        else{
            return ITEM_RECIEVE; 
        }

    }
    
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup p1, int p2) {
       if(p2==ITEM_SENT){
           View v=LayoutInflater.from(context).inflate(R.layout.group_sent_message,p1,false);  
           return new SentViewHolder(v);
           
       }
       else{
           View v=LayoutInflater.from(context).inflate(R.layout.group_recieve_message,p1,false);  
           return new RecieverViewHolder(v);
           
       }
           
        
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder p1, int p2) {
   
        final Message message=messages.get(p2);
        
        if(p1.getClass()==SentViewHolder.class){
            final SentViewHolder viewHolder=(SentViewHolder)p1; 

            if(message.getMessage().equals("Photo")){
                viewHolder.sentImage.setVisibility(View.VISIBLE);
                viewHolder.sentMessage.setVisibility(View.GONE); 
                viewHolder.sentPdfContainer.setVisibility(View.GONE);

                Glide.with(context).load(message.getImageUrl()).centerCrop().diskCacheStrategy(DiskCacheStrategy.DATA).into(viewHolder.sentImage);


            }
            else if(message.getMessage().equals("Pdf")){ 
                viewHolder.sentMessage.setVisibility(View.GONE);
                viewHolder.sentPdfName.setText(message.getPdfName());

            } else{
                viewHolder.sentPdfContainer.setVisibility(View.GONE);

            }

            viewHolder.sentPdfContainer.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(message.getPdfUrl()),"application/pdf");
                        Intent chooser=Intent.createChooser(intent,"Select Pdf Viewer");
                        context.startActivity(chooser);
                    }

                });
            FirebaseDatabase.getInstance().getReference().child("Groups")
            .child(mainRoom)
            .child("Members")
            .child(message.getSenderId())
                .addValueEventListener(new ValueEventListener(){

                    @Override
                    public void onDataChange(DataSnapshot p1) {
                     if(p1.exists()){
                     groupUsersAvailable gAvailData=p1.getValue(groupUsersAvailable.class); 
                     viewHolder.sentGroupUserName.setText(gAvailData.getUserAvailName());
                     Glide.with(context).load(gAvailData.getUserAvailImage()).centerCrop().thumbnail(0.05f)
                       .placeholder(R.drawable.ic_profile)
                       .into(viewHolder.sentGroupUserImage);
                     
                     }   
                    }

                    @Override
                    public void onCancelled(DatabaseError p1) {
                    }

                
            });
            viewHolder.sentMessage.setText(message.getMessage());
            SimpleDateFormat dateFormat=new SimpleDateFormat("hh:mm a");

            viewHolder.sentTimestamp.setText(dateFormat.format(new Date(message.getTimeStamp())));


        }
        else{
            final RecieverViewHolder viewHolder=(RecieverViewHolder)p1; 

            if(message.getMessage().equals("Photo")){
                viewHolder.recievedImage.setVisibility(View.VISIBLE);
                viewHolder.recievedMessage.setVisibility(View.GONE); 
                viewHolder.recievePdfContainer.setVisibility(View.GONE);
                Glide.with(context).load(message.getImageUrl()).centerCrop().diskCacheStrategy(DiskCacheStrategy.DATA).into(viewHolder.recievedImage);


            }
            else if(message.getMessage().equals("Pdf")){ 
                viewHolder.recievedMessage.setVisibility(View.GONE);
                viewHolder.recievePdfName.setText(message.getPdfName());

            }  
            else{
                viewHolder.recievePdfContainer.setVisibility(View.GONE);

            }
            viewHolder.recievePdfContainer.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(message.getPdfUrl()),"application/pdf");
                        Intent chooser=Intent.createChooser(intent,"Select Pdf Viewer");
                        context.startActivity(chooser);
                    }

                });
            
            FirebaseDatabase.getInstance().getReference().child("Groups")
                .child(mainRoom)
                .child("Members")
                .child(message.getSenderId())
                .addValueEventListener(new ValueEventListener(){

                    @Override
                    public void onDataChange(DataSnapshot p1) {
                        if(p1.exists()){
                            groupUsersAvailable gAvailData=p1.getValue(groupUsersAvailable.class); 
                            viewHolder.recieveGroupUserName.setText(gAvailData.getUserAvailName());
                            Glide.with(context).load(gAvailData.getUserAvailImage()).centerCrop().thumbnail(0.05f)
                                .placeholder(R.drawable.ic_profile)
                                .into(viewHolder.recieveGroupUserImage);

                        }   
                    }

                    @Override
                    public void onCancelled(DatabaseError p1) {
                    }


                });    
                
            viewHolder.recievedMessage.setText(message.getMessage());
            SimpleDateFormat dateFormat=new SimpleDateFormat("hh:mm a");
            viewHolder.recievedTimestamp.setText(dateFormat.format(new Date(message.getTimeStamp())));
        }
       
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
    
    public class SentViewHolder extends RecyclerView.ViewHolder{
        TextView sentMessage,sentTimestamp,sentPdfName,sentGroupUserName;
        ImageView sentImage,sentDownload;
        LinearLayout sentPdfContainer;
        CircleImageView sentGroupUserImage;
        
        public SentViewHolder(View v){
            super(v); 
            sentMessage=v.findViewById(R.id.groupsentMessage);
            sentTimestamp=v.findViewById(R.id.groupsentTimeStamp);
            sentImage=v.findViewById(R.id.groupsentmessageImage);
            sentPdfName=v.findViewById(R.id.groupsentPdfName);
            sentPdfContainer=v.findViewById(R.id.group_sent_PdfContainer);    
            sentDownload=v.findViewById(R.id.groupicSentDownloadPdf);
            sentGroupUserName=v.findViewById(R.id.groupSentUserName);
            sentGroupUserImage=v.findViewById(R.id.groupSentUserImage);

            
        } 
    }
    public class RecieverViewHolder extends RecyclerView.ViewHolder{
        TextView recievedMessage,recievedTimestamp,recievePdfName,recieveGroupUserName;
        ImageView recievedImage;
        LinearLayout recievePdfContainer;
        CircleImageView recieveGroupUserImage;
        public RecieverViewHolder(View v){
            super(v);
            recievedMessage=v.findViewById(R.id.grouprecievedMessage);
            recievedTimestamp=v.findViewById(R.id.grouprecieveTimeStamp);
            recievedImage=v.findViewById(R.id.grouprecievemessageImage);
            recievePdfName=v.findViewById(R.id.grouprecievePdfName);
            recievePdfContainer=v.findViewById(R.id.group_recieve_PdfContainer);
            recieveGroupUserName=v.findViewById(R.id.groupRecieveUserName);
            recieveGroupUserImage=v.findViewById(R.id.groupRecieveUserImage);
            
        } 
    }
}
