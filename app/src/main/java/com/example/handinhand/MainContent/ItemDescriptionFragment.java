package com.example.handinhand.MainContent;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.example.handinhand.R;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemDescriptionFragment extends Fragment {


    public ItemDescriptionFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            setSharedElementEnterTransition(TransitionInflater.from(getActivity())
                    .inflateTransition(android.R.transition.move));
            setSharedElementReturnTransition(TransitionInflater.from(getActivity())
                    .inflateTransition(android.R.transition.move));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_item_description, container, false);

        setUpToolBar(rootView);

        return rootView;
    }


    private void setUpToolBar(final View rootView) {

            Toolbar toolbar= rootView.findViewById(R.id.item_description_toolbar);

            toolbar.setNavigationIcon(R.drawable.ic_back);
            toolbar.setNavigationOnClickListener(view ->
                    Navigation.findNavController(rootView).popBackStack()
            );



            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    return false;
                }
            });

    }


}
