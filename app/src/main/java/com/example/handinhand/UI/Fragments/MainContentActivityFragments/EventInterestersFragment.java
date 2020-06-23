package com.example.handinhand.UI.Fragments.MainContentActivityFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.handinhand.Adapters.InterestersAdapter;
import com.example.handinhand.Helpers.SharedPreferenceHelper;
import com.example.handinhand.R;
import com.example.handinhand.ViewModels.EventInterestersViewModel;
import com.google.android.material.button.MaterialButton;


public class EventInterestersFragment extends Fragment {

    private Toolbar toolbar;
    private SwipeRefreshLayout refreshLayout;
    private RelativeLayout relativeLayout;
    private ImageView eventImage;
    private TextView eventName;
    private RecyclerView interesters;
    private ConstraintLayout fullLoadingView;
    private MaterialButton reload;
    private RelativeLayout errorPage;
    private EventInterestersViewModel viewModel;
    private InterestersAdapter adapter;

    private String token = "";
    private int id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event_interesters, container, false);
        fullLoadingView = rootView.findViewById(R.id.full_loading_view);
        errorPage = rootView.findViewById(R.id.error_page);
        refreshLayout = rootView.findViewById(R.id.interesters_swipe_refresh_layout);
        reload = rootView.findViewById(R.id.reload);
        toolbar = rootView.findViewById(R.id.interesters_toolbar);
        relativeLayout = rootView.findViewById(R.id.relative_interesters);
        eventImage = rootView.findViewById(R.id.card_view_image);
        eventName = rootView.findViewById(R.id.event_name_interesters);
        interesters = rootView.findViewById(R.id.interesters_recycler_view);
        FragmentActivity activity = getActivity();
        id = getArguments().getInt("ID");
        token = SharedPreferenceHelper.getToken(activity);
        viewModel = new ViewModelProvider(activity).get(EventInterestersViewModel.class);

        adapter = new InterestersAdapter(rootView);
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

        viewModel.getEvent(token, id).observe(getViewLifecycleOwner(), eventDescription -> {
            if(eventDescription != null && eventDescription.getStatus()){
                if(isAdded()){
                    fullLoadingView.setVisibility(View.GONE);
                    relativeLayout.setVisibility(View.VISIBLE);
                    refreshLayout.setRefreshing(false);
                    errorPage.setVisibility(View.GONE);
                    Glide.with(rootView)
                            .load(getString(R.string.events_image_url) + eventDescription.getEvent().getImage())
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .placeholder(R.drawable.ic_image_placeholder)
                            .into(eventImage);
                    eventName.setText(eventDescription.getEvent().getTitle());
                    adapter.setEventsList(eventDescription.getEvent().getInteresters());
                }
            }
        });

        reload.setOnClickListener(view -> viewModel.refresh(token, id));

        refreshLayout.setOnRefreshListener(() -> viewModel.refresh(token, id));


        return rootView;
    }
}