package com.example.guest999.imagelist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Harshad on 07-10-2016.
 */

public class GetImageAdapter extends RecyclerView.Adapter<GetImageAdapter.ViewHolder>
{
    Context context;
    String TAG=getClass().getName();
    private ArrayList<HashMap<String, String>> mDataset;

    public GetImageAdapter(Context context, ArrayList<HashMap<String, String>> albmlist) {
        mDataset = albmlist;
        this.context=context;
        Log.e(TAG, "GetImageAdapter: "+mDataset );
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
        // each data item is just a string in this case
        private ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_list);
        }
    }

    @Override
    public GetImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(GetImageAdapter.ViewHolder holder, final int position) {

      String images = mDataset.get(position).get("image");
        Log.e(TAG, "onBindViewHolder: "+images );

        Picasso.with(context)
                .load(images)
                .placeholder(R.drawable.placeholder)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}


