package com.example.plantonic;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class ProfileFragment extends Fragment {

    ImageView orderBtn, wishlistBtn, cartBtn, profileBtn, helpCenterBtn, feedbackBtm;
    TextView versionCode;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=  inflater.inflate(R.layout.fragment_profile, container, false);
        orderBtn = view.findViewById(R.id.orderViewBtn);
        wishlistBtn = view.findViewById(R.id.wishlistBtn);
        cartBtn = view.findViewById(R.id.cartBtn);
        profileBtn = view.findViewById(R.id.profileBtn);
        helpCenterBtn = view.findViewById(R.id.helpCenterBtn);
        feedbackBtm = view.findViewById(R.id.feedbackBtn);
        versionCode= view.findViewById(R.id.versionCode);

        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        wishlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.setReorderingAllowed(true)
                        .addToBackStack("HelpCenter")
                        .replace(R.id.fragmentContainerView, new FavouriteFragment());
                fragmentTransaction.commit();
                Toast.makeText(getContext(),"Your Wishlist!",Toast.LENGTH_SHORT).show();
            }
        });

        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.setReorderingAllowed(true)
                        .addToBackStack("cartFragment")
                        .replace(R.id.fragmentContainerView, new CartFragment());
                fragmentTransaction.commit();
                Toast.makeText(getContext(),"Your Cart Items!",Toast.LENGTH_SHORT).show();
            }
        });
        helpCenterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .addToBackStack("HelpCenter")
                        .replace(R.id.fragmentContainerView, new HelpCenterFragment())
                        .commit();
                Toast.makeText(getContext(),"HelpCenter",Toast.LENGTH_SHORT).show();
            }
        });

        versionCode.setText(String.valueOf(BuildConfig.VERSION_NAME));
        return view;
    }



}