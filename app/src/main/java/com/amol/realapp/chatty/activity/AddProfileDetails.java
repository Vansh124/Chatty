package com.amol.realapp.chatty.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.amol.realapp.chatty.R;
import com.amol.realapp.chatty.databinding.ActivityProfileDetailsBinding;
import com.amol.realapp.chatty.model.userProfile;
import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import de.hdodenhof.circleimageview.CircleImageView;
import java.io.File;

public class AddProfileDetails extends AppCompatActivity {

  private CircleImageView userProfileImage;
  private EditText userName;
  private FirebaseDatabase database;
  private FirebaseAuth mAuth;
  private FirebaseStorage storage;
  private MaterialButton proceedUserDetails;
  private Uri selectedImage, imageUri;
  private StorageReference storageReference;
  private Intent resultIntent;
  private String txt_userName, txt_uid, txt_phoneNumber, txt_imageUrl, profilePhotos_fileString;
  private View dialog_Updating_Layout_View;
  private AlertDialog dialog;
  private userProfile profile;
  private LinearLayout activity_profile_detailsContainer;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_profile_details);
    init();
    initListener();
    
  }

  private void init() {
    database = FirebaseDatabase.getInstance();
    storage = FirebaseStorage.getInstance();
    mAuth = FirebaseAuth.getInstance();
    activity_profile_detailsContainer = findViewById(R.id.activity_profile_detailsContainer);
    userName = findViewById(R.id.userProfileUsername);
    userProfileImage = findViewById(R.id.profile_image);
    proceedUserDetails = findViewById(R.id.userProceedDetails);
  }

  private void initListener() {
    userProfileImage.setOnClickListener(
        new View.OnClickListener() {

          @Override
          public void onClick(View view) {
            checkStoragePermission();
          }
        });
    proceedUserDetails.setOnClickListener(
        new View.OnClickListener() {

          @Override
          public void onClick(View view) {
            txt_uid = mAuth.getUid();
            txt_userName = userName.getText().toString();
            txt_phoneNumber = mAuth.getCurrentUser().getPhoneNumber();

            dialog_Updating_Layout_View =
                LayoutInflater.from(AddProfileDetails.this)
                    .inflate(R.layout.dialog_updating_profile, activity_profile_detailsContainer, false);

            if (txt_userName.isEmpty()) {
              userName.setError("Please enter your username");
              return;
            }
            if (selectedImage != null) {
              submitDetails(dialog_Updating_Layout_View);
            } else {
              submitDetailsElse(dialog_Updating_Layout_View);
            }
          }
        });
  }

  private void checkStoragePermission() {
    if (ActivityCompat.checkSelfPermission(
            AddProfileDetails.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(
          AddProfileDetails.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 34);
    } else {
      profileImageRetriever.launch("image/*");
    }
  }

  private void submitDetails(View v) {
    showDialogUpdatingLayout(v);

    storageReference = storage.getReference().child("Profiles").child(mAuth.getUid());
    storageReference
        .putFile(selectedImage)
        .addOnCompleteListener(
            new OnCompleteListener<UploadTask.TaskSnapshot>() {

              @Override
              public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> p1) {
                if (p1.isSuccessful()) {
                  storageReference
                      .getDownloadUrl()
                      .addOnSuccessListener(
                          new OnSuccessListener<Uri>() {

                            @Override
                            public void onSuccess(@NonNull Uri p1) {

                              txt_imageUrl = p1.toString();

                              userProfile profile =
                                  new userProfile(
                                      txt_uid, txt_userName, txt_phoneNumber, txt_imageUrl);
                              database
                                  .getReference()
                                  .child("Users")
                                  .child(mAuth.getCurrentUser().getUid())
                                  .setValue(profile)
                                  .addOnCompleteListener(
                                      new OnCompleteListener<Void>() {

                                        @Override
                                        public void onComplete(@NonNull Task<Void> p1) {
                                          if (p1.isSuccessful()) {
                                            dialog.dismiss();
                                            profileDetailsToMainActivity_Intent();
                                          } else {
                                            dialog.dismiss();
                                            showToast(p1.getException().getMessage());
                                          }
                                        }
                                      });
                            }
                          });
                } else {
                  showToast(p1.getException().getMessage());
                }
              }
            });
  }

  private void submitDetailsElse(View v) {
    showDialogUpdatingLayout(v);

    profile = new userProfile(txt_uid, txt_userName, txt_phoneNumber, "no image");
    database
        .getReference()
        .child("Users")
        .child(mAuth.getCurrentUser().getUid())
        .setValue(profile)
        .addOnCompleteListener(
            new OnCompleteListener<Void>() {

              @Override
              public void onComplete(@NonNull Task<Void> p1) {
                if (p1.isSuccessful()) {
                  dialog.dismiss();
                  profileDetailsToMainActivity_Intent();

                } else {
                  dialog.dismiss();

                  showToast(p1.getException().getMessage());
                }
              }
            });
  }

  private void profileDetailsToMainActivity_Intent() {
    Intent intent = new Intent(AddProfileDetails.this, MainActivity.class);
    startActivity(intent);
    finish();
  }

  private void showDialogUpdatingLayout(View v) {
    MaterialAlertDialogBuilder mDialog = new MaterialAlertDialogBuilder(AddProfileDetails.this);
    mDialog.setView(v);
    mDialog.setCancelable(false);

    dialog = mDialog.show();
  }

  private void showToast(String text) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
  }

  private ActivityResultLauncher<String> profileImageRetriever =
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
            Environment.getExternalStorageDirectory() + "/" + "Chatty" + "/" + "ProfilePhotos",
            FirebaseAuth.getInstance().getCurrentUser().getUid());
    if (!f1.exists()) {
      f1.mkdirs();
    }
    profilePhotos_fileString =
        "Chatty"
            + File.separator
            + "ProfilePhotos"
            + File.separator
            + FirebaseAuth.getInstance().getCurrentUser().getUid();

    
    Intent dsPhotoEditorIntent = new Intent(AddProfileDetails.this, DsPhotoEditorActivity.class);
    dsPhotoEditorIntent.setData(uri);

    dsPhotoEditorIntent.putExtra(
        DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, profilePhotos_fileString);

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
                    "AddProfileDetails.java",
                    "Image Uri  "
                        + imageUri.toString()
                        + " File Name: "
                        + imageUri.getLastPathSegment());

                userProfileImage.setImageURI(imageUri);
                selectedImage = imageUri;
              } else {
                Log.d("AddProfileDetails.java", "Image Uri null");
              }
            }
          });
}
