package com.example.omar.testing;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

//import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tbruyelle.rxpermissions2.RxPermissions;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    protected static TextView textView;
    Button Scanbtn;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        Scanbtn = findViewById(R.id.btn);
        textView = findViewById(R.id.textview);

        //checking is customer full fill form
        SharedPreferences sharedPreferences = getSharedPreferences("customer", Context.MODE_PRIVATE);
        if (sharedPreferences.getString("db_key", "").isEmpty()){
            startActivity(new Intent(getApplicationContext(), CustomerUsers.class));
        }
//        textView.setText(sharedPreferences.getString("db_key", ""));
        auth = FirebaseAuth.getInstance();

        Scanbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Scanner.class));
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem signinitem = menu.findItem(R.id.signin);
        MenuItem singoutitem = menu.findItem(R.id.logout);
        MenuItem profileitem = menu.findItem(R.id.profile);
        MenuItem signupitem = menu.findItem(R.id.sign_up);
        MenuItem qrgenerateitem = menu.findItem(R.id.qrgenerate);

        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();

        if (auth != null){
            signinitem.setVisible(false);
            profileitem.setVisible(true);
            signupitem.setVisible(false);
            singoutitem.setVisible(true);
            qrgenerateitem.setVisible(true);
            SharedPreferences sharedPreferences = getSharedPreferences("profile", Context.MODE_PRIVATE);
            profileitem.setTitle(sharedPreferences.getString("companyName", "Profile"));

            //user account update
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(userId).exists()){
                            String companyName = dataSnapshot.child(userId).child("companyName").getValue(String.class);
                            SharedPreferences sharedPreferences = getSharedPreferences("profile", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("companyName", companyName);
                            editor.commit();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        }else{
            signinitem.setVisible(true);
            singoutitem.setVisible(false);
            profileitem.setVisible(false);
            signupitem.setVisible(true);
            qrgenerateitem.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.signin:
                startActivity(new Intent(getApplicationContext(), Login.class));
                break;

            case R.id.logout:
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    auth.signOut();
                    SharedPreferences sharedPreferences = getSharedPreferences("profile", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("companyName", null);
                    editor.commit();
//                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
                break;

            case R.id.sign_up:
                startActivity(new Intent(getApplicationContext(), SignUp.class));
                break;

            case R.id.profile:
                startActivity(new Intent(getApplicationContext(), DownloadQr.class));
                break;
            case R.id.qrgenerate:
                startActivity(new Intent(getApplicationContext(), QrcodeGeneration.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
