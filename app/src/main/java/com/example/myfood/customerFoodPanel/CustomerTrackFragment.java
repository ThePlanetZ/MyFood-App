package com.example.myfood.customerFoodPanel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myfood.R;
import com.google.firebase.database.DatabaseReference;

public class CustomerTrackFragment extends Fragment {

    private EditText categoryNameEditText;
    private EditText imageURLEditText;
    private Button addCategoryButton;

    private DatabaseReference categoryRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_customertrack, container, false);

        getActivity().setTitle("Track Order");

        // Initialize Firebase database reference



        return v;
    }


}