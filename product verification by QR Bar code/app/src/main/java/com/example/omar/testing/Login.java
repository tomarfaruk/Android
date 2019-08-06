package com.example.omar.testing;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class Login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    ProgressBar progressBar;
    public Button button, signup1;
    public EditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progressbar);
        button = findViewById(R.id.signin);
        signup1 = findViewById(R.id.signup);
        email = findViewById(R.id.email);
        password = findViewById(R.id.pasword);

        CheckUserSignIn();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserSignInFunction();
            }
        });

        signup1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUp.class));
            }
        });
    }

    public  void UserSignInFunction(){
        String email1 = email.getText().toString().trim();
        String password1 = password.getText().toString().trim();

        if (email1.isEmpty()){
            email.setError("can't empty");
            email.requestFocus();
            return;
        }
        if (password1.isEmpty()){
            password.setError("can't empty or less then 6 len");
            password.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email1, password1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()){
                    CheckUserSignIn();
                    email.setText("");
                    password.setText("");
                    Toast.makeText(getApplicationContext(), "Login Successfull ", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Enter valide email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void CheckUserSignIn(){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            CompanyNameSet();

        }
    }

    public void CompanyNameSet(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (user!=null){

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(userId).exists()){
                        String companyName = dataSnapshot.child(userId).child("companyName").getValue(String.class);
                        // set data to shared prefarenses
                        SharedPreferences sharedPreferences = getSharedPreferences("profile", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("companyName", companyName);
                        editor.commit();

                        startActivity(new Intent(Login.this, DownloadQr.class));
                        finish();

                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        progressBar.setVisibility(View.GONE);
        super.onBackPressed();
    }
}
