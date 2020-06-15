package com.example.handinhand.UI.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;

import com.example.handinhand.Helpers.SharedPreferenceHelper;
import com.example.handinhand.R;
import com.example.handinhand.ViewModels.MainActivityViewModel;

public class MainActivity extends AppCompatActivity
        implements NavController.OnDestinationChangedListener {

    private ConstraintLayout fullLoadingView;
    private View navHostFragment;
    private MainActivityViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);

        //SharedPreferenceHelper.removeToken(this);

        if (SharedPreferenceHelper.getToken(this).length() > 0) {
            startActivity(new Intent(this, MainContentActivity.class));
            finish();
        }
        /*//TODO: Remove these two lines
        startActivity(new Intent(this, MainContentActivity.class));
        finish();
*/
        setContentView(R.layout.activity_main);

        fullLoadingView = findViewById(R.id.full_loading_view);
        navHostFragment = findViewById(R.id.nav_host_fragment);

        model = new ViewModelProvider(this).get(MainActivityViewModel.class);

        model.getIsLoading()
                .observe(this, aBoolean -> {
                    if (aBoolean) {
                        fullLoadingView.setVisibility(View.VISIBLE);
                        navHostFragment.clearFocus();
                        navHostFragment.setVisibility(View.GONE);
                    } else {
                        fullLoadingView.setVisibility(View.GONE);
                        navHostFragment.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (model.getIsLoading().getValue() != null && model.getIsLoading().getValue()) {
            model.leave();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestinationChanged(@NonNull NavController controller,
                                     @NonNull NavDestination destination,
                                     @Nullable Bundle arguments) {
        model.leave();

    }
}