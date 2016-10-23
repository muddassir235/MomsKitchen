package com.momskitchen.momskitchen.Customer.ActivitiesPlusFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.momskitchen.momskitchen.Customer.MealAdapter;
import com.momskitchen.momskitchen.R;
import com.momskitchen.momskitchen.model.MealItem;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class CustomerMenuFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_POSITION = "position";
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String TAG = "CustomerMenuFragment: ";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    public static String currentDate;

    public static MealAdapter lunchListAdapter;
    public static MealAdapter dessertListAdapter;
    public static MealAdapter complimentListAdapter;

    public static List<MealItem> mealsToAdd;
    public static List<MealItem> mealsToRemove;

    public static SwipeRefreshLayout refreshLunch;
    public static SwipeRefreshLayout refreshDessert;
    public static SwipeRefreshLayout refreshCompliment;

    public static ProgressBar progressLunch;
    public static ProgressBar progressDessert;
    public static ProgressBar progressCompliment;

    public static boolean anyLunch;
    public static boolean anyDessert;
    public static boolean anyCompliment;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CustomerMenuFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static CustomerMenuFragment newInstance(int position, int columnCount) {
        CustomerMenuFragment fragment = new CustomerMenuFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION,position);
        args.putInt(ARG_COLUMN_COUNT, columnCount);
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
        final View view = inflater.inflate(R.layout.fragment_customer_menu_item_list, container, false);

        int position = getArguments().getInt(ARG_POSITION);

        if(savedInstanceState != null){
            if(lunchListAdapter.getSize() == 0){
                view.findViewById(R.id.empty_list_layout).setVisibility(View.VISIBLE);
                ((ImageView) view.findViewById(R.id.empty_list_image_view)).setImageResource(R.drawable.lunch_icon);
                ((TextView) view.findViewById(R.id.empty_list_text_view)).setText("The are no lunch items");
            }

            if(dessertListAdapter.getSize() == 0){
                view.findViewById(R.id.empty_list_layout).setVisibility(View.VISIBLE);
                ((ImageView) view.findViewById(R.id.empty_list_image_view)).setImageResource(R.drawable.dessert_icon);
                ((TextView) view.findViewById(R.id.empty_list_text_view)).setText("The are no dessert items");
            }

            if(complimentListAdapter.getSize() == 0){
                view.findViewById(R.id.empty_list_layout).setVisibility(View.VISIBLE);
                ((ImageView) view.findViewById(R.id.empty_list_image_view)).setImageResource(R.drawable.compliment_icon);
                ((TextView) view.findViewById(R.id.empty_list_text_view)).setText("The are no compliment items");
            }
        }
        anyLunch = false;
        anyDessert = false;
        anyCompliment = false;

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), mColumnCount));
        }

        mealsToAdd = new ArrayList<>();
        mealsToRemove = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        if(position == 1){
            progressLunch = (ProgressBar) view.findViewById(R.id.progress_meals_grid);
            refreshLunch = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_meals_grid);
            refreshLunch.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    anyLunch = false;
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
                                if(lunchListAdapter.getSize() > 0) {
                                    view.findViewById(R.id.empty_list_layout).setVisibility(View.GONE);
                                }else{
                                    view.findViewById(R.id.empty_list_layout).setVisibility(View.VISIBLE);
                                    ((ImageView) view.findViewById(R.id.empty_list_image_view)).setImageResource(R.drawable.lunch_icon);
                                    ((TextView) view.findViewById(R.id.empty_list_text_view)).setText("The are no lunch items");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            });

            lunchListAdapter = new MealAdapter(getActivity(),currentDate,
                    FirebaseDatabase.getInstance().getReference().child("Meals").child("lunch"),
                    MealAdapter.TYPE_LUNCH
            );
            recyclerView.setAdapter(lunchListAdapter);

            FirebaseDatabase.getInstance().getReference().child("Meals").child("lunch").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    progressLunch.setVisibility(View.GONE);
                    refreshLunch.setRefreshing(false);
                    if(!dataSnapshot.exists() || (lunchListAdapter.getSize() == 0)){
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
                        if((dataSnapshot.getValue(MealItem.class)).dates.contains(currentDate)) {
                            view.findViewById(R.id.empty_list_layout).setVisibility(View.GONE);
                        }
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

            //recyclerView.setAdapter(lunchListAdapter);
        }else if(position == 2){
            progressDessert = (ProgressBar) view.findViewById(R.id.progress_meals_grid);
            refreshDessert = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_meals_grid);
            refreshDessert.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    anyDessert = false;
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
                                if(dessertListAdapter.getSize() > 0) {
                                    view.findViewById(R.id.empty_list_layout).setVisibility(View.GONE);
                                }else{
                                    view.findViewById(R.id.empty_list_layout).setVisibility(View.VISIBLE);
                                    ((ImageView) view.findViewById(R.id.empty_list_image_view)).setImageResource(R.drawable.dessert_icon);
                                    ((TextView) view.findViewById(R.id.empty_list_text_view)).setText("The are no dessert items");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });

            dessertListAdapter = new MealAdapter(getActivity(),currentDate,
                    FirebaseDatabase.getInstance().getReference().child("Meals").child("dessert"),
                    MealAdapter.TYPE_DESSERT
            );
            recyclerView.setAdapter(dessertListAdapter);

            FirebaseDatabase.getInstance().getReference().child("Meals").child("dessert").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    progressDessert.setVisibility(View.GONE);
                    refreshDessert.setRefreshing(false);
                    if(!dataSnapshot.exists() || (dessertListAdapter.getSize() == 0)){
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
                        if(dataSnapshot.getValue(MealItem.class).dates.contains(currentDate)) {
                            view.findViewById(R.id.empty_list_layout).setVisibility(View.GONE);
                        }
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

        }else if(position == 3){
            progressCompliment = (ProgressBar) view.findViewById(R.id.progress_meals_grid);
            refreshCompliment = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_meals_grid);
            refreshCompliment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    anyCompliment = false;
                    complimentListAdapter.notifyDataSetChanged();
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
                                if(complimentListAdapter.getSize() > 0) {
                                    view.findViewById(R.id.empty_list_layout).setVisibility(View.GONE);
                                }else{
                                    view.findViewById(R.id.empty_list_layout).setVisibility(View.VISIBLE);
                                    ((ImageView) view.findViewById(R.id.empty_list_image_view)).setImageResource(R.drawable.compliment_icon);
                                    ((TextView) view.findViewById(R.id.empty_list_text_view)).setText("The are no complimentary items");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });

            complimentListAdapter = new MealAdapter(getActivity(),currentDate,
                    FirebaseDatabase.getInstance().getReference().child("Meals").child("compliment"),
                    MealAdapter.TYPE_COMPLIMENT
            );
            recyclerView.setAdapter(complimentListAdapter);

            FirebaseDatabase.getInstance().getReference().child("Meals").child("compliment").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    progressCompliment.setVisibility(View.GONE);
                    refreshCompliment.setRefreshing(false);
                    if(!dataSnapshot.exists() || (lunchListAdapter.getSize() == 0)){
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
                        if((dataSnapshot.getValue(MealItem.class)).dates.contains(currentDate)) {
                            view.findViewById(R.id.empty_list_layout).setVisibility(View.GONE);
                        }
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
        void onListFragmentInteraction();
    }
}
