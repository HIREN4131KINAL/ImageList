package com.example.guest999.imagelist;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    public static final int RESULT_LOAD_FILE = 1;
    public static final int RESULT_LOAD_IMAGE = 0;
    public static final int RESULT_OK = -1;
    public static final int REQUEST_IMAGE_CAPTURE = 2;
    public static ArrayList<HashMap<String, String>> hello = null;
    GetImageAdapter objImageAdapter;
    FileAdapter fileAdapter;
    String TAG = getClass().getName();
    JSONParser jsonParser = new JSONParser();
    Bitmap selectedImage;
    String fileName;
    String json;
    /*private String SERVER_URL = "http://www.laxmisecurity.com/android/UploadToServer.php";*/
    String UPLOAD_URL = "http://10.0.2.2/filesharing/viewimage.php";
    private Dialog dialog;
    private RecyclerView recyclerView;
    private int columnIndex;
    private String picturePath;
    private String selectedFilePath;

    static String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        new LoadData().execute();


        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            }
        }
    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            //Toast.makeText(this, "Plz allow permision for sending file and photos", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.GET_ACCOUNTS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.image:
                itemAddClick();
                return true;
            case R.id.file:
                FilePick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void FilePick() {
      /*  Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);*/
        //intent.setType("*/*");
       /* startActivityForResult(intent, RESULT_LOAD_FILE);*/

        Intent intent = new Intent();
        //sets the select file to all types of files
        intent.setType("*/*");
        //allows to select data and return it
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //starts new activity to select file and return data
        startActivityForResult(Intent.createChooser(intent, "Choose File to Upload.."), RESULT_LOAD_FILE);
    }

    public void itemAddClick() {
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.custom_dialog_box);
        dialog.setTitle("Select Photo");

        Button btnExit = (Button) dialog.findViewById(R.id.btnExit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.btnChoosePath).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeGallery();
            }
        });
        dialog.findViewById(R.id.btnTakePhoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //activeTakePhoto();
            }
        });

        // show dialog on screen
        dialog.show();
    }

    private void activeGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            }
        }
        dialog.dismiss();
    }

    //this method is for resize image
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case RESULT_LOAD_IMAGE:
                if (resultCode == RESULT_OK && null != data) {

                    Uri filePath = data.getData();
                    selectedFilePath = FilePath.getPath(this, filePath);
                    Log.e(TAG, "filePath: " + selectedFilePath);

                    /* InputStream imageStream = this.getContentResolver().openInputStream(filePath);
                     selectedImage = BitmapFactory.decodeStream(imageStream);
                     ByteArrayOutputStream stream = new ByteArrayOutputStream();
                     selectedImage = getResizedBitmap(selectedImage, 400);
                     selectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);*/
                    //ImageAdapter.UploadImage imageAdapter=new ImageAdapter.UploadImage();
                       /* UploadImage ui=new UploadImage();
                        ui.execute(selectedImage);*/

                    //on upload button Click
                    if (selectedFilePath != null) {
                        dialog = ProgressDialog.show(MainActivity.this, "", "Sending File ...", true);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //creating new thread to handle Http Operations
                                uploadFile(selectedFilePath);
                                new UploadImage().execute(selectedFilePath);
                            }
                        }).start();

                    } else {
                        Toast.makeText(MainActivity.this, "Please choose a File First", Toast.LENGTH_SHORT).show();
                    }


                       /* byte[] byteArray = stream.toByteArray();
                        hello.add(Base64.encodeToString(byteArray, Base64.DEFAULT));
*/
                   /* recyclerView.setHasFixedSize(true);
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                       *//* mLayoutManager.setReverseLayout(true);*//*
                    mLayoutManager.setStackFromEnd(true);
                    recyclerView.setLayoutManager(mLayoutManager);

                    objImageAdapter = new GetImageAdapter(getApplicationContext(), hello);*/
                    //Log.e(TAG, "onActivityResult: " + hello + "");

                    //  linearLayout.setVisibility(View.GONE);
                    //frameLayout.setVisibility(View.VISIBLE);

                }

/*
            case RESULT_LOAD_FILE:
                if (requestCode == RESULT_LOAD_FILE &&
                        resultCode == RESULT_OK && null != data) {
                    String FilePath = data.getData().getPath();
                    Log.e("FILEPATH", FilePath);
                    String filename = FilePath.substring(FilePath.lastIndexOf("/") + 1);
                    Log.e("onActivityResult: ", filename);

                  *//*  ArrayList<String> filearray = new ArrayList<>();

                    filearray.add(filename);
                    Log.e("Adapter", filearray + "");*//*

                    hello.add(filename);

                    recyclerView.setHasFixedSize(true);
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);

                    objImageAdapter = new ImageAdapter(getApplicationContext(), hello);

                    // linearLayout.setVisibility(View.VISIBLE);
                    //  frameLayout.setVisibility(View.GONE);
                    *//*textView.setText(filename);*//*
                }
                recyclerView.setAdapter(objImageAdapter);*/
        }
    }

    //android upload file to server
    public int uploadFile(final String selectedFilePath) {

        int serverResponseCode = 0;

        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 1024;
        File selectedFile = new File(selectedFilePath);


        String[] parts = selectedFilePath.split("/");
        fileName = parts[parts.length - 1];

        if (!selectedFile.isFile()) {
            dialog.dismiss();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //tvFileName.setText("Source File Doesn't Exist: " + selectedFilePath);
                }
            });
            return 0;
        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                String SERVER_URL = "http://10.0.2.2/filesharing/UploadToServer.php";
                URL url = new URL(SERVER_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCRYPT", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("uploaded_file", selectedFilePath);

                //creating new dataoutputstream
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                //writing bytes to data outputstream
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + selectedFilePath + "\"" + lineEnd);

                dataOutputStream.writeBytes(lineEnd);

                //returns no. of bytes present in fileInputStream
                bytesAvailable = fileInputStream.available();
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];

                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0) {
                    //write the bytes read from inputstream
                    dataOutputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();

                Log.e(TAG, "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);
                //response code of 200 indicates the server status OK
                if (serverResponseCode == 200) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, fileName + "Image Sent Sucessfully ", Toast.LENGTH_SHORT).show();
                            //  tvFileName.setText("File Upload completed.\n\n You can see the uploaded file here: \n\n" + "http://www.laxmisecurity.com/android/uploads/" + fileName);
                        }
                    });
                }
                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "File Not Found", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "URL error!", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Cannot Read/Write File!", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
            return serverResponseCode;
        }

    }

    public class UploadImage extends AsyncTask<String, Void, String> {
        static final String UPLOAD_URL = "http://10.0.2.2/filesharing/upload.php";
        static final String UPLOAD_KEY = "image";

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
            new LoadData().execute();
            // loading.dismiss();
//        Toast.makeText(context,s,Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> data = new HashMap<>();
            data.put(UPLOAD_KEY, "http://localhost/filesharing/uploads/" + fileName);
            Log.e("HashMap data: ", data + "");
            String result = jsonParser.sendPostRequest(UPLOAD_URL, data);
            Log.e("result: ", result);
            return result;
        }
    }

    public class LoadData extends AsyncTask<String, String, String> {
        static final String TAG_image = "image";
        String VIEW_URL = "http://10.0.2.2/filesharing/viewimage.php";
        //static final String UPLOAD_URL = "http://10.0.2.2/filesharing/viewimage.php";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        /*loading =new ProgressDialog(context);
        loading.setMessage("Uploading...");
        loading.show();
*/
            Log.e(TAG, "HHHonPreExecute: ");
        }

        @Override
        protected String doInBackground(String... params) {
            json = jsonParser.makeServiceCall(VIEW_URL);
            Log.e(TAG, "doInBackground: ");
            Log.e("LOG JSON", json);
            return null;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            hello = new ArrayList<HashMap<String, String>>();
            JSONObject abc;
            Log.e("LOG JSON", json);
            if (json != null) {
                try {
                    abc = new JSONObject(json);
                    // looping through All b_name
                    JSONArray jsonArray = abc.getJSONArray("category");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject c = jsonArray.getJSONObject(i);

                        // Storing each json item values in variable
                        String br_image = c.getString(TAG_image);
                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_image, br_image);

                        // adding HashList to ArrayList
                        hello.add(map);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            recyclerView.setHasFixedSize(true);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(new GetImageAdapter(MainActivity.this, hello));

        }
    }
}

