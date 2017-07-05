package com.example.swornim.musicnap.customAdapterPackage;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Swornim on 1/26/2017.
 */
public class FirebaseUserModel {

    DatabaseReference  mdatabaseReference= FirebaseDatabase.getInstance().getReference("users/");
    //songs details variables
    private static String S0NGNAME;

    //instant status variables
    private static String ONLINE_OFFLINE="ONLINE_OFFLINE";
    private static String LISTENING="LISTENING";
    private static String EAR_PLUGGED_OR_NOT="EAR_PLUGGED_OR_NOT";
    private static String WHO_IS_ONLINE="WHO_IS_ONLINE";


    //general variables
    private Context context;
    private static String talkWithHim;
    private static String mMessage;//actual message



    public static String getWhoIsOnline() {
        return WHO_IS_ONLINE;
    }

    public static void setWhoIsOnline(String whoIsOnline) {
        WHO_IS_ONLINE = whoIsOnline;
    }

    public static String getS0NGNAME() {
        return S0NGNAME;
    }

    public static void setS0NGNAME(String s0NGNAME) {
        S0NGNAME = s0NGNAME;
    }

    public static String getOnlineOffline() {
        return ONLINE_OFFLINE;
    }

    public static void setOnlineOffline(String onlineOffline) {
        ONLINE_OFFLINE = onlineOffline;
    }

    public static String getLISTENING() {
        return LISTENING;
    }

    public static void setLISTENING(String LISTENING) {
        FirebaseUserModel.LISTENING = LISTENING;
    }

    public static String getEarPluggedOrNot() {
        return EAR_PLUGGED_OR_NOT;
    }

    public static void setEarPluggedOrNot(String earPluggedOrNot) {
        EAR_PLUGGED_OR_NOT = earPluggedOrNot;
    }

    public String getTalkWithHim() {
        return talkWithHim;
    }

    public void setTalkWithHim(String talkWithHim) {
        this.talkWithHim = talkWithHim;
    }

    public String getmMessage() {
        return mMessage;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }




    public class InstantMessaging extends AsyncTask<Void,Void,Void>{

        private Context context;
        private UserDatabaseInformation messageObject;


        public InstantMessaging(Context context,UserDatabaseInformation messageObject) {

            this.messageObject=messageObject;
            this.context=context;
        }

        @Override
        protected Void doInBackground(Void...params){

                mdatabaseReference.
                        child("musicnap/chats/")
                        .push()
                        .setValue(messageObject);


           return null;
        }

    }


 public class SaveFileInforDatabase extends AsyncTask<Void,Void,Void>{

        private Context context;
        private UserDatabaseInformation messageObject;


        public SaveFileInforDatabase(Context context,UserDatabaseInformation messageObject) {

            this.messageObject=messageObject;
            this.context=context;
        }

        @Override
        protected Void doInBackground(Void...params){

                mdatabaseReference.
                        child("musicnap/songlist/")
                        .push()
                        .setValue(messageObject);
           return null;
        }

    }

    public class RegisterUserSongs extends AsyncTask<Void,Void,Void>{

        private Context context;
        private UserDatabaseInformation messageObject;


        public RegisterUserSongs(Context context,UserDatabaseInformation messageObject) {

            this.messageObject=messageObject;
            this.context=context;
        }

        @Override
        protected Void doInBackground(Void...params){
            UserDatabaseInformation eachSong=new UserDatabaseInformation();
            List<songList> songsList=messageObject.getUploadingSongsList();
            for(int i=0;i<songsList.size();i++){
                Log.i("mytag",songsList.get(i).getSongName());

                eachSong.setUploadingSongName(songsList.get(i).getSongName());
                mdatabaseReference.
                        child("musicnap/mySongs/")
                        .push()
                        .setValue(eachSong);
            }



           return null;
        }

    }

    public class TestFirebase extends AsyncTask<Void,Void,Void>{

        private Context context;
        private SocialDetails messageObject;


        public TestFirebase(Context context,SocialDetails messageObject) {

            this.messageObject=messageObject;
            this.context=context;
        }

        @Override
        protected Void doInBackground(Void...params){

                mdatabaseReference.
                        child("musicnap/social/")
                        .setValue(messageObject);

           return null;
        }

    }




}






