package com.example.mycosts;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Пользователь on 26.06.2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "myDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.w("TAG_LOG","on create db");
        db.execSQL("create table costs_table ("
                + "id integer primary key autoincrement,"
                + "date text, "
                + "name text, "
                + "cost int, "
                + "type text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
