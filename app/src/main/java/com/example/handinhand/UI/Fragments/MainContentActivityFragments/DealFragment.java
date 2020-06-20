package com.example.handinhand.UI.Fragments.MainContentActivityFragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class DealFragment extends Fragment {

    private Toolbar toolbar;
    private ImageView itemImage;
    private ImageView buyerImage;
    private TextView itemName;
    private TextView buyerName;
    private TextView messageTextView;
    private TextInputEditText details;
    private TextInputLayout detailsLayout;
    private MaterialButton deal;
    private MaterialButton decline;
    private String id;
    private SharedDealViewModel sharedDealViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_deal, container, false);
        toolbar = rootView.findViewById(R.id.deal_toolbar);
        itemImage = rootView.findViewById(R.id.item_image);
        itemName = rootView.findViewById(R.id.item_name);
        messageTextView = rootView.findViewById(R.id.textView2);
        buyerImage = rootView.findViewById(R.id.buyer_image_deal);
        buyerName = rootView.findViewById(R.id.user_nme);
        details = rootView.findViewById(R.id.deal_details_input);
        detailsLayout = rootView.findViewById(R.id.deal_details_layout);
        deal = rootView.findViewById(R.id.button_deal);
        decline = rootView.findViewById(R.id.button_decline);
        FragmentActivity activity = getActivity();
        sharedDealViewModel = new ViewModelProvider(activity).get(SharedDealViewModel.class);

        toolbar.setNavigationOnClickListener(view -> Navigation.findNavController(rootView).navigateUp());
        id = String.valueOf(getArguments().getInt("ID"));

        deal.setOnClickListener(view -> {
            if(details.getText() != null && details.getText().toString().length() == 0){
                detailsLayout.setErrorEnabled(true);
                detailsLayout.setError(getString(R.string.required));
            }
            else if(NetworkUtils.getConnectivityStatus(activity) == NetworkUtils.TYPE_NOT_CONNECTED){
                Toast.makeText(activity, getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
            }
            else{
                dealItem(id);
                Navigation.findNavController(rootView).navigateUp();
            }
        });
        decline.setOnClickListener(view -> {
            declineItem(id);
            Navigation.findNavController(rootView).navigateUp();
        });

        details.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                detailsLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        sharedDealViewModel.getSelected().observe(activity, deal -> {
            if(isAdded()){
                if(deal.getShow_deal().getDeal_type().equals("products")){
                    Glide.with(rootView)
                            .load(getString(R.string.products_image_url) + deal.getShow_deal().getData().getImage())
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .placeholder(R.drawable.ic_image_placeholder)
                            .into(itemImage);
                    messageTextView.setText(getString(R.string.deal_handmade_message));

                }
                else{
                    Glide.with(rootView)
                            .load(getString(R.string.items_image_url) + deal.getShow_deal().getData().getImage())
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .placeholder(R.drawable.ic_image_placeholder)
                            .into(itemImage);
                    messageTextView.setText(getString(R.string.deal_message));

                }
                itemName.setText(deal.getShow_deal().getData().getTitle());
                Glide.with(rootView)
                        .load(getString(R.string.avatar_url) + deal.getShow_deal().getBuyer().getAvatar())
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .placeholder(R.drawable.ic_image_placeholder)
                        .into(buyerImage);
                buyerName.setText(String.format("%s %s",
                        deal.getShow_deal().getBuyer().getFirst_name(),
                        deal.getShow_deal().getBuyer().getLast_name()));

            }
        });


        return rootView;
    }

    private void dealItem(String id) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        Data data = new Data.Builder()
                .putString("TYPE", "ACCEPT")
                .putString("ELEMENT_ID", id)
                .putString("DETAILS", details.getText().toString())
                .build();

        OneTimeWorkRequest decline = new OneTimeWorkRequest
                .Builder(DealWorker.class)
                .setConstraints(constraints)
                .setInputData(data)
                .build();
        WorkManager.getInstance(getActivity()).enqueue(decline);
    }

    private void declineItem(String id) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        Data data = new Data.Builder()
                .putString("TYPE", "DECLINE")
                .putString("ELEMENT_ID", id)
                .build();

        OneTimeWorkRequest decline = new OneTimeWorkRequest
                .Builder(DealWorker.class)
                .setConstraints(constraints)
                .setInputData(data)
                .build();
        WorkManager.getInstance(getActivity()).enqueue(decline);
    }
}