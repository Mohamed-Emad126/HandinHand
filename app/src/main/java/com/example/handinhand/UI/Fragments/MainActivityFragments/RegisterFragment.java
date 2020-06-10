package com.example.handinhand.UI.Fragments.MainActivityFragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.handinhand.Helpers.PermissionsHelper;
import com.example.handinhand.Helpers.RetrofitHelper;
import com.example.handinhand.Helpers.SharedPreferenceHelper;
import com.example.handinhand.R;
import com.example.handinhand.UI.Activities.MainContentActivity;
import com.example.handinhand.Utils.NetworkUtils;
import com.example.handinhand.ViewModels.MainActivityViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;


public class RegisterFragment extends Fragment {


    private static final int READ_EXTERNAL_STORAGE_ID = 1;
    private static final int GET_IMAGE_FROM_GALLERY = 2;
    public static final String IMAGE_URI = "IMAGE_URI";
    private CircleImageView userImage;
    private RadioGroup gender;
    private TextView haveAnAccountText;

    private TextInputEditText firstName;
    private TextInputLayout firstNameLayout;

    private TextInputEditText secondName;
    private TextInputLayout secondNameLayout;

    private TextInputEditText password;
    private TextInputLayout passwordLayout;

    private TextInputEditText rePassword;
    private TextInputLayout rePasswordLayout;

    private TextInputEditText grade;
    private TextInputLayout gradeLayout;

    private TextInputEditText email;
    private TextInputLayout emailLayout;
    private MaterialButton registerButton;

    private MainActivityViewModel model;


    private Intent getImageIntent;

    Uri uri;

    public RegisterFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_register, container, false);

        FragmentActivity activity = getActivity();

        model = new ViewModelProvider(activity).get(MainActivityViewModel.class);

        userImage = rootView.findViewById(R.id.user_image);
        gender = rootView.findViewById(R.id.gender);
        haveAnAccountText = rootView.findViewById(R.id.text_already_have_an_accuont);

        firstName = rootView.findViewById(R.id.register_first_name_text);
        firstNameLayout = rootView.findViewById(R.id.register_first_name_layout);

        secondName = rootView.findViewById(R.id.register_second_name_text);
        secondNameLayout = rootView.findViewById(R.id.register_second_name_layout);

        password = rootView.findViewById(R.id.register_password_text);
        passwordLayout = rootView.findViewById(R.id.register_password_layout);

        rePassword = rootView.findViewById(R.id.register_reenter_password_text);
        rePasswordLayout = rootView.findViewById(R.id.register_reenter_password_layout);

        gradeLayout = rootView.findViewById(R.id.register_grade_layout);
        grade = rootView.findViewById(R.id.register_grade_text);

        email = rootView.findViewById(R.id.register_email_text);
        emailLayout = rootView.findViewById(R.id.register_email_layout);

        registerButton = rootView.findViewById(R.id.button_register);

        getImageIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                emailLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        rePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                rePasswordLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        if (savedInstanceState != null && savedInstanceState.getString(IMAGE_URI) != null) {
            uri = Uri.parse(savedInstanceState.getString(IMAGE_URI));
            Picasso.get().load(uri).into(userImage);
        }


        haveAnAccountText.setOnClickListener(view ->
                Navigation.findNavController(rootView).navigateUp());

        gender.setOnCheckedChangeListener((radioGroup, i) -> {
            if(uri== null && i == R.id.gender_female){
                userImage.setImageResource(R.drawable.female_avatar);
            }
            else if(uri== null &&i == R.id.gender_male){
                userImage.setImageResource(R.drawable.male_avatar);
            }
        });

        userImage.setOnClickListener(view -> {

            if(getImageIntent.resolveActivity(Objects.requireNonNull(activity).getPackageManager()) != null) {

                if (PermissionsHelper.canReadExternalStorage(getContext())) {

                    startActivityForResult(getImageIntent, GET_IMAGE_FROM_GALLERY);

                }
                else{
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            READ_EXTERNAL_STORAGE_ID);
                }
            }

        });

        registerButton.setOnClickListener(view -> {
            boolean isAble = true;

            if(isAbleToRegister(email, emailLayout)){
                if(!LoginFragment.isEmailValid(email.getText().toString())){
                    isAble = false;
                    emailLayout.setErrorEnabled(true);
                    emailLayout.setError(getString(R.string.validate_email));
                }
            }
            else{
                isAble = false;
            }

            if(!isAbleToRegister(password, passwordLayout)){
                isAble = false;
            }

            if(!isAbleToRegister(rePassword, rePasswordLayout)){
                isAble = false;
            }

            if(!isAbleToRegister(grade, gradeLayout)){
                isAble = false;
            }

            if(!isAbleToRegister(firstName, firstNameLayout)){
                isAble = false;
            }

            if(!isAbleToRegister(secondName, secondNameLayout)){
                isAble = false;
            }
            if(isAble && !password.getText().toString().equals(rePassword.getText().toString())){
                rePasswordLayout.setErrorEnabled(true);
                rePasswordLayout.setError(getString(R.string.different));
                isAble = false;
            }
            if(NetworkUtils.getConnectivityStatus(activity) == 0){
                isAble = false;
                Snackbar.make(rootView, getString(R.string.connection_error), Snackbar.LENGTH_LONG).show();
            }

            if (isAble) {
                MultipartBody.Part image = null;
                if(uri != null){
                    image = RetrofitHelper.prepareFilePart(activity, "avatar", uri);
                }
                HashMap<String, RequestBody> user = createUser();


                model.createUser(user, image).observe(activity, registerResponse -> {

                    if(registerResponse.getRegister() != null){
                        String token = registerResponse.getRegister().getToken();
                        SharedPreferenceHelper.saveToken(activity, "Bearer "+token);
                        model.leave();
                        //Toast.makeText(activity, token, Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(activity, MainContentActivity.class));
                        activity.finish();
                    }

                });

            }


        });

        model.getIsRegisterError().observe(activity, aBoolean -> {
            if(aBoolean){

                if(model.getRegisterResponse() != null){
                    if(model.getError().getValue() != null
                        &&model.getError().getValue().equals("The email has already been taken.")){
                        Toast.makeText(activity, getString(R.string.taken_email), Toast.LENGTH_SHORT).show();
                        /*Snackbar.make(rootView, getString(R.string.taken_email)
                                ,Snackbar.LENGTH_LONG).show();*/
                    }
                    else{
                        Snackbar.make(rootView, getString(R.string.undefined_error)
                                ,Snackbar.LENGTH_LONG).show();
                    }
                }

                model.leave();
            }
        });


        return rootView;
    }

    private HashMap<String, RequestBody> createUser() {
        HashMap<String, RequestBody> user = new HashMap<>();

        user.put("first_name", RetrofitHelper.createPartFromString(firstName.getText().toString().trim()));
        user.put("last_name", RetrofitHelper.createPartFromString(secondName.getText().toString().trim()));
        user.put("email", RetrofitHelper.createPartFromString(email.getText().toString().trim()));
        user.put("password", RetrofitHelper.createPartFromString(password.getText().toString().trim()));
        if(gender.getCheckedRadioButtonId() == R.id.gender_female){
            user.put("gender", RetrofitHelper.createPartFromString("female"));
        }
        else{
            user.put("gender", RetrofitHelper.createPartFromString("male"));
        }
        user.put("grade", RetrofitHelper.createPartFromString(grade.getText().toString().trim()));

        return user;
    }


    private boolean isAbleToRegister(TextInputEditText editText, TextInputLayout inputLayout) {

        if(editText.getText() != null && editText.getText().toString().length()>0) {
            return true;
        }
        else{
            inputLayout.setErrorEnabled(true);
            inputLayout.setError(getString(R.string.empty));
            return false;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
                case READ_EXTERNAL_STORAGE_ID: {
                    if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // permission was granted, yay!
                        startActivityForResult(getImageIntent, GET_IMAGE_FROM_GALLERY);
                    }
                    return;
                }

                // other 'case' lines to check for other
                // permissions this app might request.
            }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == GET_IMAGE_FROM_GALLERY && resultCode == RESULT_OK){
            if (data != null) {
                uri = data.getData();

                Picasso.get().load(uri)
                        .into(userImage);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if(uri != null){
            outState.putString(IMAGE_URI, uri.toString());
        }
        super.onSaveInstanceState(outState);
    }
}
