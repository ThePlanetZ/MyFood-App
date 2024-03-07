package com.example.myfood;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ChooseOne extends AppCompatActivity {
    Button Chef, Customer, DeliveryPerson;
    Intent intent;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_one);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        intent = getIntent();
        type = intent.getStringExtra("Home");

        Chef = findViewById(R.id.chef);
        DeliveryPerson = findViewById(R.id.delivery);
        Customer = findViewById(R.id.customer);

        Chef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type != null) {
                    if (type.equals("Email")) {
                        Intent loginemail = new Intent(ChooseOne.this, Cheflogin.class);
                        startActivity(loginemail);
                        finish();
                    } else if (type.equals("Phone")) {
                        Intent loginphone = new Intent(ChooseOne.this, Chefloginphone.class);
                        startActivity(loginphone);
                        finish();
                    } else if (type.equals("SignUp")) {
                        Intent Register = new Intent(ChooseOne.this, ChefRegistration.class);
                        startActivity(Register);
                    }
                }
            }
        });

        Customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type != null) {
                    if (type.equals("Email")) {
                        Intent loginemailcust = new Intent(ChooseOne.this, Login.class);
                        startActivity(loginemailcust);
                        finish();
                    } else if (type.equals("Phone")) {
                        Intent loginphonecust = new Intent(ChooseOne.this, Loginphone.class);
                        startActivity(loginphonecust);
                        finish();
                    } else if (type.equals("SignUp")) {
                        Intent Registercust = new Intent(ChooseOne.this, Registration.class);
                        startActivity(Registercust);
                    }
                }
            }
        });

        DeliveryPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type != null) {
                    if (type.equals("Email")) {
                        Intent loginemail = new Intent(ChooseOne.this, Delivery_Login.class);
                        startActivity(loginemail);
                        finish();
                    } else if (type.equals("Phone")) {
                        Intent loginphone = new Intent(ChooseOne.this, Delivery_Loginphone.class);
                        startActivity(loginphone);
                        finish();
                    } else if (type.equals("SignUp")) {
                        Intent Registerdelivery = new Intent(ChooseOne.this, Delivery_Registration.class);
                        startActivity(Registerdelivery);
                    }
                }
            }
        });
    }
}
