package com.example.handinhand.UI.Fragments.SettingsActivityFragments;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.handinhand.Helpers.SharedPreferenceHelper;
import com.example.handinhand.R;
import com.example.handinhand.Utils.NetworkUtils;
import com.example.handinhand.ViewModels.ProfileViewModel;
import com.example.handinhand.ViewModels.SettingsViewModel;


public class SettingsFragment extends Fragment {

    private LinearLayout askForVerification;
    private TextView askForVerificationText;
    private LinearLayout changePassword;
    private LinearLayout verifyEmail;
    private TextView verifyEmailText;
    private SettingsViewModel settingsViewModel;
    private ProfileViewModel profileViewModel;
    private Dialog dialog;

    public SettingsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        Toolbar toolbar = rootView.findViewById(R.id.settings_toolbar);
        toolbar.setNavigationOnClickListener(view -> {
            Navigation.findNavController(rootView).popBackStack();
        });
        askForVerification = rootView.findViewById(R.id.verification);
        askForVerificationText = rootView.findViewById(R.id.askForVerificationText);
        changePassword = rootView.findViewById(R.id.more_settings);
        verifyEmail = rootView.findViewById(R.id.verify_email);
        verifyEmailText = rootView.findViewById(R.id.verify_email_text);
        createProgressDialog();
        FragmentActivity activity = getActivity();
        settingsViewModel = new ViewModelProvider(activity).get(SettingsViewModel.class);
        profileViewModel = new ViewModelProvider(activity).get(ProfileViewModel.class);


        changePassword.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(getString(R.string.change_password_url)));
            startActivity(i);
        });

        profileViewModel.getProfile(SharedPreferenceHelper.getToken(activity))
                .observe(activity, profile -> {
            if(profile != null && profile.getStatus()){
                if(profile.getDetails().getUser().getIs_trusted() == 1){
                    askForVerificationText.setText(getString(R.string.verified_account));
                    askForVerificationText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    askForVerification.setClickable(false);
                    askForVerification.setEnabled(false);
                }
                if(profile.getDetails().getUser().getEmail_verified_at() != null){
                    verifyEmailText.setText(getString(R.string.verified_email));
                    verifyEmailText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    verifyEmail.setClickable(false);
                    verifyEmail.setEnabled(false);
                }
            }
        });

        askForVerification.setOnClickListener(view -> Navigation.findNavController(rootView)
                .navigate(R.id.action_settingsFragment_to_askForVerificationFragment));

        verifyEmail.setOnClickListener(view -> {
            if(NetworkUtils.getConnectivityStatus(activity) == NetworkUtils.TYPE_NOT_CONNECTED){
                Toast.makeText(activity, getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
            }
            else{
                settingsViewModel.getVerify(SharedPreferenceHelper.getToken(activity));
            }
        });

        settingsViewModel.getIsError().observe(activity, aBoolean -> {
            if(aBoolean){
                Toast.makeText(activity, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        settingsViewModel.getIsLoading().observe(activity, aBoolean -> {
            if(aBoolean){
                dialog.show();
            }
            else{
                dialog.dismiss();
            }
        });

        settingsViewModel.getVerify().observe(activity, emailVerificationResponse -> {
            if(emailVerificationResponse != null && emailVerificationResponse.getStatus()){
                Toast.makeText(activity, getString(R.string.check_mailbox), Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    private void createProgressDialog(){
        dialog = new Dialog(getActivity());
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCanceledOnTouchOutside(false);
    }
}