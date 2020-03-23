package com.example.handinhand.MainContent;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.handinhand.R;
import com.example.handinhand.ViewModels.SharedItemViewModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemDescriptionFragment extends Fragment {


    private ImageView itemImage;
    private TextView description;
    private TextView price;
    private TextView facebook;
    private TextView phoneNum;
    private CollapsingToolbarLayout titleToolbarLayout;
    private MaterialButton bookButton;

    private SharedItemViewModel sharedItemViewModel;

    private ClipboardManager clipboardManager;
    private ClipData mClipData;

    public ItemDescriptionFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            setSharedElementEnterTransition(TransitionInflater.from(getActivity())
                    .inflateTransition(android.R.transition.move));
            setSharedElementReturnTransition(TransitionInflater.from(getActivity())
                    .inflateTransition(android.R.transition.move));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_item_description, container, false);

        FragmentActivity activity = getActivity();
        setUpToolBar(rootView);

        itemImage = rootView.findViewById(R.id.item_image);
        titleToolbarLayout = rootView.findViewById(R.id.collapse_item_description);
        price = rootView.findViewById(R.id.item_description_price);
        description = rootView.findViewById(R.id.item_description_description);
        facebook = rootView.findViewById(R.id.item_description_facebook);
        phoneNum = rootView.findViewById(R.id.item_description_phoneNum);
        bookButton = rootView.findViewById(R.id.item_description_book);
        clipboardManager = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);

        sharedItemViewModel = new ViewModelProvider(requireActivity()).get(SharedItemViewModel.class);


        sharedItemViewModel.getSelected().observe(getViewLifecycleOwner(), data -> {
            Picasso.get()
                    .load( getString(R.string.items_image_url) + data.getImage())
                    .fit()
                    .into(itemImage);
            description.setText(data.getDescription());
            titleToolbarLayout.setTitle(data.getTitle());
            facebook.setText(data.getFacebook());
            phoneNum.setText(data.getPhone());
            price.setText(
                    data.getPrice().equalsIgnoreCase("0")?
                            getString(R.string.free)
                            :data.getPrice()
            );

        });


        description.setOnLongClickListener(view -> {
            mClipData = ClipData.newPlainText("text", description.getText().toString().trim());
            clipboardManager.setPrimaryClip(mClipData);
            Toast.makeText(activity, R.string.copied, Toast.LENGTH_SHORT).show();
            return true;
        });
        facebook.setOnClickListener(view -> {
            Uri webPage = Uri.parse(facebook.getText().toString());
            Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
            if (intent.resolveActivity(activity.getPackageManager()) != null) {
                startActivity(intent);
            }
        });
        phoneNum.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNum.getText()));
            if (intent.resolveActivity(activity.getPackageManager()) != null) {
                startActivity(intent);
            }
        });

        bookButton.setOnClickListener(view -> {

        });

        return rootView;
    }


    private void setUpToolBar(final View rootView) {

            Toolbar toolbar= rootView.findViewById(R.id.item_description_toolbar);

            toolbar.setNavigationIcon(R.drawable.ic_back);
            toolbar.setNavigationOnClickListener(view ->
                    Navigation.findNavController(rootView).popBackStack()
            );



            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    return false;
                }
            });

    }


}
