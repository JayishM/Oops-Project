package com.nightmarket.app;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nightmarket.app.fragment.AddItemFragment;
import com.nightmarket.app.fragment.ContactSellerFragment;
import com.nightmarket.app.fragment.HomeFragment;
import com.nightmarket.app.fragment.MyListingsFragment;
import com.nightmarket.app.fragment.ProductDetailFragment;
import com.nightmarket.app.model.Listing;

/**
 * Host Activity for the NightMarket Android application.
 * Manages fragment navigation and the bottom navigation bar.
 */
public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(Color.parseColor("#0F172A"));
        getWindow().setNavigationBarColor(Color.parseColor("#0F172A"));

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        setupBottomNavigation();

        if (savedInstanceState == null) {
            loadFragment(HomeFragment.newInstance(false), false);
        }
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home || itemId == R.id.nav_explore) {
                loadFragment(HomeFragment.newInstance(false), false);
                return true;
            } else if (itemId == R.id.nav_sell) {
                loadFragment(new AddItemFragment(), true);
                return true;
            } else if (itemId == R.id.nav_saved) {
                loadFragment(HomeFragment.newInstance(true), true);
                return true;
            } else if (itemId == R.id.nav_profile) {
                loadFragment(new MyListingsFragment(), true);
                return true;
            }
            return false;
        });
    }

    public void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    public void navigateToProductDetail(String listingId) {
        loadFragment(ProductDetailFragment.newInstance(listingId), true);
    }

    public void navigateToAddItem() {
        bottomNavigationView.setSelectedItemId(R.id.nav_sell);
    }

    public void navigateToContactSeller(Listing listing) {
        loadFragment(ContactSellerFragment.newInstance(listing), true);
    }

    public void navigateToMyListings() {
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
