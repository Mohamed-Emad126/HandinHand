package com.example.handinhand.MainContent;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import com.example.handinhand.R;
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

        navController = Navigation.findNavController(this,
                R.id.main_content_nav_host_fragment);


        toolbar.inflateMenu(R.menu.toolbar_menu);

/*
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.filter){
                    navController.
                }
                return false;
            }
        });
*/



        navController.addOnDestinationChangedListener(this);

        NavigationUI.setupWithNavController(bottomNavigation, navController);
        NavigationUI.setupWithNavController(navigationView, navController);

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
                R.id.profileFragment
        };
        List<Integer> lst = Arrays.asList(withoutToolbarAndBottomNavIds);

        if(lst.contains(destination.getId())/*destination.getId() == R.id.itemDescriptionFragment*/){

            toolbar.hideOverflowMenu();
            //Objects.requireNonNull(getSupportActionBar()).hide();
            appBarLayout.setVisibility(View.GONE);
            toolbar.setVisibility(View.GONE);
            bottomNavigation.setVisibility(View.GONE);
        }
        else{
            toolbar.showOverflowMenu();
            //Objects.requireNonNull(getSupportActionBar()).show();
            appBarLayout.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.VISIBLE);
            bottomNavigation.setVisibility(View.VISIBLE);
        }

    }

}
