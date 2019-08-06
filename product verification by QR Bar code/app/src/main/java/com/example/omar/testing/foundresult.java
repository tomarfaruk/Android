package com.example.omar.testing;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class foundresult extends AppCompatActivity {

    private TextView product_price, produce_date, end_date, company_name, product_weight, product_name;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foundresult);

        product_name = findViewById(R.id.product_name);
        company_name = findViewById(R.id.company_name);
        product_price = findViewById(R.id.product_price);
        product_weight = findViewById(R.id.product_weight);
        produce_date = findViewById(R.id.produce_date);
        end_date = findViewById(R.id.end_date);

        String key = MainActivity.textView.getText().toString();
        try {
            String comp_name = key.split("_")[0];
            String prod_name = key.split("_")[1];

        MainActivity.textView.setText("Thank You");
        product_name.setText("Product name: "+prod_name);

        databaseReference = FirebaseDatabase.getInstance().getReference("products/"+key);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("mcc", "coming");
                Viewdata(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "some error occurs", Toast.LENGTH_SHORT).show();
            }
        });

    }catch (Exception e){
            end_date.setText("Details not found");
            product_name.setVisibility(View.GONE);
            product_price.setVisibility(View.GONE);
            product_weight.setVisibility(View.GONE);
            produce_date.setVisibility(View.GONE);
        }
    }

    public void Viewdata(DataSnapshot dataSnapshot){
        Log.e("mcc", "end");
       try {
           Product product = dataSnapshot.getValue(Product.class);
           company_name.setText("Company Name: "+product.getCompany_name());
           product_price.setText("Price: "+product.getPrice());
           product_weight.setText("Weight: "+product.getWeight());
           produce_date.setText("Production Date: "+product.getProduce_date());
           end_date.setText("Expired Date: "+product.getEnddata());
       }catch (Exception e){
           end_date.setText("Details not found");
           product_name.setVisibility(View.GONE);
           product_price.setVisibility(View.GONE);
           product_weight.setVisibility(View.GONE);
           produce_date.setVisibility(View.GONE);
       }
    }

}
