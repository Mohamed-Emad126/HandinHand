package com.example.handinhand.UI.Fragments.MainContentActivityFragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.handinhand.Helpers.SharedPreferenceHelper;
import com.example.handinhand.Models.Profile;
import com.example.handinhand.R;
import com.example.handinhand.Utils.NetworkUtils;
import com.example.handinhand.ViewModels.ProfileViewModel;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment{


    private SwipeRefreshLayout refreshLayout;
    private TextView email;
    private TextView gender;
    private TextView profileName;
    private ProfileViewModel model;
    private CircleImageView profileImage;
    private RelativeLayout errorPage;
    MaterialButton reloadButton;
    private Toolbar toolbar;
    private TextView editText;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        refreshLayout = rootView.findViewById(R.id.swipe_refresh_in_profile);
        email = rootView.findViewById(R.id.user_email);
        gender = rootView.findViewById(R.id.user_gender);
        profileName = rootView.findViewById(R.id.Profile_name_in_toolbar);
        profileImage = rootView.findViewById(R.id.profile_image);
        errorPage = rootView.findViewById(R.id.error_page);
        reloadButton = rootView.findViewById(R.id.reload);
        toolbar = rootView.findViewById(R.id.profile_toolbar);
        editText = rootView.findViewById(R.id.edit_profile_info);


        //TODO: check if the fragment is loading disable the click listener
        editText.setOnClickListener(view -> {
            Navigation.findNavController(rootView)
                    .navigate(R.id.action_profileFragment_to_editProfileFragment);
        });

        FragmentActivity activity = getActivity();

        model = new ViewModelProvider(activity).get(ProfileViewModel.class);

        toolbar.setNavigationOnClickListener(view -> {
            Navigation.findNavController(rootView).popBackStack();
            //model.leave();
        });

        toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.share){
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
            return true;
        });


        if(NetworkUtils.getConnectivityStatus(activity) != 0){
            model.getProfile(SharedPreferenceHelper.getToken(activity)).observe(activity, profile -> {
                refreshLayout.setRefreshing(false);
                Profile.User user = profile.getDetails().getUser();
                email.setText(user.getEmail());
                gender.setText(user.getInfo().getGender());
                String name = user.getInfo().getFirst_name() +" "+ user.getInfo().getLast_name();
                profileName.setText(name);
                if(user.getInfo().getAvatar().contains("default")){
                    if(user.getInfo().getGender().contains("male")){
                        /*Picasso.get().load(R.drawable.male_avatar)
                                .placeholder(R.drawable.male_avatar)
                                .into(profileImage);*/

                        Glide.with(this).load(R.drawable.male_avatar)
                                .placeholder(R.drawable.male_avatar)
                                .into(profileImage);

                    }
                    else{
                        Glide.with(this).load(R.drawable.female_avatar)
                                .placeholder(R.drawable.female_avatar)
                                .into(profileImage);

                        /*Picasso.get().load(R.drawable.female_avatar)
                                .placeholder(R.drawable.female_avatar)
                                .into(profileImage);*/
                    }
                }
                else{
                    /*Picasso.get().load(getString(R.string.avatar_url) + user.getInfo().getAvatar())
                            .into(profileImage);*/

                    Glide.with(this).load(getString(R.string.avatar_url) +
                            user.getInfo().getAvatar())
                            .error(R.drawable.male_avatar)
                            .placeholder(R.drawable.male_avatar)
                            .into(profileImage);
                }

            });
        }
        else{
            model.setIsError(true);
        }
        model.getIsLoading().observe(activity, aBoolean -> {
            if(aBoolean){
                refreshLayout.setRefreshing(true);
            }
            else{
                refreshLayout.setRefreshing(false);
            }
        });

        model.getIsError().observe(activity, aBoolean -> {
            if(aBoolean){
                errorPage.setVisibility(View.VISIBLE);
                refreshLayout.setVisibility(View.GONE);
                refreshLayout.setRefreshing(false);
            }
            else{
                errorPage.setVisibility(View.GONE);
                refreshLayout.setVisibility(View.VISIBLE);
            }
        });

        reloadButton.setOnClickListener(view ->{
            if(NetworkUtils.getConnectivityStatus(activity) != 0){
                model.refresh(SharedPreferenceHelper.getToken(activity));
            }
        }
        );

        refreshLayout.setOnRefreshListener(() -> {
            if(NetworkUtils.getConnectivityStatus(activity) != 0){
                model.refresh(SharedPreferenceHelper.getToken(activity));
            }
        });

        return rootView;
    }
}
