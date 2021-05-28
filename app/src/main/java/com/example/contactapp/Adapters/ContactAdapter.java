package com.example.contactapp.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.contactapp.Models.ContactModel;
import com.example.contactapp.R;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder> {

    private Context context;
    private List<ContactModel> data;
    private Clicklistner clicklistner;


    public ContactAdapter(Context context, Clicklistner clicklistner) {
        this.context = context;
        this.data = new ArrayList<>();
        this.clicklistner = clicklistner;
    }

    public void  setdata (List<ContactModel> data){
        this.data = data;

    }

    public interface Clicklistner{
        void OnEditClick (ContactModel contactModel,int position);
        void OnDeleteClick (ContactModel contactModel,int position);
        void  OnRowClick (ContactModel contactModel);
    }


    @Override
    public MyViewHolder onCreateViewHolder( ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_contact,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder( MyViewHolder myViewHolder, int i) {

        myViewHolder.name.setText(data.get(i).getName());
        myViewHolder.number.setText(data.get(i).getNumber());

       // myViewHolder.imgrow.setImageBitmap(picturePath);


            if (data.get(i).getImage()==null){

                ColorGenerator colorGenerator = ColorGenerator.DEFAULT;

                TextDrawable drawable1 = TextDrawable.builder()
                        .buildRound(String.valueOf(data.get(i).getName().charAt(0)).toUpperCase(),colorGenerator.getRandomColor() );

                myViewHolder.imgrow.setImageDrawable(drawable1);

            }else {

                Bitmap thumbnail = (BitmapFactory.decodeFile(data.get(i).getImage()));
                myViewHolder.imgrow.setImageBitmap(thumbnail);

            }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setFilter (ArrayList<ContactModel> newlist){
        data = new ArrayList<>();
        data.addAll(newlist);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView edit,delete,imgrow;
        private TextView name,number;

        public MyViewHolder( View itemView) {
            super(itemView);
            edit = itemView.findViewById(R.id.btn_edit);
            delete = itemView.findViewById(R.id.btn_delete);
            name = itemView.findViewById(R.id.tv_name);
            number = itemView.findViewById(R.id.tv_number);
            imgrow = itemView.findViewById(R.id.img_row);

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clicklistner.OnEditClick(data.get(getAdapterPosition()),getAdapterPosition());
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clicklistner.OnDeleteClick(data.get(getAdapterPosition()),getAdapterPosition());
                }
            });

            itemView.getRootView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clicklistner.OnRowClick(data.get(getAdapterPosition()));
                }
            });

        }

    }
}
