package com.example.handinhand;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.handinhand.Helpers.RetrofitHelper;
import com.example.handinhand.Helpers.SharedPreferenceHelper;
import com.example.handinhand.MainContent.MainContentActivity;
import com.example.handinhand.Models.LoginInfo;
import com.example.handinhand.Models.LoginResponse;
import com.example.handinhand.Utils.NetworkUtils;
import com.example.handinhand.ViewModels.MainActivityViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ldoublem.loadingviewlib.view.LVNews;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.RequestBody;


public class LoginFragment extends Fragment {


    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private TextInputLayout emailTextLayout;
    private TextInputLayout passwordTextLayout;
    private MaterialButton loginButton;
    private TextView registerText;
    private TextView forgetPasswordText;

    private MainActivityViewModel model;

    public LoginFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_login,
                container, false);

        FragmentActivity activity = getActivity();
        if (activity != null) {
            model = new ViewModelProvider(activity).get(MainActivityViewModel.class);
        }

        emailEditText = rootView.findViewById(R.id.email_edit_text);
        emailTextLayout = rootView.findViewById(R.id.email_text_layout);

        passwordEditText = rootView.findViewById(R.id.password_edit_text);
        passwordTextLayout = rootView.findViewById(R.id.password_text_layout);

        loginButton = rootView.findViewById(R.id.button_login);

        registerText = rootView.findViewById(R.id.text_register);
        forgetPasswordText = rootView.findViewById(R.id.text_forget_password);




        //Change the default box stroke color of the TextInputLayouts
        try {
            Field field = TextInputLayout.class.getDeclaredField("defaultStrokeColor");
            field.setAccessible(true);
            field.set(emailTextLayout,
                    ContextCompat.getColor(rootView.getContext(), R.color.colorPrimary));
            field.set(passwordTextLayout,
                    ContextCompat.getColor(rootView.getContext(), R.color.colorPrimary));
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            Log.w("TAG", "Failed to change box color, item might look wrong");
        }

        forgetPasswordText.setOnClickListener(view ->
                Navigation.findNavController(rootView)
                        .navigate(R.id.action_loginFragment_to_forgetPasswordFragment));


        if(activity != null){
            model.getIsLoading().observe(activity, aBoolean -> {
                if(aBoolean){

                    emailTextLayout.setEnabled(false);
                    passwordTextLayout.setEnabled(false);
                    emailEditText.setEnabled(false);
                    passwordEditText.setEnabled(false);
                }
                else{
                    emailTextLayout.setEnabled(true);
                    passwordTextLayout.setEnabled(true);
                    emailEditText.setEnabled(true);
                    passwordEditText.setEnabled(true);
                }
            });
        }



        loginButton.setOnClickListener(view -> {
            boolean isAble = true;

            if(emailEditText.getText()== null || emailEditText.getText().length() ==0){
                emailTextLayout.setErrorEnabled(true);
                emailTextLayout.setError(getString(R.string.empty));
                isAble = false;
            }
            else if(!isEmailValid(emailEditText.getText().toString().trim())){
                emailTextLayout.setErrorEnabled(true);
                emailTextLayout.setError(getString(R.string.validate_email));
            }

            if(passwordEditText.getText()== null || passwordEditText.getText().length() ==0){
                passwordTextLayout.setErrorEnabled(true);
                passwordTextLayout.setError(getString(R.string.empty));
                isAble = false;
            }

            if(NetworkUtils.getConnectivityStatus(activity) == 0){
                isAble = false;
                Snackbar.make(rootView, getString(R.string.connection_error), Snackbar.LENGTH_LONG).show();
            }

            if(isAble){

                if(activity != null){
                    model.getUser(new LoginInfo(emailEditText.getText().toString(),
                            passwordEditText.getText().toString())).observe(activity, loginResponse -> {
                        if(loginResponse.getLogin() != null){
                            String token = loginResponse.getLogin().getToken();
                            SharedPreferenceHelper.saveToken(activity, "Bearer "+token);
                            Toast.makeText(activity, token, Toast.LENGTH_SHORT).show();
                            model.leave();

                            startActivity(new Intent(activity, MainContentActivity.class));
                            activity.finish();
                        }
                        else{
                            model.leave();
                        }
                    });
                }

            }

        });

        model.getIsError().observe(activity, aBoolean -> {
            if(aBoolean){
                Toast.makeText(activity, getString(R.string.login_toast_error), Toast.LENGTH_SHORT).show();
                model.leave();
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

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordTextLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        registerText.setOnClickListener(view ->
                Navigation.findNavController(rootView)
                .navigate(R.id.action_loginFragment_to_registerFragment)
        );


        return rootView;
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
