package com.example.handinhand.UI.Fragments.MainContentActivityFragments;


import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.handinhand.Adapters.ItemsAdapter;
import com.example.handinhand.Helpers.SharedPreferenceHelper;
import com.example.handinhand.Models.Profile;
import com.example.handinhand.Models.ReportResponse;
import com.example.handinhand.R;
import com.example.handinhand.ViewModels.ItemsViewModel;
import com.example.handinhand.ViewModels.ProfileViewModel;
import com.example.handinhand.ViewModels.SharedItemViewModel;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemsFragment extends Fragment {

    private FloatingActionButton addFab;

    private RecyclerView recyclerView;
    private RelativeLayout errorPage;
    private ShimmerFrameLayout shimmerLayout;
    private SwipeRefreshLayout refreshLayout;
    private MaterialButton reload;
    private ProgressBar loading;


    private ItemsAdapter itemsAdapter;
    private ItemsViewModel itemsViewModel;
    private SharedItemViewModel sharedItemViewModel;
    private ProfileViewModel user;

    int page = 0;
    int lastPage = 0;
    private Dialog reportDialog;
    private Dialog deleteDialog;
    private String userId;
    private int selectedItemId;


    public ItemsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_items, container, false);
        recyclerView = rootView.findViewById(R.id.items_recycler_view);
        shimmerLayout = rootView.findViewById(R.id.shimmer_layout);
        errorPage = rootView.findViewById(R.id.error_page);
        refreshLayout = rootView.findViewById(R.id.items_swipe_refresh_layout);
        reload = rootView.findViewById(R.id.reload);
        loading = rootView.findViewById(R.id.loading_view_progressbar);

        reportDialog = new Dialog(rootView.getContext());
        reportDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        reportDialog.setContentView(R.layout.report_dialog);
        reportDialog.setCanceledOnTouchOutside(true);

        deleteDialog = new Dialog(rootView.getContext());
        deleteDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        deleteDialog.setContentView(R.layout.delete_dialog);
        deleteDialog.setCanceledOnTouchOutside(true);

        itemsAdapter = new ItemsAdapter(rootView);
        FragmentActivity activity = requireActivity();

        itemsViewModel = new ViewModelProvider(activity).get(ItemsViewModel.class);
        sharedItemViewModel = new ViewModelProvider(activity).get(SharedItemViewModel.class);
        user = new ViewModelProvider(activity).get(ProfileViewModel.class);
        user.getProfile(SharedPreferenceHelper.getToken(requireContext())).observe(requireActivity(),
                profile -> {
                    userId = profile.getDetails().getUser().getId();
                });

        initRecyclerView(rootView);
        itemsViewModel.getmResponse(page);

        addFab = rootView.findViewById(R.id.items_fab);
        addFab.show();
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
        });

        reload.setOnClickListener(view ->
            itemsViewModel.refresh()
        );

        /*itemsViewModel.getIsError().observe(activity, aBoolean -> {
            if(aBoolean || (itemsAdapter == null)){
                shimmerLayout.setVisibility(View.GONE);
                errorPage.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
            else{

            }
        });*/
        refreshLayout.setOnRefreshListener(() -> {
            itemsViewModel.refresh();
            page = 0;
            itemsAdapter.clearAll();
        });



        itemsViewModel.getIsError().observe(activity, aBoolean -> {
            if(aBoolean){
                loading.setVisibility(View.GONE);
                Toast.makeText(activity, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                itemsViewModel.setIsError(false);
            }
        });

        itemsViewModel.getIsFirstLoading().observe(activity, aBoolean -> {
            refreshLayout.setRefreshing(false);
            if(aBoolean){
                shimmerLayout.setVisibility(View.VISIBLE);
                errorPage.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            }
        });

        itemsViewModel.getIsFirstError().observe(activity, aBoolean -> {
            if(aBoolean){
                shimmerLayout.setVisibility(View.GONE);
                errorPage.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });

        itemsViewModel.getPage().observe(activity, integer -> {
            page = integer;
            if(page == lastPage){
                loading.setVisibility(View.GONE);
            }
        });

        itemsViewModel.getLastPage().observe(activity, integer ->
            lastPage = integer
        );

        itemsViewModel.getIsLoading().observe(activity, aBoolean -> {
            if(aBoolean){
                loading.setVisibility(View.VISIBLE);
            }
            else{
                loading.setVisibility(View.GONE);
            }
        });

        itemsViewModel.getmList().observe(activity, data ->
            itemsAdapter.setItemsList(data)
        );


        deleteDialog.findViewById(R.id.cancel_button_dialog)
                .setOnClickListener(view -> deleteDialog.dismiss());

        deleteDialog.findViewById(R.id.delete_button_dialog)
                .setOnClickListener(view ->
                        itemsViewModel.deleted(SharedPreferenceHelper.getToken(activity),
                                String.valueOf(selectedItemId)).observe(activity, aBoolean -> {
                            if(aBoolean){
                                Toast.makeText(activity, getString(R.string.deleted), Toast.LENGTH_SHORT).show();
                                itemsViewModel.setDeleted(false);
                                deleteDialog.dismiss();
                            }
                        }));


        reportDialog.findViewById(R.id.report_button_dialog)
                .setOnClickListener(view -> {
                    Map<String, String> reason = new HashMap<>();
                    ChipGroup group = reportDialog.findViewById(R.id.report_chip_group);
                    if(group.getCheckedChipId() == R.id.spam_chip){
                        reason.put("reason", "spam");
                    }
                    else{
                        reason.put("reason", "inappropriate");
                    }
                            itemsViewModel.reported(SharedPreferenceHelper.getToken(activity),
                            String.valueOf(selectedItemId),
                                    reason
                            ).observe(activity, reportResponse -> {
                                if(reportResponse.getItem_report().equals("reported!!!")){
                                    Toast.makeText(activity, getString(R.string.Reported), Toast.LENGTH_SHORT).show();
                                }
                                else if(reportResponse.getError().equals("this item is already reported")){
                                    Toast.makeText(activity, getString(R.string.already_reported), Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(activity, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                                }
                            });
                });


        itemsViewModel.getSharedError().observe(activity, aBoolean -> {
            if(aBoolean){
                Toast.makeText(activity, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                itemsViewModel.setSharedError(false);
            }
        });


        /*itemsViewModel.getmResponse(page).observe(activity, itemsPaginationObject -> {

            if(itemsPaginationObject.getStatus()){
                itemsAdapter.setItemsList(itemsPaginationObject.getItems().getData());
                page = itemsPaginationObject.getItems().getCurrent_page();
                lastPage = itemsPaginationObject.getItems().getLast_page();

                shimmerLayout.setVisibility(View.GONE);
                errorPage.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
            else{
                shimmerLayout.setVisibility(View.GONE);
                errorPage.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            }
        });*/


        return rootView;
    }

    private void initRecyclerView(View rootView) {
        recyclerView.setLayoutManager(new GridLayoutManager(rootView.getContext(), 2));
        recyclerView.setSaveEnabled(true);
        recyclerView.setAdapter(itemsAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setSaveEnabled(true);

        itemsAdapter.setOnItemClickListener(new ItemsAdapter.OnItemClickListener() {
            @Override
            public void OnMenuClicked(int position, View view) {
                PopupMenu popup = new PopupMenu(rootView.getContext(), view);

                selectedItemId = itemsAdapter.getItem(position).getId();

                if(itemsAdapter.getItem(position).getUser_id().equals(userId)){
                    popup.getMenuInflater().inflate(R.menu.out_menu, popup.getMenu());
                }
                else{
                    popup.getMenuInflater().inflate(R.menu.out_menu_not_mine, popup.getMenu());
                }

                popup.show();

                popup.setOnMenuItemClickListener(item -> {
                    if(item.getItemId() == R.id.report){
                        reportDialog.show();
                    }
                    else if(item.getItemId() == R.id.share){
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        //TODO: Change the text
                        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.items_url)+
                                itemsAdapter.getItem(position).getId());
                        sendIntent.setType("text/plain");

                        Intent shareIntent = Intent.createChooser(sendIntent, null);
                        startActivity(shareIntent);
                    }
                    else if(item.getItemId() == R.id.delete){
                        deleteDialog.show();
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

                Navigation.findNavController(rootView).navigate(
                        R.id.action_itemsFragment_to_itemDescriptionFragment,
                        null,
                        null,
                        extra
                );
            }

        });


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


                if(dy > 0){ // only when scrolling up

                    final int visibleThreshold = 2;

                    GridLayoutManager layoutManager = (GridLayoutManager)recyclerView.getLayoutManager();
                    int lastItem  = layoutManager.findLastCompletelyVisibleItemPosition();
                    int currentTotalCount = layoutManager.getItemCount();

                    if(currentTotalCount <= lastItem + visibleThreshold){
                        Toast.makeText(getActivity(), "End", Toast.LENGTH_SHORT).show();
                        //show your loading view
                        // load content in background
                        if(page != lastPage || (itemsViewModel.getIsLoading().getValue() != null &&
                                itemsViewModel.getIsLoading().getValue())){
                            itemsViewModel.loadNextPage(page + 1);
                        }
                    }
                }

            }
        });
    }

}
