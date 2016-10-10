package com.example.guest999.imagelist;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by Harshad on 06-10-2016.*/



public class UploadImage extends AsyncTask<Bitmap,Void,String>
  {
    Context context;
    JSONParser jsonParser=new JSONParser();

    public static final String UPLOAD_URL = "http://10.0.2.2/filesharing/upload.php";
    public static final String UPLOAD_KEY = "image";

    public UploadImage() {

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
  /*loading =new ProgressDialog(context);
        loading.setMessage("Uploading...");
        loading.show();
*/
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
       // loading.dismiss();
//        Toast.makeText(context,s,Toast.LENGTH_LONG).show();
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
}
