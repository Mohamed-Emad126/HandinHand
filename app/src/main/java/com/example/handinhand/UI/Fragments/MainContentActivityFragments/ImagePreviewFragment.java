package com.example.handinhand.UI.Fragments.MainContentActivityFragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.handinhand.R;
import com.example.handinhand.ViewModels.ImagePreviewViewModel;
import com.example.handinhand.services.DownloadImageWorker;
import com.github.chrisbanes.photoview.PhotoView;

public class ImagePreviewFragment extends Fragment {


    public ImagePreviewFragment() {
        // Required empty public constructor
    }
    private ImagePreviewViewModel model;
    private String url = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_image_preview, container, false);
        PhotoView photoView = rootView.findViewById(R.id.image_preview);
        ImageButton downloadButton = rootView.findViewById(R.id.download_button);
        ImageButton backButton = rootView.findViewById(R.id.back_button_in_image_preview);
        FragmentActivity activity = getActivity();
        model = new ViewModelProvider(activity).get(ImagePreviewViewModel.class);

        backButton.setOnClickListener(view -> {
            Navigation.findNavController(rootView).navigateUp();
        });
        model.getUrl().observe(activity, s -> {
            if(!s.isEmpty()){
                Glide.with(activity)
                        .load(s)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                downloadButton.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                downloadButton.setVisibility(View.VISIBLE);
                                url = s;
                                return false;
                            }
                        })
                        .placeholder(R.drawable.ic_image_placeholder)
                        .into(photoView);

            }
        });


        photoView.setOnClickListener(view -> {
            if(downloadButton.getVisibility() == View.GONE){
                backButton.setVisibility(View.VISIBLE);
                downloadButton.setVisibility(View.VISIBLE);
            }
            else{
                backButton.setVisibility(View.GONE);
                downloadButton.setVisibility(View.GONE);
            }
        });

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        Data data = new Data.Builder()
                .putString("IMAGE_URL", url).build();

        OneTimeWorkRequest uploadWorkRequest = new OneTimeWorkRequest
                .Builder(DownloadImageWorker.class)
                .setConstraints(constraints)
                .setInputData(data)
                .build();

        downloadButton.setOnClickListener(view -> {

            if(ContextCompat.checkSelfPermission(requireActivity(),
                    Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED){
                requestWriteToDiskPermission();
            }
            else{
                WorkManager.getInstance(activity).enqueue(uploadWorkRequest);
            }

        });

        return rootView;
    }

    private void requestWriteToDiskPermission() {

        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(requireActivity())
                    .setTitle(R.string.permission_nedded)
                    .setMessage(R.string.permission_reason)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> requestPermissions(
                            new String[]{Manifest.permission.CALL_PHONE}, 70))
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                    .create().show();
        } else {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 70);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 70) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireActivity(), R.string.granted, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireActivity(), R.string.denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

}