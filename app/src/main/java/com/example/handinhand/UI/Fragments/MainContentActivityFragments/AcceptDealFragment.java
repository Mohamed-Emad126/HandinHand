package com.example.handinhand.UI.Fragments.MainContentActivityFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.handinhand.R;
import com.example.handinhand.Utils.NetworkUtils;
import com.example.handinhand.ViewModels.SharedDealViewModel;
import com.example.handinhand.services.DealWorker;
import com.google.android.material.button.MaterialButton;

public class AcceptDealFragment extends Fragment {

    private Toolbar toolbar;
    private ImageView itemImage;
    private ImageView ownerImage;
    private TextView itemName;
    private TextView ownerName;
    private TextView details;
    private MaterialButton deal;
    private MaterialButton decline;
    private String id;
    private SharedDealViewModel sharedDealViewModel;
    private TextView messageTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_accept_deal, container, false);
        toolbar = rootView.findViewById(R.id.deal_accept_toolbar);
        itemImage = rootView.findViewById(R.id.item_image);
        itemName = rootView.findViewById(R.id.item_name);
        messageTextView = rootView.findViewById(R.id.textView2);
        ownerImage = rootView.findViewById(R.id.owner_image_deal);
        ownerName = rootView.findViewById(R.id.user_nme);

        details = rootView.findViewById(R.id.owner_details);

        deal = rootView.findViewById(R.id.button_Accept);
        decline = rootView.findViewById(R.id.button_decline);
        FragmentActivity activity = getActivity();
        sharedDealViewModel = new ViewModelProvider(activity).get(SharedDealViewModel.class);

        toolbar.setNavigationOnClickListener(view -> Navigation.findNavController(rootView).navigateUp());
        id = String.valueOf(getArguments().getInt("ID"));

        deal.setOnClickListener(view -> {
            if(NetworkUtils.getConnectivityStatus(activity) == NetworkUtils.TYPE_NOT_CONNECTED){
                Toast.makeText(activity, getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
            }
            else{
                dealItem(id, 1);
                Navigation.findNavController(rootView).navigateUp();
            }
        });
        decline.setOnClickListener(view -> {
            dealItem(id, 0);
            Navigation.findNavController(rootView).navigateUp();
        });

        sharedDealViewModel.getSelected().observe(activity, deal -> {
            if(isAdded()){
                if(deal.getShow_deal().getDeal_type().equals("products")){
                    Glide.with(rootView)
                            .load(getString(R.string.products_image_url) + deal.getShow_deal().getData().getImage())
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .placeholder(R.drawable.ic_image_placeholder)
                            .into(itemImage);
                    messageTextView.setText(getString(R.string.you_have_booked_the_product_above_from));

                }
                else{
                    Glide.with(rootView)
                            .load(getString(R.string.items_image_url) + deal.getShow_deal().getData().getImage())
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .placeholder(R.drawable.ic_image_placeholder)
                            .into(itemImage);
                    messageTextView.setText(getString(R.string.you_have_booked_the_item_above_from));

                }
                itemName.setText(deal.getShow_deal().getData().getTitle());
                Glide.with(rootView)
                        .load(getString(R.string.avatar_url) + deal.getShow_deal().getOwner().getAvatar())
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .placeholder(R.drawable.ic_image_placeholder)
                        .into(ownerImage);
                ownerName.setText(String.format("%s %s",
                        deal.getShow_deal().getOwner().getFirst_name(),
                        deal.getShow_deal().getOwner().getLast_name()));
                details.setText(deal.getShow_deal().getDetails());
                details.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            }
        });

        return rootView;
    }

    private void dealItem(String id, int i) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        Data data = new Data.Builder()
                .putString("TYPE", "RESPONSE")
                .putString("ELEMENT_ID", id)
                .putString("DETAILS", String.valueOf(i))
                .build();

        OneTimeWorkRequest deal = new OneTimeWorkRequest
                .Builder(DealWorker.class)
                .setConstraints(constraints)
                .setInputData(data)
                .build();
        WorkManager.getInstance(getActivity()).enqueue(deal);
    }
}