package com.example.swornim.musicnap.customAdapterPackage;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.swornim.musicnap.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Swornim on 12/23/2016.
 */
public class customAdapterForChatInterface extends ArrayAdapter<UserDatabaseInformation> {


    private List<UserDatabaseInformation> sourceBucket=new ArrayList<>();//its an array list container
    private Context context;
    private String which="normal";
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;


    public customAdapterForChatInterface(Context context,List<UserDatabaseInformation> sourceBucket) {
        super(context,R.layout.custommessagelist,sourceBucket);
        this.context=context;
        this.sourceBucket=sourceBucket;
    }

//sourcebucket is now the list of arrays containing objects


    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        Typeface typeface1 = Typeface.createFromAsset(context.getAssets(), "Comfortaa-Light.ttf");
        View mView=convertView;

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());

        if (sourceBucket.get(position).getSeenM() != null) {
            if (sourceBucket.get(position).getSeenM().equals("yup")) {
                mView = layoutInflater.inflate(R.layout.seenlayout, parent, false);
                which="seen";
            }
        } else if (sourceBucket.get(position).getpM() != null) {
            if (sourceBucket.get(position).getpM().equals("yup")) {
                mView = layoutInflater.inflate(R.layout.photolayout, parent, false);
                which="photo";
            }
        } else {

            mView = layoutInflater.inflate(R.layout.left_message, parent, false);
            TextView leftMessage = (TextView) mView.findViewById(R.id.leftMessage);
            leftMessage.setTypeface(typeface1);

            TextView whoCameToChat = (TextView) mView.findViewById(R.id.whoCameToChat);
            whoCameToChat.setText(sourceBucket.get(position).getUserName());
            if (sourceBucket.get(position).getMusicnapRequest() != null){
                    if(sourceBucket.get(position).getMusicnapRequest().equals("yes")) {
                        //this logic means when friend sent me the song and also avoid own music request
                        Toast.makeText(getContext(), "Friend music request", Toast.LENGTH_LONG).show();
                    }
            }

            if (sourceBucket.get(position).getMes() != null) {
                leftMessage.setText(sourceBucket.get(position).getMes());
            }


            which="normal";
        }

        if(which.equals("seen")){
            TextView status=(TextView)mView.findViewById(R.id.statusId);
            status.setText("seen");

        }

        if(which.equals("photo")){
            final ImageView photoMessage=(ImageView)mView.findViewById(R.id.photoMessageId);



            photoMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent=new Intent("photozoom");

                    UserDatabaseInformation photoMessageObject=new UserDatabaseInformation();
                    photoMessageObject.setpUrl(sourceBucket.get(position).getpUrl());


                    intent.putExtra("photoMessageObject",photoMessageObject);

                    Log.i("mytag",sourceBucket.get(position).getpUrl());
                    LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);

                }
            });



            Glide.with(getContext()).
                    load(sourceBucket.get(position).getpUrl())
                    .centerCrop()
                    .into(photoMessage);
        }



        return mView;

    }


    public static class  FullScreenPhotoFragment extends android.support.v4.app.Fragment {

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            View mView = inflater.inflate(R.layout.fullscreenphotofragment, container, false);

            return  mView;
        }
    }



}
