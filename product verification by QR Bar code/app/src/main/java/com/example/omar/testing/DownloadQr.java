package com.example.omar.testing;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;


public class DownloadQr extends AppCompatActivity {

    TextView viewText;
    ListView listView;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_qr);
        viewText = findViewById(R.id.text);
        listView = findViewById(R.id.list_item);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        database = FirebaseDatabase.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences("profile", Context.MODE_PRIVATE);
        ref = database.getReference("company/"+sharedPreferences.getString("companyName", ""));


        final ArrayList<String> list_item = new ArrayList<>();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, list_item);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DownloadQr.this);
                builder.setTitle("Choose option for "+list_item.get(i));
                builder.setMessage("Press Download to store QR image to your device or Press Delete to delete product codes");
                builder.setCancelable(true);
                builder.setPositiveButton("Download", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GenerateQrImageAndDownload(ref, list_item.get(i)); ///generate qr and save to device
                        Toast.makeText(getApplicationContext(), "finish download", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ref.child(list_item.get(i)).removeValue(); // delete the selected product
                        Toast.makeText(getApplicationContext(), "You deleted "+list_item.get(i), Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });


        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list_item.clear();
                for (DataSnapshot d: dataSnapshot.getChildren()){
                    list_item.add(d.getKey());
                    Log.e("testing", d.getKey().toString());
                }
                listView.setAdapter(arrayAdapter);
                progressBar.setVisibility(View.GONE);
//                Toast.makeText(getApplicationContext(), "yess", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Can't load", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //start qr creation process...
    public void GenerateQrImageAndDownload(DatabaseReference demoRef, final String child){
        demoRef = demoRef.child(child);
        progressBar.setVisibility(View.VISIBLE);
        demoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("mcc", "----------------");
                for (DataSnapshot d: dataSnapshot.getChildren()){
                    String code = d.getValue(String.class);
                    Log.e("mcc", code);
                    Bitmap bm = null;
                    try {
                        bm = encodeAsBitmap(code, BarcodeFormat.QR_CODE, 150,  150);
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                    if(bm != null) {
                        storeImage(bm, child, code);  //code = image name, child= folder name
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    //Image save
    private void storeImage(Bitmap image, String path, String code) {
        File pictureFile = getOutputMediaFile(path, code);
        if (pictureFile == null) {
            Log.d("mcc","Error creating media file, check storage permissions: ");// e.getMessage());
            Toast.makeText(getApplicationContext(), "Error creating media file, check storage permissions: ", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.flush();
            fos.close();
            Toast.makeText(getApplicationContext(), "Complete save Image", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Log.d("mcc", "File not found: " + e.getMessage());
            Toast.makeText(getApplicationContext(), "File not found: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Error accessing file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("mcc", "Error accessing file: " + e.getMessage());
        }
    }

    /** Create a File for saving an image or video */
    private  File getOutputMediaFile(String path, String file_name){
        /* To be safe, you should check that the SDCard is mounted using Environment.getExternalStorageState() before doing this.*/
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/"+path);
        /*This location works best if you want the created images to be shared between applications and persist after your app has been uninstalled.Create the storage directory if it does not exist*/
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        File mediaFile;
        String mImageName="QR_"+ file_name +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }


    /* text to image convert */
    Bitmap encodeAsBitmap(String str, BarcodeFormat qrCode, int i, int i1) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, i, i1, null);
        } catch (IllegalArgumentException iae) {
            return  null; // Unsupported format
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
        return bitmap;
    }

    @Override
    public void onBackPressed() {
        progressBar.setVisibility(View.GONE);
        super.onBackPressed();
    }

}
