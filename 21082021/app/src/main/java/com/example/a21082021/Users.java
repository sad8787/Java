package com.example.a21082021;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.a21082021.Database.UserBaseHelper;
import com.example.a21082021.Database.UserDdSchema;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Users {
    private ArrayList<User> userList = new ArrayList<>();
    private SQLiteDatabase database;
    private Context context;
    public Users(Context context){
        this.context=context;
        this.database=new UserBaseHelper(this.context).getWritableDatabase();
    }

    public ArrayList<User> getUserslist() {

        this.userList=new ArrayList<>();
        UserCursorWrapper cursorWrapper=queryUsers();
        try {
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()){
                User user= cursorWrapper.getUser();
                userList.add(user);
                cursorWrapper.moveToNext();
            }
        }finally {
            cursorWrapper.close();
        }
        return this.userList;
    }

    public UserCursorWrapper queryUsers(){
        Cursor cursor=database.query(UserDdSchema.UserTable.NAME,null,null,null,null,null,null);
        return new UserCursorWrapper(cursor);


    }
    public void addUser(User user){
        /*String host = "http://0988.vozhzhaev.ru/handlerAddUser.php? name="+ user.getName()+
                "&lastname="+ user.getLastname()+
                "&phone="+user.getFonNomer()+
                "&uuid="+user.getUuid().toString();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(host);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream is = urlConnection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(is);
                    int i;
                    StringBuilder result = new StringBuilder();
                    while ((i=reader.read()) != -1){
                        result.append((char)i);
                    }
                    System.out.println(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Thread t = new Thread(runnable);
        t.start();*/
        ContentValues values=getContentValues( user);
        database.insert(UserDdSchema.UserTable.NAME,null,values);
    }
    private static ContentValues getContentValues(User user){
        ContentValues values=new ContentValues();
        values.put(UserDdSchema.Cols.UUID,user.getUuid().toString());
        values.put(UserDdSchema.Cols.NAME,user.getName());
        values.put(UserDdSchema.Cols.LASTNAME,user.getLastname());
        values.put(UserDdSchema.Cols.FONNUMBER,user.getFonNomer());

        return values;
    }
    public void ubdateUser(User user){
        deleteUser(user);
        addUser(user);
    }
    public void deleteUser(User user){
        ContentValues values=getContentValues( user);
        database.delete(UserDdSchema.UserTable.NAME,UserDdSchema.Cols.UUID+"=?",new String[]{user.getUuid().toString()});
    }
}
