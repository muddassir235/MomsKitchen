package com.momskitchen.momskitchen;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.momskitchen.momskitchen.model.User;

public class UserDataEntryActivity extends AppCompatActivity {

    public static final int RESULT_OK = 0;

    Button mEnterDataButton;
    AutoCompleteTextView mPhoneTV;
    EditText mAddressTV;
    EditText mLandmarkTV;
    ProgressBar mDataEntryProgressBar;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data_entry);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        bindViews();
        mEnterDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean allDataEntered = true;
                View focusView = null;

                if(TextUtils.isEmpty(mLandmarkTV.getText())){
                    focusView = mLandmarkTV;
                    mLandmarkTV.setError("Please enter a landmark this is useful for delivery");
                    allDataEntered = false;
                }

                if(TextUtils.isEmpty(mAddressTV.getText())){
                    focusView = mAddressTV;
                    mAddressTV.setError("Please enter your address");
                    allDataEntered = false;
                }

                if(TextUtils.isEmpty(mPhoneTV.getText())){
                    focusView = mPhoneTV;
                    mPhoneTV.setError("Please enter your phone number");
                    allDataEntered = false;
                }

                if(allDataEntered){
                    mDataEntryProgressBar.setVisibility(View.VISIBLE);
                    mEnterDataButton.setText("");
                    User  user = new User(
                            firebaseUser.getUid(),
                            firebaseUser.getDisplayName(),
                            firebaseUser.getEmail(),
                            mPhoneTV.getText().toString(),
                            mAddressTV.getText().toString(),
                            mLandmarkTV.getText().toString(),
                            null,
                            null
                    );
                    FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).setValue(user);
                    FirebaseDatabase.getInstance().getReference().child("UserThatEnteredData").child(firebaseUser.getUid()).setValue(true);
                    StartupActivity.userDataHasBeenEnteredForCustomerId(getApplicationContext(),firebaseUser.getUid());
                    setResult(RESULT_OK);
                    finish();
                }else {
                    focusView.requestFocus();
                }
            }
        });

    }

    void bindViews(){
        mEnterDataButton = (Button) findViewById(R.id.enter_data_button);
        mPhoneTV = (AutoCompleteTextView) findViewById(R.id.phone);
        mAddressTV = (EditText) findViewById(R.id.address);
        mLandmarkTV = (EditText) findViewById(R.id.landmark);
        mDataEntryProgressBar = (ProgressBar) findViewById(R.id.data_entry_progress_bar);
    }

}
