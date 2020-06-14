package com.example.handinhand.UI.Fragments.MainContentActivityFragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.transition.TransitionInflater;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.handinhand.Helpers.PermissionsHelper;
import com.example.handinhand.Helpers.RetrofitHelper;
import com.example.handinhand.Helpers.SharedPreferenceHelper;
import com.example.handinhand.R;
import com.example.handinhand.Utils.NetworkUtils;
import com.example.handinhand.ViewModels.AddEventViewModel;
import com.example.handinhand.ViewModels.ProfileViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;
import static com.example.handinhand.UI.Fragments.MainActivityFragments.RegisterFragment.IMAGE_URI;

public class AddEventFragment extends Fragment {

    private static final int GET_IMAGE_FROM_GALLERY = 4;
    private static final int READ_EXTERNAL_STORAGE_ID = 77;
    private Uri uri;
    private Intent getImageIntent;

    public AddEventFragment() {
    }

    private CoordinatorLayout layout;
    private SwitchDateTimeDialogFragment dateTimeDialog;
    private Toolbar toolbar;
    private TextInputEditText titleEditText;
    private TextInputEditText descriptionEditText;
    private TextInputEditText about;
    private TextInputEditText location;
    private TextView dateText;

    private ImageView closeIcon;
    private ImageView itemImage;
    long dateTime = 0;
    private CardView cardImage;
    private AddEventViewModel addEventViewModel;
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
        final View rootView = inflater.inflate(R.layout.fragment_add_event, container, false);
        layout = rootView.findViewById(R.id.add_event_coordinator);
        FragmentActivity activity = requireActivity();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            layout.setTransitionName("FloatingActionButtonTransition");
        }

        addEventViewModel = new ViewModelProvider(activity).get(AddEventViewModel.class);
        profileViewModel = new ViewModelProvider(activity).get(ProfileViewModel.class);
        toolbar = rootView.findViewById(R.id.add_event_toolbar);

        titleEditText = rootView.findViewById(R.id.add_event_title);
        descriptionEditText = rootView.findViewById(R.id.add_event_description);
        about = rootView.findViewById(R.id.add_event_about);
        dateText = rootView.findViewById(R.id.add_event_date);
        location = rootView.findViewById(R.id.add_event_location);

        cardImage = rootView.findViewById(R.id.add_event_card_image);
        itemImage = rootView.findViewById(R.id.add_event_image);
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
        if (savedInstanceState != null && savedInstanceState.containsKey("dateTime")) {
            dateTime = savedInstanceState.getLong("dateTime");
        } else {
            dateTime = 0;
        }
        getImageIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        itemImage.setOnClickListener(view -> {
            selectImage(activity);
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
                    if (profileViewModel.getProfile(SharedPreferenceHelper.getToken(activity))
                            .getValue() != null
                            && profileViewModel.getProfile(SharedPreferenceHelper.getToken(activity))
                            .getValue().getStatus()) {


                        if (NetworkUtils.getConnectivityStatus(activity)
                                == NetworkUtils.TYPE_NOT_CONNECTED) {
                            Snackbar.make(rootView, getString(R.string.connection_error),
                                    Snackbar.LENGTH_LONG).show();
                        } else {
                            addEventViewModel.getmResponse(
                                    SharedPreferenceHelper.getToken(activity),
                                    profileViewModel.getProfile(SharedPreferenceHelper.getToken(activity))
                                            .getValue().getDetails().getUser().getId(),
                                    itemInfo,
                                    RetrofitHelper.prepareFilePart(activity, "image", uri)
                            ).observe(activity, addEventResponse  -> {
                                if (addEventResponse.getStatus()) {
                                    Toast.makeText(activity, R.string.item_added, Toast.LENGTH_SHORT).show();
                                    setHideSoftKeyboard(rootView);
                                    Navigation.findNavController(rootView).navigateUp();
                                    addEventViewModel.leave();
                                } else {
                                    Toast.makeText(activity, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                                }

                            });
                        }
                    } else {
                        Toast.makeText(activity, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            return true;
        });

        dateText.setOnClickListener(view -> createDialog());

        addEventViewModel.getIsError().observe(activity, aBoolean -> {
            if (aBoolean) {
                Toast.makeText(activity, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
            }
        });
        addEventViewModel.getIsLoading().observe(activity, aBoolean -> {
            if (aBoolean) {
                shareItem.setEnabled(false);
                closeIcon.setVisibility(View.GONE);
                titleEditText.setEnabled(false);
                descriptionEditText.setEnabled(false);
                dateText.setEnabled(false);
                about.setEnabled(false);
                location.setEnabled(false);
            } else {
                shareItem.setEnabled(true);
                closeIcon.setVisibility(View.VISIBLE);
                titleEditText.setEnabled(true);
                descriptionEditText.setEnabled(true);
                about.setEnabled(true);
                dateText.setEnabled(true);
                location.setEnabled(true);
            }
        });

        toolbar.setNavigationOnClickListener(view -> {
                    Navigation.findNavController(rootView).navigateUp();
                    setHideSoftKeyboard(rootView);
                    addEventViewModel.leave();
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
        HashMap<String, RequestBody> item = new HashMap<>();
        item.put("title", RetrofitHelper.createPartFromString(titleEditText.getText().toString().trim()));
        item.put("description", RetrofitHelper.createPartFromString(descriptionEditText.getText().toString().trim()));
        item.put("about", RetrofitHelper.createPartFromString(about.getText().toString().trim()));
        item.put("date", RetrofitHelper.createPartFromString(dateText.getText().toString().trim()));
        item.put("location", RetrofitHelper.createPartFromString(location.getText().toString().trim()));
        return item;
    }

    private boolean checkEmptyCells(View rootView) {
        TextInputEditText[] requiredFields = {
                titleEditText,
                location,
                about
        };
        if(dateText.getText().toString().equals("Date")){
            Toast.makeText(getActivity(), R.string.date_required, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (uri == null) {
            Toast.makeText(getActivity(), R.string.upload_image, Toast.LENGTH_SHORT).show();
            //Snackbar.make(rootView, R.string.upload_image, Snackbar.LENGTH_LONG).show();
            return false;
        }
        for (TextInputEditText requiredField : requiredFields) {
            if (requiredField.getText().toString().trim().length() == 0) {
                requiredField.setError(getString(R.string.required));
                return false;
            }
        }
        return true;
    }

    private void selectImage(FragmentActivity activity) {
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
        if(dateTime != 0){
            outState.putLong("dateTime", dateTime);
        }
        super.onSaveInstanceState(outState);
    }

    private void createDialog() {
        dateTimeDialog = SwitchDateTimeDialogFragment.newInstance(
                getString(R.string.date_picker),
                getString(android.R.string.ok),
                getString(android.R.string.cancel)
        );
        dateTimeDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat);
        dateTimeDialog.setAlertStyle(R.style.pickerStyle);
        dateTimeDialog.startAtCalendarView();
        dateTimeDialog.set24HoursMode(true);
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = new GregorianCalendar();
        dateTimeDialog.setMinimumDateTime(new GregorianCalendar(cal.get(Calendar.YEAR)-1900
                , cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).getTime());
        dateTimeDialog.setOnButtonClickListener(
                new SwitchDateTimeDialogFragment.OnButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Date date) {
                        dateTime = date.getTime();
                        dateText.setText(df.format(date.getTime()));
                        //((TextView)findViewById(R.id.textView)).setText(df.format(date.getTime()));
                    }
                    @Override
                    public void onNegativeButtonClick(Date date) {
                        dateTimeDialog.dismiss();
                    }
                });
        dateTimeDialog.show(getActivity().getSupportFragmentManager(), "dialog_time");
    }
}