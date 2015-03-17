package com.ohyea.extendedtodo;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by JiaChen on 3/15/2015.
 */
public class mySQLiteHelper extends SQLiteOpenHelper{
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "todolistDB";

    private static final String TABLE_NAME = "parentItems";

    public int max_index;

    public mySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        String CREATE_ITEM_TABLE = "CREATE TABLE IF NOT EXISTS parentItems(idx INTEGER, year TEXT, month TEXT, DoM TEXT, DoW TEXT, prty TEXT, content TEXT)";
        // create books table
        db.execSQL(CREATE_ITEM_TABLE);
        //get the initial row count
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public int insertItem  (ArrayList item)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        ArrayList<String> i = (ArrayList<String>) item;
        contentValues.put("idx", ++max_index);
        contentValues.put("year", i.get(2));
        contentValues.put("month", i.get(3));
        contentValues.put("DoM", i.get(4));
        contentValues.put("DoW", i.get(5));
        contentValues.put("prty", i.get(1));
        contentValues.put("content", i.get(0));
        db.insert(TABLE_NAME, null, contentValues);
        return (max_index);
    }
/*
    public void swapRows(int index1, ArrayList item1, int index2, ArrayList item2){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        ArrayList<String> i = (ArrayList<String>) item1;
        contentValues.put("year", i.get(2));
        contentValues.put("month", i.get(3));
        contentValues.put("DoM", i.get(4));
        contentValues.put("DoW", i.get(5));
        contentValues.put("prty", i.get(1));
        contentValues.put("content", i.get(0));
        db.update(TABLE_NAME, contentValues, "idx = ? ", new String[] { Integer.toString(index2) } );

        i = (ArrayList<String>) item2;
        contentValues.put("year", i.get(2));
        contentValues.put("month", i.get(3));
        contentValues.put("DoM", i.get(4));
        contentValues.put("DoW", i.get(5));
        contentValues.put("prty", i.get(1));
        contentValues.put("content", i.get(0));
        db.update(TABLE_NAME, contentValues, "idx = ? ", new String[] { Integer.toString(index1) } );
    }
*/
    public void swapRows(int index1, int index2){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues1 = new ContentValues();
        ContentValues contentValues2 = new ContentValues();
        Cursor i =  db.rawQuery( "select * from "+TABLE_NAME+" where idx="+index1+"", null );
        i.moveToFirst();
        contentValues1.put("year", i.getString(i.getColumnIndex("year")));
        contentValues1.put("month", i.getString(i.getColumnIndex("month")));
        contentValues1.put("DoM", i.getString(i.getColumnIndex("DoM")));
        contentValues1.put("DoW", i.getString(i.getColumnIndex("DoW")));
        contentValues1.put("prty", i.getString(i.getColumnIndex("prty")));
        contentValues1.put("content", i.getString(i.getColumnIndex("content")));

        Cursor j =  db.rawQuery( "select * from "+TABLE_NAME+" where idx="+index2+"", null );
        j.moveToFirst();
        contentValues2.put("year", j.getString(j.getColumnIndex("year")));
        contentValues2.put("month", j.getString(j.getColumnIndex("month")));
        contentValues2.put("DoM", j.getString(j.getColumnIndex("DoM")));
        contentValues2.put("DoW", j.getString(j.getColumnIndex("DoW")));
        contentValues2.put("prty", j.getString(j.getColumnIndex("prty")));
        contentValues2.put("content", j.getString(j.getColumnIndex("content")));

        db.update(TABLE_NAME, contentValues1, "idx = ? ", new String[] { Integer.toString(index2) } );
        db.update(TABLE_NAME, contentValues2, "idx = ? ", new String[] { Integer.toString(index1) } );
    }

    public Cursor getData(int index){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME+" where idx="+index+"", null );
        res.moveToFirst();
        return res;
    }

    public String getContent(int index){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select content from "+TABLE_NAME+" where idx="+index+"", null );
        res.moveToFirst();
        return res.getString(res.getColumnIndex("content"));
    }

    public ArrayList<String> getDateNum(int index){
        if(index < 0) return null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select year, month, DoM, DoW from "+TABLE_NAME+" where idx="+index+"", null );
        res.moveToFirst();
        ArrayList<String> date = new ArrayList();
        date.add(res.getString(res.getColumnIndex("year")));
        date.add(res.getString(res.getColumnIndex("month")));
        date.add(res.getString(res.getColumnIndex("DoM")));
        date.add(res.getString(res.getColumnIndex("DoW")));
        return date;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return numRows;
    }
    public boolean updateDate (int index, String year, String month, String DoM, String DoW)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("year", year);
        contentValues.put("month", month);
        contentValues.put("DoM", DoM);
        contentValues.put("DoW", DoW);
        db.update(TABLE_NAME, contentValues, "idx = ? ", new String[] { Integer.toString(index) } );
        return true;
    }

    public boolean updateContent (int index, String content)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("content", content);
        db.update(TABLE_NAME, contentValues, "idx = ? ", new String[] { Integer.toString(index) } );
        return true;
    }

    public boolean updatePriority (int index, String priority)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("prty", priority);
        db.update(TABLE_NAME, contentValues, "idx = ? ", new String[] { Integer.toString(index) } );
        return true;
    }

    public boolean updateIndex (int old_index, int new_index)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("idx", new_index);
        db.update(TABLE_NAME, contentValues, "idx = ? ", new String[] { Integer.toString(old_index) } );
        return true;
    }

    public void  deleteItem (int index)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "idx = ? ", new String[] { Integer.toString(index) });
        db.rawQuery( "UPDATE "+TABLE_NAME+" SET idx = idx - 1 WHERE idx > "+index+"", null );
        --max_index;
    }

    public void delete_all(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.rawQuery( "DELETE FROM "+TABLE_NAME, null );
    }

    public ArrayList getAllItems()
    {
        ArrayList<Object> array_list = new ArrayList();
        SQLiteDatabase db = this.getReadableDatabase();
        if(db == null) Log.e("TAG", "db is null");
        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME, null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            ArrayList<String> item = new ArrayList();
            item.add(res.getString(res.getColumnIndex("content")));
            item.add(res.getString(res.getColumnIndex("prty")));
            item.add(res.getString(res.getColumnIndex("year")));
            item.add(res.getString(res.getColumnIndex("month")));
            item.add(res.getString(res.getColumnIndex("DoM")));
            item.add(res.getString(res.getColumnIndex("DoW")));
            item.add(res.getString(res.getColumnIndex("idx")));
            array_list.add(item);
            res.moveToNext();
        }
        max_index = array_list.size()-1;
        return array_list;
    }
}
