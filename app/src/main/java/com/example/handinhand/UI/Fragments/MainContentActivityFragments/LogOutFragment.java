package com.example.handinhand.UI.Fragments.MainContentActivityFragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.handinhand.Helpers.SharedPreferenceHelper;
import com.example.handinhand.UI.Activities.MainActivity;
import com.example.handinhand.R;
import com.google.android.material.button.MaterialButton;

public class LogOutFragment extends Fragment {


    private MaterialButton logout;
    private MaterialButton cancel;

    public LogOutFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_log_out, container, false);

        logout = rootView.findViewById(R.id.button_logout);
        cancel = rootView.findViewById(R.id.button_logout_cancel);
        FragmentActivity activity = getActivity();

        logout.setOnClickListener(view -> {
            SharedPreferenceHelper.removeToken(activity);
            startActivity(new Intent(activity, MainActivity.class));
            activity.finish();
        });

        cancel.setOnClickListener(view -> {
            Navigation.findNavController(rootView).popBackStack();
        });

        return rootView;
    }
}
