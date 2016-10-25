package com.momskitchen.momskitchen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.momskitchen.momskitchen.Admin.ActivitiesPlusFragments.AdminMainActivity;
import com.momskitchen.momskitchen.Admin.Adapters.PendingOrdersAdapter;
import com.momskitchen.momskitchen.Customer.ActivitiesPlusFragments.CustomerMainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StartupActivity extends AppCompatActivity {
    private static final String TAG = "StartupActivity: ";

    private static final int RC_SIGN_IN = 235;
    private static final int DATA_ENTRY = 236;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null){
            Log.v(TAG,"1. user is already signed in");
            final FirebaseUser user = auth.getCurrentUser();
            int userType = getUserType(getApplicationContext());
            if(userType == Constants.DONT_KNOW_USER_TYPE) {
                Log.v(TAG,"2. don't know user type, fetching user type from firebase");
                FirebaseDatabase.getInstance().getReference().child("Admins").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Log.v(TAG,"3. user type determined from firebase, user has admin privelages.");
                            Intent intent = new Intent(getApplicationContext(), AdminMainActivity.class);
                            startActivity(intent);
                            saveAsAdminOrCustomer(getApplicationContext(),Constants.USER_IS_ADMIN);
                            finish();
                        } else {
                            Log.v(TAG, "4. user type determined from firebase, user is a customer.");
                            if(isUserDataEnteredByCustomer(getApplicationContext(),user.getUid())) {
                                Log.v(TAG,"17. user has entered data, this was determined from sharedpreferences");
                                Intent intent = new Intent(getApplicationContext(), CustomerMainActivity.class);
                                startActivity(intent);
                                saveAsAdminOrCustomer(getApplicationContext(), Constants.USER_IS_CUSTOMER);
                                finish();
                            }else{
                                FirebaseDatabase.getInstance().getReference().child("UserThatEnteredData").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            Log.v(TAG,"18. user has entered data, this was determined from firebase");
                                            Intent intent = new Intent(getApplicationContext(), CustomerMainActivity.class);
                                            startActivity(intent);
                                            saveAsAdminOrCustomer(getApplicationContext(), Constants.USER_IS_CUSTOMER);
                                            userDataHasBeenEnteredForCustomerId(getApplicationContext(),user.getUid());
                                            finish();
                                        }else {
                                            Intent intent = new Intent(getApplicationContext(),UserDataEntryActivity.class);
                                            startActivityForResult(intent,DATA_ENTRY);
                                            Log.v(TAG,"19. user has not entered data yet, this was determined from firebase");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Toast.makeText(getApplicationContext(),"This data check has failed. Please restart the app. :(",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "Sorry :(! The check has failed please restart the app", Toast.LENGTH_SHORT).show();
                    }
                });
            }else if(userType == Constants.USER_IS_ADMIN){
                Log.v(TAG,"5. user type determined from local sharedpreferences, user has admin privelages.");
                Intent intent = new Intent(getApplicationContext(), AdminMainActivity.class);
                startActivity(intent);
                finish();
            }else if (userType == Constants.USER_IS_CUSTOMER){
                Log.v(TAG, "4. user type determined from sharedpreferences, user is a customer.");
                if(isUserDataEnteredByCustomer(getApplicationContext(),user.getUid())) {
                    Log.v(TAG,"20. user has entered data, this was determined from sharedpreferences");
                    Intent intent = new Intent(getApplicationContext(), CustomerMainActivity.class);
                    startActivity(intent);
                    //saveAsAdminOrCustomer(getApplicationContext(), Constants.USER_IS_CUSTOMER);
                    finish();
                }else{
                    FirebaseDatabase.getInstance().getReference().child("UserThatEnteredData").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                Log.v(TAG,"21. user has entered data, this was determined from firebase");
                                Intent intent = new Intent(getApplicationContext(), CustomerMainActivity.class);
                                startActivity(intent);
                                saveAsAdminOrCustomer(getApplicationContext(), Constants.USER_IS_CUSTOMER);
                                userDataHasBeenEnteredForCustomerId(getApplicationContext(),user.getUid());
                                finish();
                            }else {
                                Intent intent = new Intent(getApplicationContext(),UserDataEntryActivity.class);
                                startActivityForResult(intent,DATA_ENTRY);
                                Log.v(TAG,"22. user has not entered data yet, this was determined from firebase");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(),"This data check has failed. Please restart the app. :(",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

        }else{
            Log.v(TAG,"7. user not signed in, signing user in using firebaseUI-auth");
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setProviders(
                                    AuthUI.EMAIL_PROVIDER,
                                    AuthUI.GOOGLE_PROVIDER,
                                    AuthUI.FACEBOOK_PROVIDER)
                            .setTheme(R.style.ColoredFirebaseUI)
                            .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                            .build(),
                    RC_SIGN_IN);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN || requestCode == DATA_ENTRY) {
            if (resultCode == RESULT_OK) {
                // user is signed in!
                Log.v(TAG, "8. user login successful");

                int userType = getUserType(getApplicationContext());
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(requestCode == RC_SIGN_IN){
                    final String token = FirebaseInstanceId.getInstance().getToken();
                    if(token == null){
                        try {
                            FirebaseInstanceId.getInstance().deleteInstanceId();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Log.v(TAG, " Token: "+token);
                        FirebaseDatabase.getInstance().getReference().
                                child("MapUIDtoInstanceID").
                                child(user.getUid()).
                                child(token).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    FirebaseDatabase.getInstance().getReference().
                                            child("MapUIDtoInstanceID").
                                            child(user.getUid()).child(token).
                                            setValue(true);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
                if(userType == Constants.DONT_KNOW_USER_TYPE) {
                    Log.v(TAG,"9. don't know user type, fetching user type from firebase");
                    FirebaseDatabase.getInstance().getReference().child("Admins").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Log.v(TAG,"10. user type determined from firebase, user has admin privelages.");
                                Intent intent = new Intent(getApplicationContext(), AdminMainActivity.class);
                                startActivity(intent);
                                saveAsAdminOrCustomer(getApplicationContext(),Constants.USER_IS_ADMIN);
                                finish();
                            } else {
                                Log.v(TAG, "23 user type determined from firebase, user is a customer.");
                                if(isUserDataEnteredByCustomer(getApplicationContext(),user.getUid())) {
                                    Log.v(TAG,"24. user has entered data, this was determined from sharedpreferences");
                                    Intent intent = new Intent(getApplicationContext(), CustomerMainActivity.class);
                                    startActivity(intent);
                                    saveAsAdminOrCustomer(getApplicationContext(), Constants.USER_IS_CUSTOMER);
                                    finish();
                                }else{
                                    FirebaseDatabase.getInstance().getReference().child("UserThatEnteredData").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()){
                                                Log.v(TAG,"25. user has entered data, this was determined from firebase");
                                                Intent intent = new Intent(getApplicationContext(), CustomerMainActivity.class);
                                                startActivity(intent);
                                                saveAsAdminOrCustomer(getApplicationContext(), Constants.USER_IS_CUSTOMER);
                                                userDataHasBeenEnteredForCustomerId(getApplicationContext(),user.getUid());
                                                finish();
                                            }else {
                                                Intent intent = new Intent(getApplicationContext(),UserDataEntryActivity.class);
                                                startActivityForResult(intent,DATA_ENTRY);
                                                Log.v(TAG,"26. user has not entered data yet, this was determined from firebase");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Toast.makeText(getApplicationContext(),"This data check has failed. Please restart the app. :(",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), "Sorry :(! The check has failed please restart the app", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    if(userType == Constants.USER_IS_ADMIN){
                        Log.v(TAG,"12. user type determined from local sharedpreferences, user has admin privelages.");
                        Intent intent = new Intent(getApplicationContext(), AdminMainActivity.class);
                        startActivity(intent);
                        finish();
                    }else if (userType == Constants.USER_IS_CUSTOMER){
                        Log.v(TAG, "27. user type determined from sharedpreferences, user is a customer.");
                        if(isUserDataEnteredByCustomer(getApplicationContext(),user.getUid())) {
                            Log.v(TAG,"28. user has entered data, this was determined from sharedpreferences");
                            Intent intent = new Intent(getApplicationContext(), CustomerMainActivity.class);
                            startActivity(intent);
                            //saveAsAdminOrCustomer(getApplicationContext(), Constants.USER_IS_CUSTOMER);
                            finish();
                        }else{
                            FirebaseDatabase.getInstance().getReference().child("UserThatEnteredData").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        Log.v(TAG,"29. user has entered data, this was determined from firebase");
                                        Intent intent = new Intent(getApplicationContext(), CustomerMainActivity.class);
                                        startActivity(intent);
                                        saveAsAdminOrCustomer(getApplicationContext(), Constants.USER_IS_CUSTOMER);
                                        userDataHasBeenEnteredForCustomerId(getApplicationContext(),user.getUid());
                                        finish();
                                    }else {
                                        Intent intent = new Intent(getApplicationContext(),UserDataEntryActivity.class);
                                        startActivityForResult(intent,DATA_ENTRY);
                                        Log.v(TAG,"30. user has not entered data yet, this was determined from firebase");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(getApplicationContext(),"This data check has failed. Please restart the app. :(",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            } else {
                Log.v(TAG, "14. user sign in failed or cancelled trying again");
                // user is not signed in. Maybe just wait for the user to press
                // "sign in" again, or show a message
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setProviders(
                                        AuthUI.EMAIL_PROVIDER,
                                        AuthUI.GOOGLE_PROVIDER,
                                        AuthUI.FACEBOOK_PROVIDER)
                                .setTheme(R.style.ColoredFirebaseUI)
                                .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                                .build(),
                        RC_SIGN_IN);
            }
        }
    }

    public static void saveAsAdminOrCustomer(Context context,int userType){
        Log.v(TAG, "15. saving user type to shared preferences: "+userType);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putInt("IsAdmin",userType);
        sharedPreferencesEditor.commit();
    }

    public static boolean isUserDataEnteredByCustomer(Context context,String customerId){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(customerId,false);
    }

    public static void userDataHasBeenEnteredForCustomerId(Context context,String customerId){
        Log.v(TAG,"16. customerId: "+customerId+" data has been entered for this customer id.");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putBoolean(customerId,true);
        sharedPreferencesEditor.commit();
    }

    public static int getUserType(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt("IsAdmin",Constants.DONT_KNOW_USER_TYPE);
    }
}
