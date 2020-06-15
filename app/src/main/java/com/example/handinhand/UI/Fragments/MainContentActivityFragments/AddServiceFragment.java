package com.example.handinhand.UI.Fragments.MainContentActivityFragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.transition.TransitionInflater;

import com.example.handinhand.Helpers.SharedPreferenceHelper;
import com.example.handinhand.R;
import com.example.handinhand.Utils.NetworkUtils;
import com.example.handinhand.ViewModels.AddServiceViewModel;
import com.example.handinhand.ViewModels.ProfileViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;


public class AddServiceFragment extends Fragment {

    private RadioGroup priceRadioGroup;
    private TextInputLayout priceLayout;
    private TextInputEditText priceEditText;
    private TextInputEditText titleEditText;
    private TextInputLayout titleLayout;
    private TextInputEditText descriptionEditText;
    private TextInputLayout descriptionLayout;
    private TextInputEditText goal;
    private TextInputLayout goalLayout;
    private TextInputEditText target;
    private TextInputLayout targetLayout;

    private AddServiceViewModel addserviceViewModel;
    private ProfileViewModel profileViewModel;
    private MenuItem shareService;


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
        final View rootView = inflater.inflate(R.layout.fragment_add_service, container, false);
        CoordinatorLayout layout = rootView.findViewById(R.id.add_service_coordinator);
        FragmentActivity activity = requireActivity();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            layout.setTransitionName("FloatingActionButtonTransition");
        }

        addserviceViewModel = new ViewModelProvider(activity).get(AddServiceViewModel.class);
        profileViewModel = new ViewModelProvider(activity).get(ProfileViewModel.class);
        Toolbar toolbar = rootView.findViewById(R.id.add_service_toolbar);
        priceRadioGroup = rootView.findViewById(R.id.add_service_radio_group);
        priceLayout = rootView.findViewById(R.id.add_service_price_layout);
        priceEditText = rootView.findViewById(R.id.add_service_price);
        titleEditText = rootView.findViewById(R.id.service_title_input);
        titleLayout = rootView.findViewById(R.id.service_title_layout);
        descriptionEditText = rootView.findViewById(R.id.service_description_input);
        descriptionLayout = rootView.findViewById(R.id.service_description_layout);
        goal = rootView.findViewById(R.id.service_goal_input);
        goalLayout = rootView.findViewById(R.id.service_goal_layout);
        target = rootView.findViewById(R.id.service_target_input);
        targetLayout = rootView.findViewById(R.id.service_target_layout);

        shareService = toolbar.getMenu().getItem(0);


        priceRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.free_service) {
                priceLayout.setVisibility(View.GONE);
            } else {
                priceLayout.setVisibility(View.VISIBLE);
            }
        });

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_share) {
                if (checkEmptyCells()) {
                    Map<String, String> serviceInfo = createService();

                    if (NetworkUtils.getConnectivityStatus(activity)
                            == NetworkUtils.TYPE_NOT_CONNECTED) {
                        Snackbar.make(rootView, getString(R.string.connection_error),
                                Snackbar.LENGTH_LONG).show();
                    } else {
                        addserviceViewModel.getmResponse(
                                SharedPreferenceHelper.getToken(activity),
                                serviceInfo
                        ).observe(activity, addServiceResponse -> {
                            if (addServiceResponse.getStatus()) {
                                Toast.makeText(activity, R.string.service_added, Toast.LENGTH_SHORT).show();
                                setHideSoftKeyboard(rootView);
                                Navigation.findNavController(rootView).navigateUp();
                                addserviceViewModel.leave();
                            } else {
                                Toast.makeText(activity, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                            }

                        });
                    }
                }
            }
            return true;
        });

        addserviceViewModel.getIsError().observe(activity, aBoolean -> {
            if (aBoolean) {
                titleEditText.setEnabled(true);
                descriptionEditText.setEnabled(true);
                priceEditText.setEnabled(true);
                goalLayout.setEnabled(true);
                targetLayout.setEnabled(true);
                shareService.setEnabled(true);
                Toast.makeText(activity, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
            }
        });
        addserviceViewModel.getIsLoading().observe(activity, aBoolean -> {
            if (aBoolean) {
                titleLayout.setEnabled(false);
                descriptionLayout.setEnabled(false);
                priceLayout.setEnabled(false);
                goalLayout.setEnabled(false);
                targetLayout.setEnabled(false);
                shareService.setEnabled(false);
            } else {
                titleEditText.setEnabled(true);
                descriptionEditText.setEnabled(true);
                priceEditText.setEnabled(true);
                goalLayout.setEnabled(true);
                targetLayout.setEnabled(true);
                shareService.setEnabled(true);
            }
        });

        toolbar.setNavigationOnClickListener(view -> {
                    Navigation.findNavController(rootView).navigateUp();
                    setHideSoftKeyboard(rootView);
                    addserviceViewModel.leave();
                }
        );

        target.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                targetLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        goal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                goalLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        priceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                priceLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                titleLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        descriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                descriptionLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return rootView;
    }

    public void setHideSoftKeyboard(View view) {
        InputMethodManager mInputMethodManager = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    private Map<String, String> createService() {
        Map<String, String> service = new HashMap<>();
        service.put("title", titleEditText.getText().toString().trim());
        service.put("description", descriptionEditText.getText().toString().trim());
        if (priceRadioGroup.getCheckedRadioButtonId() == R.id.price_service) {
            service.put("price", priceEditText.getText().toString().trim());
        } else {
            service.put("price", "0");
        }
        service.put("goal", goal.getText().toString().trim());
        service.put("target", target.getText().toString().trim());
        return service;
    }

    private boolean checkEmptyCells() {
        TextInputEditText[] requiredFields = {
                titleEditText,
                descriptionEditText,
                priceEditText,
                goal,
                target
        };
        TextInputLayout[] requiredFieldsLayout = {
                titleLayout,
                descriptionLayout,
                priceLayout,
                goalLayout,
                targetLayout
        };
        for (int i = 0; i < requiredFields.length; i++) {
            if (requiredFields[i].getText() != null && requiredFields[i].getText().toString().trim().length() == 0) {
                if (i == 2 && priceRadioGroup.getCheckedRadioButtonId() == R.id.price_service) {
                    requiredFieldsLayout[i].setErrorEnabled(true);
                    requiredFieldsLayout[i].setError(getString(R.string.required));
                    return false;
                } else if (i == 2) {
                    continue;
                }
                requiredFieldsLayout[i].setError(getString(R.string.required));
                return false;
            }
        }
        return true;
    }
}