package com.example.handinhand.MainContent;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.example.handinhand.Helpers.PermissionsHelper;
import com.example.handinhand.Helpers.SharedPreferenceHelper;
import com.example.handinhand.Models.Profile;
import com.example.handinhand.R;
import com.example.handinhand.ViewModels.EditProfileViewModel;
import com.example.handinhand.ViewModels.ProfileViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;
import java.util.Objects;
import de.hdodenhof.circleimageview.CircleImageView;
import static android.app.Activity.RESULT_OK;

public class EditProfileFragment extends Fragment {


    private static final int GET_IMAGE_FROM_GALLERY = 3;
    private static final int READ_EXTERNAL_STORAGE_ID = 1;
    private static final String IMAGE_URI = "URI";
    private Toolbar toolbar;
    private CircleImageView userImage;

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

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        toolbar = rootView.findViewById(R.id.edit_profile_toolbar);
        //FragmentActivity activity = getActivity();
        userImage = rootView.findViewById(R.id.edit_profile_image);
        firstName = rootView.findViewById(R.id.edit_profile_first_name_text);
        firstNameLayout = rootView.findViewById(R.id.edit_profile_first_name_layout);

        secondName = rootView.findViewById(R.id.edit_profile_second_name_text);
        secondNameLayout = rootView.findViewById(R.id.edit_profile_second_name_layout);

        grade = rootView.findViewById(R.id.edit_profile_grade_text);
        gradeLayout = rootView.findViewById(R.id.edit_profile_grade_layout);

        getImageIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        FragmentActivity activity = getActivity();

        if (savedInstanceState != null && savedInstanceState.getString(IMAGE_URI) != null) {
            uri = Uri.parse(savedInstanceState.getString(IMAGE_URI));
            Picasso.get().load(uri).into(userImage);
        }

        model = new ViewModelProvider(activity).get(ProfileViewModel.class);
        editModel = new ViewModelProvider(activity).get(EditProfileViewModel.class);


        model.getProfile(SharedPreferenceHelper.getToken(activity)).observe(activity,
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
                );
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

                    Picasso.get().load(R.drawable.male_avatar)
                            .into(userImage);
                }
                else{
                    Picasso.get().load(R.drawable.female_avatar)
                            .into(userImage);
                }
            }
            else{
                Picasso.get().load("http://400b3c69.ngrok.io/storage/avatars/" +
                        user.getInfo().getAvatar())
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
                }
                )
                .setOnDismissListener(dialogInterface -> {
                    editModel.setIsDialogShowed(false);
                })
        .create()
        ;

        editModel.getIsDialogShowed().observe(activity, aBoolean -> {
            if(aBoolean){
                dialogBuilder.show();
            }
            else{
                dialogBuilder.dismiss();
            }
        });

        userImage.setOnClickListener(view -> {
            //selectImage(activity);
            editModel.setIsDialogShowed(true);
        });

        toolbar.setNavigationOnClickListener(view -> {
            Navigation.findNavController(rootView).popBackStack();
            editModel.leave();
        });

        toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.action_save){
                //TODO: Save the changes and leave the fragment


            }
            return true;
        });


        return rootView;
    }

    private void selectImage(FragmentActivity activity) {
        if (PermissionsHelper.canReadExternalStorage(getContext())) {

            startActivityForResult(getImageIntent, GET_IMAGE_FROM_GALLERY);

        }
        else{
            ActivityCompat.requestPermissions(Objects.requireNonNull(activity),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE_ID);
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
                editModel.setIsImageRemoved(false);
                editModel.setIsDialogShowed(false);
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
