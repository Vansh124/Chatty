package com.amol.realapp.chatty.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.amol.realapp.chatty.R;
import com.amol.realapp.chatty.adapter.groupMessageAdapter;
import com.amol.realapp.chatty.model.Message;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import de.hdodenhof.circleimageview.CircleImageView;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class GroupChatActivity extends AppCompatActivity {
  private Toolbar tb;
  private ArrayList<Message> messages;
  private groupMessageAdapter gMessageAdapter;
  private String groupName, txt_profile, recieverUid, senderUid;
  private String senderRoom, mainRoom;
  private String filePath, pdfFilePath;
  private FloatingActionButton sendMessage;
  private EditText messageBox;
  private FirebaseDatabase database;
  private RecyclerView messagesList;
  private ImageView icAttach, icCam, icBack;
  private FirebaseStorage storage;
  private TextView name, statusIndicator;
  private CircleImageView profileImage;
  private DatabaseReference messageReference;

  private LinearLayout attachPdfs, attachDoc, attachAudio, attachGallery;

  private static final int GET_GALLERY = 11;
  private static final int GET_PDFS = 12;

  private File uploadFile;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_group_chat);
    init();
    initListener();
  }

  private void init() {
    tb = findViewById(R.id.toolbar);
    icBack = findViewById(R.id.groupBack);
    profileImage = findViewById(R.id.groupProfileImage);
    name = findViewById(R.id.groupProfileName);
    statusIndicator = findViewById(R.id.online_text_indicator);

    groupName = getIntent().getStringExtra("groupName");
    txt_profile = getIntent().getStringExtra("groupProfile");

    mainRoom = getIntent().getStringExtra("groupUid");

    senderUid = FirebaseAuth.getInstance().getUid();

    messages = new ArrayList<>();
    gMessageAdapter = new groupMessageAdapter(GroupChatActivity.this, messages, mainRoom);
    senderRoom = senderUid + recieverUid;
    //  recieverRoom = recieverUid + senderUid;

    messageBox = findViewById(R.id.chatMessage);
    sendMessage = findViewById(R.id.chatMphone_send);
    database = FirebaseDatabase.getInstance();
    storage = FirebaseStorage.getInstance();
    messagesList = findViewById(R.id.messagesList);
    messagesList.setLayoutManager(new LinearLayoutManager(GroupChatActivity.this));
    messagesList.setAdapter(gMessageAdapter);

    icAttach = findViewById(R.id.chatAttachment);
    icCam = findViewById(R.id.chatCam);
  }

  private void initListener() {
    setSupportActionBar(tb);
    Glide.with(GroupChatActivity.this)
        .load(txt_profile)
        .diskCacheStrategy(DiskCacheStrategy.DATA)
        .into(profileImage);
        
    name.setText(groupName);
    getSupportActionBar().setDisplayShowTitleEnabled(false);
    getSupportActionBar().setDisplayHomeAsUpEnabled(false);

    icBack.setOnClickListener(
        new View.OnClickListener() {

          @Override
          public void onClick(View view) {
            finish();
          }
        });

    sendMessage.setOnClickListener(
        new View.OnClickListener() {

          @Override
          public void onClick(View view) {
            String messageTxt = messageBox.getText().toString();
            if (messageTxt.isEmpty()) {

            } else {
              sendMessage(messageTxt);
            }
          }
        });

    icAttach.setOnClickListener(
        new View.OnClickListener() {

          @Override
          public void onClick(View view) {
            dialogAttachment();
          }
        });
    icCam.setOnClickListener(
        new View.OnClickListener() {

          @Override
          public void onClick(View view) {}
        });
  }

  ActivityResultLauncher<String> getImageFromGallery =
      registerForActivityResult(
          new ActivityResultContracts.GetContent(),
          new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
              if (uri != null) {
                getImageFromActivityLauncher(uri);
              }
            }
          });

  private void getImageFromActivityLauncher(Uri uri) {
      
      Uri selectedImage = uri;
            Calendar cal = Calendar.getInstance();

            final StorageReference strRef =
                storage.getReference().child("Group-Chats").child(cal.getTimeInMillis() + "");
            View v =
                LayoutInflater.from(GroupChatActivity.this)
                    .inflate(R.layout.dialog_attachment, null, false);
            final Dialog mDialog = new Dialog(GroupChatActivity.this);
            mDialog.setContentView(v);
            mDialog.setCancelable(false);

            strRef
                .putFile(selectedImage)
                .addOnCompleteListener(
                    new OnCompleteListener<UploadTask.TaskSnapshot>() {

                      @Override
                      public void onComplete(Task<UploadTask.TaskSnapshot> p1) {
                        if (p1.isSuccessful()) {
                          strRef
                              .getDownloadUrl()
                              .addOnSuccessListener(
                                  new OnSuccessListener<Uri>() {

                                    @Override
                                    public void onSuccess(Uri p1) {
                                      filePath = p1.toString();
                                      mDialog.dismiss();
                                      String messageTxt = messageBox.getText().toString();
                                      sendMessageWithImage(messageTxt);
                                    }
                                  });
                        }
                      }
                    });
            mDialog.show();
      
  }
  
  ActivityResultLauncher<String> getPdfFromFiles =
      registerForActivityResult(
          new ActivityResultContracts.GetContent(),
          new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
              if (uri != null) {
                getPdfFromActivityLauncher(uri);
              }
            }
 	 });  
  private void getPdfFromActivityLauncher(Uri uri){
      
      Uri selectedPdf = uri;
            uploadFile = new File(selectedPdf.getPath());
            Calendar cal = Calendar.getInstance();
            final StorageReference strRef =
                storage.getReference().child("Group-Chats").child(uploadFile.getName());
            View v =
                LayoutInflater.from(GroupChatActivity.this)
                    .inflate(R.layout.dialog_attachment, null, false);
            final Dialog mDialog = new Dialog(GroupChatActivity.this);
            mDialog.setContentView(v);
            mDialog.setCancelable(false);

            strRef
                .putFile(selectedPdf)
                .addOnCompleteListener(
                    new OnCompleteListener<UploadTask.TaskSnapshot>() {

                      @Override
                      public void onComplete(Task<UploadTask.TaskSnapshot> p1) {
                        if (p1.isSuccessful()) {
                          strRef
                              .getDownloadUrl()
                              .addOnSuccessListener(
                                  new OnSuccessListener<Uri>() {

                                    @Override
                                    public void onSuccess(Uri p1) {
                                      pdfFilePath = p1.toString();

                                      mDialog.dismiss();
                                      String messageTxt = messageBox.getText().toString();
                                      sendMessageWithPdf(messageTxt);
                                    }
                                  });
                        }
                      }
                    });
            mDialog.show();
      
  }    
      
  

  

  private void recieveMessage() {
    // Recieve Messsages

    messageReference =
        database.getReference().child("Group-Chats").child(mainRoom).child("messages");
    messageReference.keepSynced(true);
    messageReference.addValueEventListener(
        new ValueEventListener() {

          @Override
          public void onDataChange(DataSnapshot p1) {
            messages.clear();
            for (DataSnapshot snapshot1 : p1.getChildren()) {
              Message message = snapshot1.getValue(Message.class);
              messages.add(message);
              messagesList.smoothScrollToPosition(gMessageAdapter.getItemCount());
            }
            gMessageAdapter.notifyDataSetChanged();
          }

          @Override
          public void onCancelled(DatabaseError p1) {}
        });
  }

  private void sendMessage(String messageTxt) {

    final String randomKey = database.getReference().push().getKey();

    Date date = new Date();
    final Message message = new Message(messageTxt, senderUid, date.getTime());

    messageBox.setText("");

    HashMap<String, Object> lastMessageObj = new HashMap<>();
    lastMessageObj.put("lastMessage", message.getMessage());
    lastMessageObj.put("lastMessageTime", Calendar.getInstance().getTimeInMillis());
    database
        .getReference()
        .child("Group-Chats")
        .child(mainRoom)
        .child("lstMsg")
        .updateChildren(lastMessageObj);

    database
        .getReference()
        .child("Group-Chats")
        .child(mainRoom)
        .child("messages")
        .push()
        .setValue(message)
        .addOnSuccessListener(
            new OnSuccessListener<Void>() {

              @Override
              public void onSuccess(Void p1) {}
            });
  }

  @Override
  protected void onStart() {
    super.onStart();
    recieveMessage();
  }

  private void sendMessageWithImage(String messageTxt) {
    final String randomKey = database.getReference().push().getKey();

    Date date = new Date();
    final Message message = new Message(messageTxt, senderUid, date.getTime());
    message.setMessage("Photo");
    message.setImageUrl(filePath);

    messageBox.setText("");

    HashMap<String, Object> lastMessageObj = new HashMap<>();
    lastMessageObj.put("lastMessage", message.getMessage());
    lastMessageObj.put("lastMessageTime", Calendar.getInstance().getTimeInMillis());
    database
        .getReference()
        .child("Group-Chats")
        .child(mainRoom)
        .child("lstMsg")
        .updateChildren(lastMessageObj);

    database
        .getReference()
        .child("Group-Chats")
        .child(mainRoom)
        .child("messages")
        .child(randomKey)
        .setValue(message)
        .addOnSuccessListener(
            new OnSuccessListener<Void>() {

              @Override
              public void onSuccess(Void p1) {
                database
                    .getReference()
                    .child("Group-Chats")
                    .child(mainRoom)
                    .child("messages")
                    .child(randomKey)
                    .setValue(message)
                    .addOnSuccessListener(
                        new OnSuccessListener<Void>() {

                          @Override
                          public void onSuccess(Void p1) {}
                        });
              }
            });
  }

  private void dialogAttachment() {

    View v = LayoutInflater.from(this).inflate(R.layout.mydialog_custom_attachment, null, false);
    final Dialog dialog = new Dialog(GroupChatActivity.this);
    dialog.setContentView(v);
    dialog.show();
    attachDoc = v.findViewById(R.id.attachmentDocuments);
    attachAudio = v.findViewById(R.id.attachmentAudio);
    attachGallery = v.findViewById(R.id.attachmentGallery);
    attachPdfs = v.findViewById(R.id.attachmentPdfs);
    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
    StrictMode.setVmPolicy(builder.build());

    attachGallery.setOnClickListener(
        new View.OnClickListener() {

          @Override
          public void onClick(View view) {
            dialog.dismiss();
            getImageFromGallery.launch("image/*");
           }
        });
    attachPdfs.setOnClickListener(
        new View.OnClickListener() {

          @Override
          public void onClick(View view) {
            dialog.dismiss();
            getPdfFromFiles.launch("application/pdf")
            
          }
        });
  }

  private void sendMessageWithPdf(String messageTxt) {
    final String randomKey = database.getReference().push().getKey();

    Date date = new Date();
    final Message message = new Message(messageTxt, senderUid, date.getTime());
    message.setMessage("Pdf");
    message.setPdfUrl(pdfFilePath);
    message.setPdfName(uploadFile.getName());

    HashMap<String, Object> lastMessageObj = new HashMap<>();
    lastMessageObj.put("lastMessage", message.getMessage());
    lastMessageObj.put("lastMessageTime", Calendar.getInstance().getTimeInMillis());
    database
        .getReference()
        .child("Group-Chats")
        .child(mainRoom)
        .child("lstMsg")
        .updateChildren(lastMessageObj);

    database
        .getReference()
        .child("Group-Chats")
        .child(mainRoom)
        .child("messages")
        .child(randomKey)
        .setValue(message)
        .addOnSuccessListener(
            new OnSuccessListener<Void>() {

              @Override
              public void onSuccess(Void p1) {
                database
                    .getReference()
                    .child("Group-Chats")
                    .child(mainRoom)
                    .child("messages")
                    .child(randomKey)
                    .setValue(message)
                    .addOnSuccessListener(
                        new OnSuccessListener<Void>() {

                          @Override
                          public void onSuccess(Void p1) {}
                        });
              }
            });
  }
}
