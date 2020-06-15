package com.example.handinhand.UI.Fragments.MainContentActivityFragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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
import com.example.handinhand.ViewModels.EventSharedViewModel;
import com.example.handinhand.ViewModels.ImagePreviewViewModel;
import com.example.handinhand.ViewModels.ProfileViewModel;
import com.example.handinhand.services.DeleteWorker;
import com.example.handinhand.services.InterestWorker;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class EventDescriptionFragment extends Fragment {

    public EventDescriptionFragment() {
    }

    private Toolbar toolbar;
    private SwipeRefreshLayout refreshLayout;
    private KenBurnsView kenBurnsView;
    private FloatingActionButton interestFab;
    private TextView title;
    private TextView about;
    private TextView description;
    private TextView location;
    private TextView interests;
    private EventSharedViewModel sharedViewModel;
    private ImagePreviewViewModel imagePreviewViewModel;
    private ProfileViewModel profileViewModel;
    private boolean isInterested = false;
    private String url;
    String id;
    int userId;
    int eventId;
    int position;
    private AlertDialog alertDialog;

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
        final View rootView = inflater.inflate(R.layout.fragment_event_description,
                container, false);
        position = getArguments().getInt("position");

        toolbar = rootView.findViewById(R.id.event_toolbar);
        refreshLayout = rootView.findViewById(R.id.swipe_refresh_in_event_description);
        kenBurnsView = rootView.findViewById(R.id.image_ken);
        interestFab = rootView.findViewById(R.id.interest_fab);
        title = rootView.findViewById(R.id.event_title);
        about = rootView.findViewById(R.id.event_about);
        description = rootView.findViewById(R.id.event_description);
        location = rootView.findViewById(R.id.event_location);
        interests = rootView.findViewById(R.id.event_interests);
        FragmentActivity activity = getActivity();
        createDeleteDialog(activity, rootView);
        setUpToolBar(rootView);

        sharedViewModel = new ViewModelProvider(activity).get(EventSharedViewModel.class);
        imagePreviewViewModel = new ViewModelProvider(activity).get(ImagePreviewViewModel.class);

        profileViewModel = new ViewModelProvider(activity).get(ProfileViewModel.class);
        id = profileViewModel.getProfile(SharedPreferenceHelper.getToken(activity))
                .getValue()
                .getDetails()
                .getUser()
                .getId();

        sharedViewModel.getSelected().observe(activity, data -> {
            eventId = data.getId();
            isInterested = data.getIs_interested();
            title.setText(data.getTitle());
            about.setText(data.getAbout());
            description.setText(data.getDescription());
            location.setText(data.getLocation());
            userId = data.getUser_id();
            interests.setText(String.valueOf(data.getInterests()));
            interestFab.setSelected(data.getIs_interested());
            Glide.with(rootView)
                    .load(getString(R.string.events_image_url) + data.getImage())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            url = getString(R.string.events_image_url) + data.getImage();
                            return false;
                        }
                    })
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .into(kenBurnsView);
        });

        refreshLayout.setOnRefreshListener(() ->
                new Handler().postDelayed(() -> refreshLayout.setRefreshing(false),
                        1000));

        interestFab.setOnClickListener(view -> {
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();
            Data data = new Data.Builder()
                    .putString("TYPE", "event")
                    .putString("ELEMENT_ID", String.valueOf(eventId))
                    .build();

            OneTimeWorkRequest interestWorker = new OneTimeWorkRequest
                    .Builder(InterestWorker.class)
                    .setConstraints(constraints)
                    .setInputData(data)
                    .build();
            if(NetworkUtils.getConnectivityStatus(activity) == NetworkUtils.TYPE_NOT_CONNECTED){
                Toast.makeText(activity, getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
            }
            else{
                WorkManager.getInstance(activity).enqueue(interestWorker);
                sharedViewModel.interestSelected();
                sharedViewModel.setInterestAt(position);
                //Navigation.findNavController(rootView).popBackStack();
            }
        });

        kenBurnsView.setOnClickListener(view -> {
            if(url != null) {
                imagePreviewViewModel.setUrl(url);
                Navigation.findNavController(rootView)
                        .navigate(R.id.action_eventDescriptionFragment_to_imagePreviewFragment);
            }
        });



        return rootView;
    }

    private void setUpToolBar(final View rootView) {
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
                sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.events_url)+
                        String.valueOf(eventId));
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
        bundle.putString("id", String.valueOf(eventId));
        bundle.putString("type", "event");
        Navigation.findNavController(rootView)
                .navigate(R.id.action_eventDescriptionFragment_to_reportFragment, bundle);
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
                            .putString("TYPE", "event")
                            .putString("ELEMENT_ID", String.valueOf(eventId))
                            .build();

                    OneTimeWorkRequest deleteWorker = new OneTimeWorkRequest
                            .Builder(DeleteWorker.class)
                            .setConstraints(constraints)
                            .setInputData(data)
                            .build();
                    WorkManager.getInstance(requireActivity()).enqueue(deleteWorker);
                    sharedViewModel.deleteAt(position);
                    alertDialog.dismiss();
                    Navigation.findNavController(rootView).popBackStack();
                });

        dialog.setNegativeButton(
                getString(R.string.cancel),(dialogInterface, i) -> {
                    alertDialog.dismiss();
                });

        alertDialog = dialog.create();
    }

}
