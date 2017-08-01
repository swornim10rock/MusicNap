package com.example.swornim.musicnap;

import android.app.AlertDialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.swornim.musicnap.customAdapterPackage.CustomSharedPref;
import com.example.swornim.musicnap.customAdapterPackage.CustomSharedPreferance;
import com.example.swornim.musicnap.customAdapterPackage.FirebaseUserModel;
import com.example.swornim.musicnap.customAdapterPackage.HeadsetBroadCastReceiver;
import com.example.swornim.musicnap.customAdapterPackage.MSQLiteDatabase;
import com.example.swornim.musicnap.customAdapterPackage.MediaPlayerClass;
import com.example.swornim.musicnap.customAdapterPackage.MinorDetails;
import com.example.swornim.musicnap.customAdapterPackage.SnapMusicHomePage;
import com.example.swornim.musicnap.customAdapterPackage.UserDatabaseInformation;
import com.example.swornim.musicnap.customAdapterPackage.customAdapterForSongList;
import com.example.swornim.musicnap.customAdapterPackage.friendlist;
import com.example.swornim.musicnap.customAdapterPackage.songList;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private Button uploadSongsFirebase;

    private String musicFilepath="storage/extSdCard/";

    private List<songList> songs=new ArrayList<>();
    private ArrayAdapter<songList> adapter;
    private ArrayList<File> collectedSongsList=new ArrayList<File>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.songlist);


        if(new CustomSharedPref(getApplicationContext()).getSharedPref("userName").equals("none")){
            startActivity(new Intent(MainActivity.this,RegisterActivity.class));
        }
        ListView listView=(ListView)findViewById(R.id.songListView);
        uploadSongsFirebase=(Button) findViewById(R.id.uploadSongsButton);


        File external_storage_root_music= new File(musicFilepath);//original
//        File external_storage_root_music= new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        new updateContacts().execute();//update phone contaactsz
        collectedSongsList=new songList().syncAllSongs(external_storage_root_music);

        fill();//fill the song list
        adapter=new customAdapterForSongList(this,songs);
        listView.setAdapter(adapter);

        uploadSongsFirebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if((new CustomSharedPref(getApplicationContext()).getSharedPref("dontcall")).equals("none")){
                    UserDatabaseInformation messageObject=new UserDatabaseInformation();
                    messageObject.setUploadingSongsList(songs);
                    new FirebaseUserModel().new RegisterUserSongs(getApplicationContext(),messageObject).execute();
                    new CustomSharedPref(getApplicationContext()).setSharedPref("dontcall","already");
                    Log.i("mytag",songs.toString());
                }

            }
        });



    }

    private void fill() {
        for(int i=0;i<collectedSongsList.size();i++) {

            songList songListObject=new songList();
            songListObject.setSongName(collectedSongsList.get(i).getName());
            songListObject.setSongAbsoutePath(collectedSongsList.get(i).getAbsolutePath());
            songs.add(songListObject);

        }
    }

    public class updateContacts extends AsyncTask<Void,Void,String> {

        @Override
            protected String doInBackground(Void... params) {

            String number=null;
            String name = null;
            MSQLiteDatabase msqLiteDatabase=new MSQLiteDatabase(getApplicationContext());
            Map<String, Object> map = new HashMap<>();
            Cursor mcursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            if (mcursor.getCount() > 0) {
                while (mcursor.moveToNext()) {

                    number = mcursor.getString(mcursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    name = mcursor.getString(mcursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                   // map.put(name, number);
                   msqLiteDatabase.insertUserContacts(name,number);

                   // new FirebaseUserModel(getApplicationContext()).updateContacts(map);
                }
                mcursor.close();
                //TODO PERFORM THE DATABASE OPERATION HERE JUST FOR CALLING THE METHODS




            }
            return name;
        }


        @Override
        protected void onPostExecute(String s) {
            String userName = new CustomSharedPref(getApplicationContext()).getSharedPref("userName");
//            if (userName.equals("none"))
//                showAlertDialog("UserName");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.actionbaricons,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.addFreinds){
            startActivity(new Intent(MainActivity.this,friendlist.class));
        }

        return super.onOptionsItemSelected(item);
    }



    int randomNumber(){ return new Random().nextInt(1000)+2; }

    @Override
    public void onBackPressed() {


        new CustomSharedPref(getApplicationContext()).setSharedPref("callFilterOnce","none");
        finishAffinity();
    }

    @Override
    protected void onStop() {
        new CustomSharedPref(getApplicationContext()).setSharedPref("callFilterOnce","none");
        super.onStop();
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

        popBox.show();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}












