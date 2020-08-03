package com.example.handinhand.UI.Fragments.MainContentActivityFragments;


import android.os.Build;
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
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.handinhand.Adapters.ServicesAdapter;
import com.example.handinhand.Helpers.SharedPreferenceHelper;
import com.example.handinhand.R;
import com.example.handinhand.ViewModels.ProfileViewModel;
import com.example.handinhand.ViewModels.ServiceSharedViewModel;
import com.example.handinhand.ViewModels.ServicesViewModel;
import com.example.handinhand.services.InterestWorker;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ServicesFragment extends Fragment {


    public ServicesFragment() {
    }

    private FloatingActionButton addFab;

    private RecyclerView recyclerView;
    private RelativeLayout errorPage;
    private ConstraintLayout fullLoadingView;
    private SwipeRefreshLayout refreshLayout;
    private ProgressBar loading;


    private ServicesAdapter servicesAdapter;
    private ServicesViewModel servicesViewModel;
    private ServiceSharedViewModel sharedServiceViewModel;
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
        refreshLayout = rootView.findViewById(R.id.services_swipe_refresh_layout);
        MaterialButton reload = rootView.findViewById(R.id.reload);
        loading = rootView.findViewById(R.id.services_loading_view_progressbar);
        recyclerView = rootView.findViewById(R.id.services_recycler_view);

        servicesAdapter = new ServicesAdapter(rootView);
        FragmentActivity activity = getActivity();
        token = SharedPreferenceHelper.getToken(activity);
        addFab = rootView.findViewById(R.id.services_fab);


        servicesViewModel = new ViewModelProvider(activity).get(ServicesViewModel.class);
        sharedServiceViewModel = new ViewModelProvider(activity).get(ServiceSharedViewModel.class);

        initRecyclerView(rootView);
        servicesViewModel.getmResponse(page, token);

        addFab.setOnClickListener(view -> {
            FragmentNavigator.Extras extra = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                addFab.setTransitionName("FloatingActionButtonTransition");
                extra = new FragmentNavigator.Extras.Builder()
                        .addSharedElement(addFab, "FloatingActionButtonTransition")
                        .build();
            }
            Navigation.findNavController(rootView).navigate(
                    R.id.action_servicesFragment_to_addServiceFragment,
                    null,
                    null,
                    extra
            );
            addFab.hide();
        });

        reload.setOnClickListener(view ->
                servicesViewModel.refresh(token)
        );

        refreshLayout.setOnRefreshListener(() -> {
            servicesViewModel.refresh(token);
            page = 0;
            servicesAdapter.clearAll();
        });


        servicesViewModel.getIsError().observe(activity, aBoolean -> {
            if (aBoolean) {
                loading.setVisibility(View.GONE);
                Toast.makeText(activity, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                servicesViewModel.setIsError(false);
            }
        });

        servicesViewModel.getIsFirstLoading().observe(activity, aBoolean -> {
            refreshLayout.setRefreshing(false);
            if (aBoolean) {
                fullLoadingView.setVisibility(View.VISIBLE);
                errorPage.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            }
        });

        servicesViewModel.getIsFirstError().observe(activity, aBoolean -> {
            if (aBoolean) {
                fullLoadingView.setVisibility(View.GONE);
                errorPage.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });

        servicesViewModel.getPage().observe(activity, integer -> {
            page = integer;
            if (page == lastPage) {
                loading.setVisibility(View.GONE);
                Toast.makeText(activity, R.string.end_of_list, Toast.LENGTH_SHORT).show();
            }
        });

        servicesViewModel.getLastPage().observe(activity, integer ->
                lastPage = integer
        );

        servicesViewModel.getIsLoading().observe(activity, aBoolean -> {
            if (aBoolean) {
                loading.setVisibility(View.VISIBLE);
            } else {
                loading.setVisibility(View.GONE);
            }
        });

        servicesViewModel.getmList().observe(activity, data -> {
            fullLoadingView.setVisibility(View.GONE);
            errorPage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            servicesAdapter.setServicesList(data);
            //Toast.makeText(activity, String.valueOf(data.size()), Toast.LENGTH_SHORT).show();
        });


        servicesViewModel.getSharedError().observe(activity, aBoolean -> {
            if (aBoolean) {
                Toast.makeText(activity, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                servicesViewModel.setSharedError(false);
            }
        });

        sharedServiceViewModel.getDeleteAt().observe(activity, integer -> {
            if (integer != null && integer != -1) {
                servicesViewModel.deleteService(integer);
                sharedServiceViewModel.deleteAt(-1);
            }
        });
        sharedServiceViewModel.getInterestAt().observe(activity, integer -> {
            if (integer != null && integer != -1) {
                servicesViewModel.interestService(integer);
                sharedServiceViewModel.setInterestAt(-1);
            }
        });

        return rootView;
    }

    private void initRecyclerView(View rootView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setSaveEnabled(true);
        recyclerView.setAdapter(servicesAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setSaveEnabled(true);

        servicesAdapter.setOnServiceClickListener(new ServicesAdapter.OnServiceClickListener() {
            @Override
            public void OnServiceClicked(int position) {
                sharedServiceViewModel.select(servicesAdapter.getService(position));
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                Navigation.findNavController(rootView).navigate(
                        R.id.action_servicesFragment_to_serviceDescriptionFragment,
                        bundle
                );
            }

            @Override
            public void OnServiceInterest(int position) {
                selectedItemId = servicesAdapter.getService(position).getId();
                servicesViewModel.interestService(position);
                interestService(selectedItemId);
            }

            @Override
            public void onServiceLongClicked(int position) {
                selectedItemId = servicesAdapter.getService(position).getId();
                selectedItemPosition = position;
                reportService(rootView);
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
                        if (page != lastPage && (servicesViewModel.getIsLoading().getValue() != null &&
                                !servicesViewModel.getIsLoading().getValue())) {
                            servicesViewModel.loadNextPage(page + 1, token);
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
