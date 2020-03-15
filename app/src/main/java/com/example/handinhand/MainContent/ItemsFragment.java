package com.example.handinhand.MainContent;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.handinhand.Adapters.ItemsAdapter;
import com.example.handinhand.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemsFragment extends Fragment {

    private FloatingActionButton addFab;
    private RecyclerView recyclerView;
    ItemsAdapter itemsAdapter;

    public ItemsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_items, container, false);
        recyclerView = rootView.findViewById(R.id.items_recycler_view);
        itemsAdapter = new ItemsAdapter();
        initRecyclerView();

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


        /*final ImageView imageView = rootView.findViewById(R.id.item_image);
        rootView.findViewById(R.id.item).setOnClickListener(view -> {

            FragmentNavigator.Extras extras = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                extras = new FragmentNavigator.Extras.Builder()
                        .addSharedElement(imageView, imageView.getTransitionName())
                        .addSharedElement(rootView.findViewById(R.id.item_title), "itemTitle")
                        .build();
            }
            Navigation.findNavController(view).navigate(R.id.action_itemsFragment_to_itemDescriptionFragment,
                    null, // Bundle of args
                    null, // NavOptions
                    extras);
        });*/


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

                if (true/*loading*/) {
                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        //loading = false;
                        //Log.d("tag", "LOAD NEXT ITEM");
                    }
                }

            }
        });
    }

}
