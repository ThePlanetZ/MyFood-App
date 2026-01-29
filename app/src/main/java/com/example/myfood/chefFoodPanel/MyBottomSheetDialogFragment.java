package com.example.myfood.chefFoodPanel;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfood.DeliveryPerson;
import com.example.myfood.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class MyBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private List<DeliveryPerson> deliveryList;
    private RecyclerView recyclerView;
    private DeliveryAdapter adapter;

    public MyBottomSheetDialogFragment(List<DeliveryPerson> deliveryList) {
        this.deliveryList = deliveryList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DeliveryAdapter(deliveryList,getContext());
        recyclerView.setAdapter(adapter);
        return view;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; // Custom animation style
        return dialog;
    }
}
