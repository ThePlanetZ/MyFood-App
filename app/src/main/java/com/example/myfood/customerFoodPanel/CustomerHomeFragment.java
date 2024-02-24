package com.example.myfood.customerFoodPanel;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myfood.R;
import com.example.myfood.webviewpiz;

public class CustomerHomeFragment extends Fragment {
    ImageView imageview, imageview2, imageview3, imageview4, imageview5;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.customer_menudish, container, false);
        getActivity().setTitle("Home");

        imageview = v.findViewById(R.id.menu_image);
        imageview2 = v.findViewById(R.id.menu_image2);
        imageview3 = v.findViewById(R.id.menu_image3);
        imageview4 = v.findViewById(R.id.menu_image4);
        imageview5 = v.findViewById(R.id.menu_image5);

        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebViewWithUrl("https://glovoapp.com/ma/en/casablanca/pizza-hut-cas/?content=promotion-les-deals-du-moment-sc.264438932%2Fpromotion-les-deals-du-moment-c.1568780370");
            }
        });
        imageview2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebViewWithUrl("https://glovoapp.com/ma/en/casablanca/burger-king-cas/?content=burgers-c.1233776635");
            }
        });

        imageview3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebViewWithUrl("https://glovoapp.com/ma/fr/casablanca/restaurants_1/asiatique_34834/");
            }
        });
        imageview4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebViewWithUrl("https://glovoapp.com/ma/fr/casablanca/lentrecote-in-your-baguette-cas/?content=top-des-ventes-ts");
            }
        });

        imageview5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebViewWithUrl("https://glovoapp.com/ma/fr/rabat/restaurants_1/tacos_34980/");
            }
        });


        return v;
    }

    public void openWebViewWithUrl(String url) {
        Intent intent = new Intent(requireContext(), webviewpiz.class);
        intent.putExtra("URL", url);
        startActivity(intent);
    }
}