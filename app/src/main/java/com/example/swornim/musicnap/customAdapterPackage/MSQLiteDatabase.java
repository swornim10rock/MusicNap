package com.example.swornim.musicnap.customAdapterPackage;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.*;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;

/**
 * Created by Swornim on 1/27/2017.
 */
public class MSQLiteDatabase extends SQLiteOpenHelper {

    private static Context CONTEXT;
    private Map<String,Object> contactListMap=new HashMap<>();



    public MSQLiteDatabase(Context context) {
        super(context,"USER_CONT.db", null, 1);
        CONTEXT=context;

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE USERS( ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT , NUMBER TEXT );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS USERS;");
        onCreate(db);
}


    public void insertUserContacts(String name,String number){
        ContentValues contentValues=new ContentValues();
        contentValues.put("NAME",name);
        contentValues.put("NUMBER",number);
        this.getWritableDatabase().insertOrThrow("USERS",null,contentValues);
    }

    public void DeleteUserContact(String name){
        this.getWritableDatabase().delete("USERS","NAME='"+name+"'",null);
    }

    public Map<String,Object> readAllContacts(){
        Cursor cursor=this.getReadableDatabase().rawQuery("SELECT * FROM USERS",null);
        while(cursor.moveToNext()){

            String name=cursor.getString(1);
            String number=cursor.getString(2);
            contactListMap.put(name,number);
        }
        cursor.close();//to free the cursor
        return contactListMap;
    }
}



