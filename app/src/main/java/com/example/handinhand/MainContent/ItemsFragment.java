package com.example.handinhand.MainContent;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.handinhand.Adapters.ItemsAdapter;
import com.example.handinhand.Models.ItemsPaginationObject;
import com.example.handinhand.R;
import com.example.handinhand.ViewModels.ItemsViewModel;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemsFragment extends Fragment {

    private FloatingActionButton addFab;

    private RecyclerView recyclerView;
    private RelativeLayout errorPage;
    private ShimmerFrameLayout shimmerLayout;

    private ItemsAdapter itemsAdapter;
    private ItemsViewModel itemsViewModel;
    int page = 0;


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

        itemsAdapter = new ItemsAdapter();
        FragmentActivity activity = getActivity();
        itemsViewModel = new ViewModelProvider(activity).get(ItemsViewModel.class);
        initRecyclerView();
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

        /*itemsViewModel.getIsError().observe(activity, aBoolean -> {
            if(aBoolean || (itemsAdapter == null)){
                shimmerLayout.setVisibility(View.GONE);
                errorPage.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
            else{

            }
        });*/

        itemsViewModel.getIsFirstLoading().observe(activity, aBoolean -> {
            if(aBoolean){
                shimmerLayout.setVisibility(View.VISIBLE);
                errorPage.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            }
            else{
                shimmerLayout.setVisibility(View.GONE);
                errorPage.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });


        itemsViewModel.getmResponse(page).observe(activity, itemsPaginationObject -> {

            if(itemsPaginationObject.getStatus()){
                itemsAdapter.setItemsList(itemsPaginationObject.getItems().getData());
                page = itemsPaginationObject.getItems().getCurrent_page();
                if(page == itemsPaginationObject.getItems().getLast_page()){
                    itemsAdapter.setLastPage(true);
                }
                else{
                    itemsAdapter.setLastPage(false);
                }
            }
            else{
                shimmerLayout.setVisibility(View.GONE);
                errorPage.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            }
        });


        return rootView;
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setSaveEnabled(true);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager)
                        recyclerView.getLayoutManager();


                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int pastVisibleItems = 0;
                int[] firstVisibleItems = null;
                firstVisibleItems = layoutManager.findFirstVisibleItemPositions(firstVisibleItems);
                if(firstVisibleItems != null && firstVisibleItems.length > 0) {
                    pastVisibleItems = firstVisibleItems[0];
                }

                if (itemsViewModel.getIsLoading().getValue() != null &&
                        itemsViewModel.getIsLoading().getValue()) {
                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        //loading = false;
                        //Log.d("tag", "LOAD NEXT ITEM");
                        itemsViewModel.loadNextPage(page + 1);
                    }
                }

            }
        });
    }

    private void loadMore() {

    }

}
