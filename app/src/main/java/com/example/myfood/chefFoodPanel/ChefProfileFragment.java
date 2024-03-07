package com.example.myfood.chefFoodPanel;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myfood.MainMenu;
import com.example.myfood.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class ChefProfileFragment extends Fragment {
    TextView profileName, profileEmail, profileMobile, profilast;
    TextView titleName, titleEmail;
    Button editProfile;
    Button postDish;
    ImageView logout,pic_chef;
    ImageButton select_pic;
    Uri imageuri;
    Uri mcropimageuri;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private StorageReference storageReference;
    private BroadcastReceiver profileUpdateReceiver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chef_prifile,null);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        // Initialize your TextViews and Button and images
        select_pic =v.findViewById(R.id.select_picture);
        pic_chef= v.findViewById(R.id.chef_image);
        profileName = v.findViewById(R.id.profileName);
        profilast = v.findViewById(R.id.profileLast);
        profileEmail = v.findViewById(R.id.profileEmail);
        profileMobile = v.findViewById(R.id.profilemobile);
        titleName = v.findViewById(R.id.titleName);
        titleEmail = v.findViewById(R.id.titleemail);
        editProfile = v.findViewById(R.id.edit_chef);
        logout = v.findViewById(R.id.logoutIcon);
        storageReference = FirebaseStorage.getInstance().getReference().child("chef_profile_images");
        retrieveUserData();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), MainMenu.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passUserData();
            }
        });

        select_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectImageclick(v);
            }
        });
        pic_chef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("pic chef clicked");
            }
        });





        postDish =  (Button)v.findViewById(R.id.post_dish);
        postDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),chef_postDish.class));
            }
        });
        profileUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null && intent.getBooleanExtra("refreshProfile", false)) {
                    // Refresh the profile
                    retrieveUserData();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter("com.example.myfood.PROFILE_UPDATED");
        requireActivity().registerReceiver(profileUpdateReceiver, intentFilter);
        return v;
    }
    private void retrieveUserData() {
        try {
            String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference chef = FirebaseDatabase.getInstance().getReference("Chef").child(currentUserUid);

            chef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        // Retrieve values from the database
                        String emailFromDB = dataSnapshot.child("Email id").getValue(String.class);
                        String firstNameFromDB = dataSnapshot.child("First Name").getValue(String.class);
                        String lastNameFromDB = dataSnapshot.child("Last Name").getValue(String.class);
                        String mobileNoFromDB = dataSnapshot.child("Mobile No").getValue(String.class);
                        String imageUrl = dataSnapshot.child("Profile image").getValue(String.class);

                        // Set values to TextViews
                        titleName.setText(firstNameFromDB);
                        titleEmail.setText(emailFromDB);
                        profileName.setText(firstNameFromDB);
                        profileEmail.setText(emailFromDB);
                        profilast.setText(lastNameFromDB);
                        profileMobile.setText(mobileNoFromDB);

                        // Load the image into the ImageView using Glide
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(requireContext())
                                    .load(imageUrl)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(pic_chef);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error processing user data", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    try {
                        // Handle onCancelled
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error handling database cancellation", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error retrieving user data", Toast.LENGTH_SHORT).show();
        }
    }

    public void passUserData() {
        try {
            // Get the current user's UID
            String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chef").child(currentUserUid);

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        // This method will be called when data is retrieved from the database
                        if (dataSnapshot.exists()) {
                            String nameFromDB = dataSnapshot.child("First Name").getValue(String.class);
                            String lastFromBD = dataSnapshot.child("Last Name").getValue(String.class);
                            String emailFrom = dataSnapshot.child("Email id").getValue(String.class);
                            String mobilefrom = dataSnapshot.child("Mobile No").getValue(String.class);


                            Intent intent = new Intent(requireContext(), Chef_EditProfile.class);

                            intent.putExtra("First name", nameFromDB);
                            intent.putExtra("Last Name", lastFromBD);
                            intent.putExtra("email", emailFrom);
                            intent.putExtra("mobile", mobilefrom);


                            startActivity(intent);
                        } else {
                            // TODO: Handle the case where the user data doesn't exist
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error processing user data", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    try {
                        // This method will be called if there's an error in retrieving the data
                        // TODO: Handle the error
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error handling database cancellation", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error retrieving user data", Toast.LENGTH_SHORT).show();
        }
    }
    // Chef Profile Image :

    private void startCropImageActivity(Uri imageuri) {
        CropImage.activity(imageuri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(requireContext(), this);
    }

    private void onSelectImageclick(View v) {
        CropImage.startPickImageActivity(requireContext(), this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mcropimageuri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCropImageActivity(mcropimageuri);
        } else {
            Toast.makeText(requireContext(), "Cancelling! Permission Not Granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            imageuri = CropImage.getPickImageResultUri(requireActivity(), data); // Use getContext() to get the Fragment's context
            if (CropImage.isReadExternalStoragePermissionsRequired(requireActivity(), imageuri)) {
                mcropimageuri = imageuri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {
                startCropImageActivity(imageuri);
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri croppedImageUri = result.getUri();

                // Assuming you have a Firebase Storage reference
                String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                StorageReference imageRef = storageReference.child("chef_picture_" + currentUserUid + ".jpg");
                // Upload the cropped image to Firebase Storage
                imageRef.putFile(croppedImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Handle successful upload
                                Toast.makeText(getContext(), "Profile picture uploaded successfully!", Toast.LENGTH_SHORT).show();
                                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri downloadUri) {
                                        // Update the Realtime Database with the image URL
                                        updateDatabaseWithImageUrl(downloadUri.toString());
                                        // Load the updated image into ImageView using Glide
                                        Glide.with(requireContext())
                                                .load(downloadUri)
                                                .apply(new RequestOptions().placeholder(R.drawable.prfl2))
                                                .into(pic_chef);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Handle failure to get download URL
                                        Toast.makeText(getContext(), "Failed to get download URL", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failed upload
                                Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(getContext(), "Failed To Crop: " + result.getError(), Toast.LENGTH_SHORT).show();
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    // Helper method to update the Realtime Database with the image URL
    private void updateDatabaseWithImageUrl(String imageUrl) {
        try {
            // Get the current user's UID
            String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Update the Realtime Database with the image URL
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chef").child(currentUserUid);
            databaseReference.child("Profile image").setValue(imageUrl);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error updating database with image URL", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        // Unregister the BroadcastReceiver
        if (profileUpdateReceiver != null) {
            requireActivity().unregisterReceiver(profileUpdateReceiver);
        }
    }

}