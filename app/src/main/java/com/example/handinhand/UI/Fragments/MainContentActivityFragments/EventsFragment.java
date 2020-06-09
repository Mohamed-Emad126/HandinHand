package com.example.handinhand.UI.Fragments.MainContentActivityFragments;


import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.handinhand.Adapters.EventsAdapter;
import com.example.handinhand.Helpers.SharedPreferenceHelper;
import com.example.handinhand.R;
import com.example.handinhand.ViewModels.EventsViewModel;
import com.example.handinhand.ViewModels.ProfileViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EventsFragment extends Fragment {


    public EventsFragment() {
        // Required empty public constructor
    }

    private FloatingActionButton addFab;

    private RecyclerView recyclerView;
    private RelativeLayout errorPage;
    private ConstraintLayout fullLoadingView;
    private SwipeRefreshLayout refreshLayout;
    private MaterialButton reload;
    private ProgressBar loading;


    private EventsAdapter eventsAdapter;
    private EventsViewModel eventsViewModel;
    private ProfileViewModel user;

    int page = 0;
    int lastPage = 0;
    private String userId;
    private int selectedItemId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_events, container, false);

        recyclerView = rootView.findViewById(R.id.events_recycler_view);
        errorPage = rootView.findViewById(R.id.error_page);
        refreshLayout = rootView.findViewById(R.id.events_swipe_refresh_layout);
        fullLoadingView = rootView.findViewById(R.id.full_loading_view);
        reload = rootView.findViewById(R.id.reload);
        loading = rootView.findViewById(R.id.loading_view_progressbar);


        eventsAdapter = new EventsAdapter(rootView);
        FragmentActivity activity = requireActivity();

        eventsViewModel = new ViewModelProvider(activity).get(EventsViewModel.class);
        //sharedItemViewModel = new ViewModelProvider(activity).get(SharedItemViewModel.class);
        user = new ViewModelProvider(activity).get(ProfileViewModel.class);
        user.getProfile(SharedPreferenceHelper.getToken(requireContext())).observe(requireActivity(),
                profile -> {
                    userId = profile.getDetails().getUser().getId();
                });

        initRecyclerView(rootView);
        eventsViewModel.getmResponse(page);

        /*addFab = rootView.findViewById(R.id.items_fab);
        addFab.setOnClickListener(view -> {
            FragmentNavigator.Extras extra = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                addFab.setTransitionName("FloatingActionButtonTransition");
                extra = new FragmentNavigator.Extras.Builder()
                        .addSharedElement(addFab, "FloatingActionButtonTransition")
                        .build();
            }
            Navigation.findNavController(rootView).navigate(
                    R.id.action_itemsFragment_to_addItemFragment,
                    null,
                    null,
                    extra
            );
            addFab.hide();
        });*/

        reload.setOnClickListener(view ->
                eventsViewModel.refresh()
        );

        refreshLayout.setOnRefreshListener(() -> {
            eventsViewModel.refresh();
            page = 0;
            eventsAdapter.clearAll();
        });


        eventsViewModel.getIsError().observe(activity, aBoolean -> {
            if (aBoolean) {
                loading.setVisibility(View.GONE);
                Toast.makeText(activity, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                eventsViewModel.setIsError(false);
            }
        });

        eventsViewModel.getIsFirstLoading().observe(activity, aBoolean -> {
            refreshLayout.setRefreshing(false);
            if (aBoolean) {
                fullLoadingView.setVisibility(View.VISIBLE);
                errorPage.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            }
        });

        eventsViewModel.getIsFirstError().observe(activity, aBoolean -> {
            if (aBoolean) {
                fullLoadingView.setVisibility(View.GONE);
                errorPage.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });

        eventsViewModel.getPage().observe(activity, integer -> {
            page = integer;
            if (page == lastPage) {
                loading.setVisibility(View.GONE);
                Toast.makeText(activity, R.string.end_of_list, Toast.LENGTH_SHORT).show();
            }
        });

        eventsViewModel.getLastPage().observe(activity, integer ->
                lastPage = integer
        );

        eventsViewModel.getIsLoading().observe(activity, aBoolean -> {
            if (aBoolean) {
                loading.setVisibility(View.VISIBLE);
            } else {
                loading.setVisibility(View.GONE);
            }
        });

        eventsViewModel.getmList().observe(activity, data ->
                eventsAdapter.setEventsList(data)
        );

        return rootView;
    }

    private void initRecyclerView(View rootView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(rootView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setSaveEnabled(true);
        recyclerView.setAdapter(eventsAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setSaveEnabled(true);

        eventsAdapter.setOnEventClickListener(new EventsAdapter.OnEventClickListener() {
            @Override
            public void OnEventClicked(int position, ImageView imageView) {
                //TODO: Complete the event description
                FragmentNavigator.Extras extra = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    imageView.setTransitionName("event_1");
                    extra = new FragmentNavigator.Extras.Builder()
                            .addSharedElement(imageView, "imageView")
                            .build();
                }

                Navigation.findNavController(rootView).navigate(
                        R.id.action_eventsFragment_to_eventDescriptionFragment,
                        null,
                        null,
                        extra
                );
            }

            @Override
            public void OnEventInterest(int position) {
                eventsAdapter.interestEvent(position);
                eventsViewModel.interestEvent(eventsAdapter.getEventsList());
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        Toast.makeText(getActivity(), "End", Toast.LENGTH_SHORT).show();
                        if (page != lastPage || (eventsViewModel.getIsLoading().getValue() != null &&
                                eventsViewModel.getIsLoading().getValue())) {
                            eventsViewModel.loadNextPage(page+1);
                        }
                    }
                }
            }
        });
    }

}
