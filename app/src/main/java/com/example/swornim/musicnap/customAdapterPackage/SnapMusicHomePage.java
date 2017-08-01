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
import android.provider.MediaStore;
import android.renderscript.ScriptGroup;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
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

import com.bumptech.glide.Glide;
import com.example.swornim.musicnap.MainActivity;
import com.example.swornim.musicnap.R;
import com.example.swornim.musicnap.RegisterActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

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
    private ImageView mediaplayerStop;
    private ImageView storyImageIcon;
    private EditText actualMessageView;
    private TextView photoStatus;
    private ListView customessageListView;
    private ArrayAdapter<UserDatabaseInformation> adapter;
    private Map<String, Object> userInstantMessage = new HashMap<String, Object>();
    private List<UserDatabaseInformation> sourceBucket = new ArrayList<>();
    private ImageView photoSelectId;
    private UserDatabaseInformation messageDetails;
    private UserDatabaseInformation callonce;
    private static MediaPlayer mediaPlayer ;
    private FirebaseStorage firebaseStorage;
    private DatabaseReference mdatabaseReference;
    private DatabaseReference mdatabaseReferencePhoto;
    private DatabaseReference mdatabaseReferencePlay;
    private DatabaseReference mdatabaseReferenceSeen;
    private StorageReference storageReference;
    private String whomToTalk;
    private String[] staticFriends={"9813847444","9813054341","9841001504","9860569432","981339287","9860206938"};
    private InputMethodManager inputMethodManager;

    //    private HeadsetBroadCastReceiver headsetBroadCastReceiver;
    private String messageCame="nope";
//    private String MEDIAPLAYER_PLAYING_INTENT="nope";
//    private Intent mediaPlayerIntent=new Intent("com.example.swornim.musicnap.MEDIAPLAYER_PLAYING_INTENT");//intent name
//    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.custommessagelist);

        inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(getApplication().INPUT_METHOD_SERVICE);

        Toast.makeText(getApplicationContext(),"oncreate",Toast.LENGTH_LONG).show();
        if(getIntent().getSerializableExtra("objectForMusic")!=null){
            messageDetails=(UserDatabaseInformation) getIntent().getSerializableExtra("objectForMusic");
            whomToTalk=messageDetails.getPhoneNumber();
            Log.i("mytag","getintent called"+messageDetails.getPhoneNumber());


            new StreamSongs(messageDetails).execute();

        }

        if(getIntent().getSerializableExtra("objectForChat")!=null){
            messageDetails=(UserDatabaseInformation) getIntent().getSerializableExtra("objectForChat");
            Log.i("mytag",messageDetails.getPhoneNumber());
            Log.i("mytag","getintent called"+messageDetails.getPhoneNumber());

            whomToTalk=messageDetails.getPhoneNumber();
        }

        String userName = new CustomSharedPref(getApplicationContext()).getSharedPref("userName");//primitive data type database
        if (userName.equals("none")) {
            startActivity(new Intent(SnapMusicHomePage.this, RegisterActivity.class));
        }



        String myNumber=new CustomSharedPref(getApplicationContext()).getSharedPref("userPhoneNumber");
        mdatabaseReference=FirebaseDatabase.getInstance().getReference("users/musicnap/"+myNumber+"/friends/"+whomToTalk);

        mdatabaseReference.child("/chats").limitToLast(3).addChildEventListener(new ChildEventListener() {
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

//                    messageCame="yup";//for seen logic

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

//--------------------------------------------------------------------------------------------------
        //to keep track whether both the songs are playing or not
        mdatabaseReferencePlay = FirebaseDatabase.
                getInstance().
                getReference("users/musicnap/");

        mdatabaseReferencePlay.child("social/").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.i("mytag","songs playing "+dataSnapshot.getValue());
                UserDatabaseInformation message=dataSnapshot.getValue(UserDatabaseInformation.class);//object structure {key=value

                TextView isplaying=(TextView) findViewById(R.id.playingId);

                //wherether the user is playing or not
                if(message.getPly()!=null){
                    if(message.getPly().equals("yup"))
                        isplaying.setText("Playing");
                    else
                        isplaying.setText("Not Playing");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//---------------------------Songs playing logic on both side-----------------------------------------------------------


        actualMessageView = (EditText) findViewById(R.id.actualMessageView);
        actualMessageView.setInputType(InputType.TYPE_NULL);
        customessageListView = (ListView) findViewById(R.id.customMessageListView);
        sendImageView = (ImageView) findViewById(R.id.sendImageView);
        mediaplayerStop = (ImageView) findViewById(R.id.mediaplayerStop);
        storyImageIcon = (ImageView) findViewById(R.id.storyImageIcon);
        photoSelectId = (ImageView) findViewById(R.id.photoSelectId);
        photoStatus=(TextView)findViewById(R.id.photoStatus);

        adapter = new customAdapterForChatInterface(getApplicationContext(), sourceBucket);
        customessageListView.setAdapter(adapter);


        actualMessageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);

                if(messageCame.equals("yup")){
//                    sendSeen();
//                    messageCame="nope";
                }

            }
        });

        sendImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }

        });
        photoSelectId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 0);
            }
        });

        mediaplayerStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if (mediaPlayer != null) {

                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }
                }
                if (mediaPlayer != null) {
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                    }

                }

            }
        });


    }

    public void filterMessages(UserDatabaseInformation messageObject) throws IOException {

                //play the song that mesasgeobject has
        if(messageObject.getMusicnapRequest()!=null) {
            if (messageObject.getMusicnapRequest().equals("yes")) {
                new StreamSongs(messageObject).execute();

            }
        }
    }


    public void sendMessage() {
        String actualMessageTobeSent = actualMessageView.getText().toString();

        if (!actualMessageTobeSent.isEmpty()) { //dont send blank message

            UserDatabaseInformation messageObject = new UserDatabaseInformation();
            messageObject.setMes(actualMessageTobeSent);
            messageObject.setRecePhnN(whomToTalk);
            Toast.makeText(getApplicationContext(),whomToTalk,Toast.LENGTH_LONG).show();
            messageObject.setPhoneNumber(new CustomSharedPref(getApplicationContext()).getSharedPref("userPhoneNumber"));
            messageObject.setUserName(new CustomSharedPref(getApplicationContext()).getSharedPref("userName"));

            new FirebaseUserModel().new InstantMessaging(getApplicationContext(),messageObject).execute();
        }
        UserDatabaseInformation myMessage=new UserDatabaseInformation();
        myMessage.setMes(actualMessageTobeSent);
        sourceBucket.add(myMessage);
        actualMessageView.setText(null);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);
        customessageListView.setSelection(adapter.getCount() - 1);


    }


    public int randomNumber(){ return new Random().nextInt(1000)+2; }




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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        MenuInflater menuInflater=getMenuInflater();
//        menuInflater.inflate(R.menu.actionbaricons,menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        if (item.getItemId() == R.id.addFreinds) {
//            startActivity(new Intent(SnapMusicHomePage.this, MainActivity.class));
//
//        }
//
//        if (item.getItemId() == R.id.storyImageIcon) {
//            MinorDetails minorDetails = new MinorDetails();
//            minorDetails.setUserStoryMessage("This is the user message that is going to be set by the user and playing background song.....");
//
//        Intent intent = new Intent(SnapMusicHomePage.this, storytest.class);
//        intent.putExtra("minorDetails", minorDetails);
//            startActivity(intent);
//
//    }
//        return super.onOptionsItemSelected(item);
//    }


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

                            UserDatabaseInformation messageObject=new UserDatabaseInformation();
                            messageObject.setPly("yup");

                            mdatabaseReferencePlay = FirebaseDatabase.
                                    getInstance().
                                    getReference("users/musicnap/");

                            mdatabaseReferencePlay
                                    .child("social")
                                    .setValue(messageObject);

//                            MEDIAPLAYER_PLAYING_INTENT = "yup";
//                            mediaPlayerIntent.putExtra("MEDIAPLAYER_PLAYING_INTENT",MEDIAPLAYER_PLAYING_INTENT);
//                            sendBroadcast(mediaPlayerIntent);//this gets caight by the receiver and in that receiver class you can filter the intents


                        }

                    }
                });

                //when the mediaplayer plays the entire data
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
//                        if(!(mediaPlayer.isPlaying())) {

                            UserDatabaseInformation messageObject=new UserDatabaseInformation();
                            messageObject.setPly("nope");

                            mdatabaseReferencePlay = FirebaseDatabase.
                                    getInstance().
                                    getReference("users/musicnap/");

                            mdatabaseReferencePlay
                                    .child("social")
                                    .setValue(messageObject);


//                            MEDIAPLAYER_PLAYING_INTENT = "nope";
//                            mediaPlayerIntent.putExtra("MEDIAPLAYER_PLAYING_INTENT",MEDIAPLAYER_PLAYING_INTENT);
//                            sendBroadcast(mediaPlayerIntent);
                        }

//                    }
                });
                mediaPlayer.prepareAsync();

            }else{
                //resets previous playing mediaplayer that when new song is requested
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
//                        if(!(mediaPlayer.isPlaying())) {
                            UserDatabaseInformation messageObject=new UserDatabaseInformation();
                            messageObject.setPly("nope");

                            mdatabaseReferencePlay = FirebaseDatabase.
                                    getInstance().
                                    getReference("users/musicnap/");

                            mdatabaseReferencePlay
                                    .child("social")
                                    .setValue(messageObject);

//                            MEDIAPLAYER_PLAYING_INTENT = "nope";
//                            mediaPlayerIntent.putExtra("MEDIAPLAYER_PLAYING_INTENT",MEDIAPLAYER_PLAYING_INTENT);
//                            sendBroadcast(mediaPlayerIntent);


                       // }
                    }
                });
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                        if(mediaPlayer.isPlaying()) {

                            UserDatabaseInformation messageObject=new UserDatabaseInformation();
                            messageObject.setPly("yup");

                            mdatabaseReferencePlay = FirebaseDatabase.
                                    getInstance().
                                    getReference("users/musicnap/");

                            mdatabaseReferencePlay
                                    .child("social")
                                    .setValue(messageObject);

//                            MEDIAPLAYER_PLAYING_INTENT = "yup";
//                            mediaPlayerIntent.putExtra("MEDIAPLAYER_PLAYING_INTENT",MEDIAPLAYER_PLAYING_INTENT);
//                            sendBroadcast(mediaPlayerIntent);
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
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mbroadcastReceiverPhoto);

//        unregisterReceiver(headsetBroadCastReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mbroadcastReceiverPhoto);

    }

    @Override
    protected void onStart() {

        super.onStart();
//        headsetBroadCastReceiver=new HeadsetBroadCastReceiver();
//        IntentFilter headsetIntentFilter=new IntentFilter();
//        headsetIntentFilter.addAction("android.intent.action.HEADSET_PLUG");
//
//        IntentFilter mediaPlayerIntentFilter=new IntentFilter();
//        mediaPlayerIntentFilter.addAction("com.example.swornim.musicnap.MEDIAPLAYER_PLAYING_INTENT");//dynamic intent filter, this name is the intent name which is broadcasted by sendbroadcaster
//
//        registerReceiver(headsetBroadCastReceiver,headsetIntentFilter);//dyanamic,broadcast receiver gets only called when registered
//        registerReceiver(headsetBroadCastReceiver,mediaPlayerIntentFilter);//dyanamic,broadcast receiver gets only called when registered
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mbroadcastReceiverPhoto,new IntentFilter("photozoom"));

    }

    private void sendSeen(){
//        UserDatabaseInformation messageObject = new UserDatabaseInformation();
//        messageObject.setRecePhnN(whomToTalk);
//        messageObject.setPhoneNumber(new CustomSharedPref(getApplicationContext()).getSharedPref("userPhoneNumber"));
//        messageObject.setSeenM("yup");
//        new FirebaseUserModel().new InstantSeen(getApplicationContext(),messageObject).execute();

    }


    //to get the result from the gallery when photo is clicked
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            Uri targetUri = data.getData();

            String[] projection = {MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATE_ADDED
            };
            Cursor cursor = managedQuery(targetUri, projection, null, null, null);
            if (cursor != null) {
                int column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String path = cursor.getString(column_index);
                String imageName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));

                Log.i("mytag", path);
                Log.i("mytag", imageName);
                firebaseStorage= FirebaseStorage.getInstance();
                storageReference = firebaseStorage.getReferenceFromUrl("gs://fir-cloudmessage-ac7af.appspot.com/").child(imageName);

                ImageView imageView = (ImageView) findViewById(R.id.photoSelectId);

                Glide.with(getApplicationContext()).load(targetUri)
                        .centerCrop()
                        .crossFade()
                        .into(imageView);

                UserDatabaseInformation photoMessageforme=new UserDatabaseInformation();
                photoMessageforme.setpM("yup");
                photoMessageforme.setpUrl(targetUri.toString());
                sourceBucket.add(photoMessageforme);
                adapter.notifyDataSetChanged();
                customessageListView.setSelection(adapter.getCount()- 1);



//            send this to the friends as message
                UploadTask uploadTask = storageReference.putFile(targetUri);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Errror: " + e.toString(), Toast.LENGTH_LONG).show();


                        photoStatus.setText("not sent");
                    }
                });

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //call firebase and sent the data as chat message with message included tag
                        mdatabaseReferencePhoto = FirebaseDatabase.
                                getInstance().
                                getReference("users/musicnap/"+whomToTalk+"/friends/"+new CustomSharedPref(getApplicationContext())
                                        .getSharedPref("userPhoneNumber")+"/chats/" );

                        Toast.makeText(getApplicationContext(),"photo called"+whomToTalk,Toast.LENGTH_LONG).show();
                        UserDatabaseInformation messageObject=new UserDatabaseInformation();
                        messageObject.setpM("yup");//status to change the custom view
                        messageObject.setpUrl(taskSnapshot.getDownloadUrl().toString());
                        mdatabaseReferencePhoto.push().setValue(messageObject);
                        photoStatus.setText("Delivered");

                    }
                });

                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onProgress(final UploadTask.TaskSnapshot taskSnapshot) {
                        photoStatus.setText("Sending...");
                    }
                });


            }
        }
    }


    private BroadcastReceiver mbroadcastReceiverPhoto=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            UserDatabaseInformation messageObject=(UserDatabaseInformation) intent.getSerializableExtra("photoMessageObject");
            FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            Fragment fragment = PhotoZoomFragment.newInstance(messageObject);

            fragmentTransaction.replace(R.id.customMessageLayoutId,fragment);
            fragmentTransaction.addToBackStack(fragmentTransaction.getClass().getName());//removes when back pressed

            fragmentTransaction.commit();

//            Toast.makeText(getApplicationContext(),messageObject.getpUrl(),Toast.LENGTH_LONG).show();
        }
    };





}
