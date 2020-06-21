package com.example.handinhand.UI.Fragments.SettingsActivityFragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.handinhand.Helpers.PermissionsHelper;
import com.example.handinhand.Helpers.RetrofitHelper;
import com.example.handinhand.Helpers.SharedPreferenceHelper;
import com.example.handinhand.R;
import com.example.handinhand.Utils.NetworkUtils;
import com.example.handinhand.ViewModels.SettingsViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;
import static com.example.handinhand.UI.Fragments.MainActivityFragments.RegisterFragment.IMAGE_URI;

public class AskForVerificationFragment extends Fragment {

    private static final int GET_IMAGE_FROM_GALLERY = 8888;
    private static final int READ_EXTERNAL_STORAGE_ID = 5555;
    private Uri uri;
    private Intent getImageIntent;

    public AskForVerificationFragment() {
    }

    private Toolbar toolbar;
    private TextInputEditText reason;

    private ImageView closeIcon;
    private ImageView itemImage;
    private CardView cardImage;
    private SettingsViewModel settingsViewModel;
    private MenuItem shareItem;
    private Dialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_ask_for_verification, container, false);
        FragmentActivity activity = requireActivity();
        createProgressDialog();
        settingsViewModel = new ViewModelProvider(activity).get(SettingsViewModel.class);
        toolbar = rootView.findViewById(R.id.verify_toolbar);
        reason = rootView.findViewById(R.id.reason);
        cardImage = rootView.findViewById(R.id.verify_card_image);
        itemImage = rootView.findViewById(R.id.verify_image);
        closeIcon = rootView.findViewById(R.id.close_icon);
        shareItem = toolbar.getMenu().getItem(0);


        if (savedInstanceState != null && savedInstanceState.getString(IMAGE_URI) != null) {
            uri = Uri.parse(savedInstanceState.getString(IMAGE_URI));
            Glide.with(rootView)
                    .load(uri)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .placeholder(R.drawable.ic_uploading)
                    .into(itemImage);

            closeIcon.setVisibility(View.VISIBLE);
        } else {
            closeIcon.setVisibility(View.GONE);
        }
        getImageIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        itemImage.setOnClickListener(view -> {
            selectImage();
        });
        closeIcon.setOnClickListener(view -> {
            closeIcon.setVisibility(View.GONE);
            Glide.with(rootView)
                    .load(R.drawable.ic_uploading)
                    .into(itemImage);
            uri = null;
        });

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_share) {
                if (checkEmptyCells(rootView)) {
                    HashMap<String, RequestBody> itemInfo = createItem();
                    if (NetworkUtils.getConnectivityStatus(activity)
                            == NetworkUtils.TYPE_NOT_CONNECTED) {
                        Toast.makeText(activity, getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                    } else {

                        settingsViewModel.getAsk(
                                SharedPreferenceHelper.getToken(activity),
                                itemInfo,
                                RetrofitHelper.prepareFilePart(activity, "image", uri)
                        );
                    }
                }
            }
            return true;
        });

        toolbar.setNavigationOnClickListener(view -> {
                    Navigation.findNavController(rootView).navigateUp();
                    setHideSoftKeyboard(rootView);
                });

        settingsViewModel.getIsErrorAsk().observe(activity, aBoolean -> {
            if(aBoolean){
                dialog.dismiss();
                Toast.makeText(activity, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
            }
        });

        settingsViewModel.getIsLoadingAsk().observe(activity, aBoolean -> {
            if(aBoolean){
                dialog.show();
            }
            else{
                dialog.dismiss();
            }
        });

        settingsViewModel.getAsk().observe(activity, askForVerificationResponse -> {
            if(askForVerificationResponse != null && askForVerificationResponse.getStatus()){
                Toast.makeText(activity, getString(R.string.verification_send), Toast.LENGTH_SHORT).show();
                Navigation.findNavController(rootView).navigateUp();
            }
        });

        return rootView;
    }

    public void setHideSoftKeyboard(View view) {
        InputMethodManager mInputMethodManager = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private HashMap<String, RequestBody> createItem() {
        HashMap<String, RequestBody> item = new HashMap<>();
        item.put("body", RetrofitHelper.createPartFromString(reason.getText().toString().trim()));
        return item;
    }

    private boolean checkEmptyCells(View rootView) {
        if (uri == null) {
            Snackbar.make(rootView, R.string.upload_image, Snackbar.LENGTH_LONG).show();
            return false;
        }
        if (reason.getText().toString().trim().length() == 0) {
            Toast.makeText(getActivity(), getString(R.string.complete_the_required_cell), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void selectImage() {
        if (PermissionsHelper.canReadExternalStorage(getContext())) {

            startActivityForResult(getImageIntent, GET_IMAGE_FROM_GALLERY);

        } else {
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

    private void createProgressDialog(){
        dialog = new Dialog(getActivity());
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCanceledOnTouchOutside(false);
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
        if (requestCode == GET_IMAGE_FROM_GALLERY && resultCode == RESULT_OK) {
            if (data != null) {
                uri = data.getData();
                Picasso.get().load(uri)
                        .placeholder(R.drawable.ic_uploading)
                        .into(itemImage);
                closeIcon.setVisibility(View.VISIBLE);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (uri != null) {
            outState.putString(IMAGE_URI, uri.toString());
        }
        super.onSaveInstanceState(outState);
    }

}