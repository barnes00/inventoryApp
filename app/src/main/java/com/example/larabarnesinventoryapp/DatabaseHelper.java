package com.example.larabarnesinventoryapp;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.Random;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class DatabaseHelper extends SQLiteOpenHelper { //class used to interact with the database
    private static final String DATABASE_NAME = "inventory.db";
    private static final int VERSION = 1;
    private static final String CHANNEL_ID = "inventory_notifications";
    private final Context myContext;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        myContext = context;
    }

    private static final class UserTable { //userTable to store logins
        private static final String TABLE = "users";
        private static final String COL_ID = "_id";
        private static final String COL_USERNAME = "username";
        private static final String COL_PASSWORD = "password";
    }

    private static final class ItemsTable { //itemTable to store items
        private static final String TABLE = "items";
        private static final String COL_ID = "_id";
        private static final String COL_NAME = "name";
        private static final String COL_QUANTITY = "quantity";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create login table
        db.execSQL("create table " + UserTable.TABLE + " (" +
                UserTable.COL_ID + " integer primary key autoincrement, " +
                UserTable.COL_USERNAME + " text, " +
                UserTable.COL_PASSWORD + " text)");

        //create item table
        db.execSQL("create table " + ItemsTable.TABLE + " (" +
                ItemsTable.COL_ID + " integer primary key autoincrement, " +
                ItemsTable.COL_NAME + " text, " +
                ItemsTable.COL_QUANTITY + " int)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists " + UserTable.TABLE);
        db.execSQL("drop table if exists " + ItemsTable.TABLE);
        onCreate(db);
    }

    public long addUser(String username, String password) { //add a user into userTable
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserTable.COL_USERNAME, username);
        values.put(UserTable.COL_PASSWORD, password);

        return db.insert(UserTable.TABLE, null, values); //return id if successful, -1 if not
    }


    public boolean[] validateUser(String username, String password) { //check if username and password match information in db
        SQLiteDatabase db = getReadableDatabase();
        boolean[] userDetails = {false, false};

        String sql = "select * from " + UserTable.TABLE + " where username = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{username});
        if (cursor.moveToFirst()) {
            userDetails[0] = true; //user exists
            String dbPassword = cursor.getString(2);
            if (dbPassword.equals(password)) {
                userDetails[1] = true; //password matches
            }
        }
        cursor.close();
        return userDetails;
    }

    public long addItem(String itemName, int itemQty) { //add an item into the db
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ItemsTable.COL_NAME, itemName);
        values.put(ItemsTable.COL_QUANTITY, itemQty);

        return db.insert(ItemsTable.TABLE, null, values); //return id of created item
    }

    public boolean checkItem(String itemName) { //check if item already in db
        SQLiteDatabase db = getReadableDatabase();
        String sql = "select * from " + ItemsTable.TABLE + " where name = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{itemName});
        boolean isItem = cursor.moveToFirst();
        cursor.close();
        return isItem;
    }

    public void deleteItem(long id) { //delete an item by id
        SQLiteDatabase db = getWritableDatabase();
        db.delete(ItemsTable.TABLE, ItemsTable.COL_ID + " = ?", new String[]{Long.toString(id)});
    }

    public boolean updateItem(long id, int qty) { //update item qty
        SQLiteDatabase db = getWritableDatabase();
        int newQty;
        String itemName;

        String sql = "select * from " + ItemsTable.TABLE + " where _id = ?"; //find old qty
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            itemName = cursor.getString(1);
            newQty = cursor.getInt(2) + qty;
        } else {
            return false;
        }
        cursor.close();
        if(newQty < 0){ //no action if qty 0
            return false;
        }
        else if(newQty >= 100000){ //no action if qty too large
            return false;
        }
        else if(newQty == 0){ //send notification if qty becomes 0
            addNotification(itemName);
        }

        ContentValues values = new ContentValues();
        values.put(ItemsTable.COL_QUANTITY, newQty); //update qty
        int rowsUpdated = db.update(ItemsTable.TABLE, values, "_id = ?",
                new String[]{String.valueOf(id)});
        return rowsUpdated > 0;
    }

    public void addNotification(String itemName) { //function to send notification
        if (ContextCompat.checkSelfPermission(myContext, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED) {
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(myContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setContentTitle("Low Inventory")
                .setContentText(itemName + " is out of stock")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        // Add notification
        NotificationManagerCompat manager = NotificationManagerCompat.from(myContext);
        manager.notify(new Random().nextInt() , builder.build());
    }

    public void fillArray(ArrayList<itemData> myArrayList) { //fill an array with item data from db
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "select * from " + ItemsTable.TABLE + " order by name ASC";
        Cursor cursor = db.rawQuery(sql, new String[]{});
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(0);
                String itemName = cursor.getString(1);
                int itemQty = cursor.getInt(2);
                myArrayList.add(new itemData(id, itemName, itemQty));
            } while (cursor.moveToNext());
            cursor.close();
        }
    }
}