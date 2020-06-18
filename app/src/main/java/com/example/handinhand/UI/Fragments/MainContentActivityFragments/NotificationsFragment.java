package com.example.handinhand.UI.Fragments.MainContentActivityFragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.handinhand.Adapters.NotificationAdapter;
import com.example.handinhand.Helpers.SharedPreferenceHelper;
import com.example.handinhand.R;
import com.example.handinhand.ViewModels.NotificationViewModel;
import com.example.handinhand.ViewModels.ProfileViewModel;
import com.example.handinhand.services.InterestWorker;
import com.google.android.material.button.MaterialButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationsFragment extends Fragment {


    public NotificationsFragment() { }

    private RecyclerView recyclerView;
    private RelativeLayout errorPage;
    private ConstraintLayout fullLoadingView;
    private SwipeRefreshLayout refreshLayout;
    private ProgressBar loading;


    private NotificationAdapter notificationAdapter;
    private NotificationViewModel notificationViewModel;
    private String token;

    int page = 0;
    int lastPage = 0;
    private String userId;
    private int selectedItemId;
    private int selectedItemPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_services, container, false);
        fullLoadingView = rootView.findViewById(R.id.full_loading_view);
        errorPage = rootView.findViewById(R.id.error_page);
        refreshLayout = rootView.findViewById(R.id.notifications_swipe_refresh_layout);
        MaterialButton reload = rootView.findViewById(R.id.reload);
        loading = rootView.findViewById(R.id.notifications_loading_view_progressbar);
        recyclerView = rootView.findViewById(R.id.notifications_recycler_view);

        notificationAdapter = new NotificationAdapter();
        FragmentActivity activity = getActivity();
        token = SharedPreferenceHelper.getToken(activity);


        notificationViewModel = new ViewModelProvider(activity).get(NotificationViewModel.class);
        ProfileViewModel user = new ViewModelProvider(activity).get(ProfileViewModel.class);
        user.getProfile(SharedPreferenceHelper.getToken(requireContext())).observe(requireActivity(),
                profile -> {
                    userId = profile.getDetails().getUser().getId();
                });

        initRecyclerView(rootView);
        notificationViewModel.getmResponse(page, token);

        reload.setOnClickListener(view ->
                notificationViewModel.refresh(token)
        );

        refreshLayout.setOnRefreshListener(() -> {
            notificationViewModel.refresh(token);
            page = 0;
            notificationAdapter.clearAll();
        });


        notificationViewModel.getIsError().observe(activity, aBoolean -> {
            if (aBoolean) {
                loading.setVisibility(View.GONE);
                Toast.makeText(activity, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                notificationViewModel.setIsError(false);
            }
        });

        notificationViewModel.getIsFirstLoading().observe(activity, aBoolean -> {
            refreshLayout.setRefreshing(false);
            if (aBoolean) {
                fullLoadingView.setVisibility(View.VISIBLE);
                errorPage.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            }
        });

        notificationViewModel.getIsFirstError().observe(activity, aBoolean -> {
            if (aBoolean) {
                fullLoadingView.setVisibility(View.GONE);
                errorPage.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });

        notificationViewModel.getPage().observe(activity, integer -> {
            page = integer;
            if (page == lastPage) {
                loading.setVisibility(View.GONE);
                Toast.makeText(activity, R.string.end_of_list, Toast.LENGTH_SHORT).show();
            }
        });

        notificationViewModel.getLastPage().observe(activity, integer ->
                lastPage = integer
        );

        notificationViewModel.getIsLoading().observe(activity, aBoolean -> {
            if (aBoolean) {
                loading.setVisibility(View.VISIBLE);
            } else {
                loading.setVisibility(View.GONE);
            }
        });

        notificationViewModel.getmList().observe(activity, data -> {
            fullLoadingView.setVisibility(View.GONE);
            errorPage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            notificationAdapter.setNotificationsList(data);
            //Toast.makeText(activity, String.valueOf(data.size()), Toast.LENGTH_SHORT).show();
        });


        notificationViewModel.getSharedError().observe(activity, aBoolean -> {
            if (aBoolean) {
                Toast.makeText(activity, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                notificationViewModel.setSharedError(false);
            }
        });

        return rootView;
    }

    private void initRecyclerView(View rootView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setSaveEnabled(true);
        recyclerView.setAdapter(notificationAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);
        recyclerView.setSaveEnabled(true);

        notificationAdapter.setOnNotificationClickListener(new NotificationAdapter.OnNotificationClickListener() {
            @Override
            public void OnNotificationClicked(int position) {
                //TODO
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                Navigation.findNavController(rootView).navigate(
                        R.id.action_servicesFragment_to_serviceDescriptionFragment,
                        bundle
                );
            }
        });


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        if (page != lastPage && (notificationViewModel.getIsLoading().getValue() != null &&
                                !notificationViewModel.getIsLoading().getValue())) {
                            notificationViewModel.loadNextPage(page + 1, token);
                        }
                    }
                }
            }
        });
    }

    private void reportService(View rootView) {
        Bundle bundle = new Bundle();
        bundle.putString("id", String.valueOf(selectedItemId));
        bundle.putString("type", "service");
        Navigation.findNavController(rootView)
                .navigate(R.id.action_servicesFragment_to_reportFragment, bundle);
    }

    private void interestService(int id) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        Data data = new Data.Builder()
                .putString("TYPE", "service")
                .putString("ELEMENT_ID", String.valueOf(selectedItemId))
                .build();

        OneTimeWorkRequest deleteWorker = new OneTimeWorkRequest
                .Builder(InterestWorker.class)
                .setConstraints(constraints)
                .setInputData(data)
                .build();
        WorkManager.getInstance(getActivity()).enqueue(deleteWorker);
        //servicesViewModel.interestService(id);
    }


}
