package com.momskitchen.momskitchen;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ui.email.SignInActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.momskitchen.momskitchen.dummy.DummyContent;

public class AdminMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdminOrdersFragment.OnFragmentInteractionListener,
        MealsFragment.OnFragmentInteractionListener,
        MenuFragment.OnFragmentInteractionListener,
        MealListFragment.OnListFragmentInteractionListener{

    private static final String TAG = "AdminMainActivity: ";

    FrameLayout mainFrame;
    Fragment currentFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainFrame = (FrameLayout) findViewById(R.id.main_frame);
        if (savedInstanceState == null) {
            Fragment newFragment = new AdminOrdersFragment();
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(mainFrame.getId(), newFragment).commit();
            currentFragment = newFragment;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();



        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_orders) {
            Fragment newFragment = new AdminOrdersFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.remove(currentFragment);
            ft.add(mainFrame.getId(), newFragment).commit();
            currentFragment = newFragment;
            getSupportActionBar().setTitle(item.getTitle());
        } else if (id == R.id.nav_meals) {
            Fragment newFragment = new MealsFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.remove(currentFragment);
            ft.add(mainFrame.getId(), newFragment).commit();
            currentFragment = newFragment;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                currentFragment.getView().setElevation(5f);
//            }
            getSupportActionBar().setTitle(item.getTitle());
        } else if(id == R.id.nav_menu){
            Fragment newFragment = new MenuFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.remove(currentFragment);
            ft.add(mainFrame.getId(), newFragment).commit();
            currentFragment = newFragment;
            getSupportActionBar().setTitle(item.getTitle());
        }else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.sign_out) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            startActivity(new Intent(getApplicationContext(), StartupActivity.class));
                            Log.v(TAG,"1. signing admin out");
                            StartupActivity.saveAsAdminOrCustomer(getApplicationContext(),Constants.DONT_KNOW_USER_TYPE);
                            finish();
                        }
                    });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }
}
