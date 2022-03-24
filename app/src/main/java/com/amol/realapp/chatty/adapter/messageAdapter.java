package com.amol.realapp.chatty.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.amol.realapp.chatty.R;
import com.amol.realapp.chatty.model.Message;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import android.widget.LinearLayout;
import android.content.Intent;
import android.net.Uri;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class messageAdapter extends RecyclerView.Adapter {
    private Context context;
    private ArrayList<Message> messages;
    
    final int ITEM_SENT=1;
    final int ITEM_RECIEVE=2;
    public messageAdapter(Context context, ArrayList<Message> messages) {
        this.context = context;
        this.messages = messages;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup p1, int p2) {
        if(p2==ITEM_SENT){
        View v=LayoutInflater.from(context).inflate(R.layout.sent_message,p1,false);  
        return new SentViewHolder(v);
        }
        else{
            View v=LayoutInflater.from(context).inflate(R.layout.recieve_message,p1,false);  
        return new RecieverViewHolder(v);
       
        }
        
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
    public void onBindViewHolder(final RecyclerView.ViewHolder p1, int p2) {
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
        TextView sentMessage,sentTimestamp,sentPdfName;
     ImageView sentImage,sentDownload;
     LinearLayout sentPdfContainer;
     
          public SentViewHolder(View v){
     super(v); 
     sentMessage=v.findViewById(R.id.sentMessage);
     sentTimestamp=v.findViewById(R.id.sentTimeStamp);
     sentImage=v.findViewById(R.id.sentmessageImage);
     sentPdfName=v.findViewById(R.id.sentPdfName);
     sentPdfContainer=v.findViewById(R.id.sent_PdfContainer);    
     sentDownload=v.findViewById(R.id.icSentDownloadPdf);
     } 
    }
    public class RecieverViewHolder extends RecyclerView.ViewHolder{
    TextView recievedMessage,recievedTimestamp,recievePdfName;
    ImageView recievedImage;
        LinearLayout recievePdfContainer;
        
    public RecieverViewHolder(View v){
    super(v);
        recievedMessage=v.findViewById(R.id.recievedMessage);
        recievedTimestamp=v.findViewById(R.id.recieveTimeStamp);
        recievedImage=v.findViewById(R.id.recievemessageImage);
        recievePdfName=v.findViewById(R.id.recievePdfName);
        recievePdfContainer=v.findViewById(R.id.recieve_PdfContainer);
    } 
    }
}
