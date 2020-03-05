package com.example.handinhand.MainContent;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.handinhand.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemsFragment extends Fragment {

    private FloatingActionButton addFab;

    public ItemsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_items, container, false);

        addFab = rootView.findViewById(R.id.items_fab);
        final ImageView imageView = rootView.findViewById(R.id.item_image);

        addFab.setOnClickListener(view -> {

        });

        rootView.findViewById(R.id.item).setOnClickListener(view -> {

            FragmentNavigator.Extras extras = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                extras = new FragmentNavigator.Extras.Builder()
                        .addSharedElement(imageView, imageView.getTransitionName())
                        .addSharedElement(rootView.findViewById(R.id.item_title), "itemTitle")
                        .build();
            }
            Navigation.findNavController(view).navigate(R.id.action_itemsFragment_to_itemDescriptionFragment,
                    null, // Bundle of args
                    null, // NavOptions
                    extras);
        });


        return rootView;
    }

}
