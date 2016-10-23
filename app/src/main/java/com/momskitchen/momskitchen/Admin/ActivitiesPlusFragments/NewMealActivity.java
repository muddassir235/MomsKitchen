package com.momskitchen.momskitchen.Admin.ActivitiesPlusFragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
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
import com.google.firebase.storage.FirebaseStorage;
import com.momskitchen.momskitchen.Constants;
import com.momskitchen.momskitchen.R;
import com.momskitchen.momskitchen.backend.FirebaseOperations;
import com.momskitchen.momskitchen.model.MealItem;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    TextView mTimesAvailableTV;

    Dialog mDialog;

    List<String> timesList;

    Uri thumbnailUri;
    Uri posterUri;


    Target thumbnailTarget;
    Target posterTarget;

    boolean editMode;

    boolean thumbnailUploadSuccessful;
    boolean posterUploadSuccessful;

    boolean thumbnailSelected;
    boolean posterSelected;

    MealItem mealItem;
    String catagory;

    String oldId;

    DatabaseReference mealsRef;
    DatabaseReference pushRef;
    String pushKey;
    private boolean changedThumbnailInEditMode;
    private boolean changedPosterInEditMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_meal);
        editMode = false;
        bindViews();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        activity = this;
        setSupportActionBar(toolbar);

        setupTimesSelectionAlertDialog();

        mTimesAvailableTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.show();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.catagories_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mMealCatagorySpinner.setAdapter(adapter);

        timesList = new ArrayList<>();

        changedThumbnailInEditMode = false;
        changedPosterInEditMode = false;

        thumbnailUploadSuccessful = false;
        posterUploadSuccessful = false;

        thumbnailSelected = false;
        posterSelected = false;

        if(getIntent().getExtras()!=null) {
            mealItem = (MealItem) getIntent().getExtras().getSerializable("Meal");
        }
        if(mealItem!=null){
            thumbnailSelected = true;
            posterSelected = true;
            editMode = true;
            mAddMealButton.setText("MAKE EDITS");
            mMealNameET.setText(mealItem.name);
            mMealDescriptionET.setText(mealItem.description);
            mMealPriceET.setText(""+mealItem.pricePerUnit);
            if(mealItem.catagory.equals("lunch")){
                mMealCatagorySpinner.setSelection(0);
            }else if(mealItem.catagory.equals("dessert")){
                mMealCatagorySpinner.setSelection(1);
            }else if(mealItem.catagory.equals("compliment")){
                mMealCatagorySpinner.setSelection(2);
            }

            if(mealItem.times!=null) {
                timesList = mealItem.times;
                mTimesAvailableTV.setText(convertListOfStringsIntoSingleOne(mealItem.times));
            }

            thumbnailTarget = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    mMealThumbnailIV.setPadding(0,0,0,0);
                    mMealThumbnailIV.setImageBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            posterTarget = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    mMealPosterIV.setPadding(0,0,0,0);
                    mMealPosterIV.setImageBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            catagory = mealItem.catagory;
            oldId = mealItem.id;
            Picasso.with(getApplicationContext()).load(mealItem.thumbnailURL).into(thumbnailTarget);
            Picasso.with(getApplicationContext()).load(mealItem.posterURL).into(posterTarget);
            mMealThumbnailNameTV.setText(mealItem.id+"/"+"thumbnail.jpg");
            mMealPosterNameTV.setText(mealItem.id+"/"+"poster.jpg");
            mAddThumbnailButton.setText("Change Thumbnail");
            mAddPosterButton.setText("Change Poster");
        }

        mealsRef = FirebaseDatabase.getInstance().getReference().child("Meals");

        HashMap<String,Object> timeStampCreated = new HashMap<>();
        timeStampCreated.put("date",ServerValue.TIMESTAMP);
        if(!editMode) {
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
                    null,
                    timeStampCreated,
                    null
            );
        }

        mAddThumbnailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseOperations.getInstance().uploadImage(activity, Constants.LOCAL_OPERATION_SELECT_THUMBNAIL).setThumnailUploadedListener(new FirebaseOperations.ThumnailUploadedListener() {
                    @Override
                    public void onFailure(Exception exception) {
                        Toast.makeText(getApplicationContext(),"Thumbnail Upload has failed :( Please try again",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(Uri uri) {
                        Log.v(TAG, "11. The thumbnail has been uploaded successfully"+this);
                        //TODO: handle success
                        mealItem.thumbnailURL = uri.toString();
                        Log.v(TAG,mealItem.thumbnailURL);
                        if(!editMode) {
                            thumbnailUploadSuccessful = true;
                            if (posterUploadSuccessful) {
                                Log.v(TAG, "12. not in edit mode and both the thumbnail and poster have been uploaded successfully"+this);
                                pushRef.setValue(mealItem);
                                dismissDialog();
                                finish();
                            }
                        }else{
                            if(changedThumbnailInEditMode && !changedPosterInEditMode){
                                Log.v(TAG,"12. In edit mode and only the thumbnail has been changed"+this);
                                thumbnailUploadSuccessful = true;
                                pushRef.setValue(mealItem);
                                dismissDialog();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("Meal",mealItem);
                                Intent intent = new Intent(MealListFragment.context.getApplicationContext(),MealDetailsActivityAdmin.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }else if(changedThumbnailInEditMode && changedPosterInEditMode){
                                Log.v(TAG, "13. In edit mode and both the thumbnail and poster have been changed"+this);
                                thumbnailUploadSuccessful = true;
                                if (posterUploadSuccessful) {
                                    Log.v(TAG,"14. In edit mode and the thumbnail and poster have been changed successfully"+this);
                                    pushRef.setValue(mealItem);
                                    dismissDialog();
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("Meal",mealItem);
                                    Intent intent = new Intent(MealListFragment.context.getApplicationContext(),MealDetailsActivityAdmin.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                            }
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

                if(mTimesAvailableTV.getText().toString().equals("No Times Selected")){
                    allDataEntered = false;
                    focusView = mTimesAvailableTV;
                    Toast.makeText(getApplicationContext(),"Please select the times this meal is available!",Toast.LENGTH_SHORT).show();
                }

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
                    Log.v(TAG,"1. All data entered");
                    if(!editMode){
                        Log.v(TAG, "2. In new Meal mode");
                        showLoadingDialog("Adding new meal...");
                    }else {
                        Log.v(TAG, "2. In edit mode.");
                        showLoadingDialog("Making edits...");
                    }
                    mealItem.name = mMealNameET.getText().toString();
                    mealItem.description = mMealDescriptionET.getText().toString();
                    mealItem.pricePerUnit = Long.valueOf(mMealPriceET.getText().toString());
                    mealItem.catagory = mMealCatagorySpinner.getSelectedItem().toString().toLowerCase();
                    mealItem.times = timesList;
                    if(!editMode) {
                        Log.v(TAG, "3. Not in edit mode creating new push ref"+this);
                        pushRef = mealsRef.child(mealItem.catagory).push();
                        pushKey = pushRef.getKey();
                        mealItem.id = pushKey;
                    }else{
                        if(mealItem.catagory.equals(catagory)) {
                            Log.v(TAG,"3. In edit mode and catagory is the same so keeping the old push ref"+this);
                            pushRef = mealsRef.child(mealItem.catagory).child(mealItem.id);
                            pushKey = mealItem.id;
                        }else{
                            Log.v(TAG,"3. In edit mode and catagory was changed so making a new push ref and deleting the old one"+this);
                            mealsRef.child(catagory).child(mealItem.id).setValue(null);
                            pushRef = mealsRef.child(mealItem.catagory).push();
                            pushKey = pushRef.getKey();
                            mealItem.id = pushKey;
                        }

                        if(changedThumbnailInEditMode){
                            Log.v(TAG,"4. In edit mode and the thumbnail needs to be changed so deleting the old thumbnail"+this);
                            FirebaseStorage.getInstance().getReferenceFromUrl(mealItem.thumbnailURL).delete();
                        }

                        if(changedPosterInEditMode){
                            Log.v(TAG,"5. In edit mode and and the poster has been changed so deleting the old poster"+this);
                            FirebaseStorage.getInstance().getReferenceFromUrl(mealItem.posterURL).delete();
                        }
                    }

                    if(mealItem.thumbnailURL == null || changedThumbnailInEditMode) {
                        Log.v(TAG, "6. uploading new thumbnail"+this);
                        FirebaseOperations.getInstance().uploadThumbnailTask(pushKey, thumbnailUri);
                    }

                    if(mealItem.posterURL==null || changedPosterInEditMode) {
                        Log.v(TAG, "7. uploading new poster"+this);
                        FirebaseOperations.getInstance().uploadPosterTask(pushKey, posterUri);
                    }

                    if(!changedThumbnailInEditMode && !changedPosterInEditMode){
                        if(editMode) {
                            Log.v(TAG,"8. In edit mode and neither the thumbnail nor poster has been changed so just changing the other fields and pushing."+this);
                            pushRef.setValue(mealItem);
                            dismissDialog();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("Meal",mealItem);
                            Intent intent = new Intent(MealListFragment.context.getApplicationContext(),MealDetailsActivityAdmin.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }
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
                        Log.v(TAG, "15. The poster has been uploaded successfully"+this);
                        //TODO: handle success
                        mealItem.posterURL = uri.toString();
                        Log.v(TAG, mealItem.posterURL);
                        if(!editMode) {
                            posterUploadSuccessful = true;
                            if (thumbnailUploadSuccessful) {
                                Log.v(TAG, "16. not in edit mode and both the thumbnail and poster have been uploaded successfully"+this);
                                pushRef.setValue(mealItem);
                                dismissDialog();
                                finish();
                            }
                        }else {
                            if(!changedThumbnailInEditMode && changedPosterInEditMode){
                                Log.v(TAG, "16. In edit mode and only the poster has changed."+this);
                                posterUploadSuccessful = true;
                                pushRef.setValue(mealItem);
                                dismissDialog();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("Meal",mealItem);
                                Intent intent = new Intent(MealListFragment.context.getApplicationContext(),MealDetailsActivityAdmin.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }else if(changedThumbnailInEditMode && changedPosterInEditMode){
                                Log.v(TAG, "17. In edit mode and both the poster and thumbnail have changed"+this);
                                posterUploadSuccessful = true;
                                if(thumbnailUploadSuccessful) {
                                    Log.v(TAG,"Thumbnail uploaded successfully! about to close activity"+this);
                                    pushRef.setValue(mealItem);
                                    dismissDialog();
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("Meal",mealItem);
                                    Intent intent = new Intent(MealListFragment.context.getApplicationContext(),MealDetailsActivityAdmin.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                            }
                        }
                    }
                });

            }
        });

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
        mTimesAvailableTV = (TextView) findViewById(R.id.times_text_view);
    }

    void setupTimesSelectionAlertDialog(){
        final String[] items = {
                "6:00 am", "7:00 am", "8:00 am",
                "9:00 am", "10:00 am", "11:00 am",
                "12:00 pm", "01:00 pm", "02:00 pm",
                "03:00 pm", "04:00 pm", "05:00 pm",
                "06:00 pm", "07:00 pm", "08:00 pm",
                "09:00 pm", "10:00 pm", "11:00 pm"
        };
        //final ArrayList itemsSelected = new ArrayList();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delivery Times for this item?");
        builder.setMultiChoiceItems(items, null,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedItemId,
                                        boolean isSelected) {
                        if (isSelected) {
                            timesList.add(items[selectedItemId]);
                        } else if (timesList.contains(items[selectedItemId])) {
                            timesList.remove(items[selectedItemId]);
                        }
                    }
                })
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Your logic when OK button is clicked
                        if(timesList.size() != 0) {
                            mTimesAvailableTV.setText(convertListOfStringsIntoSingleOne(timesList));
                        }else{
                            mTimesAvailableTV.setText("No Times Selected");
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        mDialog = builder.create();
        //mDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if(requestCode == Constants.LOCAL_OPERATION_SELECT_THUMBNAIL) {
                if(editMode){
                    Log.v(TAG,"9. In edit mode the thumbnail has been changed");
                    changedThumbnailInEditMode = true;
                }else {
                    Log.v(TAG, "9. Not in edit mode the thumbnail has been selected");
                }

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
                if(editMode){
                    Log.v(TAG, "10. In edit mode the poster has been changed");
                    changedPosterInEditMode = true;
                }else{
                    Log.v(TAG, "10. not in edit mode the poster has been selected");
                }
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

    String convertListOfStringsIntoSingleOne(List<String> strings){
        String times = "";
        for(int i=0; i<strings.size();i++){
            times = times + strings.get(i);
            if((strings.size()-1) != i){
                times = times+", ";
            }
        }
        return times;
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
