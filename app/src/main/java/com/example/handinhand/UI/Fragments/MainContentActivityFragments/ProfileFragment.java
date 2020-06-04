package com.example.handinhand.UI.Fragments.MainContentActivityFragments;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.handinhand.Helpers.SharedPreferenceHelper;
import com.example.handinhand.Models.Profile;
import com.example.handinhand.R;
import com.example.handinhand.Utils.NetworkUtils;
import com.example.handinhand.ViewModels.ImagePreviewViewModel;
import com.example.handinhand.ViewModels.ProfileViewModel;
import com.google.android.material.button.MaterialButton;

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
    private ImagePreviewViewModel imagePreviewViewModel;
    private String url = null;

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
        imagePreviewViewModel = new ViewModelProvider(activity).get(ImagePreviewViewModel.class);

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

                        Glide.with(this).load(R.drawable.male_avatar)
                                .diskCacheStrategy(DiskCacheStrategy.DATA)
                                .placeholder(R.drawable.male_avatar)
                                .into(profileImage);

                    }
                    else{
                        Glide.with(this).load(R.drawable.female_avatar)
                                .diskCacheStrategy(DiskCacheStrategy.DATA)
                                .placeholder(R.drawable.female_avatar)
                                .into(profileImage);

                    }
                }
                else{

                    Glide.with(this).load(getString(R.string.avatar_url) +
                            user.getInfo().getAvatar())
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    url = getString(R.string.avatar_url) +
                                            user.getInfo().getAvatar();
                                    return false;
                                }
                            })
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .error(R.drawable.male_avatar)
                            .placeholder(R.drawable.male_avatar)
                            .into(profileImage);
                }

            });
        }
        else{
            model.setIsError(true);
        }

        profileImage.setOnClickListener(view -> {
            if(url != null){
                imagePreviewViewModel.setUrl(url);
                Navigation.findNavController(rootView).navigate(R.id.action_profileFragment_to_imagePreviewFragment);
            }

        });


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
