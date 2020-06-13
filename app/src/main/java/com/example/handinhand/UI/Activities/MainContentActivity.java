package com.example.handinhand.UI.Activities;

import android.os.Bundle;
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
import com.example.handinhand.ViewModels.ProfileViewModel;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainContentActivity extends AppCompatActivity
        implements NavController.OnDestinationChangedListener {


    private NavController navController;
    private BottomNavigationView bottomNavigation;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private CircleImageView userImageInToolbar;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private ProfileViewModel model;
    private TextView userName;
    private ImageView userImageHeader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main_content);


        bottomNavigation = findViewById(R.id.main_content_bottom_navigation);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.main_Content_nav_view);
        userImageInToolbar = findViewById(R.id.toolbar_user_image);
        toolbar= findViewById(R.id.main_Content_toolbar);
        appBarLayout = findViewById(R.id.main_Content_appbar);
        userName = navigationView.getHeaderView(0).findViewById(R.id.user_name_header);
        userImageHeader = navigationView.getHeaderView(0).findViewById(R.id.user_image_header);
        model = new ViewModelProvider(this).get(ProfileViewModel.class);

        if(NetworkUtils.getConnectivityStatus(this) == 0){
            Toast.makeText(this, getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
        }
        else{
            model.getProfile(SharedPreferenceHelper.getToken(this)).observe(this,
                    profile -> {
                        if(profile != null &&
                                profile.getStatus() &&
                                profile.getDetails().getUser() != null){

                            Profile.User user = profile.getDetails().getUser();

                            String name = user.getInfo().getFirst_name() +" "+ user.getInfo().getLast_name();
                            userName.setText(name);

                            if(user.getInfo()
                                    .getAvatar().contains("default")){

                                if(user.getInfo()
                                        .getGender().contains("male")){

                                    Glide.with(this).load(R.drawable.male_avatar)
                                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                                            .placeholder(R.drawable.male_avatar)
                                            .into(userImageHeader);

                                    Glide.with(this).load(R.drawable.male_avatar)
                                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                                            .placeholder(R.drawable.male_avatar)
                                            .into(userImageInToolbar);

                                }
                                else{
                                    Glide.with(this).load(R.drawable.female_avatar)
                                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                                            .placeholder(R.drawable.female_avatar)
                                            .into(userImageHeader);

                                    Glide.with(this).load(R.drawable.female_avatar)
                                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                                            .placeholder(R.drawable.female_avatar)
                                            .into(userImageInToolbar);
                                }
                            }
                            else{

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
                        }
                        else{
                            Toast toast = Toast.makeText(this, getString(R.string.something_wrong)
                                    , Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.BOTTOM, 0, 0);
                            toast.show();
                        }
                    });
        }


        navController = Navigation.findNavController(this,
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

        /*navigationView.setNavigationItemSelectedListener(item -> {
            if(item.getItemId() == R.id.logout){
                SharedPreferenceHelper.removeToken(MainContentActivity.this);
                startActivity(new Intent(MainContentActivity.this, MainActivity.class));
                finish();
            }
            return true;
        });*/



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
        Integer []withoutToolbarAndBottomNavIds = {
                R.id.itemDescriptionFragment,
                R.id.profileFragment,
                R.id.editProfileFragment,
                R.id.addItemFragment,
                R.id.logOutFragment,
                R.id.imagePreviewFragment,
                R.id.reportFragment,
                R.id.addEventFragment,
                R.id.eventDescriptionFragment
        };
        List<Integer> lst = Arrays.asList(withoutToolbarAndBottomNavIds);

        if(lst.contains(destination.getId())){

            appBarLayout.animate().y(0).translationY(-100f).withStartAction(() ->
                    appBarLayout.setVisibility(View.GONE)
            ).start();
            //appBarLayout.setVisibility(View.GONE);
            //toolbar.setVisibility(View.GONE);
            bottomNavigation.animate().y(0).translationY(100f).withStartAction(() ->
                    bottomNavigation.setVisibility(View.GONE)
            ).start();
            //bottomNavigation.setVisibility(View.GONE);
        }
        else{
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
