package com.example.omar.testing;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.Calendar;

public class CustomerUsers extends AppCompatActivity {
    EditText namefield, phonefield;
    TextView agefield;
    Button Save;
    DatePickerDialog.OnDateSetListener mdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_users);


        //Ask user for premission storage, camera etc.
        new RxPermissions(this)
        .request(Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) // ask single or multiple permission once
        .subscribe(granted -> {
            if (granted) {
                Log.v("Permission is: ", "granted");

            } else {
                Log.v("Permission is: ", "not granted");
                return;
            }
        });

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
//            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 1);
//        }
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
//            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
//        }
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
//            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
//        }




        namefield = findViewById(R.id.customer_name);
        phonefield = findViewById(R.id.customer_phone);
        agefield = findViewById(R.id.customer_Age);
        Save = findViewById(R.id.save);

        SharedPreferences sharedPreferences = getSharedPreferences("customer", Context.MODE_PRIVATE);
        if (!sharedPreferences.getString("db_key", "").isEmpty()){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        agefield.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog2 = new DatePickerDialog(
                        CustomerUsers.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mdate = new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                month+=1;
                                agefield.setText(dayOfMonth+"/"+month+"/"+year);
                            }
                        }, year,month,day
                );
                dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog2.show();
            }
        });

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = namefield.getText().toString();
                String phone = phonefield.getText().toString();
                if (name.isEmpty() ){
                    namefield.setError("Enter your name");
                    namefield.requestFocus();
                    return ;
                }
                if (phone.isEmpty() && phone.length() > 7 ){
                    phonefield.setError("Phone number must more then 7 char");
                    phonefield.requestFocus();
                    return ;
                }

                String age = agefield.getText().toString();
                if (age.isEmpty() ){
                    agefield.setError("Select date of birth");
                    agefield.requestFocus();
                    return ;
                }


                CustomerData data = new CustomerData(name, phone, age);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Customers");
                DatabaseReference dbref = databaseReference.push();
                String key = dbref.getKey();
                databaseReference.child(key).setValue(data);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("db_key", key);
                editor.commit();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }
}
