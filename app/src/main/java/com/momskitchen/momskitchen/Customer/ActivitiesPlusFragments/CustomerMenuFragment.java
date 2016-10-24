package com.momskitchen.momskitchen.Customer.ActivitiesPlusFragments;

import android.content.Context;
import android.graphics.Color;
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
import android.widget.LinearLayout;
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

        anyLunch = false;
        anyDessert = false;
        anyCompliment = false;

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), mColumnCount));
        }

        final LinearLayout emptyLayout = (LinearLayout) view.findViewById(R.id.empty_list_layout);
        final ImageView emptyIV = (ImageView) view.findViewById(R.id.empty_list_image_view);
        final TextView emptyTV = (TextView) view.findViewById(R.id.empty_list_text_view);

        mealsToAdd = new ArrayList<>();
        mealsToRemove = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        if(position == 1){
            progressLunch = (ProgressBar) view.findViewById(R.id.progress_meals_grid);
            refreshLunch = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_meals_grid);

            lunchListAdapter = new MealAdapter(getActivity(),currentDate,
                    FirebaseDatabase.getInstance().getReference().child("Meals").child("lunch"),
                    MealAdapter.TYPE_LUNCH
            );
            recyclerView.setAdapter(lunchListAdapter);

            lunchListAdapter.setDataLoadedListener(new MealAdapter.DataLoadedListener() {
                @Override
                public void dataLoaded(int size) {
                    progressLunch.setVisibility(View.GONE);
                    refreshLunch.setRefreshing(false);
                    if(size == 0){
                        emptyLayout.setVisibility(View.VISIBLE);
                        emptyIV.setImageResource(R.drawable.buger_icon);
                        emptyTV.setText("No lunch items available");
                        view.setBackgroundColor(Color.parseColor("#ff9f9f"));
                    }else{
                        emptyLayout.setVisibility(View.GONE);
                        view.setBackgroundColor(Color.parseColor("#00ff9f9f"));
                    }
                }
            });
            refreshLunch.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    lunchListAdapter.refreshData();
                }
            });

        }else if(position == 2){
            progressDessert = (ProgressBar) view.findViewById(R.id.progress_meals_grid);
            refreshDessert = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_meals_grid);

            dessertListAdapter = new MealAdapter(getActivity(),currentDate,
                    FirebaseDatabase.getInstance().getReference().child("Meals").child("dessert"),
                    MealAdapter.TYPE_DESSERT
            );
            recyclerView.setAdapter(dessertListAdapter);

            dessertListAdapter.setDataLoadedListener(new MealAdapter.DataLoadedListener() {
                @Override
                public void dataLoaded(int size) {
                    progressDessert.setVisibility(View.GONE);
                    refreshDessert.setRefreshing(false);
                    if(size == 0){
                        emptyLayout.setVisibility(View.VISIBLE);
                        emptyIV.setImageResource(R.drawable.ice_cream_icon);
                        emptyTV.setText("No dessert items available");
                        view.setBackgroundColor(Color.parseColor("#ff9f9f"));
                    }else{
                        emptyLayout.setVisibility(View.GONE);
                        view.setBackgroundColor(Color.parseColor("#00ff9f9f"));
                    }
                }
            });

            refreshDessert.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    dessertListAdapter.refreshData();
                }
            });

        }else if(position == 3){
            progressCompliment = (ProgressBar) view.findViewById(R.id.progress_meals_grid);
            refreshCompliment = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_meals_grid);

            complimentListAdapter = new MealAdapter(getActivity(),currentDate,
                    FirebaseDatabase.getInstance().getReference().child("Meals").child("compliment"),
                    MealAdapter.TYPE_COMPLIMENT
            );
            recyclerView.setAdapter(complimentListAdapter);

            complimentListAdapter.setDataLoadedListener(new MealAdapter.DataLoadedListener() {
                @Override
                public void dataLoaded(int size) {
                    progressCompliment.setVisibility(View.GONE);
                    refreshCompliment.setRefreshing(false);
                    if(size == 0){
                        emptyLayout.setVisibility(View.VISIBLE);
                        emptyIV.setImageResource(R.drawable.coffee_icon);
                        emptyTV.setText("No compliments available");
                        view.setBackgroundColor(Color.parseColor("#ff9f9f"));
                    }else{
                        emptyLayout.setVisibility(View.GONE);
                        view.setBackgroundColor(Color.parseColor("#00ffffff"));
                    }
                }
            });
            refreshCompliment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    complimentListAdapter.refreshData();
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
