package com.momskitchen.momskitchen.Customer.ActivitiesPlusFragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.momskitchen.momskitchen.Constants;
import com.momskitchen.momskitchen.Customer.CartRootRecyclerHolder;
import com.momskitchen.momskitchen.Customer.CustomerCartRootListAdapter;
import com.momskitchen.momskitchen.R;
import com.momskitchen.momskitchen.backend.OrderHandler;
import com.momskitchen.momskitchen.model.CartRootItem;
import com.momskitchen.momskitchen.model.MealItem;
import com.momskitchen.momskitchen.model.Order;
import com.momskitchen.momskitchen.model.UserCartEntry;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CutomerCartFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CutomerCartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CutomerCartFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_POSITION = "position";
    private static final String TAG = "CustomerCartFragment: ";
    private OnFragmentInteractionListener mListener;

    public CutomerCartFragment() {
        // Required empty public constructor
    }

    static UserCartEntry deletedEntry;

    LinearLayout emptyCartLayout;
    ImageView emptyCartImageView;
    TextView emptyCartTextView;

    static boolean somethingLoaded;

    ProgressBar cartLoadingProgress;
    SwipeRefreshLayout cartRefreshLayout;

    // TODO: Rename and change types and number of parameters
    public static CutomerCartFragment newInstance(int position) {
        CutomerCartFragment fragment = new CutomerCartFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_cutomer_cart, container, false);
        bindViews(rootView);
        int position = getArguments().getInt(ARG_POSITION);

        if(savedInstanceState !=null) {
            cartLoadingProgress.setVisibility(View.GONE);
        }

        RecyclerView mealLists = (RecyclerView) rootView.findViewById(R.id.cart_meals_list);
        mealLists.setLayoutManager(new LinearLayoutManager(getContext()));
        final CustomerCartRootListAdapter adapter = new CustomerCartRootListAdapter(getActivity());

        final FloatingActionButton sendOrderButton = (FloatingActionButton) rootView.findViewById(R.id.send_order_button);

        adapter.setDataLoadedListener(new CustomerCartRootListAdapter.DataLoadedListener() {
            @Override
            public void dataLoaded(int size) {
                cartLoadingProgress.setVisibility(View.GONE);
                cartRefreshLayout.setRefreshing(false);
                if(size == 0){
                    emptyCartLayout.setVisibility(View.VISIBLE);
                    emptyCartImageView.setImageResource(R.drawable.cart_colorful_icon);
                    emptyCartTextView.setText("Your Cart Is Empty");
                    rootView.setBackgroundColor(Color.parseColor("#B0D3FE"));
                    sendOrderButton.setVisibility(View.GONE);

                }else {
                    sendOrderButton.setVisibility(View.VISIBLE);
                    emptyCartLayout.setVisibility(View.GONE);
                    rootView.setBackgroundColor(Color.parseColor("#00FFFFFF"));
                }
            }
        });

        mealLists.setAdapter(adapter);

        NestedScrollView cartScrollView = (NestedScrollView) rootView.findViewById(R.id.cart_scroll_view);

        cartScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int dy = scrollY-oldScrollY;
                if(dy>0){
                    sendOrderButton.setVisibility(View.GONE);
                }else{
                    sendOrderButton.setVisibility(View.VISIBLE);
                }
            }
        });

        cartRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.loadData();
            }
        });

        sendOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> dates = new ArrayList<String>();
                List<MealItem> meals = new ArrayList<MealItem>();
                List<Long> quantities = new ArrayList<Long>();
                List<String> times = new ArrayList<String>();

                boolean timeSet = true;

                for(String date:adapter.dates){
                    List<MealItem> dayMeals = adapter.mealItems.get(date);
                    List<Long> dayQantities = adapter.mealItemQuantities.get(date);
                    List<String> dayTimes= adapter.mealItemTimes.get(date);

                    for(int i=0;i<dayMeals.size();i++){
                        meals.add(dayMeals.get(i));
                        quantities.add(dayQantities.get(i));
                        String time = dayTimes.get(i);
                        if(time.equals("N/A")) {
                            timeSet = false;
                            break;
                        }else{
                            times.add(time);
                        }
                        dates.add(date);
                    }
                }

                if(!timeSet){
                    Toast.makeText(getActivity(),"All meals must have a time of delivery",Toast.LENGTH_SHORT).show();
                }else{
                    DialogFragment deliveryDialog = new DeliveryPointAddressDialog(
                            meals,
                            quantities,
                            times,
                            dates,adapter
                    );
                    deliveryDialog.show(getChildFragmentManager(),"Dialog");
                }
            }
        });
//        switch (position) {
//            case  1: {
//                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                FirebaseDatabase.getInstance().getReference()
//                        .child("Users")
//                        .child(user.getUid())
//                        .child("Cart")
//                        .addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                if (dataSnapshot.exists()) {
//                                    emptyCartLayout.setVisibility(View.GONE);
//                                    if(!somethingLoaded) {
//                                        cartLoadingProgress.setVisibility(View.GONE);
//                                        somethingLoaded = true;
//                                    }
//                                } else {
//                                    emptyCartLayout.setVisibility(View.VISIBLE);
//                                    emptyCartTextView.setText("Your Cart is empty");
//                                    emptyCartImageView.setImageResource(R.drawable.cart_grey);
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
//
//
//                if (true) {
//                    List<String> weekDates = MenuCreator.getInstance().getAllWeekDates(CustomerMenuFragment.currentDate);
//                    monday = weekDates.get(0);
//                    tuesday = weekDates.get(1);
//                    wednesday = weekDates.get(2);
//                    thursday = weekDates.get(3);
//                    friday = weekDates.get(4);
//                    saturday = weekDates.get(5);
//                    sunday = weekDates.get(6);
//                }
//
//                FirebaseDatabase.getInstance().getReference()
//                        .child("Users")
//                        .child(user.getUid())
//                        .child("Cart")
//                        .child(monday).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if(!somethingLoaded) {
//                            cartLoadingProgress.setVisibility(View.GONE);
//                            somethingLoaded = true;
//                        }
//                        if (dataSnapshot.exists()) {
//                            mMondayTV.setVisibility(View.VISIBLE);
//                            mMondayMealsList.setVisibility(View.VISIBLE);
//                        } else {
//                            mMondayTV.setVisibility(View.GONE);
//                            mMondayMealsList.setVisibility(View.GONE);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//
//                FirebaseDatabase.getInstance().getReference()
//                        .child("Users")
//                        .child(user.getUid())
//                        .child("Cart")
//                        .child(tuesday).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if(!somethingLoaded) {
//                            cartLoadingProgress.setVisibility(View.GONE);
//                            somethingLoaded = true;
//                        }
//                        if (dataSnapshot.exists()) {
//                            mTuesdayTV.setVisibility(View.VISIBLE);
//                            mTuesdayMealsList.setVisibility(View.VISIBLE);
//                        } else {
//                            mTuesdayTV.setVisibility(View.GONE);
//                            mTuesdayMealsList.setVisibility(View.GONE);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//
//                FirebaseDatabase.getInstance().getReference()
//                        .child("Users")
//                        .child(user.getUid())
//                        .child("Cart")
//                        .child(wednesday).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if(!somethingLoaded) {
//                            cartLoadingProgress.setVisibility(View.GONE);
//                            somethingLoaded = true;
//                        }
//                        if (dataSnapshot.exists()) {
//                            mWednesdayTV.setVisibility(View.VISIBLE);
//                            mWednesdayMealsList.setVisibility(View.VISIBLE);
//                        } else {
//                            mWednesdayTV.setVisibility(View.GONE);
//                            mWednesdayMealsList.setVisibility(View.GONE);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//                FirebaseDatabase.getInstance().getReference()
//                        .child("Users")
//                        .child(user.getUid())
//                        .child("Cart")
//                        .child(thursday).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if(!somethingLoaded) {
//                            cartLoadingProgress.setVisibility(View.GONE);
//                            somethingLoaded = true;
//                        }
//                        if (dataSnapshot.exists()) {
//                            mThursdayTV.setVisibility(View.VISIBLE);
//                            mThursdayMealsList.setVisibility(View.VISIBLE);
//                        } else {
//                            mThursdayTV.setVisibility(View.GONE);
//                            mThursdayMealsList.setVisibility(View.GONE);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//                FirebaseDatabase.getInstance().getReference()
//                        .child("Users")
//                        .child(user.getUid())
//                        .child("Cart")
//                        .child(friday).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if(!somethingLoaded) {
//                            cartLoadingProgress.setVisibility(View.GONE);
//                            somethingLoaded = true;
//                        }
//                        if (dataSnapshot.exists()) {
//                            mFridayTV.setVisibility(View.VISIBLE);
//                            mFridayMealsList.setVisibility(View.VISIBLE);
//                        } else {
//                            mFridayTV.setVisibility(View.GONE);
//                            mFridayMealsList.setVisibility(View.GONE);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//
//                FirebaseDatabase.getInstance().getReference()
//                        .child("Users")
//                        .child(user.getUid())
//                        .child("Cart")
//                        .child(saturday).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if(!somethingLoaded) {
//                            cartLoadingProgress.setVisibility(View.GONE);
//                            somethingLoaded = true;
//                        }
//                        if (dataSnapshot.exists()) {
//                            mSaturdayTV.setVisibility(View.VISIBLE);
//                            mSaturdayMealsList.setVisibility(View.VISIBLE);
//                        } else {
//                            mSaturdayTV.setVisibility(View.GONE);
//                            mSaturdayMealsList.setVisibility(View.GONE);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//
//                FirebaseDatabase.getInstance().getReference()
//                        .child("Users")
//                        .child(user.getUid())
//                        .child("Cart")
//                        .child(sunday).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if(!somethingLoaded) {
//                            cartLoadingProgress.setVisibility(View.GONE);
//                            somethingLoaded = true;
//                        }
//                        if (dataSnapshot.exists()) {
//                            mSundayTV.setVisibility(View.VISIBLE);
//                            mSundayMealsList.setVisibility(View.VISIBLE);
//                        } else {
//                            mSundayTV.setVisibility(View.GONE);
//                            mSundayMealsList.setVisibility(View.GONE);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//
//
//                final FirebaseRecyclerAdapter<UserCartEntry, CartDayListItemHolder> mondayAdapter =
//                        new FirebaseRecyclerAdapter<UserCartEntry, CartDayListItemHolder>(
//                                UserCartEntry.class,
//                                R.layout.cart_layout_meal_list_item_layout,
//                                CartDayListItemHolder.class,
//                                FirebaseDatabase.getInstance().getReference()
//                                        .child("Users")
//                                        .child(user.getUid())
//                                        .child("Cart")
//                                        .child(monday)
//                        ) {
//                            @Override
//                            protected void populateViewHolder(CartDayListItemHolder viewHolder, UserCartEntry model, int position) {
//                                cartRefreshLayout.setRefreshing(false);
//                                if(!somethingLoaded) {
//                                    cartLoadingProgress.setVisibility(View.GONE);
//                                    somethingLoaded = true;
//                                }
//
//                                mMondayTV.setVisibility(View.VISIBLE);
//                                mMondayMealsList.setVisibility(View.VISIBLE);
//
//                                Picasso.with(getActivity()).load(model.mealItem.thumbnailURL).into(viewHolder.mMealThumbnailIV);
//                                viewHolder.mMealNameTV.setText(model.mealItem.name);
//                                //viewHolder.mMealTimesSpinner.setText(model.mealItem.description);
//                                viewHolder.mMealPriceTV.setText("" + model.mealItem.pricePerUnit + " PKR");
//                                viewHolder.mMealQuantityTV.setText("" + model.quantity);
//                            }
//                        };
//                mMondayMealsList.setAdapter(mondayAdapter);
//
//                ItemTouchHelper.SimpleCallback mondaySwipeHandler = new ItemTouchHelper.SimpleCallback(
//                        0, ItemTouchHelper.RIGHT
//                ) {
//                    @Override
//                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                        return false;
//                    }
//
//                    @Override
//                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//                        final UserCartEntry userCartEntry = mondayAdapter.getItem(viewHolder.getAdapterPosition());
//                        deletedEntry = userCartEntry;
//                        FirebaseDatabase.getInstance().getReference()
//                                .child("Users")
//                                .child(user.getUid())
//                                .child("Cart")
//                                .child(monday)
//                                .child(userCartEntry.mealItem.id)
//                                .setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                if (CustomerMenu.totalTV != null) {
//                                    OrderHandler.calculateTotal();
//                                }
//                                if (CustomerMenuFragment.lunchListAdapter != null) {
//                                    CustomerMenuFragment.lunchListAdapter.notifyDataSetChanged();
//                                }
//                                if (CustomerMenuFragment.dessertListAdapter != null) {
//                                    CustomerMenuFragment.dessertListAdapter.notifyDataSetChanged();
//                                }
//                                if (CustomerMenuFragment.complimentListAdapter != null) {
//                                    CustomerMenuFragment.complimentListAdapter.notifyDataSetChanged();
//                                }
//                                showSnackBar(userCartEntry.mealItem.name);
//                            }
//                        });
//                        mondayAdapter.notifyDataSetChanged();
//                    }
//                };
//
//                ItemTouchHelper mondayItemTouchHelper = new ItemTouchHelper(mondaySwipeHandler);
//
//                mondayItemTouchHelper.attachToRecyclerView(mMondayMealsList);
//
//
//                final FirebaseRecyclerAdapter<UserCartEntry, CartDayListItemHolder> tuesdayAdapter =
//                        new FirebaseRecyclerAdapter<UserCartEntry, CartDayListItemHolder>(
//                                UserCartEntry.class,
//                                R.layout.cart_layout_meal_list_item_layout,
//                                CartDayListItemHolder.class,
//                                FirebaseDatabase.getInstance().getReference()
//                                        .child("Users")
//                                        .child(user.getUid())
//                                        .child("Cart")
//                                        .child(tuesday)
//                        ) {
//                            @Override
//                            protected void populateViewHolder(CartDayListItemHolder viewHolder, UserCartEntry model, int position) {
//                                cartRefreshLayout.setRefreshing(false);
//                                if(!somethingLoaded) {
//                                    cartLoadingProgress.setVisibility(View.GONE);
//                                    somethingLoaded = true;
//                                }
//
//                                mTuesdayTV.setVisibility(View.VISIBLE);
//                                mTuesdayMealsList.setVisibility(View.VISIBLE);
//
//                                Picasso.with(getActivity()).load(model.mealItem.thumbnailURL).into(viewHolder.mMealThumbnailIV);
//                                viewHolder.mMealNameTV.setText(model.mealItem.name);
//                                //viewHolder.mMealDescTV.setText(model.mealItem.description);
//                                viewHolder.mMealPriceTV.setText("" + model.mealItem.pricePerUnit + " PKR");
//                                viewHolder.mMealQuantityTV.setText("" + model.quantity);
//                            }
//                        };
//                mTuesdayMealsList.setAdapter(tuesdayAdapter);
//
//                ItemTouchHelper.SimpleCallback tuesdaySwipeHandler = new ItemTouchHelper.SimpleCallback(
//                        0, ItemTouchHelper.RIGHT
//                ) {
//                    @Override
//                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                        return false;
//                    }
//
//                    @Override
//                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//                        final UserCartEntry userCartEntry = tuesdayAdapter.getItem(viewHolder.getAdapterPosition());
//                        deletedEntry = userCartEntry;
//                        FirebaseDatabase.getInstance().getReference()
//                                .child("Users")
//                                .child(user.getUid())
//                                .child("Cart")
//                                .child(tuesday)
//                                .child(userCartEntry.mealItem.id)
//                                .setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                if (CustomerMenu.totalTV != null) {
//                                    OrderHandler.calculateTotal();
//                                }
//                                if (CustomerMenuFragment.lunchListAdapter != null) {
//                                    CustomerMenuFragment.lunchListAdapter.notifyDataSetChanged();
//                                }
//                                if (CustomerMenuFragment.dessertListAdapter != null) {
//                                    CustomerMenuFragment.dessertListAdapter.notifyDataSetChanged();
//                                }
//                                if (CustomerMenuFragment.complimentListAdapter != null) {
//                                    CustomerMenuFragment.complimentListAdapter.notifyDataSetChanged();
//                                }
//                                showSnackBar(userCartEntry.mealItem.name);
//                            }
//                        });
//                        tuesdayAdapter.notifyDataSetChanged();
//                    }
//                };
//
//                ItemTouchHelper tuesdayItemTouchHelper = new ItemTouchHelper(tuesdaySwipeHandler);
//
//                tuesdayItemTouchHelper.attachToRecyclerView(mTuesdayMealsList);
//
//                final FirebaseRecyclerAdapter<UserCartEntry, CartDayListItemHolder> wednesdayAdapter =
//                        new FirebaseRecyclerAdapter<UserCartEntry, CartDayListItemHolder>(
//                                UserCartEntry.class,
//                                R.layout.cart_layout_meal_list_item_layout,
//                                CartDayListItemHolder.class,
//                                FirebaseDatabase.getInstance().getReference()
//                                        .child("Users")
//                                        .child(user.getUid())
//                                        .child("Cart")
//                                        .child(wednesday)
//                        ) {
//                            @Override
//                            protected void populateViewHolder(CartDayListItemHolder viewHolder, UserCartEntry model, int position) {
//                                cartRefreshLayout.setRefreshing(false);
//                                if(!somethingLoaded) {
//                                    cartLoadingProgress.setVisibility(View.GONE);
//                                    somethingLoaded = true;
//                                }
//
//                                mWednesdayTV.setVisibility(View.VISIBLE);
//                                mWednesdayMealsList.setVisibility(View.VISIBLE);
//
//                                Picasso.with(getActivity()).load(model.mealItem.thumbnailURL).into(viewHolder.mMealThumbnailIV);
//                                viewHolder.mMealNameTV.setText(model.mealItem.name);
//                                //viewHolder.mMealDescTV.setText(model.mealItem.description);
//                                viewHolder.mMealPriceTV.setText("" + model.mealItem.pricePerUnit + " PKR");
//                                viewHolder.mMealQuantityTV.setText("" + model.quantity);
//                            }
//                        };
//                mWednesdayMealsList.setAdapter(wednesdayAdapter);
//
//                ItemTouchHelper.SimpleCallback wednesdaySwipeHandler = new ItemTouchHelper.SimpleCallback(
//                        0, ItemTouchHelper.RIGHT
//                ) {
//                    @Override
//                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                        return false;
//                    }
//
//                    @Override
//                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//                        final UserCartEntry userCartEntry = wednesdayAdapter.getItem(viewHolder.getAdapterPosition());
//                        deletedEntry = userCartEntry;
//                        FirebaseDatabase.getInstance().getReference()
//                                .child("Users")
//                                .child(user.getUid())
//                                .child("Cart")
//                                .child(wednesday)
//                                .child(userCartEntry.mealItem.id)
//                                .setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                if (CustomerMenu.totalTV != null) {
//                                    OrderHandler.calculateTotal();
//                                }
//                                if (CustomerMenuFragment.lunchListAdapter != null) {
//                                    CustomerMenuFragment.lunchListAdapter.notifyDataSetChanged();
//                                }
//                                if (CustomerMenuFragment.dessertListAdapter != null) {
//                                    CustomerMenuFragment.dessertListAdapter.notifyDataSetChanged();
//                                }
//                                if (CustomerMenuFragment.complimentListAdapter != null) {
//                                    CustomerMenuFragment.complimentListAdapter.notifyDataSetChanged();
//                                }
//                                showSnackBar(userCartEntry.mealItem.name);
//                            }
//                        });
//                        wednesdayAdapter.notifyDataSetChanged();
//                    }
//                };
//
//                ItemTouchHelper wednesdayItemTouchHelper = new ItemTouchHelper(wednesdaySwipeHandler);
//
//                wednesdayItemTouchHelper.attachToRecyclerView(mWednesdayMealsList);
//
//
//                final FirebaseRecyclerAdapter<UserCartEntry, CartDayListItemHolder> thursdayAdapter =
//                        new FirebaseRecyclerAdapter<UserCartEntry, CartDayListItemHolder>(
//                                UserCartEntry.class,
//                                R.layout.cart_layout_meal_list_item_layout,
//                                CartDayListItemHolder.class,
//                                FirebaseDatabase.getInstance().getReference()
//                                        .child("Users")
//                                        .child(user.getUid())
//                                        .child("Cart")
//                                        .child(thursday)
//                        ) {
//                            @Override
//                            protected void populateViewHolder(CartDayListItemHolder viewHolder, UserCartEntry model, int position) {
//                                cartRefreshLayout.setRefreshing(false);
//                                if(!somethingLoaded) {
//                                    cartLoadingProgress.setVisibility(View.GONE);
//                                    somethingLoaded = true;
//                                }
//
//                                mThursdayTV.setVisibility(View.VISIBLE);
//                                mThursdayMealsList.setVisibility(View.VISIBLE);
//
//                                Picasso.with(getActivity()).load(model.mealItem.thumbnailURL).into(viewHolder.mMealThumbnailIV);
//                                viewHolder.mMealNameTV.setText(model.mealItem.name);
//                                //viewHolder.mMealDescTV.setText(model.mealItem.description);
//                                viewHolder.mMealPriceTV.setText("" + model.mealItem.pricePerUnit + " PKR");
//                                viewHolder.mMealQuantityTV.setText("" + model.quantity);
//                            }
//                        };
//                mThursdayMealsList.setAdapter(thursdayAdapter);
//
//                ItemTouchHelper.SimpleCallback thursdaySwipeHandler = new ItemTouchHelper.SimpleCallback(
//                        0, ItemTouchHelper.RIGHT
//                ) {
//                    @Override
//                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                        return false;
//                    }
//
//                    @Override
//                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//                        final UserCartEntry userCartEntry = thursdayAdapter.getItem(viewHolder.getAdapterPosition());
//                        deletedEntry = userCartEntry;
//                        FirebaseDatabase.getInstance().getReference()
//                                .child("Users")
//                                .child(user.getUid())
//                                .child("Cart")
//                                .child(thursday)
//                                .child(userCartEntry.mealItem.id)
//                                .setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                if (CustomerMenu.totalTV != null) {
//                                    OrderHandler.calculateTotal();
//                                }
//                                if (CustomerMenuFragment.lunchListAdapter != null) {
//                                    CustomerMenuFragment.lunchListAdapter.notifyDataSetChanged();
//                                }
//                                if (CustomerMenuFragment.dessertListAdapter != null) {
//                                    CustomerMenuFragment.dessertListAdapter.notifyDataSetChanged();
//                                }
//                                if (CustomerMenuFragment.complimentListAdapter != null) {
//                                    CustomerMenuFragment.complimentListAdapter.notifyDataSetChanged();
//                                }
//                                showSnackBar(userCartEntry.mealItem.name);
//                            }
//                        });
//                        thursdayAdapter.notifyDataSetChanged();
//                    }
//                };
//
//                ItemTouchHelper thursdayItemTouchHelper = new ItemTouchHelper(thursdaySwipeHandler);
//
//                thursdayItemTouchHelper.attachToRecyclerView(mThursdayMealsList);
//
//                final FirebaseRecyclerAdapter<UserCartEntry, CartDayListItemHolder> fridayAdapter =
//                        new FirebaseRecyclerAdapter<UserCartEntry, CartDayListItemHolder>(
//                                UserCartEntry.class,
//                                R.layout.cart_layout_meal_list_item_layout,
//                                CartDayListItemHolder.class,
//                                FirebaseDatabase.getInstance().getReference()
//                                        .child("Users")
//                                        .child(user.getUid())
//                                        .child("Cart")
//                                        .child(friday)
//                        ) {
//                            @Override
//                            protected void populateViewHolder(CartDayListItemHolder viewHolder, UserCartEntry model, int position) {
//                                cartRefreshLayout.setRefreshing(false);
//                                if(!somethingLoaded) {
//                                    cartLoadingProgress.setVisibility(View.GONE);
//                                    somethingLoaded = true;
//                                }
//
//                                mFridayTV.setVisibility(View.VISIBLE);
//                                mFridayMealsList.setVisibility(View.VISIBLE);
//
//                                Picasso.with(getActivity()).load(model.mealItem.thumbnailURL).into(viewHolder.mMealThumbnailIV);
//                                viewHolder.mMealNameTV.setText(model.mealItem.name);
//                                //viewHolder.mMealDescTV.setText(model.mealItem.description);
//                                viewHolder.mMealPriceTV.setText("" + model.mealItem.pricePerUnit + " PKR");
//                                viewHolder.mMealQuantityTV.setText("" + model.quantity);
//                            }
//                        };
//                mFridayMealsList.setAdapter(fridayAdapter);
//
//                ItemTouchHelper.SimpleCallback fridaySwipeHandler = new ItemTouchHelper.SimpleCallback(
//                        0, ItemTouchHelper.RIGHT
//                ) {
//                    @Override
//                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                        return false;
//                    }
//
//                    @Override
//                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//                        final UserCartEntry userCartEntry = fridayAdapter.getItem(viewHolder.getAdapterPosition());
//                        deletedEntry = userCartEntry;
//                        FirebaseDatabase.getInstance().getReference()
//                                .child("Users")
//                                .child(user.getUid())
//                                .child("Cart")
//                                .child(friday)
//                                .child(userCartEntry.mealItem.id)
//                                .setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                if (CustomerMenu.totalTV != null) {
//                                    OrderHandler.calculateTotal();
//                                }
//                                if (CustomerMenuFragment.lunchListAdapter != null) {
//                                    CustomerMenuFragment.lunchListAdapter.notifyDataSetChanged();
//                                }
//                                if (CustomerMenuFragment.dessertListAdapter != null) {
//                                    CustomerMenuFragment.dessertListAdapter.notifyDataSetChanged();
//                                }
//                                if (CustomerMenuFragment.complimentListAdapter != null) {
//                                    CustomerMenuFragment.complimentListAdapter.notifyDataSetChanged();
//                                }
//                                showSnackBar(userCartEntry.mealItem.name);
//                            }
//                        });
//                        fridayAdapter.notifyDataSetChanged();
//                    }
//                };
//
//                ItemTouchHelper fridayItemTouchHelper = new ItemTouchHelper(fridaySwipeHandler);
//
//                fridayItemTouchHelper.attachToRecyclerView(mFridayMealsList);
//
//
//                final FirebaseRecyclerAdapter<UserCartEntry, CartDayListItemHolder> saturdayAdapter =
//                        new FirebaseRecyclerAdapter<UserCartEntry, CartDayListItemHolder>(
//                                UserCartEntry.class,
//                                R.layout.cart_layout_meal_list_item_layout,
//                                CartDayListItemHolder.class,
//                                FirebaseDatabase.getInstance().getReference()
//                                        .child("Users")
//                                        .child(user.getUid())
//                                        .child("Cart")
//                                        .child(saturday)
//                        ) {
//                            @Override
//                            protected void populateViewHolder(CartDayListItemHolder viewHolder, UserCartEntry model, int position) {
//                                cartRefreshLayout.setRefreshing(false);
//                                if(!somethingLoaded) {
//                                    cartLoadingProgress.setVisibility(View.GONE);
//                                    somethingLoaded = true;
//                                }
//
//                                mSaturdayTV.setVisibility(View.VISIBLE);
//                                mSaturdayMealsList.setVisibility(View.VISIBLE);
//
//                                Picasso.with(getActivity()).load(model.mealItem.thumbnailURL).into(viewHolder.mMealThumbnailIV);
//                                viewHolder.mMealNameTV.setText(model.mealItem.name);
//                                //viewHolder.mMealDescTV.setText(model.mealItem.description);
//                                viewHolder.mMealPriceTV.setText("" + model.mealItem.pricePerUnit + " PKR");
//                                viewHolder.mMealQuantityTV.setText("" + model.quantity);
//                            }
//                        };
//                mSaturdayMealsList.setAdapter(saturdayAdapter);
//
//                ItemTouchHelper.SimpleCallback saturdaySwipeHandler = new ItemTouchHelper.SimpleCallback(
//                        0, ItemTouchHelper.RIGHT
//                ) {
//                    @Override
//                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                        return false;
//                    }
//
//                    @Override
//                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//                        final UserCartEntry userCartEntry = saturdayAdapter.getItem(viewHolder.getAdapterPosition());
//                        deletedEntry = userCartEntry;
//                        FirebaseDatabase.getInstance().getReference()
//                                .child("Users")
//                                .child(user.getUid())
//                                .child("Cart")
//                                .child(saturday)
//                                .child(userCartEntry.mealItem.id)
//                                .setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                if (CustomerMenu.totalTV != null) {
//                                    OrderHandler.calculateTotal();
//                                }
//                                if (CustomerMenuFragment.lunchListAdapter != null) {
//                                    CustomerMenuFragment.lunchListAdapter.notifyDataSetChanged();
//                                }
//                                if (CustomerMenuFragment.dessertListAdapter != null) {
//                                    CustomerMenuFragment.dessertListAdapter.notifyDataSetChanged();
//                                }
//                                if (CustomerMenuFragment.complimentListAdapter != null) {
//                                    CustomerMenuFragment.complimentListAdapter.notifyDataSetChanged();
//                                }
//                                showSnackBar(userCartEntry.mealItem.name);
//                            }
//                        });
//                        saturdayAdapter.notifyDataSetChanged();
//                    }
//                };
//
//                ItemTouchHelper saturdayItemTouchHelper = new ItemTouchHelper(saturdaySwipeHandler);
//
//                saturdayItemTouchHelper.attachToRecyclerView(mSaturdayMealsList);
//
//                final FirebaseRecyclerAdapter<UserCartEntry, CartDayListItemHolder> sundayAdapter =
//                        new FirebaseRecyclerAdapter<UserCartEntry, CartDayListItemHolder>(
//                                UserCartEntry.class,
//                                R.layout.cart_layout_meal_list_item_layout,
//                                CartDayListItemHolder.class,
//                                FirebaseDatabase.getInstance().getReference()
//                                        .child("Users")
//                                        .child(user.getUid())
//                                        .child("Cart")
//                                        .child(sunday)
//                        ) {
//                            @Override
//                            protected void populateViewHolder(CartDayListItemHolder viewHolder, UserCartEntry model, int position) {
//                                cartRefreshLayout.setRefreshing(false);
//                                if(!somethingLoaded) {
//                                    cartLoadingProgress.setVisibility(View.GONE);
//                                    somethingLoaded = true;
//                                }
//
//                                mSundayTV.setVisibility(View.VISIBLE);
//                                mSundayMealsList.setVisibility(View.VISIBLE);
//
//                                Picasso.with(getActivity()).load(model.mealItem.thumbnailURL).into(viewHolder.mMealThumbnailIV);
//                                viewHolder.mMealNameTV.setText(model.mealItem.name);
//                                //viewHolder.mMealDescTV.setText(model.mealItem.description);
//                                viewHolder.mMealPriceTV.setText("" + model.mealItem.pricePerUnit + " PKR");
//                                viewHolder.mMealQuantityTV.setText("" + model.quantity);
//                            }
//                        };
//                mSundayMealsList.setAdapter(sundayAdapter);
//
//                ItemTouchHelper.SimpleCallback sundaySwipeHandler = new ItemTouchHelper.SimpleCallback(
//                        0, ItemTouchHelper.RIGHT
//                ) {
//                    @Override
//                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                        return false;
//                    }
//
//                    @Override
//                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//                        final UserCartEntry userCartEntry = sundayAdapter.getItem(viewHolder.getAdapterPosition());
//                        deletedEntry = userCartEntry;
//                        FirebaseDatabase.getInstance().getReference()
//                                .child("Users")
//                                .child(user.getUid())
//                                .child("Cart")
//                                .child(sunday)
//                                .child(userCartEntry.mealItem.id)
//                                .setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                if (CustomerMenu.totalTV != null) {
//                                    OrderHandler.calculateTotal();
//                                }
//                                if (CustomerMenuFragment.lunchListAdapter != null) {
//                                    CustomerMenuFragment.lunchListAdapter.notifyDataSetChanged();
//                                }
//                                if (CustomerMenuFragment.dessertListAdapter != null) {
//                                    CustomerMenuFragment.dessertListAdapter.notifyDataSetChanged();
//                                }
//                                if (CustomerMenuFragment.complimentListAdapter != null) {
//                                    CustomerMenuFragment.complimentListAdapter.notifyDataSetChanged();
//                                }
//                                showSnackBar(userCartEntry.mealItem.name);
//                            }
//                        });
//                        sundayAdapter.notifyDataSetChanged();
//                    }
//                };
//
//                ItemTouchHelper sundayItemTouchHelper = new ItemTouchHelper(sundaySwipeHandler);
//
//                sundayItemTouchHelper.attachToRecyclerView(mSundayMealsList);
//
//                cartRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//                    @Override
//                    public void onRefresh() {
//                        mondayAdapter.notifyDataSetChanged();
//                        tuesdayAdapter.notifyDataSetChanged();
//                        wednesdayAdapter.notifyDataSetChanged();
//                        thursdayAdapter.notifyDataSetChanged();
//                        fridayAdapter.notifyDataSetChanged();
//                        saturdayAdapter.notifyDataSetChanged();
//                        sundayAdapter.notifyDataSetChanged();
//                    }
//                });
//            }break;
//            default:{
//                cartLoadingProgress.setVisibility(View.GONE);
//            }
//        }
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    void bindViews(View view){
//        mMondayTV = (TextView) view.findViewById(R.id.monday_tv);
//        mMondayMealsList = (RecyclerView) view.findViewById(R.id.monday_meals_list);
//
//        mTuesdayTV = (TextView) view.findViewById(R.id.tuesday_tv);
//        mTuesdayMealsList = (RecyclerView) view.findViewById(R.id.tuesday_meals_list);
//
//        mWednesdayTV = (TextView) view.findViewById(R.id.wednesday_tv);
//        mWednesdayMealsList = (RecyclerView) view.findViewById(R.id.wednesday_meals_list);
//
//        mThursdayTV = (TextView) view.findViewById(R.id.thursaday_tv);
//        mThursdayMealsList = (RecyclerView) view.findViewById(R.id.thursday_meals_list);
//
//        mFridayTV = (TextView) view.findViewById(R.id.friday_tv);
//        mFridayMealsList = (RecyclerView) view.findViewById(R.id.friday_meals_list);
//
//        mSaturdayTV = (TextView) view.findViewById(R.id.saturday_tv);
//        mSaturdayMealsList = (RecyclerView) view.findViewById(R.id.saturday_meals_list);
//
//        mSundayTV = (TextView) view.findViewById(R.id.sunday_tv);
//        mSundayMealsList = (RecyclerView) view.findViewById(R.id.sunday_meals_list);
        cartRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_meals_grid);
        cartLoadingProgress = (ProgressBar) view.findViewById(R.id.progress_meals_grid);

        emptyCartLayout = (LinearLayout) view.findViewById(R.id.empty_list_layout);
        emptyCartImageView = (ImageView) view.findViewById(R.id.empty_list_image_view);
        emptyCartTextView = (TextView) view.findViewById(R.id.empty_list_text_view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public String getFullDayName(String name){
        if(name.equals("MON")){
            return "Monday";
        }else if(name.equals("TUE")){
            return "Tuesday";
        }else if(name.equals("WED")){
            return "Wednesday";
        }else if(name.equals("THU")){
            return "Thursday";
        }else if(name.equals("FRI")){
            return "Friday";
        }else if(name.equals("SAT")){
            return "Saturday";
        }else if(name.equals("SUN")){
            return "Sunday";
        }else{
            return null;
        }
    }

    @SuppressLint("ValidFragment")
    public static class DeliveryPointAddressDialog extends DialogFragment {
        List<MealItem> meals;
        List<Long> quantities;
        List<String> times;
        List<String> dates;
        CustomerCartRootListAdapter adapter;

        public DeliveryPointAddressDialog(){

        }

        public DeliveryPointAddressDialog(List<MealItem> meals,
                List<Long> quantities,
                List<String> times,
                List<String> dates,CustomerCartRootListAdapter adapter
        ){
            this.meals = meals;
            this.quantities = quantities;
            this.times = times;
            this.dates = dates;
            this.adapter = adapter;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            LayoutInflater inflater = getActivity().getLayoutInflater();

            View dialogView = inflater.inflate(R.layout.delivery_point_dialog_layout,null);

            final EditText loactionText = (EditText) dialogView.findViewById(R.id.delivery_point_address);

            builder.setView(dialogView).setTitle("Delivery Point")
                    .setPositiveButton("Order", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // FIRE ZE MISSILES!
                            if(!TextUtils.isEmpty(loactionText.toString())) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null) {
                                    DatabaseReference orderUserPushRef =
                                            FirebaseDatabase.getInstance().getReference().
                                                    child("Users").
                                                    child(user.getUid()).
                                                    child("Orders").
                                                    push();

                                    DatabaseReference orderMainListPushRef =
                                            FirebaseDatabase.getInstance().getReference().
                                                    child("Orders").
                                                    push();

                                    Order order = new Order(
                                            orderMainListPushRef.getKey(),
                                            orderUserPushRef.getKey(),
                                            meals,
                                            times,
                                            dates,
                                            quantities,
                                            user.getUid(),
                                            loactionText.getText().toString(),
                                            Constants.ORDER_PENDING
                                    );

                                    orderUserPushRef.setValue(order);
                                    orderMainListPushRef.setValue(order);
                                    FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Cart").setValue(null);

                                    OrderHandler.calculateTotal();

                                    if(CustomerMenuFragment.lunchListAdapter!=null){CustomerMenuFragment.lunchListAdapter.refreshData();}
                                    if(CustomerMenuFragment.dessertListAdapter!=null){CustomerMenuFragment.dessertListAdapter.refreshData();}
                                    if(CustomerMenuFragment.complimentListAdapter!=null){CustomerMenuFragment.complimentListAdapter.refreshData();}

                                    adapter.loadData();
                                    Toast.makeText(getActivity(),"Order made successfully :)",Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(getActivity(),"Order NOT placed please select a delivery point. :(",Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
}
