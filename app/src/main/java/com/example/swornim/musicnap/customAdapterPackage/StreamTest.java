package com.example.swornim.musicnap.customAdapterPackage;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.swornim.musicnap.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

public class StreamTest extends AppCompatActivity {

    private Button button;
    private MediaPlayer mediaPlayer;
    private FirebaseStorage storage= FirebaseStorage.getInstance();
    private StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_test);

        button=(Button) findViewById(R.id.SongplayingId);

//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(mediaPlayer.isPlaying())
//                    Toast.makeText(getApplicationContext(),"Yes both side are playing songs",Toast.LENGTH_LONG).show();
//            }
//        });
//
//
//
//      storageReference=storage.getReferenceFromUrl("gs://fir-cloudmessage-ac7af.appspot.com").child("Users/martin gariix animal.mp3");
//
//        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                Toast.makeText(getApplicationContext(),uri.toString(),Toast.LENGTH_LONG).show();
//            }
//        });

        //get the url of the media files and call the upload class





    }





    }


