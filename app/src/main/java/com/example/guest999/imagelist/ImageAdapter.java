package com.example.guest999.imagelist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.guest999.imagelist.MainActivity.LoadData.TAG_image;
import static com.example.guest999.imagelist.MainActivity.RESULT_LOAD_IMAGE;


/**
 * Created by Guest999 on 10/3/2016.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    int response;
    private Context context;
    private JSONParser jsonParser = new JSONParser();
    private ArrayList<HashMap<String, String>>  albumlist;


    public ImageAdapter(Context context, ArrayList<HashMap<String, String>> hello) {
        this.albumlist = hello;
        this.context = context;
    }

  /*  public int getItemViewType(int position) {
        return position;

        *//*if (position==0) return RESULT_CODE;
        else return RESULT_LOAD_IMAGE;*//*
    }*/

    @Override
    public int getItemViewType(int position)
    {

        return RESULT_LOAD_IMAGE;

       /* if (position == albumlist.lastIndexOf(albumlist.get(position))) {
            return RESULT_LOAD_IMAGE;
        } else {
            return RESULT_LOAD_FILE;
        }*/
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*View v1, v2;
        Log.e("VIEWTYPE", viewType + "");

        ImagePick ip = null;
        FilePick fp = null;
        if (viewType == RESULT_LOAD_IMAGE) {
            v1 = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.raw, parent, false);
            ip = new ImagePick(v1);
            return ip;
        } else {
            v2 = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.file_raw, parent, false);
            fp = new FilePick(v2);
            return fp;
        }*/

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final String Image_name = albumlist.get(position).get(TAG_image);
        ImagePick image = (ImagePick) holder;
        Picasso.with(context)
                .load(Image_name)
                .into(image.score);

        /*switch (holder.getItemViewType()) {
            case RESULT_LOAD_IMAGE:
                ImagePick image = (ImagePick) holder;
                byte[] decodeValue = new byte[0];
                try {
                    decodeValue = Base64.decode(albumlist.get(position).getBytes("UTF-8"), Base64.DEFAULT);
                    Log.e("4", Arrays.toString(decodeValue) + "");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodeValue, 0, decodeValue.length);
                image.score.setImageBitmap(bitmap);
                break;

            case RESULT_CODE:
                FilePick file = (FilePick) holder;
                file.temp.setText(albumlist.get(position));
                break;

        }*/

        /*if (holder.getItemViewType() == RESULT_LOAD_FILE) {
            FilePick file = (FilePick) holder;
            file.temp.setText(albumlist.get(position).get(TAG_image));

        } else if (holder.getItemViewType() == RESULT_LOAD_IMAGE) {
            ImagePick image = (ImagePick) holder;
            byte[] decodeValue = new byte[0];
            try {
                decodeValue = Base64.decode(albumlist.get(position).get(TAG_image).getBytes("UTF-8"), Base64.DEFAULT);
                Log.e("4", decodeValue + "");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodeValue, 0, decodeValue.length);
            image.score.setImageBitmap(bitmap);


               // new UploadImage().execute(last);
        }*/
    }

    /*@Override
    public int getItemViewType(int position)
    {
        return 1;
        //return Math.random()<0 ? RESULT_LOAD_IMAGE : RESULT_CODE;
        //  return position == 0? RESULT_CODE : RESULT_LOAD_IMAGE;
        //  return response % 2 == 0 ? RESULT_LOAD_IMAGE : RESULT_CODE;
    }*/

    public int getViewTypeCount() {
        return 2;
    }


    @Override
    public int getItemCount() {

        return albumlist.size();

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class FilePick extends ViewHolder {
        TextView temp;

        FilePick(View v) {
            super(v);
            this.temp = (TextView) v.findViewById(R.id.file_name);
        }
    }

    private class ImagePick extends ViewHolder {
        ImageView score;

        ImagePick(View v) {
            super(v);
            this.score = (ImageView) v.findViewById(R.id.image_list);
        }
    }


    /*public class UploadImage extends AsyncTask<Bitmap, Void, String> {
        static final String UPLOAD_URL = "http://10.0.2.2/filesharing/upload.php";
        static final String UPLOAD_KEY = "image";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           *//* loading = new ProgressDialog(context);
            loading.setMessage("Uploading...");
            loading.show();*//*
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(context, "error", Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(Bitmap... params) {
            Bitmap bitmap = params[0];
            String uploadImage = MainActivity.getStringImage(bitmap);
            Log.e( "uploadImage: ", uploadImage);

            HashMap<String, String> data = new HashMap<>();

            data.put(UPLOAD_KEY, uploadImage);
            Log.e("HashMap data: ",data+"");
            String result = jsonParser.sendPostRequest(UPLOAD_URL, data);
            Log.e("result: ",result);

            return result;
        }
    }*/
}
