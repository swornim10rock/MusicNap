package com.example.swornim.musicnap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.swornim.musicnap.customAdapterPackage.CustomSharedPref;
import com.example.swornim.musicnap.customAdapterPackage.FirebaseUserModel;
import com.example.swornim.musicnap.customAdapterPackage.SnapMusicHomePage;
import com.example.swornim.musicnap.customAdapterPackage.UserDatabaseInformation;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    ImageView imageView;
    private String[] staticFriends={"9813847444","9813054341","9841001504","9860569432","981339287","9860206938"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        showUserName();
        showPhoneNumber();


    }

    public void showUserName(){

        AlertDialog.Builder popBox = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater=LayoutInflater.from(getApplicationContext());
        final View view=layoutInflater.inflate(R.layout.alertedit,null);
        popBox.setCancelable(false);

        popBox.setView(view);
        popBox.setTitle("Enter the userName");

        popBox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText editText=(EditText) view.findViewById(R.id.alertEditText);
                if(editText.getText().toString().equals(""))
                    showUserName();// call again if nothing is entered
                else{
                    new CustomSharedPref(getApplicationContext()).setSharedPref("userName",editText.getText().toString());
                    registeringProcess();
                    dialog.dismiss();
                }

            }
        });

        popBox.show();

    }

    public void showPhoneNumber(){

        AlertDialog.Builder popBox = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater=LayoutInflater.from(getApplicationContext());
        final View view=layoutInflater.inflate(R.layout.alertedit,null);
        popBox.setCancelable(false);

        popBox.setView(view);
        popBox.setTitle("Enter the PhoneNumber");

        popBox.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText editText=(EditText) view.findViewById(R.id.alertEditText);
                if(editText.getText().toString().equals(""))
                    showPhoneNumber();// call again if nothing is entered
                else{
                    new CustomSharedPref(getApplicationContext()).setSharedPref("userPhoneNumber",editText.getText().toString());
                    dialog.dismiss();
                }

            }
        });
        popBox.show();
    }

    public void registeringProcess(){

        UserDatabaseInformation messageObject=new UserDatabaseInformation();
        messageObject.setPhoneNumber(new CustomSharedPref(getApplicationContext()).getSharedPref("userPhoneNumber"));
        messageObject.setUserName(new CustomSharedPref(getApplicationContext()).getSharedPref("userName"));

        List<UserDatabaseInformation> list=new ArrayList<>();


        for(int i=0;i<staticFriends.length;i++){
            UserDatabaseInformation message=new UserDatabaseInformation();
            message.setFriensNumber(staticFriends[i]);
            list.add(message);

        }
        messageObject.setList(list);
        new FirebaseUserModel().new AddContactsToFirebase(getApplicationContext(),messageObject).execute();
        startActivity(new Intent(RegisterActivity.this,SnapMusicHomePage.class));
    }



}
