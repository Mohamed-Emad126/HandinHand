package com.example.handinhand.UI.Fragments.MainActivityFragments;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.handinhand.R;
import com.example.handinhand.Utils.NetworkUtils;
import com.example.handinhand.ViewModels.MainActivityViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Objects;


public class ForgetPasswordFragment extends Fragment {


    public ForgetPasswordFragment() {
        // Required empty public constructor
    }

    private MaterialButton sendLinkButton;
    private TextInputEditText emailEditText;
    private TextInputLayout emailTextLayout;
    private MainActivityViewModel model;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_forget_password, container, false);


        sendLinkButton = rootView.findViewById(R.id.send_link_button);
        emailEditText = rootView.findViewById(R.id.reset_password_edit_text);
        emailTextLayout = rootView.findViewById(R.id.reset_password_email_layout);
        FragmentActivity activity = getActivity();
        model = new ViewModelProvider(activity).get(MainActivityViewModel.class);


        sendLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Objects.requireNonNull(emailEditText.getText()).toString().trim().length() == 0){
                    emailTextLayout.setErrorEnabled(true);
                    emailEditText.setError(getString(R.string.empty));
                }
                else if(!isEmailValid(emailEditText.getText().toString())){
                    emailTextLayout.setErrorEnabled(true);
                    emailEditText.setError(getString(R.string.validate_email));
                }
                else{
                    if(NetworkUtils.getConnectivityStatus(activity) == 0){
                        Snackbar.make(rootView, "No Network Connection", Snackbar.LENGTH_LONG).show();
                    }
                    else{
                        HashMap<String, String> emailMap = new HashMap<>();
                        emailMap.put("email", emailEditText.getText().toString().trim());

                        model.sendResetEmail(emailMap).observe(activity, sendResetPasswordEmailResponse -> {
                            if (sendResetPasswordEmailResponse.getSend_reset_password_email()!= null) {
                                Toast.makeText(activity, "Check your mail box", Toast.LENGTH_LONG).show();

                                Navigation.findNavController(rootView).navigateUp();
                            }
                            else{
                                Snackbar.make(rootView, "We are Sorry Something went Wrong", Snackbar.LENGTH_LONG).show();
                            }
                            model.leave();

                        });
                    }

                }

            }
        });


        model.getIsForgetError().observe(activity, aBoolean -> {
            if(aBoolean){
                Snackbar.make(rootView, "We are Sorry Something went Wrong", Snackbar.LENGTH_LONG).show();
                model.leave();
                //Toast.makeText(activity, "We are Sorry\nSomething went Wrong", Toast.LENGTH_SHORT).show();
            }
        });

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                emailTextLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        setUpToolBar(rootView);


        return rootView;
    }


    private void setUpToolBar(final View rootView) {

        Toolbar toolbar= rootView.findViewById(R.id.fragment_reset_password_toolbar);

        final AppCompatActivity activity=(AppCompatActivity) getActivity();

        if(activity != null){
            activity.setSupportActionBar(toolbar);
            toolbar.setTitle(R.string.forget_password_title);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            toolbar.setNavigationOnClickListener(view ->
                    Navigation.findNavController(rootView).popBackStack());

        }
    }

    /**
     * Function that check the validation of the Email
     * @param email text the user type in emailEditText
     * @return true if {@param email} match the pattern of Email address.
     */
    public static boolean isEmailValid(CharSequence email){
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }
}
