package com.example.handinhand.UI.Fragments.MainContentActivityFragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.handinhand.Helpers.SharedPreferenceHelper;
import com.example.handinhand.R;
import com.example.handinhand.Utils.NetworkUtils;
import com.example.handinhand.ViewModels.ProfileViewModel;
import com.example.handinhand.ViewModels.ServiceSharedViewModel;
import com.example.handinhand.services.DeleteWorker;
import com.example.handinhand.services.InterestWorker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ServiceDescriptionFragment extends Fragment {

    public ServiceDescriptionFragment() {
    }

    private Toolbar toolbar;
    private SwipeRefreshLayout refreshLayout;
    private FloatingActionButton interestFab;
    private TextView title;
    private TextView target;
    private TextView price;
    private TextView description;
    private TextView goal;
    private TextView interests;
    private ServiceSharedViewModel serviceSharedViewModel;
    private ProfileViewModel profileViewModel;
    private boolean isInterested = false;
    private String url;
    String id;
    int userId;
    int serviceId;
    int position;
    private AlertDialog alertDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_service_description,
                container, false);
        position = getArguments().getInt("position");

        toolbar = rootView.findViewById(R.id.service_toolbar);
        refreshLayout = rootView.findViewById(R.id.swipe_refresh_in_service_description);
        interestFab = rootView.findViewById(R.id.interest_fab);
        title = rootView.findViewById(R.id.service_title);
        goal = rootView.findViewById(R.id.service_goal);
        price = rootView.findViewById(R.id.service_price);
        target = rootView.findViewById(R.id.service_target);
        title = rootView.findViewById(R.id.service_title);
        description = rootView.findViewById(R.id.service_description);
        interests = rootView.findViewById(R.id.service_interests);
        FragmentActivity activity = getActivity();
        createDeleteDialog(activity, rootView);
        setUpToolBar(rootView);

        serviceSharedViewModel = new ViewModelProvider(activity).get(ServiceSharedViewModel.class);

        profileViewModel = new ViewModelProvider(activity).get(ProfileViewModel.class);
        id = profileViewModel.getProfile(SharedPreferenceHelper.getToken(activity))
                .getValue()
                .getDetails()
                .getUser()
                .getId();

        serviceSharedViewModel.getSelected().observe(activity, data -> {
            serviceId = data.getId();
            isInterested = data.getIs_interested();
            title.setText(data.getTitle());
            description.setText(data.getDescription());
            target.setText(data.getTarget());
            goal.setText(String.valueOf(data.getGoal()));
            price.setText(String.valueOf(data.getPrice()));
            interests.setText(String.valueOf(data.getInterests()));
            interestFab.setSelected(data.getIs_interested());
            userId = data.getUser_id();
        });

        refreshLayout.setOnRefreshListener(() ->
                new Handler().postDelayed(() -> refreshLayout.setRefreshing(false),
                        1000));

        interestFab.setOnClickListener(view -> {
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();
            Data data = new Data.Builder()
                    .putString("TYPE", "service")
                    .putString("ELEMENT_ID", String.valueOf(serviceId))
                    .build();

            OneTimeWorkRequest interestWorker = new OneTimeWorkRequest
                    .Builder(InterestWorker.class)
                    .setConstraints(constraints)
                    .setInputData(data)
                    .build();
            if (NetworkUtils.getConnectivityStatus(activity) == NetworkUtils.TYPE_NOT_CONNECTED) {
                Toast.makeText(activity, getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
            } else {
                WorkManager.getInstance(activity).enqueue(interestWorker);
                serviceSharedViewModel.interestSelected();
                serviceSharedViewModel.setInterestAt(position);
            }
        });

        return rootView;
    }

    private void setUpToolBar(final View rootView) {
        toolbar.setNavigationOnClickListener(view ->
                Navigation.findNavController(rootView).popBackStack()
        );
        toolbar.setOnMenuItemClickListener(item -> {
            if (String.valueOf(userId).equals(id) && item.getItemId() == R.id.delete) {
                item.setVisible(false);
                item.setEnabled(false);
            }
            if (item.getItemId() == R.id.report) {
                reportItem(rootView);
            } else if (item.getItemId() == R.id.share) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                //TODO: Change the text
                sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.services_url) +
                        String.valueOf(serviceId));
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            } else if (item.getItemId() == R.id.delete) {
                alertDialog.show();
            }
            return true;
        });
    }

    private void reportItem(View rootView) {
        Bundle bundle = new Bundle();
        bundle.putString("id", String.valueOf(serviceId));
        bundle.putString("type", "service");
        Navigation.findNavController(rootView)
                .navigate(R.id.action_serviceDescriptionFragment_to_reportFragment, bundle);
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
                            .putString("TYPE", "service")
                            .putString("ELEMENT_ID", String.valueOf(serviceId))
                            .build();

                    OneTimeWorkRequest deleteWorker = new OneTimeWorkRequest
                            .Builder(DeleteWorker.class)
                            .setConstraints(constraints)
                            .setInputData(data)
                            .build();
                    WorkManager.getInstance(requireActivity()).enqueue(deleteWorker);
                    serviceSharedViewModel.deleteAt(position);
                    alertDialog.dismiss();
                    Navigation.findNavController(rootView).popBackStack();
                });

        dialog.setNegativeButton(
                getString(R.string.cancel), (dialogInterface, i) -> {
                    alertDialog.dismiss();
                });

        alertDialog = dialog.create();
    }
}