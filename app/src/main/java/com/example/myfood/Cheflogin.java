package com.example.myfood;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class Cheflogin extends AppCompatActivity {

    TextInputLayout email,pass;
    Button Signin,Signinphone;
    TextView Forgotpassword , signup;
    FirebaseAuth Fauth;
    String emailid,pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheflogin);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        try{

            email = (TextInputLayout)findViewById(R.id.Lemail);
            pass = (TextInputLayout)findViewById(R.id.Lpassword);
            Signin = (Button)findViewById(R.id.button4);
            signup = (TextView) findViewById(R.id.textView3);
            Forgotpassword = (TextView)findViewById(R.id.forgotpass);
            Signinphone = (Button)findViewById(R.id.btnphone);

            Fauth = FirebaseAuth.getInstance();

            Signin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    emailid = email.getEditText().getText().toString().trim();
                    pwd = pass.getEditText().getText().toString().trim();

                    if(isValid()){

                        final ProgressDialog mDialog = new ProgressDialog(Cheflogin.this);
                        mDialog.setCanceledOnTouchOutside(false);
                        mDialog.setCancelable(false);
                        mDialog.setMessage("Sign In Please Wait.......");
                        mDialog.show();

                        Fauth.signInWithEmailAndPassword(emailid,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    mDialog.dismiss();

                                    if (Fauth.getCurrentUser().isEmailVerified()) {
                                        retrieveUserRole();

                                    } else {
                                        ReusableCodeForAll.ShowAlert(Cheflogin.this, "Verification Failed", "You Have Not Verified Your Email");
                                    }
                                } else {
                                    mDialog.dismiss();
                                    ReusableCodeForAll.ShowAlert(Cheflogin.this, "Error", task.getException().getMessage());
                                }
                            }
                        });
                    }
                }
            });
            signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Cheflogin.this,ChefRegistration.class));
                    finish();
                }
            });

            Signinphone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Cheflogin.this,Chefloginphone.class));
                    finish();
                }
            });
        }catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

    private void retrieveUserRole() {
        DatabaseReference userRoleRef = FirebaseDatabase.getInstance().getReference("User")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userRoleRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String role = snapshot.child("Role").getValue(String.class);

                    if ("Chef".equals(role)) {
                        // Fetch the FCM token
                        FirebaseMessaging.getInstance().getToken()
                                .addOnCompleteListener(new OnCompleteListener<String>() {
                                    @Override
                                    public void onComplete(@NonNull Task<String> task) {
                                        if (task.isSuccessful() && task.getResult() != null) {
                                            String fcmToken = task.getResult();
                                            // Store the FCM token under the user node
                                            userRoleRef.child("token").setValue(fcmToken)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> tokenTask) {
                                                            if (tokenTask.isSuccessful()) {
                                                                Log.d("FCM Token", "FCM token stored successfully: " + fcmToken);
                                                                // Proceed to the Chef Food Panel activity
                                                                Toast.makeText(Cheflogin.this, "Congratulation! You Have Successfully Logged In", Toast.LENGTH_SHORT).show();
                                                                Intent Z = new Intent(Cheflogin.this, ChefFoodPanel_BottomNavigation.class);
                                                                startActivity(Z);
                                                                finish(); // Finish the current login activity
                                                            } else {
                                                                // Handle token storage failure
                                                                Log.e("FCM Token", "Failed to store FCM token: " + tokenTask.getException());
                                                                Toast.makeText(Cheflogin.this, "Error: Failed to store FCM token", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        } else {
                                            // Handle error fetching FCM token
                                            Log.e("FCM Token", "Failed to fetch FCM token: " + task.getException());
                                            Toast.makeText(Cheflogin.this, "Error: Failed to fetch FCM token", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else if ("Customer".equals(role) || "DeliveryPerson".equals(role)) {
                        // Handle invalid user role
                        Toast.makeText(Cheflogin.this, "Invalid user role", Toast.LENGTH_LONG).show();
                        FirebaseAuth.getInstance().signOut(); // Sign out the user explicitly
                        startActivity(new Intent(Cheflogin.this, MainMenu.class));
                        finish(); // Finish the current login activity
                    } else {
                        // Handle unknown user role
                        Toast.makeText(Cheflogin.this, "Error: Unknown user role", Toast.LENGTH_LONG).show();
                        FirebaseAuth.getInstance().signOut(); // Sign out the user explicitly
                    }
                } else {
                    // Handle user data not found
                    Toast.makeText(Cheflogin.this, "Error: User data not found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error if needed
            }
        });
    }



    String emailpattern  = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    public boolean isValid(){

        email.setErrorEnabled(false);
        email.setError("");
        pass.setErrorEnabled(false);
        pass.setError("");

        boolean isvalid=false,isvalidemail=false,isvalidpassword=false;
        if(TextUtils.isEmpty(emailid)){
            email.setErrorEnabled(true);
            email.setError("Email is required");
        }else{
            if(emailid.matches(emailpattern)){
                isvalidemail=true;
            }else{
                email.setErrorEnabled(true);
                email.setError("Invalid Email Address");
            }
        }
        if(TextUtils.isEmpty(pwd)){

            pass.setErrorEnabled(true);
            pass.setError("Password is Required");
        }else{
            isvalidpassword=true;
        }
        isvalid=(isvalidemail && isvalidpassword)?true:false;
        return isvalid;
    }
}