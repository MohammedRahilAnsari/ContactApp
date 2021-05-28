package com.example.contactapp.Activity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.contactapp.Models.ContactModel;

import java.util.ArrayList;
import java.util.List;

public class  DataBaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DB_Name = "contacts";
    private static final int DB_Version = 2;

    public DataBaseHelper( Context context) {
        super(context, DB_Name, null, DB_Version);
        this.context = context;
        Toast.makeText(context, "IN Constructor", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Toast.makeText(context, "IN ONcreate", Toast.LENGTH_SHORT).show();
        db.execSQL(ContactModel.Create_Table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE "+ DB_Name );
        db.execSQL(ContactModel.Create_Table);

        Toast.makeText(context, "IN ONupgrade", Toast.LENGTH_SHORT).show();

    }

    public long InsertContact (ContactModel contactModel){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContactModel.Coloumn_name,contactModel.getName());
        values.put(ContactModel.Coloumn_number,contactModel.getNumber());
        values.put(ContactModel.Coloumn_number,contactModel.getNumber());
        values.put(ContactModel.Coloumn_image,contactModel.getImage());


        long id = db.insert(ContactModel.Table_Name,null,values);

        db.close();

        return id;
    }

    public List<ContactModel> getallcontact (){
        List <ContactModel> contactModelList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + ContactModel.Table_Name;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        if (cursor.moveToFirst()) {
            do {
                ContactModel contactModel = new ContactModel();
                contactModel.setId(cursor.getInt(cursor.getColumnIndex(ContactModel.Coloumn_id)));
                contactModel.setName(cursor.getString(cursor.getColumnIndex(ContactModel.Coloumn_name)));
                contactModel.setNumber(cursor.getString(cursor.getColumnIndex(ContactModel.Coloumn_number)));
                contactModel.setImage(cursor.getString(cursor.getColumnIndex(ContactModel.Coloumn_image)));

                contactModelList.add(contactModel);
            } while (cursor.moveToNext());
        }

        db.close();

        return contactModelList;
    }

    public  void deleteContact(ContactModel contactModel){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(ContactModel.Table_Name,ContactModel.Coloumn_id + " = " +contactModel.getId(),null);
        db.close();

    }

    public  int UpdateContact(ContactModel contactModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContactModel.Coloumn_name, contactModel.getName());
        values.put(ContactModel.Coloumn_number, contactModel.getNumber());
        values.put(ContactModel.Coloumn_image, contactModel.getImage());

        return db.update(ContactModel.Table_Name, values, ContactModel.Coloumn_id + " = ? ",
                new String[]{String.valueOf(contactModel.getId())});



    }
}
