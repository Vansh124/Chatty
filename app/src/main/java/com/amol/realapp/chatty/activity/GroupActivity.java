package com.amol.realapp.chatty.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.amol.realapp.chatty.R;
import com.amol.realapp.chatty.model.groupProfile;
import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import de.hdodenhof.circleimageview.CircleImageView;
import java.io.File;

public class GroupActivity extends AppCompatActivity {

  private DatabaseReference groupRef;
  private StorageReference storageRef;
  private CircleImageView groupImage;
  private FirebaseDatabase database;
  private FirebaseStorage storage;
  private EditText name;
  private String txtName;
  private MaterialButton submiGrpDetails;
  private FirebaseAuth mAuth;
  private String key, groupProfilePhotos_fileString;
  private Intent resultIntent;
  private Uri selectedImage, imageUri;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_group);
    init();
    initListener();
  }

  private void initListener() {

    groupImage.setOnClickListener(
        new View.OnClickListener() {

          @Override
          public void onClick(View view) {
            groupImageRetriever.launch("image/*");
          }
        });
    submiGrpDetails.setOnClickListener(
        new View.OnClickListener() {

          @Override
          public void onClick(View view) {
            txtName = name.getText().toString();

            if (txtName.isEmpty()) {
              name.setError("Please enter group's name");
              return;
            } else {
              actCreateGroup();
            }
          }
        });
  }

  private void init() {

    groupImage = findViewById(R.id.groupProfileImage);
    name = findViewById(R.id.groupProfileName);
    mAuth = FirebaseAuth.getInstance();
    storage = FirebaseStorage.getInstance();
    database = FirebaseDatabase.getInstance();
    submiGrpDetails = findViewById(R.id.submitGroupDetails);
  }

  private void actCreateGroup() {
    SharedPreferences sPref = getSharedPreferences("pushKeyFile", Context.MODE_PRIVATE);
    key = sPref.getString("dbPushKey", "");

    groupRef = database.getReference().child("Groups");

    if (selectedImage != null) {
      storageRef = storage.getReference().child("Groups");

      storageRef
          .putFile(selectedImage)
          .addOnCompleteListener(
              new OnCompleteListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onComplete(Task<UploadTask.TaskSnapshot> p1) {
                  if (p1.isSuccessful()) {

                    storageRef
                        .getDownloadUrl()
                        .addOnSuccessListener(
                            new OnSuccessListener<Uri>() {

                              @Override
                              public void onSuccess(Uri p1) {
                                String imageUrl = p1.toString();

                                groupProfile groupProfile =
                                    new groupProfile(key, txtName, imageUrl);

                                database
                                    .getReference()
                                    .child("Groups")
                                    .child(key)
                                    .child("groupInfo")
                                    .setValue(groupProfile)
                                    .addOnCompleteListener(
                                        new OnCompleteListener<Void>() {

                                          @Override
                                          public void onComplete(Task<Void> p1) {
                                            if (p1.isSuccessful()) {

                                              Intent intent =
                                                  new Intent(
                                                      GroupActivity.this, MainActivity.class);
                                              startActivity(intent);
                                              finish();

                                            } else {
                                              Toast.makeText(
                                                      GroupActivity.this,
                                                      p1.getException().getMessage(),
                                                      Toast.LENGTH_SHORT)
                                                  .show();
                                            }
                                          }
                                        });
                              }
                            });
                  }
                }
              });
    } else {

      groupProfile gProfile = new groupProfile(key, txtName, "no image");
      groupRef
          .child(key)
          .child("groupInfo")
          .setValue(gProfile)
          .addOnCompleteListener(
              new OnCompleteListener<Void>() {

                @Override
                public void onComplete(Task<Void> p1) {
                  if (p1.isSuccessful()) {
                    String newKey = key.toString();

                    Intent intent = new Intent(GroupActivity.this, MainActivity.class);
                    intent.putExtra("myPushKey", newKey);
                    startActivity(intent);
                    finish();
                  } else {
                    Toast.makeText(
                            GroupActivity.this, p1.getException().getMessage(), Toast.LENGTH_SHORT)
                        .show();
                  }
                }
              });
    }
  }

  private ActivityResultLauncher<String> groupImageRetriever =
      registerForActivityResult(
          new ActivityResultContracts.GetContent(),
          new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
              if (uri != null) {
                getUriFromActivityLauncher(uri);
              }
            }
          });

  public void getUriFromActivityLauncher(Uri uri) {
    File f1 =
        new File(
            Environment.getExternalStorageDirectory() + "/" + "Chatty" + "/" + "GroupProfilePhotos",
            FirebaseAuth.getInstance().getCurrentUser().getUid());
    if (!f1.exists()) {
      f1.mkdirs();
    }
    groupProfilePhotos_fileString =
        "Chatty"
            + File.separator
            + "GroupProfilePhotos"
            + File.separator
            + FirebaseAuth.getInstance().getCurrentUser().getUid();

    Intent dsPhotoEditorIntent = new Intent(GroupActivity.this, DsPhotoEditorActivity.class);
    dsPhotoEditorIntent.setData(uri);

    dsPhotoEditorIntent.putExtra(
        DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, groupProfilePhotos_fileString);

    int[] toolsToHide = {
      DsPhotoEditorActivity.TOOL_CONTRAST,
      DsPhotoEditorActivity.TOOL_DRAW,
      DsPhotoEditorActivity.TOOL_EXPOSURE,
      DsPhotoEditorActivity.TOOL_FILTER,
      DsPhotoEditorActivity.TOOL_FRAME,
      DsPhotoEditorActivity.TOOL_ORIENTATION,
      DsPhotoEditorActivity.TOOL_PIXELATE,
      DsPhotoEditorActivity.TOOL_ROUND,
      DsPhotoEditorActivity.TOOL_SATURATION,
      DsPhotoEditorActivity.TOOL_SHARPNESS,
      DsPhotoEditorActivity.TOOL_STICKER,
      DsPhotoEditorActivity.TOOL_TEXT,
      DsPhotoEditorActivity.TOOL_VIGNETTE,
      DsPhotoEditorActivity.TOOL_WARMTH
    };
    dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_TOOLS_TO_HIDE, toolsToHide);
    intentLauncher.launch(dsPhotoEditorIntent);
  }

  private ActivityResultLauncher<Intent> intentLauncher =
      registerForActivityResult(
          new ActivityResultContracts.StartActivityForResult(),
          result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
              resultIntent = result.getData();
              imageUri = resultIntent.getData();

              if (imageUri != null) {
                Log.d(
                    "GroupActivity.java",
                    "Image Uri  "
                        + imageUri.toString()
                        + " File Name: "
                        + imageUri.getLastPathSegment());

                groupImage.setImageURI(imageUri);
                selectedImage = imageUri;
              } else {
                Log.d("GroupActivity.java", "Image Uri null");
              }
            }
          });

  private void showToast(String text) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
  }
}
