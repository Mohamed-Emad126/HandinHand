package com.example.handinhand.MainContent;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.handinhand.R;

public class EditProfileFragment extends Fragment {


    private Toolbar toolbar;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        toolbar = rootView.findViewById(R.id.edit_profile_toolbar);
        //FragmentActivity activity = getActivity();


        toolbar.setNavigationOnClickListener(view -> {
            Navigation.findNavController(rootView).popBackStack();
            //TODO: Leave the model in the ViewModel of the Fragment
        });

        toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.action_save){
                //TODO: Save the changes and leave the fragment
            }
            return true;
        });

        return rootView;
    }

}
