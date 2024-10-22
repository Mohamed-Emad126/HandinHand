package com.example.handinhand.UI.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.handinhand.Helpers.SharedPreferenceHelper;
import com.example.handinhand.Models.Profile;
import com.example.handinhand.R;
import com.example.handinhand.Utils.NetworkUtils;
import com.example.handinhand.ViewModels.NotificationViewModel;
import com.example.handinhand.ViewModels.ProfileViewModel;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainContentActivity extends AppCompatActivity
        implements NavController.OnDestinationChangedListener {


    private static final String TAG = "MainContent-Pusher";
    private BottomNavigationView bottomNavigation;
    private DrawerLayout drawerLayout;
    private CircleImageView userImageInToolbar;
    private AppBarLayout appBarLayout;
    private TextView userName;
    private ImageView userImageHeader;
    private Pusher pusher;
    private NotificationViewModel notificationViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main_content);


        initPusher();
        bottomNavigation = findViewById(R.id.main_content_bottom_navigation);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.main_Content_nav_view);
        userImageInToolbar = findViewById(R.id.toolbar_user_image);
        Toolbar toolbar = findViewById(R.id.main_Content_toolbar);
        appBarLayout = findViewById(R.id.main_Content_appbar);
        userName = navigationView.getHeaderView(0).findViewById(R.id.user_name_header);
        userImageHeader = navigationView.getHeaderView(0).findViewById(R.id.user_image_header);
        ProfileViewModel model = new ViewModelProvider(this).get(ProfileViewModel.class);
        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);


        if (NetworkUtils.getConnectivityStatus(this) == 0) {
            Toast.makeText(this, getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
        } else {
            model.getProfile(SharedPreferenceHelper.getToken(this)).observe(this,
                    profile -> {
                        if (profile != null &&
                                profile.getStatus() &&
                                profile.getDetails().getUser() != null) {
                            startPusher(profile.getDetails().getUser().getId());

                            Profile.User user = profile.getDetails().getUser();

                            String name = user.getInfo().getFirst_name() + " " + user.getInfo().getLast_name();
                            userName.setText(name);

                            if (user.getInfo()
                                    .getAvatar().contains("default")) {

                                if (user.getInfo()
                                        .getGender().contains("male")) {

                                    Glide.with(this).load(R.drawable.male_avatar)
                                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                                            .placeholder(R.drawable.male_avatar)
                                            .into(userImageHeader);

                                    Glide.with(this).load(R.drawable.male_avatar)
                                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                                            .placeholder(R.drawable.male_avatar)
                                            .into(userImageInToolbar);

                                } else {
                                    Glide.with(this).load(R.drawable.female_avatar)
                                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                                            .placeholder(R.drawable.female_avatar)
                                            .into(userImageHeader);

                                    Glide.with(this).load(R.drawable.female_avatar)
                                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                                            .placeholder(R.drawable.female_avatar)
                                            .into(userImageInToolbar);
                                }
                            } else {

                                Glide.with(this).load(getString(R.string.avatar_url) +
                                        user.getInfo().getAvatar())
                                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                                        .placeholder(R.drawable.male_avatar)
                                        .into(userImageHeader);

                                Glide.with(this).load(getString(R.string.avatar_url) +
                                        user.getInfo().getAvatar())
                                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                                        .placeholder(R.drawable.male_avatar)
                                        .into(userImageInToolbar);

                            }
                        } else {
                            Toast toast = Toast.makeText(this, getString(R.string.something_wrong)
                                    , Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.BOTTOM, 0, 0);
                            toast.show();
                        }
                    });
        }

        notificationViewModel.getNewNotification().observe(this, aBoolean -> {
            if (aBoolean) {
                bottomNavigation.getOrCreateBadge(R.id.notificationsFragment);
            } else {
                bottomNavigation.removeBadge(R.id.notificationsFragment);
            }
        });


        NavController navController = Navigation.findNavController(this,
                R.id.main_content_nav_host_fragment);


        toolbar.inflateMenu(R.menu.toolbar_menu);

        navController.addOnDestinationChangedListener(this);

        //AppBarConfiguration configuration = new AppBarConfiguration.Builder(bottomNavigation.getMenu()).build();

        NavigationUI.setupWithNavController(bottomNavigation, navController);
        NavigationUI.setupWithNavController(navigationView, navController);
        //NavigationUI.setupWithNavController(toolbar, navController, configuration);

        userImageInToolbar.setOnClickListener(view ->
                drawerLayout.openDrawer(GravityCompat.START)
        );

    }

    private void initPusher() {
        PusherOptions options = new PusherOptions();
        options.setCluster("eu");
        pusher = new Pusher("4970d5ea2182736d2d20", options);

        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                //Toast.makeText(MainContentActivity.this, "", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onConnectionStateChange: Connected");
            }

            @Override
            public void onError(String message, String code, Exception e) {
                Log.e(TAG, "onError: Pusher Error", e.fillInStackTrace());
            }
        }, ConnectionState.ALL);
    }

    private void startPusher(String id) {
        Channel channel = pusher.subscribe("user-" + id);

        channel.bind("App\\Events\\NotificationWasPushed", event -> {
                    runOnUiThread(() -> {
                        Log.d(TAG, "startPusher: " + event.getEventName());
                        Log.d(TAG, "startPusher: user-" + id);
                        notificationViewModel.setNewNotification(true);
                    });
                }
        );
    }


    /**
     * Handle back pressed
     * if navigationView is open close it else do the default back pressed
     */
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestinationChanged(@NonNull NavController controller,
                                     @NonNull NavDestination destination,
                                     @Nullable Bundle arguments) {
        Integer[] withoutToolbarAndBottomNavIds = {
                R.id.itemDescriptionFragment,
                R.id.profileFragment,
                R.id.editProfileFragment,
                R.id.addItemFragment,
                R.id.logOutFragment,
                R.id.imagePreviewFragment,
                R.id.reportFragment,
                R.id.addEventFragment,
                R.id.eventDescriptionFragment,
                R.id.productDescriptionFragment,
                R.id.addProductFragment,
                R.id.addServiceFragment,
                R.id.serviceDescriptionFragment,
                R.id.eventInterestersFragment,
                R.id.serviceInterestersFragment,
                R.id.dealFragment,
                R.id.dealCompletedFragment,
                R.id.acceptDealFragment
        };
        List<Integer> lst = Arrays.asList(withoutToolbarAndBottomNavIds);

        if (lst.contains(destination.getId())) {

            appBarLayout.animate().y(0).translationY(-100f).withStartAction(() ->
                    appBarLayout.setVisibility(View.GONE)
            ).start();
            //appBarLayout.setVisibility(View.GONE);
            //toolbar.setVisibility(View.GONE);
            bottomNavigation.animate().y(0).translationY(100f).withStartAction(() ->
                    bottomNavigation.setVisibility(View.GONE)
            ).start();
            //bottomNavigation.setVisibility(View.GONE);
        } else {
            appBarLayout.animate().y(-100).translationY(0f).withStartAction(() ->
                    appBarLayout.setVisibility(View.VISIBLE)
            ).start();
            //appBarLayout.setVisibility(View.VISIBLE);
            //toolbar.setVisibility(View.VISIBLE);
            bottomNavigation.animate().y(100).translationY(0f).withStartAction(() ->
                    bottomNavigation.setVisibility(View.VISIBLE)
            ).start();
            //bottomNavigation.setVisibility(View.VISIBLE);
        }

    }

}
