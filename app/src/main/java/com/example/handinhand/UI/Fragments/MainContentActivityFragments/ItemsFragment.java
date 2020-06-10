package com.example.handinhand.UI.Fragments.MainContentActivityFragments;


import android.app.AlertDialog;
import android.content.Intent;
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
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.handinhand.Adapters.ItemsAdapter;
import com.example.handinhand.Helpers.SharedPreferenceHelper;
import com.example.handinhand.R;
import com.example.handinhand.ViewModels.ItemsViewModel;
import com.example.handinhand.ViewModels.ProfileViewModel;
import com.example.handinhand.ViewModels.SharedItemViewModel;
import com.example.handinhand.services.DeleteWorker;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
    private String token;
    private ProfileViewModel user;

    int page = 0;
    int lastPage = 0;

    AlertDialog alertDialog;
    private String userId;
    private int selectedItemId;
    private int selectedItemPosition;


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

        itemsAdapter = new ItemsAdapter(rootView);
        FragmentActivity activity = getActivity();
        token = SharedPreferenceHelper.getToken(activity);

        createDeleteDialog(activity);


        itemsViewModel = new ViewModelProvider(activity).get(ItemsViewModel.class);
        sharedItemViewModel = new ViewModelProvider(activity).get(SharedItemViewModel.class);
        user = new ViewModelProvider(activity).get(ProfileViewModel.class);
        user.getProfile(SharedPreferenceHelper.getToken(requireContext())).observe(requireActivity(),
                profile -> {
                    userId = profile.getDetails().getUser().getId();
                });

        initRecyclerView(rootView);
        itemsViewModel.getmResponse(page, token);

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
            itemsViewModel.refresh(token)
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
            itemsViewModel.refresh(token);
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
                Toast.makeText(activity, R.string.end_of_list, Toast.LENGTH_SHORT).show();
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



        itemsViewModel.getSharedError().observe(activity, aBoolean -> {
            if(aBoolean){
                Toast.makeText(activity, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                itemsViewModel.setSharedError(false);
            }
        });

        sharedItemViewModel.getDeleteAt().observe(activity, integer -> {
            if(integer != null && integer != -1){
                itemsViewModel.deleteItem(integer);
                sharedItemViewModel.deleteAt(-1);
            }
        });
        sharedItemViewModel.getRequestAt().observe(activity, integer -> {
            if(integer != null && integer != -1){
                itemsViewModel.requestItem(integer);
                sharedItemViewModel.setRequestAt(-1);
            }
        });

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

                selectedItemId = itemsAdapter.getItem(position).getId();
                selectedItemPosition = position;

                PopupMenu popup = new PopupMenu(rootView.getContext(), view);
                if(itemsAdapter.getItem(position).getUser_id().equals(userId)){
                    popup.getMenuInflater().inflate(R.menu.out_menu, popup.getMenu());
                }
                else{
                    popup.getMenuInflater().inflate(R.menu.out_menu_not_mine, popup.getMenu());
                }
                popup.show();


                popup.setOnMenuItemClickListener(item -> {

                    if(item.getItemId() == R.id.report){
                        reportItem(rootView);
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
                            itemsViewModel.loadNextPage(page + 1, token);
                        }
                    }
                }

            }
        });
    }


    private void reportItem(View rootView) {
        Bundle bundle = new Bundle();
        bundle.putString("id", String.valueOf(selectedItemId));
        bundle.putString("type", "item");
        Navigation.findNavController(rootView)
                .navigate(R.id.action_itemsFragment_to_reportFragment, bundle);
    }

    private void createDeleteDialog(FragmentActivity activity) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle(R.string.warning);
        dialog.setMessage(R.string.warning_message);
        dialog.setCancelable(true);

        dialog.setPositiveButton(
                getString(R.string.delete), (dialogInterface, i) -> {

                    Constraints constraints = new Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build();
                    Data data = new Data.Builder()
                            .putString("TYPE", "item")
                            .putString("ELEMENT_ID", String.valueOf(selectedItemId))
                            .build();

                    OneTimeWorkRequest deleteWorker = new OneTimeWorkRequest
                            .Builder(DeleteWorker.class)
                            .setConstraints(constraints)
                            .setInputData(data)
                            .build();
                    WorkManager.getInstance(requireActivity()).enqueue(deleteWorker);
                    itemsViewModel.deleteItem(selectedItemPosition);
                    alertDialog.dismiss();
                });

        dialog.setNegativeButton(
                getString(R.string.cancel),(dialogInterface, i) -> {
                    alertDialog.dismiss();
                });

        alertDialog = dialog.create();
    }

}
