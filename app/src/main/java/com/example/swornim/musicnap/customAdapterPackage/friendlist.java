package com.example.swornim.musicnap.customAdapterPackage;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Looper;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.print.PrintDocumentAdapter;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swornim.musicnap.MainActivity;
import com.example.swornim.musicnap.R;
import com.firebase.client.Firebase;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.sql.RowIdLifetime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.zip.Inflater;

public class friendlist extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private List<String> containerForAdapter=new ArrayList<>();
    private ListView friendchatList;
    private TextView snapCallUserName;
    private TextView snap_profile_timer;
    private String freindNumber;

    private Button btnmessageHimHer;
    private Button btnSnapMusic;
    private ImageView userProfilePic;
    private ListView firebasesongsListView;
    private ArrayAdapter<UserDatabaseInformation> firebaseAdapter;
    private List<UserDatabaseInformation> firebaseAdapterSource=new ArrayList<>();
    private DatabaseReference mDatabaseReferences;//for the retrieving the songs from firebase
    private String[] staticFriends={"9813847444","9813054341","9841001504","9860569432","9841339287","9860206938"};


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendlist);

        friendchatList =(ListView) findViewById(R.id.friendchatlist);
        snapCallUserName=(TextView) findViewById(R.id.snapCallUserName);

        adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,staticFriends);
        friendchatList.setAdapter(adapter);

        friendchatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                setContentView(R.layout.snap_profile_for_chat);
                 freindNumber=staticFriends[position];

                notifyAllOnclickListener();

            }
        });


    }

    public void notifyAllOnclickListener(){

        snap_profile_timer=(TextView) findViewById(R.id.snap_profile_timer);
        btnmessageHimHer=(Button) findViewById(R.id.btnmessageHimHer);
        btnSnapMusic=(Button) findViewById(R.id.btnSnapMusicProf);

        btnmessageHimHer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(friendlist.this,SnapMusicHomePage.class);

                UserDatabaseInformation messageObject=new UserDatabaseInformation();
                messageObject.setPhoneNumber(freindNumber);
                Log.i("mytag",freindNumber);
                intent.putExtra("objectForChat",messageObject);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });

        btnSnapMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new CustomSharedPref(getApplicationContext()).setSharedPref("whichFriendTotalk",freindNumber);
                new CustomSharedPref(getApplicationContext()).setSharedPref("whichFriendTotalkSnap",freindNumber);

                final String userSongFilePath= new CustomSharedPreferance(getApplicationContext()).getSharedPref("userSongFilePath","none");
                final ValueAnimator animator=new ValueAnimator();
                animator.setObjectValues(4,1);
                animator.setDuration(2000);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {

                        snap_profile_timer.setText(""+animator.getAnimatedValue());
                    }
                });

                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                        snap_profile_timer.setText("LETS'S MUSIC SNAP");


                        Thread thread=new Thread(){
                            @Override
                            public void run() {

                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }finally {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run(){
                                            //show the firebase songs list name

                                            setContentView(R.layout.firebasesongslist);
                                            getSupportActionBar().setTitle(" Snap Music Store");

                                            firebasesongsListView=(ListView) findViewById(R.id.firebasesongsListView);

                                            firebaseAdapter=new ShowFirebaseSongsList(getApplicationContext(),firebaseAdapterSource);
                                            firebasesongsListView.setAdapter(firebaseAdapter);
                                            if(firebaseAdapter==null)
                                                firebaseAdapter.clear();

                                            mDatabaseReferences= FirebaseDatabase.getInstance().getReference("users/");

                                            mDatabaseReferences.child("musicnap/songlist/").addChildEventListener(new ChildEventListener() {
                                                @Override
                                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                                    //to get the class object from eacb sub node or child just use in this way

                                                     UserDatabaseInformation messageObject=dataSnapshot.getValue(UserDatabaseInformation.class);

                                                     firebaseAdapterSource.add(messageObject);
                                                     firebaseAdapter.notifyDataSetChanged();

                                                }

                                                @Override
                                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                                }

                                                @Override
                                                public void onChildRemoved(DataSnapshot dataSnapshot) {

                                                }

                                                @Override
                                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });

                                        }
                                    });
                                }

                            }
                        };

                        thread.start();


                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

                animator.start();


            }
        });

    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.actionbaricons,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.addFreinds){
            Intent intent=new Intent(friendlist.this,MainActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }



    public class ShowFirebaseSongsList extends ArrayAdapter<UserDatabaseInformation>{

        private List<UserDatabaseInformation> firebaseAdapterSource=new ArrayList<>();

        public ShowFirebaseSongsList( Context context,List<UserDatabaseInformation> firebaseAdapterSource) {
            super(context, R.layout.firebasesongslisttemplate,firebaseAdapterSource);
            this.firebaseAdapterSource=firebaseAdapterSource;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View mView=convertView;
            if(mView==null){
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                mView = layoutInflater.inflate(R.layout.firebasesongslisttemplate, parent, false);
            }

            TextView firebasesongsName=(TextView) mView.findViewById(R.id.firebasesongslistName);
            firebasesongsName.setText(firebaseAdapterSource.get(position).getUploadingSongName());

            firebasesongsName.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    showAlertDialog(firebaseAdapterSource,position);

                    return false;
                }
            });

            return mView;
        }
    }

    public void showAlertDialog(List<UserDatabaseInformation> firebaseAdapterSource,int position){


        final UserDatabaseInformation messageObject=firebaseAdapterSource.get(position);

        AlertDialog.Builder popBox = new AlertDialog.Builder(this);
        popBox.setCancelable(false);
            popBox.setTitle("Do You Wanna Snap This Song");

            popBox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //play the song in your phone and send that song path to friends phone

                    messageObject.setMes("Snap Music ");
                    messageObject.setRecePhnN(new CustomSharedPref(getApplicationContext()).getSharedPref("whichFriendTotalkSnap"));
                    messageObject.setUploadingSongName(messageObject.getUploadingSongName());
                    messageObject.setMusicnapRequest("yes");
                    messageObject.setUploadingFilePath(messageObject.getUploadingFilePath());
//                    messageObject.setRecePhnN(freindNumber);
                    messageObject.setPhoneNumber(new CustomSharedPref(getApplicationContext()).getSharedPref("userPhoneNumber"));

                    Toast.makeText(getApplication(),messageObject.getPhoneNumber(),Toast.LENGTH_LONG).show();
                    new FirebaseUserModel().new InstantMessagingMusic(getApplicationContext(),messageObject).execute();

                    Intent intent=new Intent(friendlist.this,SnapMusicHomePage.class);

                    messageObject.setStreamNow("yes");
                    messageObject.setPhoneNumber(new CustomSharedPref(getApplicationContext()).getSharedPref("whichFriendTotalk"));
                    intent.putExtra("objectForMusic",messageObject);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Log.i("mytag","From music clicked"+messageObject.getPhoneNumber());
                    startActivity(intent);

                    dialog.dismiss();
                }
            });

        popBox.setNegativeButton("Nope", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                }
            });

        popBox.show();


    }


    @Override
    protected void onStop() {
        new CustomSharedPref(getApplicationContext()).setSharedPref("callFilterOnce","none");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
