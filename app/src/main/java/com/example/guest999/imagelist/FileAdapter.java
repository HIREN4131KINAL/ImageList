package com.example.guest999.imagelist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Guest999 on 10/1/2016.
 */
public class FileAdapter extends RecyclerView.Adapter<FileAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<String> fileList;

    @Override
    public FileAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.raw, parent, false);
        FileAdapter.MyViewHolder vh = new FileAdapter.MyViewHolder(itemView);
        return vh;
    }


    public FileAdapter(ArrayList<String> filearray) {
        this.fileList = filearray;
        Log.e("FileAdapter: " + fileList + "", "hello" + filearray);
    }

    @Override
    public void onBindViewHolder(FileAdapter.MyViewHolder holder, int position) {
        holder.textView.setText(fileList.get(position));
      /*  byte[] decodeValue = new byte[0];
        try {
            decodeValue = Base64.decode(fileList.get(position).getBytes("UTF-8"), Base64.DEFAULT);
            Log.e("4", decodeValue + "");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Bitmap bitmap = BitmapFactory.decodeByteArray(decodeValue, 0, decodeValue.length);
        Log.e("on", bitmap + "");
        holder.imageView.setImageBitmap(bitmap);*/

    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

   /* void addToList(String strPath) {
        this.albumList.add(strPath);
        this.notifyDataSetChanged();
    }*/

    public class MyViewHolder extends RecyclerView.ViewHolder {
        //   ImageView imageView;
        TextView textView;

        public MyViewHolder(View view) {
            super(view);
            //imageView = (ImageView) view.findViewById(R.id.upload_image_rec);
            textView = (TextView) view.findViewById(R.id.file_name);


        }
    }

}