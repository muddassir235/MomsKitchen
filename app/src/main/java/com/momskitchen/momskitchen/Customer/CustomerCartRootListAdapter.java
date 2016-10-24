package com.momskitchen.momskitchen.Customer;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.api.model.StringList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.momskitchen.momskitchen.Customer.ActivitiesPlusFragments.CustomerMenu;
import com.momskitchen.momskitchen.Customer.ActivitiesPlusFragments.CustomerMenuFragment;
import com.momskitchen.momskitchen.R;
import com.momskitchen.momskitchen.backend.MenuCreator;
import com.momskitchen.momskitchen.backend.OrderHandler;
import com.momskitchen.momskitchen.model.MealItem;
import com.momskitchen.momskitchen.model.UserCartEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hp on 10/20/2016.
 */

public class CustomerCartRootListAdapter extends RecyclerView.Adapter<CartRootRecyclerHolder> {
    private static final String TAG = CustomerCartRootListAdapter.class.getName()+": ";

    DataLoadedListener dataLoadedListener;
    Activity activity;

    public interface DataLoadedListener{
        public void dataLoaded(int size);
    }

    public void setDataLoadedListener(DataLoadedListener dataLoadedListener){
        this.dataLoadedListener = dataLoadedListener;
    }

    // Reference to the Cart
    private DatabaseReference reference;

    // List of the days of the week
    private List<String> days;

    // List of the dates for which meal items have been selected by the user
    public List<String> dates;

    // Hashmap of Lists of meal items for each day
    public HashMap<String,List<MealItem>> mealItems;

    // Hashmap of the time for each mealItem respectively for a day
    public HashMap<String,List<String>> mealItemTimes;

    // Hashmap of the quantity of each meal item repectively for a day
    public HashMap<String,List<Long>> mealItemQuantities;

    // Entry that has been deleted just now
    UserCartEntry deletedEntry;

    public CustomerCartRootListAdapter(Activity activity) {
        super();
        mealItems = new HashMap<>();
        this.activity = activity;
        loadDataInitailly();
    }

    @Override
    public CartRootRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_list_root_item_layout, parent, false);
        CartRootRecyclerHolder holder = new CartRootRecyclerHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(CartRootRecyclerHolder holder, int position) {
        if(position == (dates.size()-1)){
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.mView.getLayoutParams();
            params.bottomMargin=((int)(holder.mView.getResources().getDisplayMetrics().density*16));
        }
        final String finalDate = dates.get(position);
        final List<MealItem> mealItemList = mealItems.get(dates.get(position));

        if(mealItemList.size() !=0) {
            String day = getFullDayName(days.get(position));
            holder.mDayTextView.setText(day);
            CustomerCartDayMealsList customerCartDayMealsList =
                    new CustomerCartDayMealsList(
                            mealItems.get(dates.get(position)),
                            mealItemQuantities.get(dates.get(position)),
                            finalDate,
                            mealItemTimes.get(dates.get(position))
                    );
            holder.mMealListRecyclerView.setLayoutManager(new LinearLayoutManager(holder.mView.getContext()));
            holder.mMealListRecyclerView.setAdapter(customerCartDayMealsList);

            final List<Long> finalQuantities = mealItemQuantities.get(dates.get(position));
            ItemTouchHelper.SimpleCallback swipeHandler = new ItemTouchHelper.SimpleCallback(
                    0, ItemTouchHelper.RIGHT
            ) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    deletedEntry = new UserCartEntry(
                            FirebaseAuth.getInstance().getCurrentUser().getUid(),
                            mealItemList.get(viewHolder.getAdapterPosition()), finalDate,
                            Integer.valueOf(String.valueOf(finalQuantities.get(viewHolder.getAdapterPosition())))
                    );
                    FirebaseDatabase.getInstance().getReference()
                            .child("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("Cart")
                            .child(finalDate)
                            .child(deletedEntry.mealItem.id)
                            .setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (CustomerMenu.totalTV != null) {
                                OrderHandler.calculateTotal();
                            }
                            if (CustomerMenuFragment.lunchListAdapter != null) {
                                CustomerMenuFragment.lunchListAdapter.notifyDataSetChanged();
                            }
                            if (CustomerMenuFragment.dessertListAdapter != null) {
                                CustomerMenuFragment.dessertListAdapter.notifyDataSetChanged();
                            }
                            if (CustomerMenuFragment.complimentListAdapter != null) {
                                CustomerMenuFragment.complimentListAdapter.notifyDataSetChanged();
                            }
                            showSnackBar(deletedEntry.mealItem.name);
                        }
                    });
                }
            };

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeHandler);
            itemTouchHelper.attachToRecyclerView(holder.mMealListRecyclerView);
        }
    }

    String getFullDayName(String day){
        if(day.equals("MON")){
            return "Monday";
        }else if(day.equals("TUE")){
            return "Tuesday";
        }else if(day.equals("WED")){
            return "Wednesday";
        }else if(day.equals("THU")){
            return "Thursday";
        }else if(day.equals("FRI")){
            return "Friday";
        }else if(day.equals("SAT")){
            return "Saturday";
        }else if(day.equals("SUN")){
            return "Sunday";
        }else{
            return "invalid";
        }
    }

    @Override
    public int getItemCount() {
        return mealItems.size();
    }

    public void loadDataInitailly(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            // setting reference to the Cart of the user
            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Cart");

            // querying for data from Firebase
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    days = new ArrayList<>();
                    dates = new ArrayList<>();
                    mealItems = new HashMap<>();
                    mealItemQuantities = new HashMap<>();
                    mealItemTimes = new HashMap<>();

                    if(dataSnapshot.exists()){

                        // initializing array lists

                        // for each day in the cart
                        for(DataSnapshot dataSnapshotB:dataSnapshot.getChildren()){

                            // add this date to list of dates
                            dates.add(dataSnapshotB.getKey());

                            days.add(MenuCreator.getInstance().getDayFromDate(dataSnapshotB.getKey()));
                            // create list of meal items for this day
                            List<MealItem> dayMealItems = new ArrayList<MealItem>();

                            // create respective list of meal quantities for each meal item
                            List<Long> dayMealItemQuantities = new ArrayList<Long>();

                            // create a repective list of time for each meal item of the day
                            List<String> dayMealTimes = new ArrayList<String>();

                            // for each meal item
                            for(DataSnapshot dataSnapshotC:dataSnapshotB.getChildren()){

                                // current meal item
                                MealItem mealItem = dataSnapshotC.child("mealItem").getValue(MealItem.class);

                                // quantity of current meal item
                                Long quantity = (Long) dataSnapshotC.child("quantity").getValue();

                                // time of delivery for current meal
                                String time = (String) dataSnapshotC.child("time").getValue();

                                if(time!=null){
                                    dayMealTimes.add(time);
                                }else{
                                    dayMealTimes.add("N/A");
                                }

                                // add meal item to the list of meal items for the day
                                dayMealItems.add(mealItem);

                                // add the quantity of meals to the list of quantities for the day
                                dayMealItemQuantities.add(quantity);

                            }

                            // add the list of meal items to a key value pair for that day
                            mealItems.put(dataSnapshotB.getKey(),dayMealItems);

                            // add the list of meal quantities to a key value pair for that day
                            mealItemQuantities.put(dataSnapshotB.getKey(),dayMealItemQuantities);

                            // add the list of meal times to kay value pair for that day
                            mealItemTimes.put(dataSnapshotB.getKey(),dayMealTimes);
                        }
                        notifyDataSetChanged();
                    }
                    if(dataLoadedListener!=null){

                        // data has been loaded
                        dataLoadedListener.dataLoaded(days.size());

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
            throw new NullPointerException("User in not logged in");
        }
    }

    public void loadData(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            // setting reference to the Cart of the user
            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Cart");

            // initializing array lists
            days = new ArrayList<>();
            dates = new ArrayList<>();
            mealItems = new HashMap<>();
            mealItemQuantities = new HashMap<>();
            mealItemTimes = new HashMap<>();

            // querying for data from Firebase
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        // for each day in the cart
                        for(DataSnapshot dataSnapshotB:dataSnapshot.getChildren()){

                            // add this date to list of dates
                            dates.add(dataSnapshotB.getKey());

                            days.add(MenuCreator.getInstance().getDayFromDate(dataSnapshotB.getKey()));
                            // create list of meal items for this day
                            List<MealItem> dayMealItems = new ArrayList<MealItem>();

                            // create respective list of meal quantities for each meal item
                            List<Long> dayMealItemQuantities = new ArrayList<Long>();

                            // create a repective list of time for each meal item of the day
                            List<String> dayMealTimes = new ArrayList<String>();

                            // for each meal item
                            for(DataSnapshot dataSnapshotC:dataSnapshotB.getChildren()){

                                // current meal item
                                MealItem mealItem = dataSnapshotC.child("mealItem").getValue(MealItem.class);

                                // quantity of current meal item
                                Long quantity = (Long) dataSnapshotC.child("quantity").getValue();

                                // time of delivery for current meal
                                String time = (String) dataSnapshotC.child("time").getValue();

                                if(time!=null){
                                    dayMealTimes.add(time);
                                }else{
                                    dayMealTimes.add("N/A");
                                }

                                // add meal item to the list of meal items for the day
                                dayMealItems.add(mealItem);

                                // add the quantity of meals to the list of quantities for the day
                                dayMealItemQuantities.add(quantity);
                            }

                            // add the list of meal items to a key value pair for that day
                            mealItems.put(dataSnapshotB.getKey(),dayMealItems);

                            // add the list of meal quantities to a key value pair for that day
                            mealItemQuantities.put(dataSnapshotB.getKey(),dayMealItemQuantities);

                            // add the list of meal times to kay value pair for that day
                            mealItemTimes.put(dataSnapshotB.getKey(),dayMealTimes);
                        }
                    }
                    notifyDataSetChanged();
                    if(dataLoadedListener!=null){

                        // data has been loaded
                        dataLoadedListener.dataLoaded(dates.size());

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
            throw new NullPointerException("User in not logged in");
        }
    }

    void showSnackBar(final String name){
        Snackbar snackbar = Snackbar
                .make(activity.findViewById(R.id.shopping_cart_coord_layout), name+" has been deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseDatabase.getInstance().getReference()
                                .child("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("Cart")
                                .child(deletedEntry.date)
                                .child(deletedEntry.mealItem.id).setValue(deletedEntry).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if(CustomerMenu.totalTV!=null){
                                    OrderHandler.calculateTotal();
                                }
                                if(CustomerMenuFragment.lunchListAdapter!=null){
                                    CustomerMenuFragment.lunchListAdapter.notifyDataSetChanged();
                                }
                                if(CustomerMenuFragment.dessertListAdapter!=null){
                                    CustomerMenuFragment.dessertListAdapter.notifyDataSetChanged();
                                }
                                if(CustomerMenuFragment.complimentListAdapter!=null){
                                    CustomerMenuFragment.complimentListAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                });

        snackbar.show();
    }
}
