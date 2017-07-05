package com.example.swornim.musicnap.customAdapterPackage;

import android.content.Context;
import android.graphics.Typeface;
import android.media.Image;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swornim.musicnap.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Swornim on 12/23/2016.
 */
public class customAdapterForChatInterface extends ArrayAdapter<UserDatabaseInformation> {


    private List<UserDatabaseInformation> sourceBucket=new ArrayList<>();//its an array list container
    private Context context;


    public customAdapterForChatInterface(Context context,List<UserDatabaseInformation> sourceBucket) {
        super(context,R.layout.custommessagelist,sourceBucket);
        this.context=context;
        this.sourceBucket=sourceBucket;
    }

//sourcebucket is now the list of arrays containing objects


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Typeface typeface1=Typeface.createFromAsset(context.getAssets(),"Comfortaa-Light.ttf");
        View mView = convertView;
        if(convertView==null) {

            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            mView = layoutInflater.inflate(R.layout.left_message, parent, false);
        }


        TextView leftMessage=(TextView) mView.findViewById(R.id.leftMessage);
        leftMessage.setTypeface(typeface1);

        TextView whoCameToChat=(TextView) mView.findViewById(R.id.whoCameToChat);
        ImageView musicRequestImage=(ImageView) mView.findViewById(R.id.musicRequestImage);
        final String userName=new CustomSharedPref(getContext()).getSharedPref("userName");

        if(sourceBucket.get(position).getMusicnapRequest()!=null &&
                sourceBucket.get(position).getMusicnapRequest().equals("yes") &&
                !(sourceBucket.get(position).getUserName().equals(userName))){
              musicRequestImage.setImageResource(R.drawable.ic_music_note_black_36dp);
              musicRequestImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //call the streaming class and stream the file path of song from the firebase server
                    //send the diduseraccept variable as a chat and filter the chat on snaphome
                }
            });
        }

        if(sourceBucket.get(position).getCurrentMessageTobeSent()!=null){
            leftMessage.setText(sourceBucket.get(position).getCurrentMessageTobeSent());
        }

        if(sourceBucket.get(position).getCurrentAppUserName()!=null){
            whoCameToChat.setText(sourceBucket.get(position).getCurrentAppUserName());
        }



        return mView;

    }



}
