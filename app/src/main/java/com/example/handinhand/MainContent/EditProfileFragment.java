package com.example.handinhand.MainContent;


import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.handinhand.R;
import com.example.handinhand.ViewModels.ProfileViewModel;

public class EditProfileFragment extends Fragment {


    private Toolbar toolbar;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        toolbar = rootView.findViewById(R.id.profile_toolbar);
        //FragmentActivity activity = getActivity();

        toolbar.setNavigationOnClickListener(view -> {
            Navigation.findNavController(rootView).popBackStack();
            //TODO: Leave the model in the ViewModel of the Fragment
        });

        return rootView;
    }

}
