package com.example.swornim.musicnap.customAdapterPackage;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swornim.musicnap.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.File;

/**
 * Created by Swornim on 5/19/2017.
 */

public class UploadFileFirebase extends AppCompatActivity {

    private Context context;
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    private UserDatabaseInformation messageObject;
    private TextView timerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.uploadfilefirebase);
        timerId=(TextView) findViewById(R.id.timerId);

        if (getIntent().getSerializableExtra("messageObject")!= null) {
            messageObject = (UserDatabaseInformation) getIntent().getSerializableExtra("messageObject");
        }
        storageReference = firebaseStorage.getReferenceFromUrl("gs://fir-cloudmessage-ac7af.appspot.com").child("Users/" + messageObject.getUploadingSongName());
        uploadFileNow();

    }

    public void uploadFileNow() {
        Uri fileTobeuploaded = Uri.fromFile(new File(messageObject.getUploadingFilePath()));
        UploadTask uploadTask = storageReference.putFile(fileTobeuploaded);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Errror: " + e.toString(), Toast.LENGTH_LONG).show();

            }
        });

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @SuppressWarnings("VisibleForTests")
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //tasksnapshot contains the information about the file
                //calll the database and store the key and value pair of the uploaded data

               // messageObject.setUploadingSongName(.....); this has been intialized from caller class
                messageObject.setUploadingFilePath(taskSnapshot.getDownloadUrl().toString());
                messageObject.setUploaderUserName(new CustomSharedPref(getApplicationContext()).getSharedPref("userName"));

                new FirebaseUserModel().new SaveFileInforDatabase(getApplicationContext(), messageObject).execute();



            }
        });

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @SuppressWarnings("VisibleForTests")
            @Override
            public void onProgress(final UploadTask.TaskSnapshot taskSnapshot) {

                double progress=(100.0*taskSnapshot.getBytesTransferred())/(taskSnapshot.getTotalByteCount());
                progress=(int)progress;
                timerId.setText(String.valueOf(progress)+ " %");
            }
        });

    }
}


