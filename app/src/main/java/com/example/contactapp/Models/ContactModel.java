package com.example.contactapp.Models;

public class ContactModel {

    public static final String Table_Name = "contacts";


    public static final String Coloumn_id = "id";
    public static final String Coloumn_name = "name";
    public static final String Coloumn_number = "number";
    public static final String Coloumn_image = "image";
    private int id;
    private String name, number, image;

    public static final String Create_Table =
            "Create Table " + Table_Name + " ( "
                    + Coloumn_id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + Coloumn_name + " TEXT, "
                    + Coloumn_number + " TEXT ,"
                    + Coloumn_image + " TEXT "
                    + ")";

    public ContactModel(int id, String name, String number, String image) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.image = image;
    }

    public ContactModel() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
