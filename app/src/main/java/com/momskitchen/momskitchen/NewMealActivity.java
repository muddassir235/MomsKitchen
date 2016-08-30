package com.momskitchen.momskitchen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.momskitchen.momskitchen.backend.FirebaseOperations;
import com.momskitchen.momskitchen.model.MealItem;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;

public class NewMealActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;

    private static final String TAG = "NewMealActivity: ";
    private static NewMealActivity activity;

    ImageButton mCancelMealIB;
    Button mAddMealButton;
    EditText mMealNameET;
    EditText mMealDescriptionET;
    EditText mMealPriceET;
    Spinner mMealCatagorySpinner;
    ImageView mMealThumbnailIV;
    TextView mMealThumbnailNameTV;
    Button mAddThumbnailButton;
    ImageView mMealPosterIV;
    TextView mMealPosterNameTV;
    Button mAddPosterButton;

    Uri thumbnailUri;
    Uri posterUri;

    boolean thumbnailUploadSuccessful;
    boolean posterUploadSuccessful;

    boolean thumbnailSelected;
    boolean posterSelected;

    MealItem mealItem;

    DatabaseReference mealsRef;
    DatabaseReference pushRef;
    String pushKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_meal);
        bindViews();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        activity = this;
        setSupportActionBar(toolbar);

        mealsRef = FirebaseDatabase.getInstance().getReference().child("Meals");

        HashMap<String,Object> timeStampCreated = new HashMap<>();
        timeStampCreated.put("date",ServerValue.TIMESTAMP);
        mealItem = new MealItem(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                timeStampCreated
        );

        thumbnailUploadSuccessful = false;
        posterUploadSuccessful = false;

        thumbnailSelected = false;
        posterSelected = false;

        mAddThumbnailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseOperations.getInstance().uploadImage(activity,Constants.LOCAL_OPERATION_SELECT_THUMBNAIL).setThumnailUploadedListener(new FirebaseOperations.ThumnailUploadedListener() {
                    @Override
                    public void onFailure(Exception exception) {
                        Toast.makeText(getApplicationContext(),"Thumbnail Upload has failed :( Please try again",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(Uri uri) {
                        //TODO: handle success
                        mealItem.thumbnailURL = uri.toString();
                        Log.v(TAG,mealItem.thumbnailURL);
                        thumbnailUploadSuccessful = true;
                        if(posterUploadSuccessful){
                            pushRef.setValue(mealItem);
                            dismissDialog();
                            finish();
                        }


                    }
                });
            }
        });

        mAddMealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View focusView = null;
                boolean allDataEntered = true;

                if(TextUtils.isEmpty(mMealPriceET.getText())){
                    allDataEntered = false;
                    focusView = mMealPriceET;
                    mMealPriceET.setHint(Html.fromHtml("<font color='red'>Please enter the price.</font>"));
                    mMealPriceET.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View view, int i, KeyEvent keyEvent) {
                            mMealPriceET.setHint("Enter Price (in PKR)");
                            return false;
                        }
                    });
                }

                if(TextUtils.isEmpty(mMealDescriptionET.getText())){
                    allDataEntered = false;
                    focusView = mMealDescriptionET;
                    mMealDescriptionET.setHint(Html.fromHtml("<font color='red'>Please enter the description.</font>"));
                    mMealDescriptionET.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View view, int i, KeyEvent keyEvent) {
                            mMealDescriptionET.setHint("Enter Description");
                            return false;
                        }
                    });
                }

                if(TextUtils.isEmpty(mMealNameET.getText())){
                    allDataEntered = false;
                    focusView = mMealNameET;
                    mMealNameET.setHint(Html.fromHtml("<font color='red'>Please enter the meal's name.</font>"));
                    mMealNameET.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View view, int i, KeyEvent keyEvent) {
                            mMealNameET.setHint("Enter the meal's name");
                            return false;
                        }
                    });
                }

                if(allDataEntered) {
                    if (!thumbnailSelected) {
                        allDataEntered = false;
                        Toast.makeText(getApplicationContext(), "Please select a thumbnail image.", Toast.LENGTH_SHORT).show();
                    } else if (!posterSelected) {
                        allDataEntered = false;
                        Toast.makeText(getApplicationContext(), "Please select a poster image.", Toast.LENGTH_SHORT).show();
                    }

                }

                if(allDataEntered){
                    Log.v(TAG,"All data entered");
                    showLoadingDialog("Adding new meal...");
                    mealItem.name = mMealNameET.getText().toString();
                    mealItem.description = mMealDescriptionET.getText().toString();
                    mealItem.pricePerUnit = Long.valueOf(mMealPriceET.getText().toString());
                    mealItem.catagory = mMealCatagorySpinner.getSelectedItem().toString().toLowerCase();

                    pushRef = mealsRef.child(mealItem.catagory).push();
                    pushKey = pushRef.getKey();
                    mealItem.id = pushKey;
                    FirebaseOperations.getInstance().uploadThumbnailTask(pushKey,mealItem.catagory,thumbnailUri);
                    FirebaseOperations.getInstance().uploadPosterTask(pushKey,mealItem.catagory,posterUri);
                }else {
                    if(focusView!=null){
                        focusView.requestFocus();
                    }
                }


            }
        });

        mCancelMealIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mAddPosterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseOperations.getInstance().uploadImage(activity,Constants.LOCAL_OPERATION_SELECT_POSTER).setPosterUploadedListeners(new FirebaseOperations.PosterUploadedListener() {
                    @Override
                    public void onFailure(Exception exception) {
                        Toast.makeText(getApplicationContext(),"Poster Upload has failed :( Please try again",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(Uri uri) {
                        //TODO: handle success
                        mealItem.posterURL = uri.toString();
                        Log.v(TAG, mealItem.posterURL);
                        posterUploadSuccessful = true;
                        if(thumbnailUploadSuccessful){
                            pushRef.setValue(mealItem);
                            dismissDialog();
                            finish();
                        }
                    }
                });

            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.catagories_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mMealCatagorySpinner.setAdapter(adapter);

    }

    void bindViews(){
        mCancelMealIB = (ImageButton) findViewById(R.id.cancel_add_new_meal_image_button);
        mAddMealButton = (Button) findViewById(R.id.upload_new_meal_data_button);
        mMealNameET = (EditText) findViewById(R.id.meal_name_edit_text);
        mMealDescriptionET = (EditText) findViewById(R.id.meal_description_edit_text);
        mMealPriceET = (EditText) findViewById(R.id.price_meal_entry_edit_text);
        mMealCatagorySpinner = (Spinner) findViewById(R.id.catagory_selection_spinner);
        mMealThumbnailIV = (ImageView) findViewById(R.id.thumbnail_image_view);
        mMealThumbnailNameTV = (TextView) findViewById(R.id.thumbnail_image_name_text_view);
        mAddThumbnailButton = (Button) findViewById(R.id.add_thumbnail_button);
        mMealPosterIV = (ImageView) findViewById(R.id.poster_image_view);
        mMealPosterNameTV = (TextView) findViewById(R.id.poster_image_name_text_view);
        mAddPosterButton = (Button) findViewById(R.id.add_poster_button);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if(requestCode == Constants.LOCAL_OPERATION_SELECT_THUMBNAIL) {
                mAddThumbnailButton.setText("Change Thumbnail");
                thumbnailSelected = true;
                thumbnailUri = data.getData();
                if (thumbnailUri.getScheme().equals("file")) {
                    mMealThumbnailNameTV.setText(thumbnailUri.getLastPathSegment());
                } else {
                    Cursor cursor = null;
                    try {
                        cursor = getContentResolver().query(thumbnailUri, new String[]{
                                MediaStore.Images.ImageColumns.DISPLAY_NAME
                        }, null, null, null);

                        if (cursor != null && cursor.moveToFirst()) {
                            mMealThumbnailNameTV.setText(cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME)));

                        }
                    } finally {

                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                }
                mMealThumbnailIV.setPadding(0,0,0,0);
                Picasso.with(this).
                        load(thumbnailUri).
                        resize(getPXFromDP(100),getPXFromDP(100)).
                        centerCrop().
                        into(mMealThumbnailIV);
            }else if(requestCode == Constants.LOCAL_OPERATION_SELECT_POSTER) {
                mAddPosterButton.setText("CHANGE POSTER");
                posterSelected = true;
                posterUri = data.getData();
                if (posterUri.getScheme().equals("file")) {
                    mMealPosterNameTV.setText(posterUri.getLastPathSegment());
                } else {
                    Cursor cursor = null;
                    try {
                        cursor = getContentResolver().query(posterUri, new String[]{
                                MediaStore.Images.ImageColumns.DISPLAY_NAME
                        }, null, null, null);

                        if (cursor != null && cursor.moveToFirst()) {
                            mMealPosterNameTV.setText(cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME)));

                        }
                    } finally {

                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                }
                mMealPosterIV.setPadding(0,0,0,0);
                Picasso.with(this)
                        .load(posterUri)
                        .resize(getPXFromDP(178),getPXFromDP(100))
                        .centerCrop()
                        .into(mMealPosterIV);
            }
        }
    }

    public void showLoadingDialog(String message) {
        dismissDialog();
        mProgressDialog = ProgressDialog.show(this, "", message, true);
    }

    public void dismissDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    int getPXFromDP(int dp){
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return (int) px;
    }
}
