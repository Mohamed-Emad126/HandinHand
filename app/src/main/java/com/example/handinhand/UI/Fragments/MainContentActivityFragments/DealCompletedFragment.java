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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.handinhand.Helpers.SharedPreferenceHelper;
import com.example.handinhand.R;
import com.example.handinhand.Utils.NetworkUtils;
import com.example.handinhand.ViewModels.ProfileViewModel;
import com.example.handinhand.ViewModels.SharedDealViewModel;
import com.google.android.material.button.MaterialButton;

public class DealCompletedFragment extends Fragment {

    private Toolbar toolbar;
    private ImageView itemImage;
    private ImageView buyerImage;
    private TextView itemName;
    private TextView buyerName;
    private TextView messageTextView;
    private TextView buyerResponse;
    private TextView buyerResponseStatus;
    private TextView messageToClose;
    private MaterialButton close;
    private String id;
    private SharedDealViewModel sharedDealViewModel;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_deal_completed, container, false);
        toolbar = rootView.findViewById(R.id.deal_completed_toolbar);
        itemImage = rootView.findViewById(R.id.item_image);
        itemName = rootView.findViewById(R.id.item_name);

        messageTextView = rootView.findViewById(R.id.textView2);
        buyerResponse = rootView.findViewById(R.id.textView3);
        buyerResponseStatus = rootView.findViewById(R.id.buyer_response);
        messageToClose = rootView.findViewById(R.id.message_to_close);

        buyerImage = rootView.findViewById(R.id.buyer_image_deal);
        buyerName = rootView.findViewById(R.id.user_nme);

        close = rootView.findViewById(R.id.button_close_item);

        FragmentActivity activity = getActivity();
        sharedDealViewModel = new ViewModelProvider(activity).get(SharedDealViewModel.class);

        toolbar.setNavigationOnClickListener(view -> Navigation.findNavController(rootView).navigateUp());
        id = String.valueOf(getArguments().getInt("ID"));
        ProfileViewModel user = new ViewModelProvider(activity).get(ProfileViewModel.class);
        user.getProfile(SharedPreferenceHelper.getToken(requireContext())).observe(requireActivity(),
                profile -> {
                    userId = profile.getDetails().getUser().getId();
                });


        sharedDealViewModel.getSelected().observe(activity, deal -> {
            if(isAdded()){
                if(deal.getShow_deal().getDeal_type().equals("products")){
                    Glide.with(rootView)
                            .load(getString(R.string.products_image_url) + deal.getShow_deal().getData().getImage())
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .placeholder(R.drawable.ic_image_placeholder)
                            .into(itemImage);
                }
                else{
                    Glide.with(rootView)
                            .load(getString(R.string.items_image_url) + deal.getShow_deal().getData().getImage())
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .placeholder(R.drawable.ic_image_placeholder)
                            .into(itemImage);
                }
                itemName.setText(deal.getShow_deal().getData().getTitle());
                if(deal.getShow_deal().getOwner().getId() == Integer.parseInt(userId)){
                    if(deal.getShow_deal().getIs_closed() >=0){
                        close.setVisibility(View.GONE);
                        messageToClose.setVisibility(View.GONE);
                    }
                    Glide.with(rootView)
                            .load(getString(R.string.avatar_url) + deal.getShow_deal().getBuyer().getAvatar())
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .placeholder(R.drawable.ic_image_placeholder)
                            .into(buyerImage);
                    buyerName.setText(String.format("%s %s",
                            deal.getShow_deal().getBuyer().getFirst_name(),
                            deal.getShow_deal().getBuyer().getLast_name()));
                    String s = deal.getShow_deal().getBuyer_status() >= 0 ? "ACCEPTED" : "DECLINED";
                    buyerResponseStatus.setText(s);
                    if(s.equals("ACCEPTED")){
                        buyerResponseStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    }
                    else{
                        buyerResponseStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    }
                }
                else{
                    messageTextView.setText(R.string.dealt_with);
                    buyerResponse.setText(R.string.owner_details);
                    close.setVisibility(View.GONE);
                    messageToClose.setVisibility(View.GONE);
                    Glide.with(rootView)
                            .load(getString(R.string.avatar_url) + deal.getShow_deal().getOwner().getAvatar())
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .placeholder(R.drawable.ic_image_placeholder)
                            .into(buyerImage);
                    buyerName.setText(String.format("%s %s",
                            deal.getShow_deal().getOwner().getFirst_name(),
                            deal.getShow_deal().getOwner().getLast_name()));
                    String s = deal.getShow_deal().getDetails();
                    buyerResponseStatus.setText(s);
                    buyerResponseStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                }
            }
        });

        close.setOnClickListener(view -> {
            if(NetworkUtils.getConnectivityStatus(activity) == NetworkUtils.TYPE_NOT_CONNECTED){
                Toast.makeText(activity, getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
            }
            else{
                //closeItem(id);
                Navigation.findNavController(rootView).navigateUp();
            }
        });


        return rootView;
    }

    //TODO: close Item
    /*private void closeItem(String id) {
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
    }*/
}