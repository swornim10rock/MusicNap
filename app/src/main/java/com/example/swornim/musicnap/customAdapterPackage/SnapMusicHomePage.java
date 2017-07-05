package com.example.swornim.musicnap.customAdapterPackage;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.hardware.input.InputManager;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.renderscript.ScriptGroup;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.text.BoringLayout;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swornim.musicnap.MainActivity;
import com.example.swornim.musicnap.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class SnapMusicHomePage extends AppCompatActivity {

    private ImageView sendImageView;
    private ImageView storyImageIcon;
    private EditText actualMessageView;
    private ListView customessageListView;
    private ArrayAdapter<UserDatabaseInformation> adapter;
    private Map<String, Object> userInstantMessage = new HashMap<String, Object>();
    private List<UserDatabaseInformation> sourceBucket = new ArrayList<>();
    private DatabaseReference mdatabaseReference;
    private int random;
    private UserDatabaseInformation messageDetails;
    private UserDatabaseInformation callonce;
    private static MediaPlayer mediaPlayer ;
    private HeadsetBroadCastReceiver headsetBroadCastReceiver;
    private String MEDIAPLAYER_PLAYING_INTENT="nope";
    private Intent mediaPlayerIntent=new Intent("com.example.swornim.musicnap.MEDIAPLAYER_PLAYING_INTENT");//intent name
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.custommessagelist);

        if(getIntent().getSerializableExtra("messageDetails")!=null && getIntent().getSerializableExtra("callOnce")!=null){
            messageDetails=(UserDatabaseInformation) getIntent().getSerializableExtra("messageDetails");
            callonce=(UserDatabaseInformation) getIntent().getSerializableExtra("callOnce");
            if(callonce.getStreamNow().equals("yes")){
                new StreamSongs(messageDetails).execute();
            }
        }



        mdatabaseReference=FirebaseDatabase.getInstance().getReference("users/musicnap/chats/");

        mdatabaseReference.limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getValue()!=null) {
                    UserDatabaseInformation messageObject = dataSnapshot.getValue(UserDatabaseInformation.class);

                    sourceBucket.add(messageObject);
                    adapter.notifyDataSetChanged();
                    customessageListView.setSelection(adapter.getCount() - 1);
                    try {
                        filterMessages(messageObject);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

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


        final InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(getApplication().INPUT_METHOD_SERVICE);

        actualMessageView = (EditText) findViewById(R.id.actualMessageView);
        actualMessageView.setInputType(InputType.TYPE_NULL);
        customessageListView = (ListView) findViewById(R.id.customMessageListView);
        sendImageView = (ImageView) findViewById(R.id.sendImageView);
        storyImageIcon = (ImageView) findViewById(R.id.storyImageIcon);

        adapter = new customAdapterForChatInterface(getApplicationContext(), sourceBucket);
        customessageListView.setAdapter(adapter);


        actualMessageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualMessageView.setInputType(InputType.TYPE_CLASS_TEXT);
                actualMessageView.requestFocus();
                inputMethodManager.showSoftInput(actualMessageView, InputMethodManager.RESULT_SHOWN);
                if(adapter!=null)
                    adapter.clear();
            }
        });

        sendImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otherStuffs();
            }

        });

    }

    public void filterMessages(UserDatabaseInformation messageObject) throws IOException {

                //play the song that mesasgeobject has

        if(messageObject.getMusicnapRequest()!=null && messageObject.getUploadingFilePath()!=null)
            new StreamSongs(messageObject).execute();
    }


    public void otherStuffs() {
        String actualMessageTobeSent = actualMessageView.getText().toString();

        if (!actualMessageTobeSent.isEmpty()) { //dont send blank message

            UserDatabaseInformation messageObject = new UserDatabaseInformation();
            messageObject.setCurrentMessageTobeSent(actualMessageTobeSent);

            new FirebaseUserModel().new InstantMessaging(getApplicationContext(),messageObject).execute();
        }
        actualMessageView.setText(null);
    }


    public int randomNumber(){ return new Random().nextInt(1000)+2; }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(SnapMusicHomePage.this, MainActivity.class));
    }

    public void showAlertDialog(final String title){

        AlertDialog.Builder popBox = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater=LayoutInflater.from(getApplicationContext());
        final View view=layoutInflater.inflate(R.layout.alertedit,null);
        popBox.setTitle(title);
        popBox.setCancelable(false);

            popBox.setView(view);
            popBox.setTitle("Enter the userName");

            popBox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditText editText=(EditText) view.findViewById(R.id.alertEditText);
                    if(editText.getText().toString().equals(""))
                        showAlertDialog(title);// call again if nothing is entered
                    else{
                        new CustomSharedPreferance(getApplicationContext()).setSharedPref("userName",editText.getText().toString());
                        dialog.dismiss();
                    }

                }
            });


    }


    public void notificationBar(String message,DataSnapshot dataSnapshot){

        Intent intent=new Intent(getApplicationContext(),test.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationManager mNotificationManager=(NotificationManager) getApplicationContext().getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        Notification mNotification=new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("New Moments")
                .setContentIntent(pendingIntent)
                .setContentText(message)
                .build();

        mNotification.flags=Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(100,mNotification);

        new CustomSharedPreferance(getApplicationContext()).setSharedPref("message","none");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.actionbaricons,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.addFreinds) {
            startActivity(new Intent(SnapMusicHomePage.this, friendlist.class));
        }

        if (item.getItemId() == R.id.storyImageIcon) {
            MinorDetails minorDetails = new MinorDetails();
            minorDetails.setUserStoryMessage("This is the user message that is going to be set by the user and playing background song.....");

        Intent intent = new Intent(SnapMusicHomePage.this, storytest.class);
        intent.putExtra("minorDetails", minorDetails);
            startActivity(intent);

    }
        return super.onOptionsItemSelected(item);
    }


    public class StreamSongs extends AsyncTask<Void,Void,String>{

        private UserDatabaseInformation messageObject;

        public StreamSongs(UserDatabaseInformation messageObject ) {
            this.messageObject=messageObject;
        }

        @Override
        protected String doInBackground(Void... voids) {
            if((mediaPlayer==null )) {
                //for the first time instanc creation
                mediaPlayer = new MediaPlayer();
                mediaPlayer.reset();
                mediaPlayer.stop();

                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mediaPlayer.setDataSource(messageObject.getUploadingFilePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();

                        if(mediaPlayer.isPlaying()) {
                            MEDIAPLAYER_PLAYING_INTENT = "yup";
                            mediaPlayerIntent.putExtra("MEDIAPLAYER_PLAYING_INTENT",MEDIAPLAYER_PLAYING_INTENT);
                            sendBroadcast(mediaPlayerIntent);//this gets caight by the receiver and in that receiver class you can filter the intents


                        }

                    }
                });
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        if(!(mediaPlayer.isPlaying())) {
                            MEDIAPLAYER_PLAYING_INTENT = "nope";
                            mediaPlayerIntent.putExtra("MEDIAPLAYER_PLAYING_INTENT",MEDIAPLAYER_PLAYING_INTENT);
                            sendBroadcast(mediaPlayerIntent);
                        }

                    }
                });
                mediaPlayer.prepareAsync();

            }else{
                //resets previous playing mediaplayer
                mediaPlayer.reset();
                mediaPlayer.stop();

                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mediaPlayer.setDataSource(messageObject.getUploadingFilePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        if(!(mediaPlayer.isPlaying())) {
                            MEDIAPLAYER_PLAYING_INTENT = "nope";
                            mediaPlayerIntent.putExtra("MEDIAPLAYER_PLAYING_INTENT",MEDIAPLAYER_PLAYING_INTENT);
                            sendBroadcast(mediaPlayerIntent);


                        }
                    }
                });
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                        if(mediaPlayer.isPlaying()) {
                            MEDIAPLAYER_PLAYING_INTENT = "yup";
                            mediaPlayerIntent.putExtra("MEDIAPLAYER_PLAYING_INTENT",MEDIAPLAYER_PLAYING_INTENT);
                            sendBroadcast(mediaPlayerIntent);
                        }
                    }
                });
                mediaPlayer.prepareAsync();
            }

            return "Playing";
        }

        @Override
        protected void onPostExecute(String s) {
            final TextView firebaseSongDisplay=(TextView) findViewById(R.id.firebaseSongDisplay);
            final TextView firebaseUploaderName=(TextView) findViewById(R.id.firebaseUploaderName);
            firebaseSongDisplay.setText(messageObject.getUploadingSongName());
            firebaseUploaderName.setText("Uploaded by: "+messageObject.getUploaderUserName());
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        new CustomSharedPref(getApplicationContext()).setSharedPref("callFilterOnce","none");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(headsetBroadCastReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        headsetBroadCastReceiver=new HeadsetBroadCastReceiver();
        IntentFilter headsetIntentFilter=new IntentFilter();
        headsetIntentFilter.addAction("android.intent.action.HEADSET_PLUG");

        IntentFilter mediaPlayerIntentFilter=new IntentFilter();
        mediaPlayerIntentFilter.addAction("com.example.swornim.musicnap.MEDIAPLAYER_PLAYING_INTENT");//dynamic intent filter, this name is the intent name which is broadcasted by sendbroadcaster

        registerReceiver(headsetBroadCastReceiver,headsetIntentFilter);//dyanamic,broadcast receiver gets only called when registered
        registerReceiver(headsetBroadCastReceiver,mediaPlayerIntentFilter);//dyanamic,broadcast receiver gets only called when registered
    }
}
