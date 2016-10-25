package com.momskitchen.momskitchen.Admin.ActivitiesPlusFragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.momskitchen.momskitchen.Admin.ViewHolders.MealHolder;
import com.momskitchen.momskitchen.R;
import com.momskitchen.momskitchen.model.MealItem;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.HashMap;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MealListFragment extends Fragment {

    public static Context context;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_POSITON = "position";
    private static final String TAG = "MealListFragment: ";
    // TODO: Customize parameters
    private int mColumnCount = 2;
    private OnListFragmentInteractionListener mListener;

    private HashMap<Integer,Target> lunchTargetMap;
    private HashMap<Integer,Target> dessertTargetMap;
    private HashMap<Integer,Target> complimentTargetMap;

    public static FirebaseRecyclerAdapter<MealItem,MealHolder> lunchListAdapter;
    public static FirebaseRecyclerAdapter<MealItem,MealHolder> dessertListAdapter;
    public static FirebaseRecyclerAdapter<MealItem,MealHolder> complimentListAdapter;

    public static SwipeRefreshLayout refreshLunch;
    public static SwipeRefreshLayout refreshDessert;
    public static SwipeRefreshLayout refreshCompliment;

    public static ProgressBar progressLunch;
    public static ProgressBar progressDessert;
    public static ProgressBar progressCompliment;

    public static int imageSide;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MealListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static MealListFragment newInstance(int positon,int columnCount) {
        MealListFragment fragment = new MealListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putInt(ARG_POSITON,positon);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_meal_list, container, false);
        context = getActivity();

        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        imageSide = size.x/2;

        lunchTargetMap = new HashMap<>();
        dessertTargetMap = new HashMap<>();
        complimentTargetMap = new HashMap<>();

        int position = getArguments().getInt(ARG_POSITON);
        // Set the adapter

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(MealsFragment.context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(MealsFragment.context, mColumnCount));
        }
        recyclerView.setHasFixedSize(true);
        if(position == 1){
            progressLunch = (ProgressBar) view.findViewById(R.id.progress_meals_grid);
            refreshLunch = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_meals_grid);
            refreshLunch.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    lunchListAdapter.notifyDataSetChanged();
                    FirebaseDatabase.getInstance().getReference().child("Meals").child("lunch").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            progressLunch.setVisibility(View.GONE);
                            refreshLunch.setRefreshing(false);
                            if(!dataSnapshot.exists()){
                                view.findViewById(R.id.empty_list_layout).setVisibility(View.VISIBLE);
                                ((ImageView) view.findViewById(R.id.empty_list_image_view)).setImageResource(R.drawable.lunch_icon);
                                ((TextView) view.findViewById(R.id.empty_list_text_view)).setText("The are no lunch items");
                            }else {
                                view.findViewById(R.id.empty_list_layout).setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            });
            lunchListAdapter = new FirebaseRecyclerAdapter<MealItem, MealHolder>(MealItem.class,R.layout.meal_item_layout_admin,MealHolder.class, FirebaseDatabase.getInstance().getReference().child("Meals").child("lunch")) {
                @Override
                protected void populateViewHolder(final MealHolder viewHolder, final MealItem model, final int position) {
                    progressLunch.setVisibility(View.GONE);
                    refreshLunch.setRefreshing(false);
                    Log.v(TAG, "mealname :"+model.name);
                    viewHolder.mItem = model;

                    RelativeLayout.LayoutParams posterLayoutParams = (RelativeLayout.LayoutParams) viewHolder.mealPosterIV.getLayoutParams();
                    posterLayoutParams.height = imageSide;
                    posterLayoutParams.width = imageSide;
                    viewHolder.mealPosterIV.setImageResource(R.drawable.lunch_icon);

                    lunchTargetMap.put(position, new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            Log.v(TAG,model.name+" image loaded");
                            viewHolder.mealPosterIV.setPadding(0,0,0,0);
                            Palette.Builder palleteBuilder = Palette.from(bitmap);
                            Palette palette = palleteBuilder.generate();
                            viewHolder.mealPosterIV.setImageBitmap(bitmap);
                            if(palette!=null) {
                                if(getActivity()!=null) {
                                    viewHolder.mMealInfoLayout.setBackgroundColor(palette.getVibrantColor(getActivity().getResources().getColor(R.color.colorAccent)));
                                }
                            }
                            lunchTargetMap.remove(position);
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                            Log.v(TAG,model.name+" image prepared");
                        }
                    });
                    Picasso.with(getActivity().getApplicationContext()).load(model.thumbnailURL).resize(imageSide,imageSide).into(lunchTargetMap.get(position));
                    //Picasso.with(getActivity().getApplicationContext()).load(model.thumbnailURL).resize(imageSide,imageSide).into(viewHolder.mealPosterIV);
                    viewHolder.mNameTV.setText(model.name);
                    viewHolder.mPriceTV.setText("Price: "+model.pricePerUnit+" PKR");
                }
            };

            FirebaseDatabase.getInstance().getReference().child("Meals").child("lunch").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    progressLunch.setVisibility(View.GONE);
                    refreshLunch.setRefreshing(false);
                    if(!dataSnapshot.exists()){
                        view.findViewById(R.id.empty_list_layout).setVisibility(View.VISIBLE);
                        ((ImageView) view.findViewById(R.id.empty_list_image_view)).setImageResource(R.drawable.lunch_icon);
                        ((TextView) view.findViewById(R.id.empty_list_text_view)).setText("The are no lunch items");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            FirebaseDatabase.getInstance().getReference().child("Meals").child("lunch").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if(view.findViewById(R.id.empty_list_layout).getVisibility()== View.VISIBLE) {
                        view.findViewById(R.id.empty_list_layout).setVisibility(View.GONE);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            recyclerView.setAdapter(lunchListAdapter);
        }else if(position == 2){
            progressDessert = (ProgressBar) view.findViewById(R.id.progress_meals_grid);
            refreshDessert = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_meals_grid);
            refreshDessert.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    dessertListAdapter.notifyDataSetChanged();
                    FirebaseDatabase.getInstance().getReference().child("Meals").child("dessert").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            progressDessert.setVisibility(View.GONE);
                            refreshDessert.setRefreshing(false);
                            if(!dataSnapshot.exists()) {
                                view.findViewById(R.id.empty_list_layout).setVisibility(View.VISIBLE);
                                ((ImageView) view.findViewById(R.id.empty_list_image_view)).setImageResource(R.drawable.dessert_icon);
                                ((TextView) view.findViewById(R.id.empty_list_text_view)).setText("The are no dessert items");
                            }else {
                                view.findViewById(R.id.empty_list_layout).setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });

            dessertListAdapter = new FirebaseRecyclerAdapter<MealItem, MealHolder>(MealItem.class,R.layout.meal_item_layout_admin,MealHolder.class, FirebaseDatabase.getInstance().getReference().child("Meals").child("dessert")) {
                @Override
                protected void populateViewHolder(final MealHolder viewHolder, final MealItem model, final int position) {
                    progressDessert.setVisibility(View.GONE);
                    refreshDessert.setRefreshing(false);
                    Log.v(TAG, "mealname :"+model.name);
                    viewHolder.mItem = model;

                    RelativeLayout.LayoutParams posterLayoutParams = (RelativeLayout.LayoutParams) viewHolder.mealPosterIV.getLayoutParams();
                    posterLayoutParams.height = imageSide;
                    posterLayoutParams.width = imageSide;
                    viewHolder.mealPosterIV.setImageResource(R.drawable.dessert_icon);

                    dessertTargetMap.put(position, new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            Log.v(TAG,model.name+" image loaded");
                            viewHolder.mealPosterIV.setPadding(0,0,0,0);
                            Palette.Builder palleteBuilder = Palette.from(bitmap);
                            Palette palette = palleteBuilder.generate();
                            viewHolder.mealPosterIV.setImageBitmap(bitmap);
                            if(palette!=null) {
                                if(getActivity()!=null) {
                                    viewHolder.mMealInfoLayout.setBackgroundColor(palette.getVibrantColor(getActivity().getResources().getColor(R.color.colorAccent)));
                                }
                            }
                            dessertTargetMap.remove(position);
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                            Log.v(TAG,model.name+" image prepared");
                        }
                    });
                    Picasso.with(getActivity().getApplicationContext()).load(model.thumbnailURL).resize(imageSide,imageSide).into(dessertTargetMap.get(position));
                    //Picasso.with(getActivity().getApplicationContext()).load(model.thumbnailURL).resize(imageSide,imageSide).into(viewHolder.mealPosterIV);
                    viewHolder.mNameTV.setText(model.name);
                    viewHolder.mPriceTV.setText("Price: "+model.pricePerUnit+" PKR");
                }


            };

            FirebaseDatabase.getInstance().getReference().child("Meals").child("dessert").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    progressDessert.setVisibility(View.GONE);
                    refreshDessert.setRefreshing(false);
                    if(!dataSnapshot.exists()) {
                        view.findViewById(R.id.empty_list_layout).setVisibility(View.VISIBLE);
                        ((ImageView) view.findViewById(R.id.empty_list_image_view)).setImageResource(R.drawable.dessert_icon);
                        ((TextView) view.findViewById(R.id.empty_list_text_view)).setText("The are no dessert items");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            FirebaseDatabase.getInstance().getReference().child("Meals").child("dessert").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if(view.findViewById(R.id.empty_list_layout).getVisibility()== View.VISIBLE) {
                        view.findViewById(R.id.empty_list_layout).setVisibility(View.GONE);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            recyclerView.setAdapter(dessertListAdapter);
        }else if(position == 3){
            progressCompliment = (ProgressBar) view.findViewById(R.id.progress_meals_grid);
            refreshCompliment = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_meals_grid);
            refreshCompliment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    FirebaseDatabase.getInstance().getReference().child("Meals").child("compliment").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            progressCompliment.setVisibility(View.GONE);
                            refreshCompliment.setRefreshing(false);
                            if(!dataSnapshot.exists()) {
                                view.findViewById(R.id.empty_list_layout).setVisibility(View.VISIBLE);
                                ((ImageView) view.findViewById(R.id.empty_list_image_view)).setImageResource(R.drawable.compliment_icon);
                                ((TextView) view.findViewById(R.id.empty_list_text_view)).setText("The are no complimentary items");
                            }else {
                                view.findViewById(R.id.empty_list_layout).setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    complimentListAdapter.notifyDataSetChanged();
                }
            });
            complimentListAdapter = new FirebaseRecyclerAdapter<MealItem, MealHolder>(MealItem.class,R.layout.meal_item_layout_admin,MealHolder.class, FirebaseDatabase.getInstance().getReference().child("Meals").child("compliment")) {
                @Override
                protected void populateViewHolder(final MealHolder viewHolder, final MealItem model, final int position) {
                    progressCompliment.setVisibility(View.GONE);
                    refreshCompliment.setRefreshing(false);
                    Log.v(TAG, "mealname :"+model.name);
                    viewHolder.mItem = model;

                    RelativeLayout.LayoutParams posterLayoutParams = (RelativeLayout.LayoutParams) viewHolder.mealPosterIV.getLayoutParams();
                    posterLayoutParams.height = imageSide;
                    posterLayoutParams.width = imageSide;
                    viewHolder.mealPosterIV.setImageResource(R.drawable.compliment_icon);

                    complimentTargetMap.put(position, new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            viewHolder.mealPosterIV.setPadding(0,0,0,0);
                            Log.v(TAG,model.name+" image loaded");
                            Palette.Builder palleteBuilder = Palette.from(bitmap);
                            Palette palette = palleteBuilder.generate();
                            viewHolder.mealPosterIV.setImageBitmap(bitmap);
                            if(palette!=null) {
                                if(getActivity()!=null) {
                                    viewHolder.mMealInfoLayout.setBackgroundColor(palette.getVibrantColor(getActivity().getResources().getColor(R.color.colorAccent)));
                                }
                            }
                            complimentTargetMap.remove(position);
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                            Log.v(TAG,model.name+" image prepared");
                        }
                    });
                    Picasso.with(getActivity().getApplicationContext()).load(model.thumbnailURL).resize(imageSide,imageSide).into(complimentTargetMap.get(position));
                    //Picasso.with(getActivity().getApplicationContext()).load(model.thumbnailURL).resize(imageSide,imageSide).into(viewHolder.mealPosterIV);
                    viewHolder.mNameTV.setText(model.name);
                    viewHolder.mPriceTV.setText("Price: "+model.pricePerUnit+" PKR");
                }
            };

            FirebaseDatabase.getInstance().getReference().child("Meals").child("compliment").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    progressCompliment.setVisibility(View.GONE);
                    refreshCompliment.setRefreshing(false);
                    if(!dataSnapshot.exists()) {
                        view.findViewById(R.id.empty_list_layout).setVisibility(View.VISIBLE);
                        ((ImageView) view.findViewById(R.id.empty_list_image_view)).setImageResource(R.drawable.compliment_icon);
                        ((TextView) view.findViewById(R.id.empty_list_text_view)).setText("The are no complimentary items");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            FirebaseDatabase.getInstance().getReference().child("Meals").child("compliment").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if(view.findViewById(R.id.empty_list_layout).getVisibility()== View.VISIBLE) {
                        view.findViewById(R.id.empty_list_layout).setVisibility(View.GONE);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            recyclerView.setAdapter(complimentListAdapter);
        }




        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(MealItem item);
    }


}
