package com.example.handinhand.UI.Fragments.MainContentActivityFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.handinhand.R;


public class EventDescriptionFragment extends Fragment {

    public EventDescriptionFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_event_description,
                container, false);


        return rootView;
    }

}
