package com.example.myfood.customerFoodPanel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myfood.R;

public class CustomerCartFragment extends Fragment {

//    private RecyclerView recyclerView;
//    private List<CartItem> cartItemList;
//    private CartAdapter adapter;
//
//    private TextView txtTotalFee;
//    private String customerUID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customercart, container, false);



        return view;
    }


}
