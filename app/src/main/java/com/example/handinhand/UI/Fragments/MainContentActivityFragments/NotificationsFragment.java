package com.example.handinhand.UI.Fragments.MainContentActivityFragments;


import android.app.Dialog;
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
import com.example.handinhand.ViewModels.DealViewModel;
import com.example.handinhand.ViewModels.EventSharedViewModel;
import com.example.handinhand.ViewModels.NotificationViewModel;
import com.example.handinhand.ViewModels.ProfileViewModel;
import com.example.handinhand.ViewModels.ServiceSharedViewModel;
import com.example.handinhand.ViewModels.SharedDealViewModel;
import com.example.handinhand.services.DealWorker;
import com.google.android.material.button.MaterialButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationsFragment extends Fragment {


    private Dialog dialog;
    private EventSharedViewModel eventSharedViewModel;
    private ServiceSharedViewModel serviceSharedViewModel;

    public NotificationsFragment() { }

    private RecyclerView recyclerView;
    private RelativeLayout errorPage;
    private ConstraintLayout fullLoadingView;
    private SwipeRefreshLayout refreshLayout;
    private ProgressBar loading;


    private NotificationAdapter notificationAdapter;
    private NotificationViewModel notificationViewModel;
    private DealViewModel dealViewModel;
    private SharedDealViewModel sharedDealViewModel;
    private String token;

    int page = 0;
    int lastPage = 0;
    private String userId;
    private int selectedItemId;

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
        createProgressDialog();


        notificationViewModel = new ViewModelProvider(activity).get(NotificationViewModel.class);
        dealViewModel = new ViewModelProvider(activity).get(DealViewModel.class);
        sharedDealViewModel = new ViewModelProvider(activity).get(SharedDealViewModel.class);
        eventSharedViewModel = new ViewModelProvider(activity).get(EventSharedViewModel.class);
        serviceSharedViewModel = new ViewModelProvider(activity).get(ServiceSharedViewModel.class);

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
        });


        notificationViewModel.getSharedError().observe(activity, aBoolean -> {
            if (aBoolean) {
                Toast.makeText(activity, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                notificationViewModel.setSharedError(false);
            }
        });

        dealViewModel.getDeal().observe(activity, deal -> {
            sharedDealViewModel.select(deal);
            if(deal.getShow_deal().getOwner_id() == Integer.parseInt(userId)){
                if(deal.getShow_deal().getOwner_status() == 0){
                    Bundle bundle = new Bundle();
                    bundle.putInt("ID", selectedItemId);
                    Navigation.findNavController(rootView).navigate(
                            R.id.action_notificationsFragment_to_dealFragment,
                            bundle
                    );
                }
                else{
                    Bundle bundle = new Bundle();
                    bundle.putInt("ID", selectedItemId);
                    Navigation.findNavController(rootView).navigate(
                            R.id.action_notificationsFragment_to_dealCompletedFragment,
                            bundle
                    );
                }
            }
            else{
                if(deal.getShow_deal().getBuyer_status() == 0){
                    Bundle bundle = new Bundle();
                    bundle.putInt("ID", selectedItemId);
                    Navigation.findNavController(rootView).navigate(
                            R.id.action_notificationsFragment_to_acceptDealFragment,
                            bundle
                    );
                }
                else{
                    Bundle bundle = new Bundle();
                    bundle.putInt("ID", selectedItemId);
                    Navigation.findNavController(rootView).navigate(
                            R.id.action_notificationsFragment_to_dealCompletedFragment,
                            bundle
                    );
                }
            }
        });
        dealViewModel.getIsLoading().observe(activity, aBoolean -> {
            if(aBoolean){
                dialog.show();
            }
        });

        dealViewModel.getIsError().observe(activity, aBoolean -> {
            if(aBoolean){
                dialog.dismiss();
                Toast.makeText(activity, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
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

        notificationAdapter.setOnNotificationClickListener(position -> {
            int n =notificationAdapter.getNotification(position).getUrl().length();
            StringBuilder notId = new StringBuilder();
            for(int i =n-1; i>=0; --i){
                if(notificationAdapter.getNotification(position).getUrl().charAt(i) == '/'){
                    break;
                }
                notId.append(notificationAdapter.getNotification(position).getUrl().charAt(i));
            }
            notId.reverse();
            int integerId = Integer.parseInt(notId.toString());
            selectedItemId = integerId;
            Bundle bundle = new Bundle();
            bundle.putInt("ID", integerId);
            readNotification(String.valueOf(notificationAdapter.getNotification(position).getId()));
            if(notificationAdapter.getNotification(position).getUrl().contains("events")){
                Navigation.findNavController(rootView).navigate(
                        R.id.action_notificationsFragment_to_eventInterestersFragment,
                        bundle
                );
            }
            else if(notificationAdapter.getNotification(position).getUrl().contains("services")){
                Navigation.findNavController(rootView).navigate(
                        R.id.action_notificationsFragment_to_serviceInterestersFragment,
                        bundle
                );
            }
            else{
                dealViewModel.getDeal(SharedPreferenceHelper.getToken(getActivity()), integerId);
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

    private void readNotification(String integerId) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        Data data = new Data.Builder()
                .putString("ELEMENT_ID", integerId)
                .build();

        OneTimeWorkRequest read = new OneTimeWorkRequest
                .Builder(DealWorker.class)
                .setConstraints(constraints)
                .setInputData(data)
                .build();
        WorkManager.getInstance(getActivity()).enqueue(read);
    }

    private void createProgressDialog(){
        dialog = new Dialog(getActivity());
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCanceledOnTouchOutside(false);
    }


}
