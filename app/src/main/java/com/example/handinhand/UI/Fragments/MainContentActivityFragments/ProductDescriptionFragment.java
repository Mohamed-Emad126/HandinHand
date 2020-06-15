package com.example.handinhand.UI.Fragments.MainContentActivityFragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.transition.TransitionInflater;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.handinhand.Helpers.SharedPreferenceHelper;
import com.example.handinhand.R;
import com.example.handinhand.Utils.NetworkUtils;
import com.example.handinhand.ViewModels.ImagePreviewViewModel;
import com.example.handinhand.ViewModels.ProfileViewModel;
import com.example.handinhand.ViewModels.SharedProductViewModel;
import com.example.handinhand.services.DeleteWorker;
import com.example.handinhand.services.InterestWorker;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;

public class ProductDescriptionFragment extends Fragment {

    private ImageView productImage;
    private TextView description;
    private TextView price;
    private TextView facebook;
    private TextView phoneNum;
    private CollapsingToolbarLayout titleToolbarLayout;
    private MaterialButton bookButton;

    private SharedProductViewModel sharedProductViewModel;
    private ImagePreviewViewModel imagePreviewViewModel;
    private ProfileViewModel profileViewModel;
    private Toolbar toolbar;

    private ClipboardManager clipboardManager;
    private ClipData mClipData;
    private String url = null;
    String id;
    int itemId;
    int userId;
    int position;
    private AlertDialog alertDialog;
    private Intent intent;

    public ProductDescriptionFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_product_description, container,
                false);

        FragmentActivity requireActivity = requireActivity();
        FragmentActivity activity = requireActivity;
        createDeleteDialog(requireActivity, rootView);
        position = getArguments().getInt("position");

        productImage = rootView.findViewById(R.id.product_image);
        titleToolbarLayout = rootView.findViewById(R.id.collapse_product_description);
        price = rootView.findViewById(R.id.product_description_price);
        description = rootView.findViewById(R.id.product_description_description);
        facebook = rootView.findViewById(R.id.product_description_facebook);
        phoneNum = rootView.findViewById(R.id.product_description_phoneNum);
        bookButton = rootView.findViewById(R.id.product_description_book);
        clipboardManager = (ClipboardManager) requireActivity.getSystemService(Context.CLIPBOARD_SERVICE);

        sharedProductViewModel = new ViewModelProvider(requireActivity).get(SharedProductViewModel.class);
        imagePreviewViewModel = new ViewModelProvider(requireActivity).get(ImagePreviewViewModel.class);
        profileViewModel = new ViewModelProvider(requireActivity).get(ProfileViewModel.class);
        toolbar = rootView.findViewById(R.id.product_description_toolbar);
        setUpToolBar(rootView);

        id = profileViewModel.getProfile(SharedPreferenceHelper.getToken(requireActivity))
                .getValue()
                .getDetails()
                .getUser()
                .getId();

        sharedProductViewModel.getSelected().observe(getViewLifecycleOwner(), data -> {
            Glide.with(rootView)
                    .load(getString(R.string.products_image_url) + data.getImage())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            url = getString(R.string.products_image_url) + data.getImage();
                            return false;
                        }
                    })
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .placeholder(R.color.gray)
                    .into(productImage);
            itemId = data.getId();
            userId = data.getUser_id();
            description.setText(data.getDescription());
            titleToolbarLayout.setTitle(data.getTitle());
            facebook.setText(data.getFacebook());
            phoneNum.setText(data.getPhone());
            price.setText(
                    data.getPrice().equalsIgnoreCase("0") ?
                            getString(R.string.free)
                            : data.getPrice()
            );

            if(data.getUser_id()== Integer.parseInt(id)){
                toolbar.inflateMenu(R.menu.out_menu);
            }
            else{
                toolbar.inflateMenu(R.menu.out_menu_not_mine);
            }

        });

        productImage.setOnClickListener(view -> {
            if(url != null) {
                imagePreviewViewModel.setUrl(url);
                Navigation.findNavController(rootView)
                        .navigate(R.id.action_productDescriptionFragment_to_imagePreviewFragment);
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
            if (intent.resolveActivity(requireActivity.getPackageManager()) != null) {
                startActivity(intent);
            }
        });
        phoneNum.setOnClickListener(view -> {
            intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNum.getText()));
            if(ContextCompat.checkSelfPermission(requireActivity(),
                    Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED){
                requestPhoneCallPermission();
            }
            else{
                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        bookButton.setOnClickListener(view -> {
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();
            Data data = new Data.Builder()
                    .putString("TYPE", "product")
                    .putString("ELEMENT_ID", String.valueOf(itemId))
                    .build();

            OneTimeWorkRequest interestWorker = new OneTimeWorkRequest
                    .Builder(InterestWorker.class)
                    .setConstraints(constraints)
                    .setInputData(data)
                    .build();
            if(NetworkUtils.getConnectivityStatus(requireActivity) == NetworkUtils.TYPE_NOT_CONNECTED){
                Toast.makeText(activity, getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
            }
            else{
                WorkManager.getInstance(requireActivity).enqueue(interestWorker);
                Toast.makeText(activity, getString(R.string.requested), Toast.LENGTH_SHORT).show();
                Navigation.findNavController(rootView).popBackStack();
            }
        });

        return rootView;
    }


    private void setUpToolBar(final View rootView) {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(view ->
                Navigation.findNavController(rootView).popBackStack()
        );
        toolbar.setOnMenuItemClickListener(item -> {
            if(String.valueOf(userId).equals(id) && item.getItemId() == R.id.delete){
                item.setVisible(false);
                item.setEnabled(false);
            }
            if(item.getItemId() == R.id.report){
                reportItem(rootView);
            }
            else if(item.getItemId() == R.id.share){
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                //TODO: Change the text
                sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.products_url)+
                        String.valueOf(itemId));
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
            else if(item.getItemId() == R.id.delete){
                alertDialog.show();
            }
            return true;
        });
    }

    private void reportItem(View rootView) {
        Bundle bundle = new Bundle();
        bundle.putString("id", String.valueOf(itemId));
        bundle.putString("type", "product");
        Navigation.findNavController(rootView)
                .navigate(R.id.action_productDescriptionFragment_to_reportFragment, bundle);
    }

    private void createDeleteDialog(FragmentActivity activity, View rootView) {
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
                            .putString("ELEMENT_ID", String.valueOf(itemId))
                            .build();

                    OneTimeWorkRequest deleteWorker = new OneTimeWorkRequest
                            .Builder(DeleteWorker.class)
                            .setConstraints(constraints)
                            .setInputData(data)
                            .build();
                    WorkManager.getInstance(requireActivity()).enqueue(deleteWorker);
                    sharedProductViewModel.deleteAt(position);
                    alertDialog.dismiss();
                    Navigation.findNavController(rootView).navigateUp();
                });

        dialog.setNegativeButton(
                getString(R.string.cancel),(dialogInterface, i) -> {
                    alertDialog.dismiss();
                });

        alertDialog = dialog.create();
    }

    private void requestPhoneCallPermission() {

            if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                new AlertDialog.Builder(requireActivity())
                        .setTitle(R.string.permission_nedded)
                        .setMessage(R.string.permission_reason3)
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> requestPermissions(
                                new String[]{Manifest.permission.CALL_PHONE}, 1222))
                        .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                        .create().show();
            } else {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1222);
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                               @NonNull int[] grantResults) {
            if (requestCode == 1222) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(intent);
                    }
                    Toast.makeText(requireActivity(), R.string.granted, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireActivity(), R.string.denied, Toast.LENGTH_SHORT).show();
                }
            }
        }
}