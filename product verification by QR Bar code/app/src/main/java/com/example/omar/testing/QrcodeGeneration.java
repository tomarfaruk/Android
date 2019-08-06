package com.example.omar.testing;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class QrcodeGeneration extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private ProgressDialog dialog;
    DatePickerDialog.OnDateSetListener mdate;

    Button insertData, viewdelailsBtn, startdate, expireddate;
    EditText productname, numberofCode,product_price, product_weight, product_unite;
    TextView companyname, produce_date, end_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_generation);

        //check storage permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }


         companyname = findViewById(R.id.companyname);
         productname = findViewById(R.id.productname);
         numberofCode = findViewById(R.id.numberofcode);
         insertData = findViewById(R.id.codegenerat);
         viewdelailsBtn = findViewById(R.id.gonextpage);
         expireddate = findViewById(R.id.end_dateId);
         startdate = findViewById(R.id.start_dateId);
         product_price = findViewById(R.id.product_price);
         product_weight = findViewById(R.id.product_weight);
         produce_date = findViewById(R.id.produce_date);
         end_date = findViewById(R.id.end_date);
         product_unite = findViewById(R.id.product_unite);

        SharedPreferences sharedPreferences = getSharedPreferences("profile", Context.MODE_PRIVATE);
        companyname.setText(sharedPreferences.getString("companyName", "null"));   // set the company name from user database

         viewdelailsBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                 if (user != null){
                     startActivity(new Intent(getApplicationContext(), DownloadQr.class));
                 }
             }
         });

         startdate.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Calendar calendar = Calendar.getInstance();
                 int year = calendar.get(Calendar.YEAR);
                 int month = calendar.get(Calendar.MONTH);
                 int day = calendar.get(Calendar.DAY_OF_MONTH);

                 DatePickerDialog dialog1 = new DatePickerDialog(
                         QrcodeGeneration.this,
                         android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                         mdate = new DatePickerDialog.OnDateSetListener() {
                             @Override
                             public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                 month+=1;
                                 produce_date.setText(dayOfMonth+"/"+month+"/"+year);
                             }
                         },
                         year,month,day
                 );
                 dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                 dialog1.show();
             }
         });

         //expired date
        expireddate.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Calendar calendar = Calendar.getInstance();
                 int year = calendar.get(Calendar.YEAR);
                 int month = calendar.get(Calendar.MONTH);
                 int day = calendar.get(Calendar.DAY_OF_MONTH);

                 DatePickerDialog dialog2 = new DatePickerDialog(
                         QrcodeGeneration.this,
                         android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                         mdate = new DatePickerDialog.OnDateSetListener() {
                     @Override
                     public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                         month+=1;
                         end_date.setText(dayOfMonth+"/"+month+"/"+year);
                     }
                 }, year,month,day
                 );
                 dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                 dialog2.show();
             }
         });

         insertData.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 String company_name = companyname.getText().toString();
                 String product_name = productname.getText().toString();
                 String price = product_price.getText().toString();
                 String weight = product_weight.getText().toString();
                 String stdate = produce_date.getText().toString();
                 String expiredate = end_date.getText().toString();
                 String productunite = product_unite.getText().toString();

                 if (price.isEmpty() ){
                     product_price.setError("Enter price");
                     product_price.requestFocus();
                     return ;
                 }if (weight.isEmpty() ){
                     product_weight.setError("Enter a weight");
                     product_weight.requestFocus();
                     return ;
                 }if (stdate.isEmpty() ){
                     produce_date.setError("Enter date");
                     produce_date.requestFocus();
                     return ;
                 }if (expiredate.isEmpty() ){
                     end_date.setError("Enter date");
                     end_date.requestFocus();
                     return ;
                 }
                 if (product_name.isEmpty()){
                     productname.setError("Enter a name");
                     productname.requestFocus();
                     return ;
                 }
                 if (productunite.isEmpty()){
                     product_unite.setError("Enter Unit");
                     product_unite.requestFocus();
                     return ;
                 }

                 int number;
                 try {
                     number = Integer.parseInt(numberofCode.getText().toString());
                 }catch (Exception e){
                     number=0;
                 }

                 if (number>10000 || number < 1) {
                     numberofCode.setError("enter 1-10000");
                     numberofCode.requestFocus();
                     return;
                 }

                 weight += productunite;
                 numberofCode.setText("");
                 productname.setText("");
                 end_date.setText("");
                 produce_date.setText("");
                 product_weight.setText("");
                 product_price.setText("");
                 product_unite.setText("");

                 ///inserting data qr code
                 InsertQRCODE(company_name, product_name, number);

                 ///product details
                 InsertDetails(company_name, product_name, price, stdate, expiredate, weight);

                 startActivity(new Intent(getApplicationContext(), DownloadQr.class));
             }
         });

    }

    private void InsertDetails(String company_name, String product_name, String price, String stdate, String expiredate, String weight) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("products/");
        Product data = new Product(company_name, expiredate, price, stdate, weight);
        databaseReference.child(company_name+"_"+product_name).setValue(data);

    }// end of details data insertion



    ///data insersion in firebase
    private void InsertQRCODE(String name, String product, int number) {
        dialog = new ProgressDialog(QrcodeGeneration.this);
        dialog.setTitle("Sending data");
        dialog.setMessage("Please wait some while...");
        dialog.setCancelable(false);
        dialog.show();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("company/"+name+"/"+product);
        int i;
        for (i=0; i < number; i++) {
            dialog.show();
            String id = databaseReference.push().getKey();
            String qrcode = name+"@"+product+"@"+id;
            Task initTask = databaseReference.child(id).setValue(qrcode);
            initTask.addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "QR code generation successful", Toast.LENGTH_SHORT).show();
                }
            });

            initTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
