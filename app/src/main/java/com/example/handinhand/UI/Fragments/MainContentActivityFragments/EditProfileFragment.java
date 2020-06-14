package com.example.handinhand.UI.Fragments.MainContentActivityFragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.handinhand.Helpers.PermissionsHelper;
import com.example.handinhand.Helpers.RetrofitHelper;
import com.example.handinhand.Helpers.SharedPreferenceHelper;
import com.example.handinhand.Models.Profile;
import com.example.handinhand.R;
import com.example.handinhand.ViewModels.EditProfileViewModel;
import com.example.handinhand.ViewModels.ProfileViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ldoublem.loadingviewlib.view.LVNews;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;

public class EditProfileFragment extends Fragment {


    private static final int GET_IMAGE_FROM_GALLERY = 3;
    private static final int READ_EXTERNAL_STORAGE_ID = 55;
    private static final String IMAGE_URI = "URI";
    private Toolbar toolbar;
    private CircleImageView userImage;
    private ConstraintLayout fullLoadingView;
    private LVNews loadingView;

    private TextInputEditText firstName;
    private TextInputLayout firstNameLayout;

    private TextInputEditText secondName;
    private TextInputLayout secondNameLayout;

    private TextInputEditText grade;
    private TextInputLayout gradeLayout;

    private ProfileViewModel model;
    private EditProfileViewModel editModel;
    private Profile.User user;

    private Intent getImageIntent;
    private Uri uri;
    private AlertDialog dialogBuilder;

    private MenuItem saveMenuItem;
    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        toolbar = rootView.findViewById(R.id.edit_profile_toolbar);
        userImage = rootView.findViewById(R.id.edit_profile_image);
        firstName = rootView.findViewById(R.id.edit_profile_first_name_text);
        firstNameLayout = rootView.findViewById(R.id.edit_profile_first_name_layout);

        secondName = rootView.findViewById(R.id.edit_profile_second_name_text);
        secondNameLayout = rootView.findViewById(R.id.edit_profile_second_name_layout);

        grade = rootView.findViewById(R.id.edit_profile_grade_text);
        gradeLayout = rootView.findViewById(R.id.edit_profile_grade_layout);
        saveMenuItem = toolbar.getMenu().getItem(0);

        fullLoadingView = rootView.findViewById(R.id.full_loading_view);
        loadingView = rootView.findViewById(R.id.loading_view);
        loadingView.setViewColor(Color.rgb(255, 0, 0));
        loadingView.startAnim(1000);

        getImageIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        FragmentActivity activity = requireActivity();

        if (savedInstanceState != null && savedInstanceState.getString(IMAGE_URI) != null) {
            uri = Uri.parse(savedInstanceState.getString(IMAGE_URI));
            Glide.with(rootView).load(uri).diskCacheStrategy(DiskCacheStrategy.DATA).into(userImage);
        }

        model = new ViewModelProvider(activity).get(ProfileViewModel.class);
        editModel = new ViewModelProvider(activity).get(EditProfileViewModel.class);

        editModel.getIsLoading().observe(activity, aBoolean -> {
            if(aBoolean){
                fullLoadingView.setVisibility(View.VISIBLE);
                saveMenuItem.setEnabled(false);
            }
            else{
                fullLoadingView.setVisibility(View.GONE);
                saveMenuItem.setEnabled(true);
            }
        });

        editModel.getIsError().observe(activity, aBoolean -> {
            if(aBoolean){
                Toast.makeText(activity, getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
            }
        });

        Profile value = model.getProfile(SharedPreferenceHelper.getToken(activity)).getValue();

        if(value != null &&
                value.getStatus()){

            user = value.getDetails().getUser();
            editModel.setUser(user);
        }
        else{
            Toast.makeText(activity, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
            Navigation.findNavController(rootView).navigateUp();
        }

        /*model.getProfile(SharedPreferenceHelper.getToken(activity)).observe(activity,
                profile -> {
                    if(profile != null &&
                            profile.getStatus()){

                        user = profile.getDetails().getUser();
                        editModel.setUser(user);
                    }
                    else{
                        Toast.makeText(activity, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(rootView).navigateUp();
                    }
                }
                );*/
        editModel.getIsImageRemoved().observe(activity, aBoolean -> {
            if(aBoolean){
                userImage = null;
                userImage = rootView.findViewById(R.id.edit_profile_image);
                if(user.getInfo()
                        .getGender().contains("male")){
                    Picasso.get().load(R.drawable.male_avatar)
                            .placeholder(R.drawable.male_avatar)
                            .into(userImage);
                }
                else{
                    Picasso.get().load(R.drawable.female_avatar)
                            .placeholder(R.drawable.female_avatar)
                            .into(userImage);
                }
            }
        });

        editModel.getUser().observe(activity, user -> {
            firstName.setText(user.getInfo().getFirst_name());
            secondName.setText(user.getInfo().getLast_name());
            grade.setText(user.getInfo().getGrade());

            if(user.getInfo()
                    .getAvatar().contains("default") ||
                    (editModel.getIsImageRemoved().getValue() != null
                            && editModel.getIsImageRemoved().getValue())){

                if(user.getInfo()
                        .getGender().contains("male")){

                    Glide.with(rootView).load(R.drawable.male_avatar)
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .placeholder(R.drawable.male_avatar)
                            .into(userImage);
                }
                else{
                    Glide.with(rootView).load(R.drawable.female_avatar)
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .placeholder(R.drawable.female_avatar)
                            .into(userImage);
                }
            }
            else{
                Glide.with(rootView).load(getString(R.string.avatar_url) +
                        user.getInfo().getAvatar())
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .error(R.drawable.female_avatar)
                        .placeholder(R.drawable.female_avatar)
                        .into(userImage);

            }
        });



        dialogBuilder = new MaterialAlertDialogBuilder(activity)
                .setCancelable(true)
                .setItems(R.array.edit_profile_dialog_choices, (dialogInterface, i) -> {
                    if(i == 0){
                        uri = null;
                        editModel.setIsImageRemoved(true);
                        editModel.setIsDialogShowed(false);
                    }
                    else{
                        selectImage(activity);
                    }
                })
                .setOnDismissListener(dialogInterface ->
                    editModel.setIsDialogShowed(false)
                )
                .create();

        editModel.getIsDialogShowed().observe(activity, aBoolean -> {
            if(aBoolean){
                dialogBuilder.show();
            }
            else{
                dialogBuilder.dismiss();
            }
        });

        userImage.setOnClickListener(view ->
            editModel.setIsDialogShowed(true)
        );

        toolbar.setNavigationOnClickListener(view -> {
            Navigation.findNavController(rootView).popBackStack();
            editModel.leave();
        });

        toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.action_save){
                //TODO: Save the changes and leave the fragment
                if(firstName.getText()!= null
                &&firstName.getText().toString().length()== 0){
                    firstNameLayout.setError(getString(R.string.empty));
                }
                else if(secondName.getText()!= null
                        &&secondName.getText().toString().length() == 0){
                    secondNameLayout.setError(getString(R.string.empty));
                }
                else{

                    HashMap<String, RequestBody> updates = getUpdates(activity);
                    if(editModel.getIsImageRemoved().getValue()!= null
                        && editModel.getIsImageRemoved().getValue()){

                        updates.remove("avatar");
                        updates.put("avatar", RetrofitHelper.createPartFromString("default.png"));
                        editModel.getResponse(
                                SharedPreferenceHelper.getToken(activity),
                                updates,
                                null
                        ).observe(activity, profileUpdateResponse -> {
                            if(profileUpdateResponse != null
                                && profileUpdateResponse.getStatus()){
                                Toast.makeText(activity, getString(R.string.update_successfully),
                                        Toast.LENGTH_LONG).show();
                                model.refresh(SharedPreferenceHelper.getToken(activity));
                                Navigation.findNavController(rootView).navigateUp();
                                editModel.leave();
                            }
                        });
                    }
                    else if(uri != null){

                        editModel.getResponse(
                                SharedPreferenceHelper.getToken(activity),
                                updates,
                                RetrofitHelper.prepareFilePart(activity, "avatar", uri)
                        ).observe(activity, profileUpdateResponse -> {
                            if(profileUpdateResponse != null
                                    && profileUpdateResponse.getStatus()){
                                Toast.makeText(activity, getString(R.string.update_successfully),
                                        Toast.LENGTH_LONG).show();
                                model.refresh(SharedPreferenceHelper.getToken(activity));
                                Navigation.findNavController(rootView).navigateUp();
                                editModel.leave();
                            }
                        });
                    }
                    else{
                        editModel.getResponse(
                                SharedPreferenceHelper.getToken(activity),
                                updates,
                                null
                        ).observe(activity, profileUpdateResponse -> {
                            if(profileUpdateResponse != null
                                    && profileUpdateResponse.getStatus()){
                                Toast.makeText(activity, getString(R.string.update_successfully),
                                        Toast.LENGTH_LONG).show();
                                model.refresh(SharedPreferenceHelper.getToken(activity));
                                Navigation.findNavController(rootView).navigateUp();
                                editModel.leave();
                            }
                        });
                    }
                }
            }
            return true;
        });


        return rootView;
    }

    private HashMap<String, RequestBody> getUpdates(FragmentActivity activity) {
        HashMap<String, RequestBody> updates = new HashMap<>();

        model.getProfile(SharedPreferenceHelper.getToken(activity)).observe(activity,
                profile -> {
                    if(profile != null &&
                            profile.getStatus()){

                        user = profile.getDetails().getUser();

                        updates.put("_method", RetrofitHelper.createPartFromString("PATCH"));

                        updates.put("email", RetrofitHelper.createPartFromString(user.getEmail()));
                        updates.put("password", RetrofitHelper.createPartFromString(user.getPassword()));

                        updates.put("first_name", RetrofitHelper.createPartFromString(firstName.getText().toString().trim()));
                        updates.put("last_name", RetrofitHelper.createPartFromString(secondName.getText().toString().trim()));
                        updates.put("grade", RetrofitHelper.createPartFromString(grade.getText().toString().trim()));

                        updates.put("gender", RetrofitHelper.createPartFromString(user.getInfo().getGender()));
                        updates.put("avatar", RetrofitHelper.createPartFromString(user.getInfo().getAvatar()));
                    }
                }
        );



        return updates;
    }

    private void selectImage(FragmentActivity activity) {
        if (PermissionsHelper.canReadExternalStorage(getContext())) {

            startActivityForResult(getImageIntent, GET_IMAGE_FROM_GALLERY);

        }
        else{
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(requireActivity())
                        .setTitle(R.string.permission_nedded)
                        .setMessage(R.string.permission_reason2)
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> requestPermissions(
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_ID))
                        .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                        .create().show();
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_ID);
            }
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
                    startActivityForResult(getImageIntent, GET_IMAGE_FROM_GALLERY);
                }
                return;
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == GET_IMAGE_FROM_GALLERY && resultCode == RESULT_OK){
            if (data != null) {
                uri = data.getData();
                editModel.setIsImageRemoved(false);
                editModel.setIsDialogShowed(false);
                Glide.with(requireActivity())
                        .load(uri)
                        .placeholder(R.drawable.male_avatar)
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
