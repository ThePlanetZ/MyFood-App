package com.example.myfood.customerFoodPanel;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myfood.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Customer_EditProfile extends AppCompatActivity {

    EditText editName, editLastName, editMobile, editEmail;
    Button saveButton, passwordResetButton;
    String nameUser, lastNameUser,  mobileUser, emailUser;
    DatabaseReference reference;
    private AlertDialog alertDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chef_edit_profile);

        reference = FirebaseDatabase.getInstance().getReference("Customer");

        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editLastName = findViewById(R.id.editlastname);
        editMobile = findViewById(R.id.editMobile);
        saveButton = findViewById(R.id.saveButton);
        passwordResetButton = findViewById(R.id.changepassword);
        // changeEmailButton = findViewById(R.id.changeemail);




        showData();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNameChanged() || isEmailChanged() || isLastChanged() || isPhoneChanged()) {
                    Toast.makeText(Customer_EditProfile.this, "Saved", Toast.LENGTH_SHORT).show();
                    // Notify the CustomerProfileFragment to refresh the profile
                    Intent intent = new Intent("com.example.myfood.PROFILE_UPDATED");
                    intent.putExtra("refreshProfile", true);
                    sendBroadcast(intent);
                } else {
                    Toast.makeText(Customer_EditProfile.this, "No Changes Found", Toast.LENGTH_SHORT).show();
                }
            }
        });


        passwordResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPasswordResetDialog();
            }
        });


//        changeEmailButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                showChangeEmailDialog();
//            }
//        });
    }



    public boolean isNameChanged() {
        String newName = editName.getText().toString().trim();
        if (!nameUser.equals(newName)) {
            reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("First Name").setValue(newName);
            nameUser = newName;
            return true;
        } else {
            return false;
        }
    }

    public boolean isPhoneChanged() {
        String newMobile = editMobile.getText().toString().trim();
        if (!mobileUser.equals(newMobile)) {
            reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Mobile No").setValue(newMobile);
            mobileUser = newMobile;
            return true;
        } else {
            return false;
        }
    }

    public boolean isLastChanged() {
        String newLastName = editLastName.getText().toString().trim();
        if (!lastNameUser.equals(newLastName)) {
            reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Last Name").setValue(newLastName);
            lastNameUser = newLastName;
            return true;
        } else {
            return false;
        }
    }

    public boolean isEmailChanged() {
        String newEmail = editEmail.getText().toString().trim();
        if (!emailUser.equals(newEmail)) {
            reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("EmailId").setValue(newEmail);
            emailUser = newEmail;
            return true;
        } else {
            return false;
        }
    }

    public void showData() {
        try {
            Intent intent = getIntent();

            nameUser = intent.getStringExtra("First name");
            lastNameUser = intent.getStringExtra("Last Name");
            emailUser = intent.getStringExtra("email");
            mobileUser = intent.getStringExtra("mobile");


            editName.setText(nameUser);
            editEmail.setText(emailUser);
            editLastName.setText(lastNameUser);
            editMobile.setText(mobileUser);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(Customer_EditProfile.this, "Error displaying user data", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPasswordResetDialog() {
        // Inflate the dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_forget, null);

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        // Find views in the dialog layout
        EditText emailBox = dialogView.findViewById(R.id.emailBox);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnReset = dialogView.findViewById(R.id.btnReset);

        // Set click listener for Cancel button
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the dialog
                alertDialog.dismiss();
            }
        });

        // Set click listener for Reset button
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the email entered by the user
                String email = emailBox.getText().toString().trim();

                // Check if the email is not empty
                if (!TextUtils.isEmpty(email)) {
                    // Use Firebase Authentication to send a password reset email
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        // Password reset email sent successfully
                                        Toast.makeText(Customer_EditProfile.this, "Password reset email sent.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // If password reset email sending fails
                                        Toast.makeText(Customer_EditProfile.this, "Failed to send password reset email.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    // If email is empty, show a message
                    Toast.makeText(Customer_EditProfile.this, "Enter your email address.", Toast.LENGTH_SHORT).show();
                }

                // Dismiss the dialog
                alertDialog.dismiss();
            }
        });

        // Create and show the dialog
        alertDialog = builder.create();
        alertDialog.show();
    }
//    private void showChangeEmailDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        View dialogView = getLayoutInflater().inflate(R.layout.dialogemailchange, null);
//
//        // Initialize views in the dialog layout
//        EditText newPasswordEditText = dialogView.findViewById(R.id.currentpassword);
//        EditText newEmailEditText = dialogView.findViewById(R.id.newEmail);
//        Button authButton = dialogView.findViewById(R.id.btnAuth);
//        Button changeEmailButton = dialogView.findViewById(R.id.btnChange);
//
//        builder.setView(dialogView);
//        AlertDialog changeEmailDialog = builder.create();
//
//        // Set click listener for the Auth button
//        authButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Get the entered password
//                String password = newPasswordEditText.getText().toString();
//
//                // Authenticate the user
//                FirebaseAuth.getInstance().signInWithEmailAndPassword(emailUser, password)
//                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if (task.isSuccessful()) {
//                                    // Authentication successful, allow the user to change the email
//                                    Toast.makeText(EditProfileActivity.this, "Authentication successful", Toast.LENGTH_SHORT).show();
//                                    newEmailEditText.setEnabled(true); // Enable the new email field
//                                    changeEmailButton.setEnabled(true); // Enable the Change Email button
//                                } else {
//                                    // Authentication failed, show an error message
//                                    Toast.makeText(EditProfileActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//            }
//        });
//        // Set click listener for the Change Email button
//        changeEmailButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Get the entered new email
//                String newEmail = newEmailEditText.getText().toString();
//
//                // Perform necessary actions, e.g., update the email in the database
//                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//                if (user != null) {
//                    user.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()) {
//                                // Email update successful, notify the user
//                                Toast.makeText(EditProfileActivity.this, "Email updated successfully", Toast.LENGTH_SHORT).show();
//                                changeEmailDialog.dismiss(); // Dismiss the dialog after a successful email change
//                            } else {
//                                // Email update failed, show an error message
//                                Toast.makeText(EditProfileActivity.this, "Email update failed", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//                }
//            }
//        });
//
//        // Show the dialog
//        changeEmailDialog.show();
//}
}