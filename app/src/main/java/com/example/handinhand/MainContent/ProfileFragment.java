package com.example.handinhand.MainContent;


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
import com.example.handinhand.Helpers.SharedPreferenceHelper;
import com.example.handinhand.Models.Profile;
import com.example.handinhand.R;
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

        FragmentActivity activity = getActivity();

        model = new ViewModelProvider(activity).get(ProfileViewModel.class);

        toolbar.setNavigationOnClickListener(view -> {
            Navigation.findNavController(rootView).popBackStack();
            model.leave();
        });


        model.getProfile(SharedPreferenceHelper.getToken(activity)).observe(activity, profile -> {
            refreshLayout.setRefreshing(false);
            Profile.User user = profile.getDetails().getUser();
            email.setText(user.getEmail());
            gender.setText(user.getInfo().getGender());
            String name = user.getInfo().getFirst_name() +" "+ user.getInfo().getLast_name();
            profileName.setText(name);
            Picasso.get().load("http://75f00637.ngrok.io/storage/avatars/" + user.getInfo().getAvatar())
                    .into(profileImage);

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

        reloadButton.setOnClickListener(view ->
                model.refresh(SharedPreferenceHelper.getToken(activity))
        );

        refreshLayout.setOnRefreshListener(() -> {
            model.refresh(SharedPreferenceHelper.getToken(activity));
        });

        return rootView;
    }
}
