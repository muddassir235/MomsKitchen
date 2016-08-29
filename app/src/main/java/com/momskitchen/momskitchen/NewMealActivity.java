package com.momskitchen.momskitchen;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class NewMealActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_meal);
        bindViews();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

}
