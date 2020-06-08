package com.example.handinhand.UI.Fragments.MainContentActivityFragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.transition.TransitionInflater;

import com.example.handinhand.R;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class EventDescriptionFragment extends Fragment {

    public EventDescriptionFragment() {
    }

    private Toolbar toolbar;
    private SwipeRefreshLayout refreshLayout;
    private KenBurnsView kenBurnsView;
    private FloatingActionButton interestFab;
    private TextView title;
    private TextView about;
    private TextView description;
    private TextView location;
    private TextView interests;

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
        final View rootView = inflater.inflate(R.layout.fragment_event_description,
                container, false);

        toolbar = rootView.findViewById(R.id.event_toolbar);
        refreshLayout = rootView.findViewById(R.id.swipe_refresh_in_event_description);
        kenBurnsView = rootView.findViewById(R.id.image_ken);
        interestFab = rootView.findViewById(R.id.interest_fab);
        title = rootView.findViewById(R.id.event_title);
        about = rootView.findViewById(R.id.event_about);
        description = rootView.findViewById(R.id.event_description);
        location = rootView.findViewById(R.id.event_location);
        interests = rootView.findViewById(R.id.event_interests);


        return rootView;
    }

}
