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

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.handinhand.Adapters.EventsAdapter;
import com.example.handinhand.Helpers.SharedPreferenceHelper;
import com.example.handinhand.R;
import com.example.handinhand.ViewModels.EventSharedViewModel;
import com.example.handinhand.ViewModels.EventsViewModel;
import com.example.handinhand.ViewModels.ProfileViewModel;
import com.example.handinhand.services.InterestWorker;
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
    private EventSharedViewModel sharedEventViewModel;
    private String token;
    private ProfileViewModel user;

    int page = 0;
    int lastPage = 0;
    private String userId;
    private int selectedItemId;
    private int selectedItemPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_events, container, false);
        fullLoadingView = rootView.findViewById(R.id.full_loading_view);
        errorPage = rootView.findViewById(R.id.error_page);
        refreshLayout = rootView.findViewById(R.id.events_swipe_refresh_layout);
        reload = rootView.findViewById(R.id.reload);
        loading = rootView.findViewById(R.id.events_loading_view_progressbar);
        recyclerView = rootView.findViewById(R.id.events_recycler_view);

        eventsAdapter = new EventsAdapter(rootView);
        FragmentActivity activity = getActivity();
        token = SharedPreferenceHelper.getToken(activity);


        eventsViewModel = new ViewModelProvider(activity).get(EventsViewModel.class);
        sharedEventViewModel = new ViewModelProvider(activity).get(EventSharedViewModel.class);
        user = new ViewModelProvider(activity).get(ProfileViewModel.class);
        user.getProfile(SharedPreferenceHelper.getToken(requireContext())).observe(requireActivity(),
                profile -> {
                    userId = profile.getDetails().getUser().getId();
                    if(profile.getDetails().getUser().getIs_trusted() == 1){
                        addFab.show();
                    }
                });

        initRecyclerView(rootView);
        eventsViewModel.getmResponse(page, token);

        addFab = rootView.findViewById(R.id.events_fab);
        addFab.setOnClickListener(view -> {
            FragmentNavigator.Extras extra = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                addFab.setTransitionName("FloatingActionButtonTransition");
                extra = new FragmentNavigator.Extras.Builder()
                        .addSharedElement(addFab, "FloatingActionButtonTransition")
                        .build();
            }
            Navigation.findNavController(rootView).navigate(
                    R.id.action_eventsFragment_to_addEventFragment,
                    null,
                    null,
                    extra
            );
            addFab.hide();
        });

        reload.setOnClickListener(view ->
                eventsViewModel.refresh(token)
        );

        refreshLayout.setOnRefreshListener(() -> {
            eventsViewModel.refresh(token);
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

        eventsViewModel.getmList().observe(activity, data -> {
            fullLoadingView.setVisibility(View.GONE);
            errorPage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            eventsAdapter.setEventsList(data);
            Toast.makeText(activity, String.valueOf(data.size()), Toast.LENGTH_SHORT).show();
        });


        eventsViewModel.getSharedError().observe(activity, aBoolean -> {
            if (aBoolean) {
                Toast.makeText(activity, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                eventsViewModel.setSharedError(false);
            }
        });

        sharedEventViewModel.getDeleteAt().observe(activity, integer -> {
            if (integer != null && integer != -1) {
                eventsViewModel.deleteEvent(integer);
                sharedEventViewModel.deleteAt(-1);
            }
        });
        sharedEventViewModel.getInterestAt().observe(activity, integer -> {
            if (integer != null && integer != -1) {
                eventsViewModel.interestEvent(integer);
                sharedEventViewModel.setInterestAt(-1);
            }
        });

        return rootView;
    }

    private void initRecyclerView(View rootView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setSaveEnabled(true);
        recyclerView.setAdapter(eventsAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setSaveEnabled(true);

        eventsAdapter.setOnEventClickListener(new EventsAdapter.OnEventClickListener() {
            @Override
            public void OnEventClicked(int position, ImageView imageView) {
                sharedEventViewModel.select(eventsAdapter.getEvent(position));
                FragmentNavigator.Extras extra = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    imageView.setTransitionName("event_1");
                    extra = new FragmentNavigator.Extras.Builder()
                            .addSharedElement(imageView, "event_1")
                            .build();
                }
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);

                Navigation.findNavController(rootView).navigate(
                        R.id.action_eventsFragment_to_eventDescriptionFragment,
                        bundle,
                        null,
                        extra
                );
            }

            @Override
            public void OnEventInterest(int position) {
                interestEvent(position);
                eventsViewModel.interestEvent(position);
            }

            @Override
            public void onEventLongClicked(int position) {
                selectedItemId = eventsAdapter.getEvent(position).getId();
                selectedItemPosition = position;
                reportEvent(rootView);
            }
        });
        /*itemsAdapter.setOnItemClickListener(new ItemsAdapter.OnItemClickListener() {
            @Override
            public void OnMenuClicked(int position, View view) {

                selectedItemId = itemsAdapter.getItem(position).getId();
                selectedItemPosition = position;

                PopupMenu popup = new PopupMenu(rootView.getContext(), view);
                if (itemsAdapter.getItem(position).getUser_id().equals(userId)) {
                    popup.getMenuInflater().inflate(R.menu.out_menu, popup.getMenu());
                } else {
                    popup.getMenuInflater().inflate(R.menu.out_menu_not_mine, popup.getMenu());
                }
                popup.show();


                popup.setOnMenuItemClickListener(item -> {

                    if (item.getItemId() == R.id.report) {
                        reportItem(rootView);
                    } else if (item.getItemId() == R.id.share) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        //TODO: Change the text
                        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.items_url) +
                                itemsAdapter.getItem(position).getId());
                        sendIntent.setType("text/plain");

                        Intent shareIntent = Intent.createChooser(sendIntent, null);
                        startActivity(shareIntent);
                    } else if (item.getItemId() == R.id.delete) {
                        alertDialog.show();
                    }
                    popup.dismiss();

                    return true;
                });
            }

            @Override
            public void OnItemClicked(int position, ImageView imageView) {
                sharedItemViewModel.select(itemsAdapter.getItem(position));

                FragmentNavigator.Extras extra = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    imageView.setTransitionName("imageView");
                    extra = new FragmentNavigator.Extras.Builder()
                            .addSharedElement(imageView, "imageView")
                            .build();
                }
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);

                Navigation.findNavController(rootView).navigate(
                        R.id.action_itemsFragment_to_itemDescriptionFragment,
                        bundle,
                        null,
                        extra
                );
            }

        });*/


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        Toast.makeText(getActivity(), "End", Toast.LENGTH_SHORT).show();
                        //show your loading view
                        // load content in background
                        if (page != lastPage || (eventsViewModel.getIsLoading().getValue() != null &&
                                eventsViewModel.getIsLoading().getValue())) {
                            eventsViewModel.loadNextPage(page + 1, token);
                        }
                    }
                }
            }
        });
    }

    private void reportEvent(View rootView) {
        Bundle bundle = new Bundle();
        bundle.putString("id", String.valueOf(selectedItemId));
        bundle.putString("type", "event");
        Navigation.findNavController(rootView)
                .navigate(R.id.action_itemsFragment_to_reportFragment, bundle);
    }

    private void interestEvent(int position){
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        Data data = new Data.Builder()
                .putString("TYPE", "event")
                .putString("ELEMENT_ID", String.valueOf(selectedItemId))
                .build();

        OneTimeWorkRequest deleteWorker = new OneTimeWorkRequest
                .Builder(InterestWorker.class)
                .setConstraints(constraints)
                .setInputData(data)
                .build();
        WorkManager.getInstance(getActivity()).enqueue(deleteWorker);
        eventsViewModel.interestEvent(eventsAdapter.getEvent(position).getId());
    }

}
