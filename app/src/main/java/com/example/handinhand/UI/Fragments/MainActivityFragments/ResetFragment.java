package com.example.handinhand.UI.Fragments.MainActivityFragments;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.handinhand.R;
import com.example.handinhand.Utils.NetworkUtils;
import com.example.handinhand.ViewModels.MainActivityViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;


public class ResetFragment extends Fragment {


    private MaterialButton confirmButton;
    private ProgressBar progressBar;
    private TextInputEditText newPasswordEditText;
    private TextInputEditText reEnterNewPasswordEditText;
    private TextInputLayout reEnterNewPasswordTextLayout;
    private TextInputLayout newPasswordTextLayout;
    private String user_email;
    private String user_token;
    private MainActivityViewModel model;


    public ResetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reset, container, false);

        setUpToolBar(rootView);

        FragmentActivity activity = getActivity();
        model = new ViewModelProvider(activity).get(MainActivityViewModel.class);


        confirmButton = rootView.findViewById(R.id.confirm_button);
        progressBar = rootView.findViewById(R.id.progress);
        newPasswordEditText = rootView.findViewById(R.id.new_password_text);
        newPasswordTextLayout = rootView.findViewById(R.id.new_password_layout);
        reEnterNewPasswordEditText = rootView.findViewById(R.id.new_reenter_password_text);
        reEnterNewPasswordTextLayout = rootView.findViewById(R.id.new_reenter_password_layout);


        if(getArguments() != null && getArguments().containsKey("user_email")
            && getArguments().containsKey("user_token")
            ){
            //TODO: Use the arguments to set the new password to the specific email
            user_email = getArguments().getString("user_email");
            user_token = getArguments().getString("user_token");
            Snackbar.make(rootView, user_email, Snackbar.LENGTH_LONG).show();

        }

        confirmButton.setOnClickListener(view -> {
            //TODO: Navigate to the application content
            boolean isAble = true;
            if(newPasswordEditText.getText() != null &&
                    newPasswordEditText.getText().toString().length()==0){
                newPasswordTextLayout.setErrorEnabled(true);
                newPasswordTextLayout.setError(getString(R.string.empty));
                isAble = false;
            }

            if(reEnterNewPasswordEditText.getText() != null &&
                    reEnterNewPasswordEditText.getText().toString().length()==0){
                reEnterNewPasswordTextLayout.setErrorEnabled(true);
                reEnterNewPasswordTextLayout.setError(getString(R.string.empty));
                isAble = false;
            }
            if(isAble && !reEnterNewPasswordEditText.getText().toString().equals(
                    newPasswordEditText.getText().toString()
                    )){
                reEnterNewPasswordTextLayout.setErrorEnabled(true);
                reEnterNewPasswordTextLayout.setError(getString(R.string.different));
                isAble = false;
            }
            if(NetworkUtils.getConnectivityStatus(activity) == 0){
                isAble = false;
                Snackbar.make(rootView, getString(R.string.connection_error), Snackbar.LENGTH_LONG).show();
            }
            if(isAble){
                if(user_email != null && user_token != null){

                    HashMap<String, String > info = reset(user_token,
                            user_email,
                            newPasswordEditText.getText().toString());

                    model.resetPassword(info).observe(activity, resetPasswordResponse -> {
                        if(resetPasswordResponse.getReset_password() != null){
                            Toast.makeText(activity, "Done!\nYour password is updated", Toast.LENGTH_LONG).show();
                            Navigation.findNavController(rootView).navigate(R.id.action_resetFragment_to_loginFragment);
                        }
                        else{
                            Snackbar.make(rootView, "We are Sorry Something went Wrong", Snackbar.LENGTH_LONG).show();
                        }
                    });

                }
                else{
                    Toast.makeText(activity, "Expired Link", Toast.LENGTH_LONG).show();
                    Navigation.findNavController(rootView).navigate(R.id.action_resetFragment_to_loginFragment);
                }
            }
        });



        newPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                newPasswordTextLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        reEnterNewPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                reEnterNewPasswordTextLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        model.getIsResetError().observe(activity, aBoolean -> {
            if(aBoolean){
                Snackbar.make(rootView, "We are Sorry Something went Wrong", Snackbar.LENGTH_LONG).show();
                model.leave();
            }
        });

        return rootView;
    }

    private HashMap<String, String> reset(String user_token, String user_email, String password) {
        HashMap<String, String > info = new HashMap<>();

        info.put("email",user_email);
        info.put("password",password);
        info.put("password_confirmation",password);
        info.put("token",user_token);

        return info;
    }


    private void setUpToolBar(final View rootView) {

            Toolbar toolbar= rootView.findViewById(R.id.new_password_toolbar);

            final AppCompatActivity activity=(AppCompatActivity) getActivity();

            if(activity != null){
                activity.setSupportActionBar(toolbar);
                toolbar.setTitle(R.string.reset_password);

            }
        }

}
