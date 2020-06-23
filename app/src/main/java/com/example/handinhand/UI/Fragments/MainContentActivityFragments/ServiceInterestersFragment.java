package com.example.handinhand.UI.Fragments.MainContentActivityFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.handinhand.Adapters.InterestersServiceAdapter;
import com.example.handinhand.Helpers.SharedPreferenceHelper;
import com.example.handinhand.R;
import com.example.handinhand.ViewModels.ServiceInterestersViewModel;
import com.google.android.material.button.MaterialButton;

public class ServiceInterestersFragment extends Fragment {

    private Toolbar toolbar;
    private SwipeRefreshLayout refreshLayout;
    private RelativeLayout relativeLayout;
    private TextView serviceName;
    private TextView serviceDescriptionText;
    private RecyclerView interesters;
    private ConstraintLayout fullLoadingView;
    private MaterialButton reload;
    private RelativeLayout errorPage;
    private ServiceInterestersViewModel viewModel;
    private InterestersServiceAdapter adapter;

    private String token = "";
    private int id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_service_interesters, container, false);
        fullLoadingView = rootView.findViewById(R.id.full_loading_view);
        errorPage = rootView.findViewById(R.id.error_page);
        refreshLayout = rootView.findViewById(R.id.interesters_swipe_refresh_layout);
        reload = rootView.findViewById(R.id.reload);
        toolbar = rootView.findViewById(R.id.interesters_toolbar);
        relativeLayout = rootView.findViewById(R.id.relative_interesters);
        serviceName = rootView.findViewById(R.id.service_name_interesters);
        serviceDescriptionText = rootView.findViewById(R.id.service_description_interesters);
        interesters = rootView.findViewById(R.id.interesters_recycler_view);
        FragmentActivity activity = getActivity();
        id = getArguments().getInt("ID");
        token = SharedPreferenceHelper.getToken(activity);
        viewModel = new ViewModelProvider(activity).get(ServiceInterestersViewModel.class);

        adapter = new InterestersServiceAdapter(rootView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        interesters.setLayoutManager(layoutManager);
        interesters.setSaveEnabled(true);
        interesters.setAdapter(adapter);
        interesters.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        interesters.setHasFixedSize(true);

        viewModel.getEvent(token, id);

        viewModel.getIsError().observe(activity, aBoolean -> {
            if(aBoolean){
                fullLoadingView.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.GONE);
                refreshLayout.setEnabled(false);
                refreshLayout.setRefreshing(false);
                errorPage.setVisibility(View.VISIBLE);
            }
        });
        toolbar.setNavigationOnClickListener(view -> Navigation.findNavController(rootView).navigateUp());

        viewModel.getIsFirstLoading().observe(activity, aBoolean -> {
            if(aBoolean){
                fullLoadingView.setVisibility(View.VISIBLE);
                relativeLayout.setVisibility(View.GONE);
                refreshLayout.setEnabled(false);
                refreshLayout.setRefreshing(false);
                errorPage.setVisibility(View.GONE);
            }
        });

        viewModel.getIsLoading().observe(activity, aBoolean -> {
            if(aBoolean){
                fullLoadingView.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
                refreshLayout.setRefreshing(true);
                errorPage.setVisibility(View.GONE);
            }
            else{
                fullLoadingView.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
                refreshLayout.setRefreshing(false);
                errorPage.setVisibility(View.GONE);
            }
        });

        viewModel.getEvent(token, id).observe(activity, serviceDescription -> {
            if(serviceDescription.getStatus()){
                fullLoadingView.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
                refreshLayout.setRefreshing(false);
                errorPage.setVisibility(View.GONE);
                serviceName.setText(serviceDescription.getService().getTitle());
                serviceDescriptionText.setText(serviceDescription.getService().getDescription());
                adapter.setEventsList(serviceDescription.getService().getInteresters());
            }
        });

        reload.setOnClickListener(view -> viewModel.refresh(token, id));

        refreshLayout.setOnRefreshListener(() -> viewModel.refresh(token, id));


        return rootView;
    }
}