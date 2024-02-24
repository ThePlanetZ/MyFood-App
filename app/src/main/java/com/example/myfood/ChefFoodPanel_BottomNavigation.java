package com.example.myfood;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.myfood.chefFoodPanel.ChefHomeFragment;
import com.example.myfood.chefFoodPanel.ChefOrderFragment;
import com.example.myfood.chefFoodPanel.ChefPendingOrderFragment;
import com.example.myfood.chefFoodPanel.ChefProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ChefFoodPanel_BottomNavigation extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_food_panel_bottom_navigation);
        BottomNavigationView navigationView = findViewById(R.id.chef_bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(this);
     // Set the default fragment (ChefHomeFragment) when the activity starts
        loadcheffragment(new ChefHomeFragment());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        if (item.getItemId() == R.id.chefHome) {
            fragment = new ChefHomeFragment();
        } else if (item.getItemId() == R.id.pendingOrders) {
            fragment = new ChefPendingOrderFragment();
        } else if (item.getItemId() == R.id.Orders) {
            fragment = new ChefOrderFragment();
        } else if (item.getItemId() == R.id.chefProfiele) {
            fragment = new ChefProfileFragment();
        }
        return loadcheffragment(fragment);
    }

    private boolean loadcheffragment(Fragment fragment) {

        if (fragment != null){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,fragment).commit();
            return true;
        }
        return false;
    }
}