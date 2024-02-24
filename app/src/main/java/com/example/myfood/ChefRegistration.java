package com.example.myfood;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;

public class ChefRegistration extends AppCompatActivity {
    TextInputLayout Fname,Lname,Email,Pass,cpass,mobileno,houseno,area,pincode;
    Spinner Cityspin;
    Button signup, Emaill,Phone;
    CountryCodePicker Cpp;
    FirebaseAuth FAuth;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    String fname,lname,emailid,password,confpassword,mobile,house,Area,Pincode,role="Chef",City1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_registration);
        Fname= (TextInputLayout) findViewById(R.id.Firstname);
        Lname= (TextInputLayout) findViewById(R.id.Lastname);
        Email= (TextInputLayout) findViewById(R.id.Email);
        Pass= (TextInputLayout) findViewById(R.id.Pwd);
        cpass= (TextInputLayout) findViewById(R.id.Cpass);
        mobileno= (TextInputLayout) findViewById(R.id.Mobileno);
        houseno= (TextInputLayout) findViewById(R.id.houseNo);
        pincode= (TextInputLayout) findViewById(R.id.Pincode);
        Cityspin= (Spinner) findViewById(R.id.City1);
        area =(TextInputLayout) findViewById(R.id.Area);

        signup=(Button)findViewById(R.id.Signup);
        Emaill=(Button)findViewById(R.id.email);
        Phone=(Button)findViewById(R.id.phone);

        Cpp=(CountryCodePicker)findViewById(R.id.countrycode);

        Cityspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object value = parent.getItemAtPosition(position);
                City1= value.toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        databaseReference = firebaseDatabase.getInstance().getReference("Chef");
        FAuth= FirebaseAuth.getInstance();
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fname= Fname.getEditText().getText().toString().trim();
                lname= Lname.getEditText().getText().toString().trim();
                emailid= Email.getEditText().getText().toString().trim();
                mobile= mobileno.getEditText().getText().toString().trim();
                password= Pass.getEditText().getText().toString().trim();
                confpassword= cpass.getEditText().getText().toString().trim();
                Area= area.getEditText().getText().toString().trim();
                house= houseno.getEditText().getText().toString().trim();
                Pincode= pincode.getEditText().getText().toString().trim();

                if (isValid()){
                    final ProgressDialog mDialog = new ProgressDialog(ChefRegistration.this);
                    mDialog.setCancelable(false);
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.setMessage("Registration in progress, please wait....");
                    mDialog.show();

                    FAuth.createUserWithEmailAndPassword(emailid,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                String useridd= FirebaseAuth.getInstance().getCurrentUser().getUid();
                                databaseReference = FirebaseDatabase.getInstance().getReference("User").child(useridd);
                                final HashMap<String , String> hashMap = new HashMap<>();
                                hashMap.put("Role",role);
                                databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        HashMap<String , String> hashMap1 = new HashMap<>();
                                        hashMap1.put("Mobile No",mobile);
                                        hashMap1.put("First Name",fname);
                                        hashMap1.put("Last Name",lname);
                                        hashMap1.put("Email id",emailid);
                                        hashMap1.put("City",City1);
                                        hashMap1.put("Area",Area);
                                        hashMap1.put("Password",password);
                                        hashMap1.put("Pincode",Pincode);
                                        hashMap1.put("Confirm Password",confpassword);
                                        hashMap1.put("House",house);

                                        firebaseDatabase.getInstance().getReference("Chef")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(hashMap1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        mDialog.dismiss();
                                                        FAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    AlertDialog.Builder builder = new AlertDialog.Builder(ChefRegistration.this);
                                                                    builder.setMessage("You have Registered! Make sure To verify your Email");
                                                                    builder.setCancelable(false);
                                                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            dialog.dismiss();

                                                                        }
                                                                    });
                                                                    AlertDialog Alert = builder.create();
                                                                    Alert.show();
                                                                } else {
                                                                    mDialog.dismiss();
                                                                    ReusableCodeForAll.ShowAlert(ChefRegistration.this,"Error",task.getException().getMessage());
                                                                }

                                                            }
                                                        });


                                                    }
                                                });



                                    }
                                });
                            }

                        }
                    });

                }




            }
        });
        Phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChefRegistration.this,Chefloginphone.class));
                finish();
            }
        });

        Emaill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChefRegistration.this,Login.class));
                finish();
            }
        });





    }
    String emailpattern= "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public boolean isValid(){
        Email.setErrorEnabled(false);
        Email.setError("");
        Fname.setErrorEnabled(false);
        Fname.setError("");
        Lname.setErrorEnabled(false);
        Lname.setError("");
        Pass.setErrorEnabled(false);
        Pass.setError("");
        mobileno.setErrorEnabled(false);
        mobileno.setError("");
        cpass.setErrorEnabled(false);
        cpass.setError("");
        area.setErrorEnabled(false);
        area.setError("");
        houseno.setErrorEnabled(false);
        houseno.setError("");
        pincode.setErrorEnabled(false);
        pincode.setError("");
        boolean isValid=false,isValidhouseno=false,isValidlname=false,isValidname=false,isValidemail=false,isValidpassword=false,isValidconfpassword=false,isValidmobilenum=false,isValidarea=false,isValipincode=false;
        if(TextUtils.isEmpty(fname)){
            Fname.setErrorEnabled(true);
            Fname.setError("Enter First your name");
        }
        else {
            isValidname=true;
        }
        if(TextUtils.isEmpty(lname)){
            Lname.setErrorEnabled(true);
            Lname.setError("Enter Last your name");
        }
        else {
            isValidlname=true;
        }
        if(TextUtils.isEmpty(emailid)){
            Fname.setErrorEnabled(true);
            Fname.setError("Email is Required");
        }
        else {
            if (emailid.matches(emailpattern)){
                isValidemail=true;
            }
            else {
                Email.setErrorEnabled(true);
                Email.setError("Enter a valid Email ");
            }

        }
        if(TextUtils.isEmpty(password)){
            Pass.setErrorEnabled(true);
            Pass.setError("Enter your Password");
        }
        else {
            if(password.length()<8){
                Pass.setErrorEnabled(true);
                Pass.setError("Password is weak");
            }
            else {
                isValidpassword=true;
            }
        }
        if(TextUtils.isEmpty(confpassword)){
            cpass.setErrorEnabled(true);
            cpass.setError("Re-Enter your password");
        }
        else {
            if(!password.equals(confpassword)){
                cpass.setErrorEnabled(true);
                cpass.setError("Password and confirm password Does not match ");
            }
            else {
                isValidconfpassword=true;
            }
        }
        if(TextUtils.isEmpty(mobile)){
            mobileno.setErrorEnabled(true);
            mobileno.setError("Phone number is Required");
        }
        else {
            if(mobile.length()<10){
                mobileno.setErrorEnabled(true);
                mobileno.setError("Invalid mobile Number");
            }
            else{
                isValidmobilenum=true;
            }

        }
        if(TextUtils.isEmpty(Area)){
            area.setErrorEnabled(true);
            area.setError("Area is Required");
        }
        else {
            isValidarea=true;
        }
        if(TextUtils.isEmpty(Pincode)){
            pincode.setErrorEnabled(true);
            pincode.setError("Enter First your name");
        }
        else {
            isValipincode=true;
        }
        if(TextUtils.isEmpty(house)){
            houseno.setErrorEnabled(true);
            houseno.setError("Fields can't be Empty");
        }
        else {
            isValidhouseno=true;
        }

     isValid =(isValidarea && isValidhouseno && isValidlname && isValidconfpassword && isValidemail && isValidname && isValipincode && isValidpassword && isValidmobilenum )? true : false;
        return isValid;









    }

}