package com.example.swornim.musicnap.customAdapterPackage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.renderscript.ScriptGroup;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swornim.musicnap.R;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.firebase.client.snapshot.LongNode;
import com.firebase.client.utilities.Base64;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class test extends AppCompatActivity {

    private File file1;
    private File file2;
    private File file3;
    private File musicStatus;
    private InputStream inputStream;
    private FileInputStream fileInputStream;
    private FileOutputStream fileout;
    private MediaMetadataRetriever mediaMetadataRetriever;
    private MediaExtractor mediaExtractor;
    private long durationInt;
    private long oneSecondBytes;
    private String durationString;
    private int bitrate;
    private int startTime;
    private String startTimeForClipper;//time
    private String endTimeForClipper;//time
    private int startBytes;//time
    private int endBytes;//time
    private long fileSize;
    private SeekBar seekBar;
    private TextView showTimeSeek;
    private TextView uploadingStatusSong;
    private MediaPlayer mediaPlayer;
    private Handler mHandler=new Handler();
    private Handler updateProgressBarHandler=new Handler();
    private EditText startTimeEditText;
    private EditText endTimeEditText;
    private Button mButton;
    private Button playClipped;
    private Button uploadSONG;
    private Button playOriginalSong;
    private Button Done;
    private int i=0;
    private ProgressBar progressbar;
    private boolean onpostFinished=true;//first time
    private StorageReference storageReference;
    private StorageReference musicStatusStorageRef;
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private ImageView backSeek;
    private ImageView forwardSeek;
    private String path;
    private String musicName;
    private HeadsetBroadCastReceiver headsetBroadCastReceiver;
    private DatabaseReference mDatabaseReference=FirebaseDatabase.getInstance().getReference("users/musicnap");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Intent getIntent=getIntent();
        path=getIntent.getStringExtra("path");
        musicName=getIntent.getStringExtra("musicName");

        headsetBroadCastReceiver=new HeadsetBroadCastReceiver();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("android.intent.action.HEADSET_PLUG");
        registerReceiver(headsetBroadCastReceiver,intentFilter);//dyanamic,broadcast receiver gets only called when registered

        progressbar=(ProgressBar) findViewById(R.id.circularSpinner);
        progressbar.setVisibility(View.GONE);


        uploadingStatusSong=(TextView) findViewById(R.id.uploadingStatusSong);
        uploadingStatusSong.setText(musicName);
        backSeek=(ImageView) findViewById(R.id.backSeek);
        forwardSeek=(ImageView) findViewById(R.id.forwardSeek);

        startTimeEditText=(EditText) findViewById(R.id.startTime);
        endTimeEditText=(EditText) findViewById(R.id.endTime);
//        mButton=(Button) findViewById(R.id.clipper);
        playClipped=(Button) findViewById(R.id.playClipped);
        playOriginalSong=(Button) findViewById(R.id.playOriginalSong);
        uploadSONG=(Button) findViewById(R.id.uploadSONG);
        musicStatusStorageRef = firebaseStorage.getReferenceFromUrl("gs://fir-cloudmessage-ac7af.appspot.com").child("Users/musicTest.mp3");

        file1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MusicNap/");//PRIMARY I.E INTERNAL STORAGE
        file2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MusicNap/gangs.mp3");
        musicStatus = new File(path);
        file2=musicStatus;//change this code to make default put comments
        Log.i("mytag",path);
        if(!(file1.exists()))
        file1.mkdirs();

        file3 = new File(file1, "www.mp3");
        try {
            inputStream = new FileInputStream(musicStatus);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
//            inputStream=getResources().openRawResource(R.raw.ganesh_chaturthi_jai_dev_jai_dev_mp3_49049);
        mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(musicStatus.getAbsolutePath());

        bitrate = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE));
        bitrate/=(1000);//bits  for second for buffer calculations
        Log.i("mytag","Actual file size "+musicStatus.length()+"Bytes");
        durationString = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        Log.i("mytag","Actual duration "+durationString);

        //calculates bytes per seconds
        durationInt = Integer.parseInt(durationString)/1000;//seconds
        fileSize=musicStatus.length();//bytes
        oneSecondBytes=fileSize/durationInt;

        Log.i("mytag", "Ok all  done");

        seekBar=(SeekBar) findViewById(R.id.seekbarId);
        seekBar.setClickable(true);

        showTimeSeek=(TextView) findViewById(R.id.showTimeSeek);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mediaPlayer.seekTo(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        uploadSONG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFileNow();
            }
        });

//
//        mButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
////                if (startTimeForClipper != null && endTimeForClipper != null) {
////
////                    try {
////                        oneSecondEquivalentBytes(oneSecondBytes);
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////
////                    try {
////                       new PerformClippingProcess().execute();
////                    } catch (Exception e) {
////                        e.printStackTrace();
////                    }
////                }else
////                    Toast.makeText(getApplicationContext(),"Start/end time has not been initialized",Toast.LENGTH_LONG).show();
//            }
//        });

        playClipped.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(),file3.getAbsolutePath(),Toast.LENGTH_LONG).show();

                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(file3.getAbsolutePath());
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        seekBar.setMax(mediaPlayer.getDuration());
                        mHandler.post(updateSongsTime);//call this runnable every 100 ms

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if(mediaPlayer.isPlaying()){
                        mediaPlayer.release();
                        mediaPlayer=new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(file3.getAbsolutePath());
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                            seekBar.setMax(mediaPlayer.getDuration());
                            mHandler.post(updateSongsTime);//call this runnable every 100 ms

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if(!mediaPlayer.isPlaying()){
                    mediaPlayer=new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(file3.getAbsolutePath());
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        seekBar.setMax(mediaPlayer.getDuration());
                        mHandler.post(updateSongsTime);//call this runnable every 100 ms

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        playOriginalSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mediaPlayer == null) {
                        mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(musicStatus.getAbsolutePath());
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                            seekBar.setMax(mediaPlayer.getDuration());
                            mHandler.post(updateSongsTime);//call this runnable every 100 ms

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else if(mediaPlayer.isPlaying()){
                        mediaPlayer.reset();
                        mediaPlayer=new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(musicStatus.getAbsolutePath());
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                            seekBar.setMax(mediaPlayer.getDuration());
                            mHandler.post(updateSongsTime);//call this runnable every 100 ms

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else if(!mediaPlayer.isPlaying()){

                    mediaPlayer=new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(musicStatus.getAbsolutePath());
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        seekBar.setMax(mediaPlayer.getDuration());
                        mHandler.post(updateSongsTime);//call this runnable every 100 ms

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                   }
                }
        });


        backSeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.seekTo(startTime-5000);//current mediaplayer position -  5 seconds
                startTime=startTime-5;

            }
        });

        forwardSeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.seekTo(startTime+5000);//current mediaplayer position -  5 seconds
                startTime=startTime+5;
            }
        });

        SocialDetails socialDetails=new SocialDetails();

        socialDetails.setMusicStatus("Life can be hard sometimes just because someone is not taking to you for a long perio of time");
        new FirebaseUserModel().new TestFirebase(getApplicationContext(),socialDetails).execute();

        mDatabaseReference.child("social/").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("mytag",dataSnapshot.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

    private Runnable updateSongsTime=new Runnable() {
        @Override
        public void run() {
            startTime=mediaPlayer.getCurrentPosition();
            int totalTimeSeconds=startTime/1000;
          //  Log.i("mytag","totalSeconds "+String.valueOf(totalTimeSeconds));
            seekBar.setSecondaryProgress(startTime);

            //time format
//            showTimeSeek.setText(
//                    String.format("%d min : %d sec",
//                            TimeUnit.MILLISECONDS.toMinutes((long) startTime),
//                            TimeUnit.MILLISECONDS.toSeconds((long) startTime)-
//                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)startTime)))
//            );

            showTimeSeek.setText(totalTimeSeconds+" seconds");
            mHandler.post(updateSongsTime);

            if(!(mediaPlayer.isPlaying()))
                mHandler.removeCallbacks(updateSongsTime);

        }
    };

    private Runnable updateProgressBar=new Runnable() {
        @Override
        public void run() {

            if(!(onpostFinished))
                progressbar.setVisibility(View.VISIBLE);
            else {
                progressbar.setVisibility(View.GONE);
                updateProgressBarHandler.removeCallbacks(updateProgressBar);
            }

            updateProgressBarHandler.post(updateProgressBar);

        }
    };


    //sizetoread is for reading the seek time bytes of data
//    public void saveClippedAudio() throws IOException {
//
//        int bufferSize=1024;// or make 512
//        byte[] buffer = new byte[bufferSize];
//        int eachByteData = 0;
//        ByteArrayOutputStream byteout = new ByteArrayOutputStream();
//       //eachbytedata gets the size of buffer size in one loop
//
//        int addEachData=0;
//
//        //todo Note that inputstream takes 1024(buffersize) audio size and puts on eachByteData in one loop
//
//
//
//            while ((eachByteData = inputStream.read(buffer, 0, buffer.length)) != -1) { //buffer.length and true size is the end poin
//
//                addEachData = addEachData + eachByteData;
//                Log.i("mytag", " eachByteData : " + String.valueOf(eachByteData));//it returns bytes data not in  bits
//
//                //prefer the paper for how to calculate the bytes data from the seek audio time
//
//                //Todo user beigin data in 960000
//                if (addEachData >= startBytes) { //this starts to read from the begin position of the songs byte
//                    if (byteout.size() <= endBytes) { //this reads till the end position of the songs byte, also checking the range to write
//                        byteout.write(buffer, 0, eachByteData);
//                        Log.i("mytag", " size written is " + String.valueOf(byteout.size()));//it returns bytes data not in  bits
//                    }
//
//                }
//
//                //todo user end data in 1040000
//                if (addEachData >= endBytes)//this reads till the end position of the songs byte
//                    break;
//
//            }
//            byte[] music = byteout.toByteArray();
//            fileout = new FileOutputStream(file3);
//            fileout.write(music);
//            fileout.close();
//
//    }

  public void oneSecondEquivalentBytes(long oneSecondBytes) throws IOException {

       startBytes=(int)oneSecondBytes*Integer.parseInt(startTimeForClipper);
       endBytes=(int)oneSecondBytes*Integer.parseInt(endTimeForClipper);

    }

    public class PerformClippingProcess extends AsyncTask<Void,Integer,String>{
        @Override
        protected String doInBackground(Void... voids) {

            int bufferSize=1024;// or make 512
            byte[] buffer = new byte[bufferSize];
            int eachByteData = 0;
            ByteArrayOutputStream byteout = new ByteArrayOutputStream();
            //eachbytedata gets the size of buffer size in one loop

            int addEachData=0;

            //todo Note that inputstream takes 1024(buffersize) audio size and puts on eachByteData in one loop


            try {
                while ((eachByteData = inputStream.read(buffer, 0, buffer.length)) != -1) { //buffer.length and true size is the end poin

                    addEachData = addEachData + eachByteData;
                    Log.i("mytag", " eachByteData : " + String.valueOf(eachByteData));//it returns bytes data not in  bits

                    //prefer the paper for how to calculate the bytes data from the seek audio time

                    //Todo user beigin data in 960000
                    if (addEachData >= startBytes) { //this starts to read from the begin position of the songs byte
                        if (byteout.size() <= endBytes) { //this reads till the end position of the songs byte, also checking the range to write
                            byteout.write(buffer, 0, eachByteData);
                            Log.i("mytag", " size written is " + String.valueOf(byteout.size()));//it returns bytes data not in  bits
                        }

                    }

                    //todo user end data in 1040000
                    if (addEachData >= endBytes)//this reads till the end position of the songs byte
                        break;

                }

                byte[] music = byteout.toByteArray();

                fileout = new FileOutputStream(file3);
                fileout.write(music);
                fileout.close();
                //send to the server and render the song


            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            //this method is called before the backgroudn thread runs only once
            progressbar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressbar.setProgress(values[0]);

        }

        @Override
        protected void onPostExecute(String data) {
            Log.i("mytag","clipping finished");
//            progressbar.setVisibility(View.GONE);

//            onpostFinished=true;
           }
    }

    public void uploadFileNow() {

        //get the start and endtime

        if(startTimeEditText.getText()!=null && endTimeEditText.getText()!=null) {

            startTimeForClipper = startTimeEditText.getText().toString();
            endTimeForClipper = endTimeEditText.getText().toString();
            startTimeEditText.setText(null);
            endTimeEditText.setText(null);
        }else
            Toast.makeText(getApplicationContext(),"Enter the data in seconds",Toast.LENGTH_LONG).show();


        //clip before uploading the songs status on the server
        if (startTimeForClipper != null && endTimeForClipper != null) {

            try {
                oneSecondEquivalentBytes(oneSecondBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                new PerformClippingProcess().execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else
            Toast.makeText(getApplicationContext(),"Start/end time has not been initialized",Toast.LENGTH_LONG).show();
        //upload the song to the web

        Uri fileTobeuploaded = Uri.fromFile(file3);
        UploadTask uploadTask = musicStatusStorageRef.putFile(fileTobeuploaded);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Errror: " + e.toString(), Toast.LENGTH_LONG).show();

            }
        });

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @SuppressWarnings("VisibleForTests")
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //tasksnapshot contains the information about the file
                //calll the database and store the key and value pair of the uploaded data

                // messageObject.setUploadingSongName(.....); this has been intialized from caller class
                UserDatabaseInformation messageObject=new UserDatabaseInformation();
                messageObject.setUploadingSongName(musicName);
                messageObject.setUploadingFilePath(taskSnapshot.getDownloadUrl().toString());
                messageObject.setUploaderUserName("Swornim Bikram Shah");
                progressbar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),"Successfully Uploaded",Toast.LENGTH_LONG).show();

                new FirebaseUserModel().new SaveFileInforDatabase(getApplicationContext(), messageObject).execute();


            }
        });

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @SuppressWarnings("VisibleForTests")
            @Override
            public void onProgress(final UploadTask.TaskSnapshot taskSnapshot) {

                double progress=(100.0*taskSnapshot.getBytesTransferred())/(taskSnapshot.getTotalByteCount());
                progress=(int)progress;
                progressbar.setVisibility(View.VISIBLE);
                progressbar.setProgress((int)progress);

            }



        });

    }

    @Override
    protected void onStop() {
        super.onStop();

//        unregisterReceiver(headsetBroadCastReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(headsetBroadCastReceiver);
    }


}

