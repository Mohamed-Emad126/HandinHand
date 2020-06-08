package com.example.handinhand.UI.Fragments.MainContentActivityFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.handinhand.R;
import com.example.handinhand.services.ReportWorker;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;


public class ReportFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_report, container, false);
        MaterialButton cancel = rootView.findViewById(R.id.button_report_cancel);
        MaterialButton report = rootView.findViewById(R.id.report_button);
        String reason = "";
        ChipGroup group = rootView.findViewById(R.id.report_chip_group_fragment);
        if(group.getCheckedChipId() == R.id.spam_chip_fragment){
            reason = "spam";
        }
        else{
            reason = "inappropriate";
        }

        String id = getArguments().getString("id");
        String type = getArguments().getString("type");

        cancel.setOnClickListener(view -> {
            Navigation.findNavController(rootView).navigateUp();
        });

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        Data data = new Data.Builder()
                .putString("TYPE", type)
                .putString("ELEMENT_ID", id)
                .putString("REASON", reason)
                .build();

        OneTimeWorkRequest reportWorker = new OneTimeWorkRequest
                .Builder(ReportWorker.class)
                .setConstraints(constraints)
                .setInputData(data)
                .build();
        report.setOnClickListener(view -> {
            WorkManager.getInstance(requireActivity()).enqueue(reportWorker);
            Navigation.findNavController(rootView).navigateUp();
        });


        return rootView;
    }
}