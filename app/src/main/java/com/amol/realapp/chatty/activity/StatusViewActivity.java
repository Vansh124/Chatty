package com.amol.realapp.chatty.activity;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import com.google.firebase.database.FirebaseDatabase;
import com.amol.realapp.chatty.R;
import android.content.Intent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.amol.realapp.chatty.model.Status;
import com.amol.realapp.chatty.model.userStatus;
import java.util.ArrayList;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Callback;
import android.view.View;
import android.widget.Toast;
import com.amol.realapp.chatty.StoriesView;
import android.view.MotionEvent;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import android.graphics.drawable.Drawable;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import android.view.Window;
import android.view.WindowManager;

public class StatusViewActivity extends AppCompatActivity implements StoriesView.StoriesListener {







    StoriesView storiesView;
    ImageView storiesImage,storyPrev,storyNext,storyHold;
    private ArrayList<Status> mStatusList=new ArrayList<>();
    Status uStatus;
    boolean isSpeakButtonPressed=false;

    int counter=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        init();
        initListener();

    }

    private void init() {
        
        storiesView = findViewById(R.id.stories);
        storiesImage = findViewById(R.id.stories_image);
        storyNext = findViewById(R.id.storyNext);
        storyPrev = findViewById(R.id.storyPrev);
        storyHold = findViewById(R.id.hold);

    }


    private void initListener() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        FirebaseDatabase.getInstance().getReference().child("Stories")
         .addValueEventListener(new ValueEventListener(){

                @Override
                public void onDataChange(DataSnapshot p1) {
                  mStatusList.clear();
                  for(DataSnapshot d: p1.getChildren()){
                      for(DataSnapshot md:d.child("statuses").getChildren()){

                          String imageUrl=md.child("imageUrl").getValue(String.class);
                          long timeStamp=md.child("timeStamp").getValue(long.class);
                          Status status=md.getValue(Status.class);  
                          status.setImageUrl(imageUrl);
                          status.setTimeStamp(timeStamp);

                          mStatusList.add(status);
                      }
                      storiesView.setStoriesCount(mStatusList.size()); 
                      storiesView.setStoryDuration(2000L);
                      storiesView.setStoriesListener(StatusViewActivity.this);

                      storiesView.startStories();
                      storiesView.pause();
                      Glide.with(StatusViewActivity.this).load(mStatusList.get(counter).getImageUrl()).listener(new RequestListener<Drawable>(){

                              @Override
                              public boolean onLoadFailed(GlideException p1, Object p2, Target<Drawable> p3, boolean p4) {
                                  return false;
                              }

                              @Override
                              public boolean onResourceReady(Drawable p1, Object p2, Target<Drawable> p3, DataSource p4, boolean p5) {
                                  storiesView.resume();

                                  return false;
                              }





                          }).thumbnail( 0.05f).into(storiesImage);
                      holdListener(storyHold);
                      skipStory(storyNext);
                      reverseStory(storyPrev);
                  }
                }

                @Override
                public void onCancelled(DatabaseError p1) {
                }

            
        });
        

    }





    @Override
    protected void onDestroy() {
        storiesView.destroy();
        super.onDestroy();
    }



    @Override
    public void onNext() {
        storiesView.pause();
        counter++;
        Toast.makeText(this, String.valueOf(counter), Toast.LENGTH_SHORT).show();
        Glide.with(StatusViewActivity.this).load(mStatusList.get(counter).getImageUrl()).listener(new RequestListener<Drawable>(){

                @Override
                public boolean onLoadFailed(GlideException p1, Object p2, Target<Drawable> p3, boolean p4) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable p1, Object p2, Target<Drawable> p3, DataSource p4, boolean p5) {
                    storiesView.resume();

                    return false;
                }





            }).thumbnail(0.05f).into(storiesImage);
    }

    @Override
    public void onPrev() {
        if(counter - 1 >=0){
            counter--;
            Toast.makeText(this, String.valueOf(counter), Toast.LENGTH_SHORT).show();
            
            Glide.with(this).load(mStatusList.get(counter).getImageUrl()).thumbnail(0.05f).into(storiesImage);   
            
            }
    }

    @Override
    public void onComplete() {
        finish();
    }

    @Override
    protected void onPause() {
        Glide.with(this).load(mStatusList.get(counter).getImageUrl()).thumbnail(0.05f).into(storiesImage);   
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    


    public void holdListener(View v) {
        v.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View view) {
                    isSpeakButtonPressed = true;
                    
                    storiesView.pause();
                    return true;
                }
            });
        v.setOnTouchListener(new View.OnTouchListener(){

                @Override
                public boolean onTouch(View p1, MotionEvent p2) {
                    p1.onTouchEvent(p2);
                    int action = p2.getAction();
                    if (action == MotionEvent.ACTION_DOWN) {
                        //click
                    } else if (action == MotionEvent.ACTION_UP) {
                        //up
                        storiesView.resume();
                        if (isSpeakButtonPressed) {
                            isSpeakButtonPressed = false; 
                        }
                    } else if (action == MotionEvent.ACTION_MOVE) {
                        //move
                    }
                    return true;

                }



            });

    }
    public void skipStory(View v) {
        v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    storiesView.skip();
                }
            });
    }
    public void reverseStory(View v) {
        v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    storiesView.reverse();
                }
            });
    }
}
