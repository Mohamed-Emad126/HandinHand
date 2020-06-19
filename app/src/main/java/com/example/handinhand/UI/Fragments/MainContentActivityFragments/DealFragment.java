package com.example.handinhand.UI.Fragments.MainContentActivityFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.handinhand.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class DealFragment extends Fragment {

    private Toolbar toolbar;
    private ImageView itemImage;
    private ImageView buyerImage;
    private TextView itemName;
    private TextView buyerName;
    private TextInputEditText details;
    private TextInputLayout detailsLayout;
    private MaterialButton deal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_deal, container, false);
        toolbar = rootView.findViewById(R.id.deal_toolbar);
        itemImage = rootView.findViewById(R.id.item_image);
        itemName = rootView.findViewById(R.id.item_name);
        buyerImage = rootView.findViewById(R.id.buyer_image_deal);
        buyerName = rootView.findViewById(R.id.user_nme);
        details = rootView.findViewById(R.id.deal_details_input);
        detailsLayout = rootView.findViewById(R.id.deal_details_layout);
        deal = rootView.findViewById(R.id.button_deal);

        toolbar.setNavigationOnClickListener(view -> Navigation.findNavController(rootView).navigateUp());


        return rootView;
    }
}