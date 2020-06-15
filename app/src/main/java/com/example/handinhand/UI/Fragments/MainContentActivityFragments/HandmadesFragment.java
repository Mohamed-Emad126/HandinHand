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

import com.example.handinhand.Adapters.ProductsAdapter;
import com.example.handinhand.Helpers.SharedPreferenceHelper;
import com.example.handinhand.R;
import com.example.handinhand.ViewModels.ProductsViewModel;
import com.example.handinhand.ViewModels.ProfileViewModel;
import com.example.handinhand.ViewModels.SharedProductViewModel;
import com.example.handinhand.services.DeleteWorker;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HandmadesFragment extends Fragment {


    private FloatingActionButton addFab;

    private RecyclerView recyclerView;
    private RelativeLayout errorPage;
    private ShimmerFrameLayout shimmerLayout;
    private SwipeRefreshLayout refreshLayout;
    private ProgressBar loading;


    private ProductsAdapter productsAdapter;
    private ProductsViewModel productsViewModel;
    private SharedProductViewModel sharedProductViewModel;
    private String token;

    int page = 0;
    int lastPage = 0;

    AlertDialog alertDialog;
    private String userId;
    private int selectedItemId;
    private int selectedItemPosition;


    public HandmadesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_handmades, container, false);
        recyclerView = rootView.findViewById(R.id.products_recycler_view);
        shimmerLayout = rootView.findViewById(R.id.shimmer_layout);
        errorPage = rootView.findViewById(R.id.error_page);
        refreshLayout = rootView.findViewById(R.id.products_swipe_refresh_layout);
        MaterialButton reload = rootView.findViewById(R.id.reload);
        loading = rootView.findViewById(R.id.loading_view_progressbar);

        productsAdapter = new ProductsAdapter(rootView);
        FragmentActivity activity = getActivity();
        token = SharedPreferenceHelper.getToken(activity);

        createDeleteDialog(activity);


        productsViewModel = new ViewModelProvider(activity).get(ProductsViewModel.class);
        sharedProductViewModel = new ViewModelProvider(activity).get(SharedProductViewModel.class);
        ProfileViewModel user = new ViewModelProvider(activity).get(ProfileViewModel.class);
        user.getProfile(SharedPreferenceHelper.getToken(requireContext())).observe(requireActivity(),
                profile -> userId = profile.getDetails().getUser().getId());

        initRecyclerView(rootView);
        productsViewModel.getmResponse(page, token);

        addFab = rootView.findViewById(R.id.products_fab);
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
                    R.id.action_handmadesFragment_to_addProductFragment,
                    null,
                    null,
                    extra
            );
            addFab.hide();
        });

        reload.setOnClickListener(view ->
                productsViewModel.refresh(token)
        );

        refreshLayout.setOnRefreshListener(() -> {
            productsViewModel.refresh(token);
            page = 0;
            productsAdapter.clearAll();
        });


        productsViewModel.getIsError().observe(activity, aBoolean -> {
            if (aBoolean) {
                loading.setVisibility(View.GONE);
                Toast.makeText(activity, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                productsViewModel.setIsError(false);
            }
        });

        productsViewModel.getIsFirstLoading().observe(activity, aBoolean -> {
            refreshLayout.setRefreshing(false);
            if (aBoolean) {
                shimmerLayout.setVisibility(View.VISIBLE);
                errorPage.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            }
        });

        productsViewModel.getIsFirstError().observe(activity, aBoolean -> {
            if (aBoolean) {
                shimmerLayout.setVisibility(View.GONE);
                errorPage.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });

        productsViewModel.getPage().observe(activity, integer -> {
            page = integer;
            if (page == lastPage) {
                loading.setVisibility(View.GONE);
                Toast.makeText(activity, R.string.end_of_list, Toast.LENGTH_SHORT).show();
            }
        });

        productsViewModel.getLastPage().observe(activity, integer ->
                lastPage = integer
        );

        productsViewModel.getIsLoading().observe(activity, aBoolean -> {
            if (aBoolean) {
                loading.setVisibility(View.VISIBLE);
            } else {
                loading.setVisibility(View.GONE);
            }
        });

        productsViewModel.getmList().observe(activity, data -> {
            shimmerLayout.setVisibility(View.GONE);
            errorPage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            productsAdapter.setProductsList(data);
            //Toast.makeText(activity, String.valueOf(data.size()), Toast.LENGTH_SHORT).show();
        });


        productsViewModel.getSharedError().observe(activity, aBoolean -> {
            if (aBoolean) {
                Toast.makeText(activity, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                productsViewModel.setSharedError(false);
            }
        });

        sharedProductViewModel.getDeleteAt().observe(activity, integer -> {
            if (integer != null && integer != -1) {
                productsViewModel.deleteItem(integer);
                sharedProductViewModel.deleteAt(-1);
            }
        });

        return rootView;
    }

    private void initRecyclerView(View rootView) {
        recyclerView.setLayoutManager(new GridLayoutManager(rootView.getContext(), 2));
        recyclerView.setSaveEnabled(true);
        recyclerView.setAdapter(productsAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setSaveEnabled(true);

        productsAdapter.setOnProductClickListener(new ProductsAdapter.OnProductClickListener() {
            @Override
            public void OnMenuClicked(int position, View view) {

                selectedItemId = productsAdapter.getItem(position).getId();
                selectedItemPosition = position;

                PopupMenu popup = new PopupMenu(rootView.getContext(), view);
                if (productsAdapter.getItem(position).getUser_id() == Integer.parseInt(userId)) {
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
                                productsAdapter.getItem(position).getId());
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
            public void OnProductClicked(int position, ImageView imageView) {
                sharedProductViewModel.select(productsAdapter.getItem(position));

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
                        R.id.action_handmadesFragment_to_productDescriptionFragment,
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
                if (dy > 0) { // only when scrolling up

                    final int visibleThreshold = 2;

                    GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                    int lastItem = layoutManager.findLastCompletelyVisibleItemPosition();
                    int currentTotalCount = layoutManager.getItemCount();

                    if (currentTotalCount <= lastItem + visibleThreshold) {
                        if (page != lastPage || (productsViewModel.getIsLoading().getValue() != null &&
                                productsViewModel.getIsLoading().getValue())) {
                            productsViewModel.loadNextPage(page + 1, token);
                        }
                    }
                }
            }
        });
    }

    private void reportItem(View rootView) {
        Bundle bundle = new Bundle();
        bundle.putString("id", String.valueOf(selectedItemId));
        bundle.putString("type", "product");
        Navigation.findNavController(rootView)
                .navigate(R.id.action_handmadesFragment_to_reportFragment, bundle);
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
                            .putString("TYPE", "product")
                            .putString("ELEMENT_ID", String.valueOf(selectedItemId))
                            .build();

                    OneTimeWorkRequest deleteWorker = new OneTimeWorkRequest
                            .Builder(DeleteWorker.class)
                            .setConstraints(constraints)
                            .setInputData(data)
                            .build();
                    WorkManager.getInstance(requireActivity()).enqueue(deleteWorker);
                    productsViewModel.deleteItem(selectedItemPosition);
                    alertDialog.dismiss();
                });

        dialog.setNegativeButton(
                getString(R.string.cancel), (dialogInterface, i) -> {
                    alertDialog.dismiss();
                });

        alertDialog = dialog.create();
    }


}
