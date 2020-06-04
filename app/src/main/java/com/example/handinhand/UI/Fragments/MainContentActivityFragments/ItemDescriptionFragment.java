package com.example.handinhand.UI.Fragments.MainContentActivityFragments;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.transition.TransitionInflater;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.handinhand.R;
import com.example.handinhand.ViewModels.ImagePreviewViewModel;
import com.example.handinhand.ViewModels.SharedItemViewModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;


public class ItemDescriptionFragment extends Fragment {


    private ImageView itemImage;
    private TextView description;
    private TextView price;
    private TextView facebook;
    private TextView phoneNum;
    private CollapsingToolbarLayout titleToolbarLayout;
    private MaterialButton bookButton;

    private SharedItemViewModel sharedItemViewModel;
    private ImagePreviewViewModel imagePreviewViewModel;

    private ClipboardManager clipboardManager;
    private ClipData mClipData;
    private String url = null;

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
        View rootView = inflater.inflate(R.layout.fragment_item_description, container,
                false);

        FragmentActivity activity = requireActivity();
        setUpToolBar(rootView);

        itemImage = rootView.findViewById(R.id.item_image);
        titleToolbarLayout = rootView.findViewById(R.id.collapse_item_description);
        price = rootView.findViewById(R.id.item_description_price);
        description = rootView.findViewById(R.id.item_description_description);
        facebook = rootView.findViewById(R.id.item_description_facebook);
        phoneNum = rootView.findViewById(R.id.item_description_phoneNum);
        bookButton = rootView.findViewById(R.id.item_description_book);
        clipboardManager = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);

        sharedItemViewModel = new ViewModelProvider(requireActivity()).get(SharedItemViewModel.class);
        imagePreviewViewModel = new ViewModelProvider(requireActivity()).get(ImagePreviewViewModel.class);


        sharedItemViewModel.getSelected().observe(getViewLifecycleOwner(), data -> {
            Glide.with(rootView)
                    .load(getString(R.string.items_image_url) + data.getImage())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            url = getString(R.string.items_image_url) + data.getImage();
                            return false;
                        }
                    })
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .placeholder(R.color.gray)
                    .into(itemImage);
            description.setText(data.getDescription());
            titleToolbarLayout.setTitle(data.getTitle());
            facebook.setText(data.getFacebook());
            phoneNum.setText(data.getPhone());
            price.setText(
                    data.getPrice().equalsIgnoreCase("0") ?
                            getString(R.string.free)
                            : data.getPrice()
            );

        });

        itemImage.setOnClickListener(view -> {
            if(url != null) {
                imagePreviewViewModel.setUrl(url);
                Navigation.findNavController(rootView)
                        .navigate(R.id.action_itemDescriptionFragment_to_imagePreviewFragment);
            }
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
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
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

        Toolbar toolbar = rootView.findViewById(R.id.item_description_toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(view ->
                Navigation.findNavController(rootView).popBackStack()
        );

        toolbar.setOnMenuItemClickListener(item -> false);

    }


}
