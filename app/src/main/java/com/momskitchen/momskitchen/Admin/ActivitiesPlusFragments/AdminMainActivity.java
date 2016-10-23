package com.momskitchen.momskitchen.Admin.ActivitiesPlusFragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.momskitchen.momskitchen.Constants;
import com.momskitchen.momskitchen.R;
import com.momskitchen.momskitchen.StartupActivity;
import com.momskitchen.momskitchen.backend.MenuCreator;
import com.momskitchen.momskitchen.model.MealItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AdminMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdminOrdersFragment.OnFragmentInteractionListener,
        MealsFragment.OnFragmentInteractionListener,
        MenuFragment.OnFragmentInteractionListener,
        MealListFragment.OnListFragmentInteractionListener,
        AdminMenuListFragment.OnListFragmentInteractionListener,
        AdminOrderFragment.OnFragmentInteractionListener,
        AdminPackagedMealsFragment.OnFragmentInteractionListener,
        AdminDeliveredMealsFragment.OnFragmentInteractionListener
{

    private static final String TAG = "AdminMainActivity: ";

    FrameLayout mainFrame;
    Fragment currentFragment;

    public static Calendar currentCalendar;

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

        findViewById(R.id.current_date_text_view).setVisibility(View.GONE);
        findViewById(R.id.calendar_icon_image_button).setVisibility(View.GONE);

        ((ImageButton) findViewById(R.id.calendar_icon_image_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        currentCalendar = Calendar.getInstance();
        if(currentCalendar.get(Calendar.HOUR_OF_DAY)>=14){
            currentCalendar.add(Calendar.DATE,1);
        }
        AdminMenuListFragment.currentDate = MenuCreator.getInstance().getDateFromCalendar(currentCalendar);
        ((TextView) findViewById(R.id.current_date_text_view)).setText(DatePickerFragment.getReadableDateFromCalendar(currentCalendar));
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
            ((TextView) findViewById(R.id.page_title_text_view)).setText(item.getTitle());
            findViewById(R.id.current_date_text_view).setVisibility(View.GONE);
            findViewById(R.id.calendar_icon_image_button).setVisibility(View.GONE);
        } else if (id == R.id.nav_meals) {
            Fragment newFragment = new MealsFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.remove(currentFragment);
            ft.add(mainFrame.getId(), newFragment).commit();
            currentFragment = newFragment;
            ((TextView) findViewById(R.id.page_title_text_view)).setText(item.getTitle());
            findViewById(R.id.current_date_text_view).setVisibility(View.GONE);
            findViewById(R.id.calendar_icon_image_button).setVisibility(View.GONE);
        } else if(id == R.id.nav_menu){
            Fragment newFragment = new MenuFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.remove(currentFragment);
            ft.add(mainFrame.getId(), newFragment).commit();
            currentFragment = newFragment;
            ((TextView) findViewById(R.id.page_title_text_view)).setText(item.getTitle());
            findViewById(R.id.current_date_text_view).setVisibility(View.VISIBLE);
            findViewById(R.id.calendar_icon_image_button).setVisibility(View.VISIBLE);
        }else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.sign_out) {
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
                            Log.v(TAG,"1. signing admin out");
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
    public void onListFragmentInteraction(MealItem item) {

    }

    @Override
    public void onListFragmentInteraction() {

    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            int year = currentCalendar.get(Calendar.YEAR);
            int month = currentCalendar.get(Calendar.MONTH);
            int day = currentCalendar.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            currentCalendar= Calendar.getInstance();
            currentCalendar.set(year,month,day);
            AdminMenuListFragment.currentDate = MenuCreator.getInstance().getDateFromCalendar(currentCalendar);
            AdminMenuListFragment.anyLunch = false;
            AdminMenuListFragment.anyDessert = false;
            AdminMenuListFragment.anyCompliment = false;
            AdminMenuListFragment.lunchListAdapter.notifyDataSetChanged();
            AdminMenuListFragment.dessertListAdapter.notifyDataSetChanged();
            AdminMenuListFragment.complimentListAdapter.notifyDataSetChanged();

            String dateString = getReadableDateFromCalendar(currentCalendar);
            ((TextView) getActivity().findViewById(R.id.current_date_text_view)).setText(dateString);
        }

        public static String getReadableDateFromCalendar(Calendar c){
            String monthString;
            String dayString;
            int month = c.get(Calendar.MONTH);
            switch (month){
                case Calendar.JANUARY: monthString = "Jan";break;
                case Calendar.FEBRUARY: monthString = "Feb";break;
                case Calendar.MARCH: monthString = "Mar";break;
                case Calendar.APRIL: monthString = "Apr";break;
                case Calendar.MAY: monthString = "May";break;
                case Calendar.JUNE: monthString = "Jun";break;
                case Calendar.JULY: monthString = "Jul";break;
                case Calendar.AUGUST: monthString = "Aug";break;
                case Calendar.SEPTEMBER: monthString = "Sep";break;
                case Calendar.OCTOBER: monthString = "Oct";break;
                case Calendar.NOVEMBER: monthString = "Nov";break;
                default: monthString = "Dec";break;
            }
            int day = c.get(Calendar.DAY_OF_MONTH);
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
            Calendar calendarForWeek = Calendar.getInstance();
            int today = calendarForWeek.get(Calendar.DAY_OF_WEEK);
            Log.v("Compare: ",""+ Calendar.SUNDAY+" and "+today+" and "+dayOfWeek );
            calendarForWeek.add(Calendar.DAY_OF_WEEK, 1);
            int tommorow = calendarForWeek.get(Calendar.DAY_OF_WEEK);

            if(dayOfWeek == today){
                //Log.v("Date", " Today "+dayOfWeek+" "+today);
                dayString = "Today";
            }else if(dayOfWeek == tommorow ){
                dayString = "Tommorrow";
            }else if(dayOfWeek == Calendar.MONDAY){
                dayString = "Mon";
            }else if (dayOfWeek == Calendar.TUESDAY){
                dayString = "Tue";
            }else if (dayOfWeek == Calendar.WEDNESDAY){
                dayString = "Wed";
            }else if (dayOfWeek == Calendar.THURSDAY){
                dayString = "Thu";
            }else if (dayOfWeek == Calendar.FRIDAY){
                dayString = "Fri";
            }else if (dayOfWeek == Calendar.SATURDAY){
                dayString = "Sat";
            }else{
                //Log.v("Date", " Sunday"+" "+dayOfWeek+" "+Calendar.SUNDAY);
                dayString = "Sun";
            }
            int year = c.get(Calendar.YEAR);

            return dayString+", "+monthString+" "+day+", "+year;
        }


    }
}
