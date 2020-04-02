package com.example.handinhand.UI.Fragments.MainContentActivityFragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.transition.TransitionInflater;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.example.handinhand.Helpers.PermissionsHelper;
import com.example.handinhand.Helpers.RetrofitHelper;
import com.example.handinhand.Helpers.SharedPreferenceHelper;
import com.example.handinhand.R;
import com.example.handinhand.Utils.NetworkUtils;
import com.example.handinhand.ViewModels.AddItemViewModel;
import com.example.handinhand.ViewModels.ProfileViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
import java.util.Objects;
import okhttp3.RequestBody;
import static android.app.Activity.RESULT_OK;
import static com.example.handinhand.UI.Fragments.MainActivityFragments.RegisterFragment.IMAGE_URI;

public class AddItemFragment extends Fragment {

    private static final int GET_IMAGE_FROM_GALLERY = 4;
    private static final int READ_EXTERNAL_STORAGE_ID = 1;
    private Uri uri;
    private Intent getImageIntent;

    public AddItemFragment() {
    }

    private CoordinatorLayout layout;
    private Toolbar toolbar;
    private RadioGroup priceRadioGroup;
    private TextInputLayout priceLayout;
    private TextInputEditText priceEditText;
    private TextInputEditText titleEditText;
    private TextInputEditText descriptionEditText;
    private TextInputEditText facebookUrl;
    private TextInputEditText phoneNumber;

    private ImageView closeIcon;
    private ImageView itemImage;
    private CardView cardImage;
    private AddItemViewModel addItemViewModel;
    private ProfileViewModel profileViewModel;
    private MenuItem shareItem;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSharedElementEnterTransition(TransitionInflater.from(getActivity())
                                .inflateTransition(android.R.transition.move));
            setSharedElementReturnTransition(TransitionInflater.from(getActivity())
                    .inflateTransition(android.R.transition.move));
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_add_item, container, false);
        layout = rootView.findViewById(R.id.add_item_coordinator);
        FragmentActivity activity = getActivity();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            layout.setTransitionName("FloatingActionButtonTransition");
        }

        addItemViewModel = new ViewModelProvider(activity).get(AddItemViewModel.class);
        profileViewModel = new ViewModelProvider(activity).get(ProfileViewModel.class);
        toolbar = rootView.findViewById(R.id.add_item_toolbar);
        priceRadioGroup = rootView.findViewById(R.id.add_item_radio_group);
        priceLayout = rootView.findViewById(R.id.add_item_price_layout);

        priceEditText = rootView.findViewById(R.id.add_item_price);
        titleEditText = rootView.findViewById(R.id.add_item_title);
        facebookUrl = rootView.findViewById(R.id.add_item_Facebook);
        phoneNumber = rootView.findViewById(R.id.add_item_phone_number);
        descriptionEditText = rootView.findViewById(R.id.add_item_description);

        cardImage = rootView.findViewById(R.id.add_item_card_image);
        itemImage = rootView.findViewById(R.id.add_item_image);
        closeIcon = rootView.findViewById(R.id.close_icon);
        shareItem = toolbar.getMenu().getItem(0);


        if (savedInstanceState != null && savedInstanceState.getString(IMAGE_URI) != null) {
            uri = Uri.parse(savedInstanceState.getString(IMAGE_URI));
            Picasso.get()
                    .load(uri)
                    .placeholder(R.drawable.ic_uploading)
                    .into(itemImage);
            closeIcon.setVisibility(View.VISIBLE);
        }
        else{
            closeIcon.setVisibility(View.GONE);
        }
        getImageIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        itemImage.setOnClickListener(view -> {
            selectImage(activity);
        });
        closeIcon.setOnClickListener(view -> {
            closeIcon.setVisibility(View.GONE);
            Picasso.get()
                    .load(R.drawable.ic_uploading)
                    .into(itemImage);
            uri = null;
        });

        priceRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if(i == R.id.free_item){
                priceLayout.setVisibility(View.GONE);
            }
            else{
                priceLayout.setVisibility(View.VISIBLE);
            }
        });

        toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.action_share){
                if(checkEmptyCells(rootView)){
                    HashMap<String, RequestBody> itemInfo = createItem();
                    if(profileViewModel.getProfile(SharedPreferenceHelper.getToken(activity))
                            .getValue() != null
                    && profileViewModel.getProfile(SharedPreferenceHelper.getToken(activity))
                            .getValue().getStatus()){


                        if(NetworkUtils.getConnectivityStatus(activity)
                                == NetworkUtils.TYPE_NOT_CONNECTED){
                            Snackbar.make(rootView, getString(R.string.connection_error),
                                    Snackbar.LENGTH_LONG).show();
                        }
                        else{
                            addItemViewModel.getmResponse(
                                    SharedPreferenceHelper.getToken(activity),
                                    profileViewModel.getProfile(SharedPreferenceHelper.getToken(activity))
                                            .getValue().getDetails().getUser().getId(),
                                    itemInfo,
                                    RetrofitHelper.prepareFilePart(activity, "image", uri)
                            ).observe(activity, addItemResponse -> {
                                if (addItemResponse.getStatus()) {
                                    Toast.makeText(activity, R.string.item_added, Toast.LENGTH_SHORT).show();
                                    setHideSoftKeyboard(rootView);
                                    Navigation.findNavController(rootView).navigateUp();
                                    addItemViewModel.leave();
                                }
                                else{
                                    Toast.makeText(activity, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                                }

                            });
                        }
                    }
                    else{
                        Toast.makeText(activity, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            return true;
        });

        addItemViewModel.getIsError().observe(activity, aBoolean -> {
            if(aBoolean){
                Toast.makeText(activity, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
            }
        });
        addItemViewModel.getIsLoading().observe(activity, aBoolean -> {
            if(aBoolean){
                shareItem.setEnabled(false);
                closeIcon.setVisibility(View.GONE);
                titleEditText.setEnabled(false);
                priceEditText.setEnabled(false);
                descriptionEditText.setEnabled(false);
                facebookUrl.setEnabled(false);
                phoneNumber.setEnabled(false);
            }
            else{
                shareItem.setEnabled(true);
                closeIcon.setVisibility(View.VISIBLE);
                titleEditText.setEnabled(true);
                priceEditText.setEnabled(true);
                descriptionEditText.setEnabled(true);
                facebookUrl.setEnabled(true);
                phoneNumber.setEnabled(true);
            }
        });

        toolbar.setNavigationOnClickListener(view ->{
                Navigation.findNavController(rootView).navigateUp();
                setHideSoftKeyboard(rootView);
                addItemViewModel.leave();
        }
        );


        return rootView;
    }

    public void setHideSoftKeyboard(View view) {
        InputMethodManager mInputMethodManager = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    private HashMap<String, RequestBody> createItem() {
        /*
        title
        description
        price (format : nnnnn.dd)
        phone (max:15 number)
        facebook
        image (file)
        */
        HashMap <String, RequestBody> item = new HashMap<>();
        item.put("title", RetrofitHelper.createPartFromString(titleEditText.getText().toString().trim()));
        item.put("description", RetrofitHelper.createPartFromString(descriptionEditText.getText().toString().trim()));
        if (priceRadioGroup.getCheckedRadioButtonId() == R.id.price_item) {
            item.put("price", RetrofitHelper.createPartFromString(priceEditText.getText().toString().trim()));
        }
        else{
            item.put("price", RetrofitHelper.createPartFromString("0"));
        }
        item.put("phone", RetrofitHelper.createPartFromString(phoneNumber.getText().toString().trim()));
        item.put("facebook", RetrofitHelper.createPartFromString(facebookUrl.getText().toString().trim()));
        return item;
    }

    private boolean checkEmptyCells(View rootView) {
        TextInputEditText []requiredFields = {
                titleEditText,
                priceEditText,
                facebookUrl,
                phoneNumber
        };
        if(uri == null){
            Snackbar.make(rootView, R.string.upload_image, Snackbar.LENGTH_LONG).show();
            return false;
        }
        for(int i=0; i<requiredFields.length; i++){
            if(requiredFields[i].getText().toString().trim().length() ==0){
                if(i == 1&& priceRadioGroup.getCheckedRadioButtonId() == R.id.price_item){
                    requiredFields[i].setError(getString(R.string.required));
                    return false;
                }
                else if(i ==1){
                    continue;
                }
                requiredFields[i].setError(getString(R.string.required));
                return false;
            }
            else if(i == 2 && requiredFields[i].getText().toString().trim().length() !=0){
                if(URLUtil.isValidUrl(facebookUrl.getText().toString().trim())){
                    requiredFields[i].setError(getString(R.string.validate_email));
                    return false;
                }
            }
        }
        return true;
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
        if(uri != null){
            outState.putString(IMAGE_URI, uri.toString());
        }
        super.onSaveInstanceState(outState);
    }
}
