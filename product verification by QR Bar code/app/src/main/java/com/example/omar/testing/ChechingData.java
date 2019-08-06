package com.example.omar.testing;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChechingData extends AppCompatActivity {
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheching_data);

        progressBar = findViewById(R.id.progressbar);


        String s = MainActivity.textView.getText().toString();
        CheckDatabase(s);
    }

    public void CheckDatabase(String text) {
        String[] s = text.split("@");
        if (s.length == 3) {
            Log.e("mcc", s[0] + s[1] + s[2]);

            progressBar.setVisibility(View.VISIBLE);
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("company");
            rootRef = rootRef.child(s[0] + "/" + s[1]);
            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    progressBar.setVisibility(View.GONE);
                    if (dataSnapshot.hasChild(s[2])) {
                        MainActivity.textView.setText(s[0]+"_"+s[1]);
                        startActivity(new Intent(ChechingData.this, foundresult.class));
                        finish();
                    }
                    else show(false);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    progressBar.setVisibility(View.GONE);
                    show(false);
                }
            });
        }
        else show(false);
    }

    public void show(boolean b){
        if (b) {
            MainActivity.textView.setText("Thank You");
            startActivity(new Intent(getApplicationContext(), foundresult.class));
            finish();
        }
        else {
            MainActivity.textView.setText("Thank You");
            startActivity(new Intent(getApplicationContext(), notfound.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        progressBar.setVisibility(View.GONE);
        super.onBackPressed();
    }
}
