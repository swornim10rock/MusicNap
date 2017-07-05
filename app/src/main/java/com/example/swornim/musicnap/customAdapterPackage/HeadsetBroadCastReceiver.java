package com.example.swornim.musicnap.customAdapterPackage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Swornim on 6/3/2017.
 */

public class HeadsetBroadCastReceiver extends BroadcastReceiver {
//this is the dynamic broadcast receiver which takes system intents and analyses those intents values
    //this class handles the headset and mediaplayer related operations


    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals("android.intent.action.HEADSET_PLUG")) {
            int state = intent.getIntExtra("state", -1);
            SocialDetails messageObject = new SocialDetails();

            switch (state) {
                case 0:
                    new CustomSharedPref(context).setSharedPref("earphonePlugged", "nope");//for displaying UI switch
                    //you can implement the database updates of yours node headset plugged?
                    messageObject.setHeadSetPlugged("nope");
                    new FirebaseUserModel().new TestFirebase(context, messageObject).execute();
                    break;
                case 1:
                    new CustomSharedPref(context).setSharedPref("earphonePlugged", "yup");
                    messageObject.setHeadSetPlugged("yup");
                    new FirebaseUserModel().new TestFirebase(context, messageObject).execute();
                    break;
                default:

            }

        }

        if(intent.getAction().equals("com.example.swornim.musicnap.MEDIAPLAYER_PLAYING_INTENT")){

            SocialDetails messageObject=new SocialDetails();
            messageObject.setMediaplayerPlaying(intent.getStringExtra("MEDIAPLAYER_PLAYING_INTENT"));
            new FirebaseUserModel().new TestFirebase(context,messageObject).execute();

        }


    }
}
