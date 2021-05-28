package com.example.contactapp.Activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contactapp.Adapters.ContactAdapter;
import com.example.contactapp.Models.ContactModel;
import com.example.contactapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements  ContactAdapter.Clicklistner , SearchView.OnQueryTextListener {


    private static final int REQUEST_GALLERY_PERMISSION = 100;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private static final int RESULT_CAMERA = 300;
    private static final int RESULT_GALLERY = 400;

    DataBaseHelper helper;
    ContactAdapter  contactAdapter;
    String number;
    String picturePath;
    private Context context;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helper = new DataBaseHelper(this);

        FloatingActionButton floatingActionButton = findViewById(R.id.floating_action_button);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        contactAdapter = new ContactAdapter(this,MainActivity.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(contactAdapter);

        context = this;

            setAdapter ();
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addDailog();
                }
            });

    }

    private void opendailog() {

            final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Add Photo!");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (options[item].equals("Take Photo")) {
                        if (hasPermssionCamera()) {
                            openCamera();
                        } else {
                            requestPermissionCamera();
                        }
                    } else if (options[item].equals("Choose from Gallery")) {
                        if(hasPermssionGallery()){
                            openGallery();
                        }else {
                            requestPermissionGallery();
                        }
                    } else if (options[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();
        }

        private boolean hasPermssionCamera() {
            int res;
            String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
            for (String perms : permissions) {
                res = checkCallingOrSelfPermission(perms);
                if (!(res == PackageManager.PERMISSION_GRANTED)) {
                    return false;
                }
            }
            return true;

        }

        private boolean hasPermssionGallery() {
            int res;
            String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
            for (String perms : permissions) {
                res = checkCallingOrSelfPermission(perms);
                if (!(res == PackageManager.PERMISSION_GRANTED)) {
                    return false;
                }
            }
            return true;

        }

        public void requestPermissionGallery() {
            String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, REQUEST_GALLERY_PERMISSION);
            }
        }


        //Request Camera Permission
        public void requestPermissionCamera() {
            String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, REQUEST_CAMERA_PERMISSION);
            }
        }


        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);


            switch (requestCode){
                case 1000 : {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + number));
                        startActivity(callIntent);

                    } else{
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
            }


            switch (requestCode){

                case REQUEST_CAMERA_PERMISSION:
                    boolean allowed = true;
                    for (int res : grantResults) {
                        allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                    }

                    if (allowed) {
                        openCamera();
                    } else {
                        Toast.makeText(context, "Required Camera Permission", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case REQUEST_GALLERY_PERMISSION:
                    boolean b = true;
                    for (int res : grantResults) {
                        b = b && (res == PackageManager.PERMISSION_GRANTED);
                    }

                    if (b) {
                        openGallery();
                    } else {
                        Toast.makeText(context, "Required Storage Permission", Toast.LENGTH_SHORT).show();
                    }
                    break;



            }

        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (resultCode == RESULT_OK) {
                if (requestCode == RESULT_CAMERA) {
                    File f = new File(Environment.getExternalStorageDirectory().toString());
                    for (File temp : f.listFiles()) {
                        if (temp.getName().equals("temp.jpg")) {
                            f = temp;
                            break;
                        }
                    }
                    try {
                        Bitmap bitmap;
                        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                        bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                                bitmapOptions);
                        imageView.setImageBitmap(bitmap);
                        String path = Environment
                                .getExternalStorageDirectory()
                                + File.separator
                                + "Phoenix" + File.separator + "default";
                        f.delete();
                        OutputStream outFile = null;
                        File dir = new File(path);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");

                        //This is used to store in sqlite


                         picturePath = file.getAbsolutePath();
                        try {
                            outFile = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                            outFile.flush();
                            outFile.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (requestCode == RESULT_GALLERY) {
                    Uri selectedImage = data.getData();
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);

                    //This is used to store in sqlite

                     picturePath = c.getString(columnIndex);
                    c.close();

                    Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                    imageView.setImageBitmap(thumbnail);
                }
            }




        }




    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        startActivityForResult(intent, RESULT_CAMERA);
    }

    private void openGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_GALLERY);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_bar,menu);

        MenuItem search = menu.findItem(R.id.search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setOnQueryTextListener(this);
        return  true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newtext) {
        newtext = newtext.toLowerCase();
        ArrayList<ContactModel> newlist = new ArrayList<>();
        for (ContactModel contactModel : helper.getallcontact() )

        {
            String name = (contactModel.getName().toLowerCase());
            if (name.contains(newtext)){
                newlist.add(contactModel);
            }
        }
        contactAdapter.setFilter(newlist);
        return true;
    }

    private void addDailog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Add Contact")
                .setView(R.layout.dailog_box)
                .setPositiveButton("Add",null)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();

        final EditText edtname = alertDialog.findViewById(R.id.edt_name);
        final EditText edtnumber = alertDialog.findViewById(R.id.edt_number);
         imageView = alertDialog.findViewById(R.id.dailog_pic);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opendailog();
            }
        });



        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtname.getText().toString().equals("")){
                    edtname.setError("Name Required");
                }else if(edtnumber.getText().toString().equals("")){
                    edtnumber.setError("Number Required");
                }else {

                    alertDialog.dismiss();

                    ContactModel contacts = new ContactModel();
                    contacts.setName(edtname.getText().toString());
                    contacts.setNumber(edtnumber.getText().toString());
                    contacts.setImage(picturePath);

                    long last_id = helper.InsertContact(contacts);

                    if(last_id > 0){
                        setAdapter();
                        Toast.makeText(MainActivity.this, "Added", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(MainActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void setAdapter() {
        contactAdapter.setdata(helper.getallcontact());
    }



    @Override
    public void OnEditClick(final ContactModel contactModel, final int position) {
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Edit Contact")
                .setView(R.layout.dailog_box)
                .setPositiveButton("Update", null)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();


        final EditText edtName = alertDialog.findViewById(R.id.edt_name);
        final EditText edtNumber = alertDialog.findViewById(R.id.edt_number);
        //final ImageView ivv = alertDialog.findViewById(R.id.dailog_pic);

        edtName.setText(contactModel.getName());
        edtNumber.setText(contactModel.getNumber());
        //ivv.setImageResource(Integer.parseInt(contactModel.getImage()));

        Log.d("TAGEDIT", String.valueOf(contactModel.getId()));

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(edtName.getText().toString().equals("")){
                    edtName.setError("Name Required");
                }else if(edtNumber.getText().toString().equals("")){
                    edtNumber.setError("Number Required");
                }else {

                    alertDialog.dismiss();
                    ContactModel contacts = new ContactModel();
                    contacts.setName(edtName.getText().toString());
                    contacts.setNumber(edtNumber.getText().toString());
                    contacts.setId(contactModel.getId());

                    long last_id = helper.UpdateContact(contacts);

                    if(last_id > 0){
                        setAdapter();
                        contactAdapter.notifyItemChanged(position);
                        Toast.makeText(MainActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(MainActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void OnDeleteClick(ContactModel contactModel, int position) {
        helper.deleteContact(contactModel);
        contactAdapter.setdata(helper.getallcontact());
        contactAdapter.notifyItemRemoved(position);
    }



    @Override
    public void OnRowClick(ContactModel contactModel) {

        number =contactModel.getNumber();

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE},
                        1000);

        } else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+ contactModel.getNumber()));
                startActivity(callIntent);
        }
    }
}