package com.example.swornim.musicnap.customAdapterPackage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swornim.musicnap.MainActivity;
import com.example.swornim.musicnap.R;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Swornim on 11/29/2016.
 */
public class customAdapterForSongList extends ArrayAdapter<songList> {

    private Context context;
    private MediaPlayer mediaPlayer;
    private List<songList> songs;
    private ArrayList<File> collectedSongList= new ArrayList<File>();
    private ArrayList<String> nameOfSong=new ArrayList<String>();

    public customAdapterForSongList(Context context, List<songList> songs) {

        super(context, R.layout.customsonglist,songs);
        this.context=context;
        this.songs=songs;
         mediaPlayer=new MediaPlayer();
    }


//getview is called by listview when users scrolls
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        //getview is called nth times for the nth items seen on the screen

        View itemView=convertView;
        if(convertView==null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            itemView = layoutInflater.inflate(R.layout.customsonglist, parent, false);
        }


        //reference to everything
        final TextView songsName=(TextView) itemView.findViewById(R.id.songsName);
        TextView songsNumber=(TextView) itemView.findViewById(R.id.songNumber);

        songsName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//            String filePath="storage/extSdCard/Music";
//                filePath+=songs.get(position).getSongName();
                Toast.makeText(getContext(), songs.get(position).getSongName(),Toast.LENGTH_SHORT).show();

                try {
                    MediaPlayerClass.setUpMediaPlayer(getContext(),songs.get(position).getSongAbsoutePath(),mediaPlayer);

//                    new CustomSharedPreferance(context).setSharedPref("userSongFilePath",songs.get(position).getSongAbsoutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        songsName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder popBox = new AlertDialog.Builder(getContext());
                popBox.setTitle(" Upload This Song For Friends? ");
                popBox.setCancelable(false);

                popBox.setPositiveButton("Yup", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String uriPath=songs.get(position).getSongAbsoutePath();
                         UserDatabaseInformation messageObject=new UserDatabaseInformation();
                        messageObject.setUploadingFilePath(uriPath);
                        messageObject.setUploadingSongName(songs.get(position).getSongName());
                        Intent intent = new Intent(context, UploadFileFirebase.class);
                        intent.putExtra("messageObject",messageObject);
                        Log.i("mytag","path "+messageObject.getUploadingFilePath());
                        context.startActivity(intent);
                    }
                });

                popBox.show();


                return true;
            }

        });


        songsName.setText(songs.get(position).getSongName());
        songsNumber.setText(position+1+".");
        //initialize values for ezach rows loop

        return itemView;


    }

}

