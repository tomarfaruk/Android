package com.example.omar.testing;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {
    private EditText companyName, companyEmail, password, rePassword;
    private Button signUp;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        progressBar = findViewById(R.id.progressbar);

        companyName = findViewById(R.id.company_name);
        companyEmail = findViewById(R.id.company_email);
        password = findViewById(R.id.company_password);
        rePassword = findViewById(R.id.company_password1);
        signUp = findViewById(R.id.signup);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        auth = FirebaseAuth.getInstance();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = companyName.getText().toString();
                final String email = companyEmail.getText().toString();
                String pass = password.getText().toString();
                String pass1 = rePassword.getText().toString();

                if (name.isEmpty()){
                    companyName.setError("can't empty");
                    companyName.requestFocus();
                    return;
                }if (email.isEmpty()){
                    companyEmail.setError("can't empty");
                    companyEmail.requestFocus();
                    return;
                }if (pass.isEmpty() || pass.length()<6){
                    password.setError("can't empty less then 6 character");
                    password.requestFocus();
                    return;
                }if (pass1.isEmpty() || pass.length()<6){
                    rePassword.setError("can't empty less then 6 character");
                    rePassword.requestFocus();
                    return;
                }
                if (!pass.equals(pass1)){
                    password.setError("Password doesn't match");
                    password.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                auth.createUserWithEmailAndPassword(email, pass1).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String id = auth.getCurrentUser().getUid();
                            databaseReference = databaseReference.child(id);
                            databaseReference.child("companyName").setValue(name);
                            databaseReference.child("companyEmail").setValue(email);
                            progressBar.setVisibility(View.GONE);

                            //set data to shared prefarences
                            SharedPreferences sharedPreferences = getSharedPreferences("profile", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("companyName", name);
                            editor.commit();

                            startActivity(new Intent(SignUp.this, DownloadQr.class));
                            finish();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Sign up failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
