package com.momskitchen.momskitchen.Customer.ActivitiesPlusFragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.momskitchen.momskitchen.Admin.ActivitiesPlusFragments.AdminOrdersFragment;
import com.momskitchen.momskitchen.Constants;
import com.momskitchen.momskitchen.Customer.MealAdapter;
import com.momskitchen.momskitchen.R;
import com.momskitchen.momskitchen.StartupActivity;
import com.momskitchen.momskitchen.backend.MenuCreator;
import com.squareup.picasso.Picasso;
import com.touchboarder.weekdaysbuttons.WeekdaysDataItem;
import com.touchboarder.weekdaysbuttons.WeekdaysDataSource;
import com.touchboarder.weekdaysbuttons.WeekdaysDrawableProvider;

import java.io.IOException;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CustomerMainActivity extends AppCompatActivity
        implements WeekdaysDataSource.Callback,NavigationView.OnNavigationItemSelectedListener,AdminOrdersFragment.OnFragmentInteractionListener, CustomerMenu.OnFragmentInteractionListener, CustomerMenuFragment.OnListFragmentInteractionListener {

    public static CustomerMainActivity activity;
    private static final String TAG = "CustomerMainActivity: ";

    FrameLayout mainFrame;
    Fragment currentFragment;

    public static Calendar calendar;

    private WeekdaysDataSource weekDaysDataSource;

    private int prevPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(FirebaseInstanceId.getInstance().getId()==null) {
            try {
                FirebaseInstanceId.getInstance().deleteInstanceId();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mainFrame = (FrameLayout) findViewById(R.id.main_frame);
        if (savedInstanceState == null) {
            calendar = Calendar.getInstance();
//            if(calendar.get(Calendar.HOUR_OF_DAY)>=14){
//                calendar.add(Calendar.DATE,1);
//            }
            CustomerMenuFragment.currentDate = MenuCreator.getInstance().getDateFromCalendar(calendar);
            prevPos = MenuCreator.getInstance().weekDayStringToNumber(
                    MenuCreator.getInstance().getDayFromDate(
                            MenuCreator.getInstance().getDateFromCalendar(calendar)
                    )
            );
            Fragment newFragment = new CustomerMenu();
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(mainFrame.getId(), newFragment).commit();
            currentFragment = newFragment;
        }else{
            String date =  savedInstanceState.getString("Date");
            if(date!=null) {
                calendar = MenuCreator.getInstance().getCalendarFromDate(date);
                CustomerMenuFragment.currentDate = date;
            }else{
                calendar = Calendar.getInstance();
//                if(calendar.get(Calendar.HOUR_OF_DAY)>=14){
//                    calendar.add(Calendar.DATE,1);
//                }
                CustomerMenuFragment.currentDate = MenuCreator.getInstance().getDateFromCalendar(calendar);
            }
            prevPos = savedInstanceState.getInt("PrevPos");
        }

        if(savedInstanceState!=null){
            weekDaysDataSource = WeekdaysDataSource.restoreState("week",savedInstanceState,this,this,null);
        }else{
            setupWeekdaysButtons();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_customer_main);

        TextView nameTV = (TextView) headerView.findViewById(R.id.profile_name_text_view);
        TextView emailTV = (TextView) headerView.findViewById(R.id.profile_email_text_view);
        final ImageView profileImage = (ImageView) headerView.findViewById(R.id.imageView);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            nameTV.setText(user.getDisplayName());
            emailTV.setText(user.getEmail());
            List<UserInfo> providers = (List<UserInfo>) user.getProviderData();
            String photoURL = null;
            if(user.getPhotoUrl()!=null) {
                photoURL = user.getPhotoUrl().toString();
            }else if(providers!=null) {
                for(UserInfo provider:providers){
                    if(provider.getPhotoUrl()!=null){
                        Log.v(TAG, " photo url found:"+provider.getPhotoUrl());
                        photoURL = provider.getPhotoUrl().toString();
                    }
                }
            }else {
                Log.v(TAG, " photo url is not available");
            }

            if(photoURL!=null) {
                Picasso.with(getApplicationContext()).load(photoURL).resize(getPXfromDP(70f), getPXfromDP(70f)).centerCrop().into(profileImage);
            }

        }
    }

    int getPXfromDP(float dps){
        return (int)(getResources().getDisplayMetrics().density*dps);
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

        if (id == R.id.sign_out) {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final String token = FirebaseInstanceId.getInstance().getToken();
            if(token != null){
                FirebaseDatabase.getInstance().getReference().
                        child("MapUIDtoInstanceID").
                        child(user.getUid()).
                        child(token).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            FirebaseDatabase.getInstance().getReference().
                                    child("MapUIDtoInstanceID").
                                    child(user.getUid()).child(token).
                                    setValue(null);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            startActivity(new Intent(getApplicationContext(), StartupActivity.class));
                            Log.v(TAG,"1. signing customer out");
                            StartupActivity.saveAsAdminOrCustomer(getApplicationContext(), Constants.DONT_KNOW_USER_TYPE);
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
    public void onListFragmentInteraction() {

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (weekDaysDataSource != null) {
            // If the weekdaysDataSource isn't null, save it's state for restoration in onCreate()
            weekDaysDataSource.saveState("week", outState);
        }
        outState.putString("Date",CustomerMenuFragment.currentDate);
        outState.putInt("PrevPos",prevPos);
    }


    @Override
    public void onWeekdaysItemClicked(int attachId, WeekdaysDataItem item) {
        Log.v(TAG," week day position: "+item.getPosition());
        if(item.isSelected()) {
            if(
                    item.getPosition()>=
                            MenuCreator.getInstance().weekDayStringToNumber(
                                    MenuCreator.getInstance().getDayFromDate(
                                            MenuCreator.getInstance().getDateFromCalendar(calendar)
                                    )
                            )
                    ) {
                String currentDate = MenuCreator.getInstance().getDateFromCalendar(calendar);
                String weekStart = MenuCreator.getInstance().getWeekStartFromDate(currentDate);
                String date = currentDate;
                weekDaysDataSource.selectAll(false);
                switch (item.getPosition()) {
                    case 0:
                        date = MenuCreator.getInstance().getDateFromWeekStartAndDay(weekStart, "MON");
                        break;
                    case 1:
                        date = MenuCreator.getInstance().getDateFromWeekStartAndDay(weekStart, "TUE");
                        break;
                    case 2:
                        date = MenuCreator.getInstance().getDateFromWeekStartAndDay(weekStart, "WED");
                        break;
                    case 3:
                        date = MenuCreator.getInstance().getDateFromWeekStartAndDay(weekStart, "THU");
                        break;
                    case 4:
                        date = MenuCreator.getInstance().getDateFromWeekStartAndDay(weekStart, "FRI");
                        break;
                    case 5:
                        date = MenuCreator.getInstance().getDateFromWeekStartAndDay(weekStart, "SAT");
                        break;
                    case 6:
                        date = MenuCreator.getInstance().getDateFromWeekStartAndDay(weekStart, "SUN");
                        break;
                    default: {
                    };
                    break;
                }
                CustomerMenuFragment.currentDate = date;
                CustomerMenuFragment.anyLunch = false;
                CustomerMenuFragment.anyDessert = false;
                CustomerMenuFragment.anyCompliment = false;
                MealAdapter.currentDate = date;
                CustomerMenuFragment.lunchListAdapter.refreshData();
                CustomerMenuFragment.dessertListAdapter.refreshData();
                CustomerMenuFragment.complimentListAdapter.refreshData();
                weekDaysDataSource.setSelectedDays(item.getPosition());
                prevPos = item.getPosition();
            }else{
                weekDaysDataSource.selectAll(false);
                weekDaysDataSource.setSelectedDays(prevPos);
            }
        }else {
            weekDaysDataSource.setSelectedDays(item.getPosition());
        }
    }

    @Override
    public void onWeekdaysSelected(int attachId, ArrayList<WeekdaysDataItem> items) {
        String selectedDays = getSelectedDaysFromWeekdaysData(items);
    }

    private String getSelectedDaysFromWeekdaysData(ArrayList<WeekdaysDataItem> items) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean selected = false;
        for (WeekdaysDataItem dataItem : items
                ) {
            if (dataItem.isSelected()) {
                selected = true;
                stringBuilder.append(dataItem.getLabel());
                stringBuilder.append(", ");
            }
        }
        if (selected) {
            String result = stringBuilder.toString();
            return result.substring(0, result.lastIndexOf(","));
        } else return "No days selected";
    }





    private void setupWeekdaysButtons() {
        weekDaysDataSource = new WeekdaysDataSource(this, R.id.weekdays_stub)
                .setDrawableType(WeekdaysDrawableProvider.MW_ROUND_RECT)
                .setTextColorSelected(Color.WHITE)
                .setFirstDayOfWeek(Calendar.MONDAY)
                .setSelectedDays(calendar.get(Calendar.DAY_OF_WEEK))
                .setTextColorUnselectedRes(R.color.whiteTransparent)
                .setFontBaseSize(16)
                .setFontTypeFace(Typeface.DEFAULT_BOLD)
                .setUnselectedColorRes(R.color.colorPrimary)
                .setSelectedColorRes(R.color.colorAccent)
                .setNumberOfLetters(2)
                .start(this);
    }

}
