package com.momskitchen.momskitchen.Admin.ActivitiesPlusFragments;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.momskitchen.momskitchen.R;
import com.momskitchen.momskitchen.Tools;
import com.momskitchen.momskitchen.model.MealItem;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class MealDetailsActivityAdmin extends AppCompatActivity implements AdminMealDetailsFragment.OnFragmentInteractionListener{

    private static List<OnPosterLoadedListener> posterLoadedListeners;

    MealItem mealItem;
    Target target;
    ImageView posterImage;
    FrameLayout filterLayout;
    public static Palette posterPalette;

    public interface OnPosterLoadedListener{
        public void onPosterLoad();
    }

    public static void setPosterLoadedListeners(OnPosterLoadedListener onPosterLoadedListener){
        posterLoadedListeners.add(onPosterLoadedListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_details_activity_admin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        posterLoadedListeners = new ArrayList<>();
        adjustScrollViewTopMargin();
        mealItem = (MealItem) getIntent().getExtras().getSerializable("Meal");

        ((CoordinatorLayout) findViewById(R.id.root_layout)).setBackgroundColor(Color.parseColor("#FFFFFF"));


        posterImage = (ImageView) findViewById(R.id.food_poster_image_view);
        filterLayout = (FrameLayout) findViewById(R.id.poster_filter);

        if(mealItem.catagory.equals("lunch")){
            posterImage.setImageResource(R.drawable.lunch_icon);
        }else if(mealItem.catagory.equals("dessert")){
            posterImage.setImageResource(R.drawable.dessert_icon);
        }else if(mealItem.catagory.equals("compliment")){
            posterImage.setImageResource(R.drawable.compliment_icon);
        }
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewMealActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("Meal",mealItem);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Palette.Builder palleteBuilder = Palette.from(bitmap);
                posterPalette = palleteBuilder.generate();
                posterImage.setImageBitmap(bitmap);
                posterImage.setPadding(0,0,0,0);
                if(posterPalette.getLightVibrantSwatch()!=null) {
                    filterLayout.setBackgroundColor(Tools.setAplaOfColor(posterPalette.getLightVibrantSwatch().getBodyTextColor(), 55));
                }

                ((CoordinatorLayout) findViewById(R.id.root_layout)).setBackgroundColor(Tools.brightenColor(posterPalette.getLightVibrantColor(Color.parseColor("#FFFFFF"))));
                fab.setBackgroundTintList(ColorStateList.valueOf(posterPalette.getVibrantColor(getResources().getColor(R.color.colorAccent))));

                for(ListIterator<OnPosterLoadedListener> it = posterLoadedListeners.listIterator(); it.hasNext(); ){
                    OnPosterLoadedListener aListener = it.next();
                    aListener.onPosterLoad();
                    posterLoadedListeners.remove(aListener);
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        Picasso.with(getApplicationContext()).load(mealItem.posterURL).into(target);

        //Picasso.with(getApplicationContext()).load(mealItem.posterURL).centerCrop().into();
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putSerializable("Meal",mealItem);
            //mTicketPOJO = (TicketPOJO) getIntent().getSerilizableExtra("Ticket");
            AdminMealDetailsFragment fragment = new AdminMealDetailsFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.meal_detail_container, fragment)
                    .commit();
        }



        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    int getPXFromDP(int dp){
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return (int) px;
    }

    void adjustScrollViewTopMargin(){
        NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.meal_detail_container);
        CoordinatorLayout.LayoutParams scrollViewLayoutParams = (CoordinatorLayout.LayoutParams) nestedScrollView.getLayoutParams();
        scrollViewLayoutParams.topMargin-=getStatusBarHeight();
    }
}
