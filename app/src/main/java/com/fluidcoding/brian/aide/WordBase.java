package com.fluidcoding.brian.aide;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by brian on 11/5/2015.
 */
public class WordBase extends SQLiteOpenHelper {
    private static  final String TBL_NAME = "WORDS";
    private static final String DB_NAME = "AideBase";
    public WordBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    public WordBase(Context context) {
        super(context, DB_NAME, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables
        db.execSQL("CREATE TABLE " + TBL_NAME + "("
                + "ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "TYPE TEXT, "
                + "DISPLAY_TEXT TEXT); ");
        /*

        CREATE TABLE WORDS (
            ID INTEGER PRIMARY KEY AUTOINCREMENT,
            TYPE TEXT,
            DISPLAY_TEXT);

         */
           //      + "IMAGE_RESOURCE_ID INTEGER);");
        insertWord(db, "IDENTIFIER", "YOU");
        insertWord(db, "OTHER", "WANT");
        insertWord(db, "OTHER", "NEED");
        insertWord(db, "OTHER", "LIKE");
        insertWord(db, "OTHER", "YES");
        insertWord(db, "OTHER", "NO");
        insertWord(db, "OTHER", "HELP");
        insertWord(db, "FOOD", "PIZZA");
        insertWord(db, "IDENTIFIER", "I");
    }

    private static void insertWord(SQLiteDatabase db, String name, String text){
        ContentValues contentValues = new ContentValues();
        contentValues.put("TYPE", name);
        contentValues.put("DISPLAY_TEXT", text);

        db.insert(TBL_NAME, null, contentValues);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

/*Table WORDS
    ID(INTEGER)      NAME(TEXT)        DISPLAY_TEXT(TEXT)      IMAGE_RESOURCE_ID(INTEGER)


    Table PAGES
    MAIN
 */