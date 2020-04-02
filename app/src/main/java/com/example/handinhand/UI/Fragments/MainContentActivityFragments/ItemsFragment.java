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
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.handinhand.Adapters.ItemsAdapter;
import com.example.handinhand.R;
import com.example.handinhand.ViewModels.ItemsViewModel;
import com.example.handinhand.ViewModels.SharedItemViewModel;
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
    int page = 0;
    int lastPage = 0;


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
        FragmentActivity activity = requireActivity();
        itemsViewModel = new ViewModelProvider(activity).get(ItemsViewModel.class);
        sharedItemViewModel = new ViewModelProvider(activity).get(SharedItemViewModel.class);
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
                popup.getMenuInflater().inflate(R.menu.item_description_menu, popup.getMenu());
                popup.show();
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
