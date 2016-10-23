package com.momskitchen.momskitchen.Customer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.momskitchen.momskitchen.Customer.ActivitiesPlusFragments.CustomerMenuFragment;
import com.momskitchen.momskitchen.R;
import com.momskitchen.momskitchen.backend.OrderHandler;
import com.momskitchen.momskitchen.model.MealItem;
import com.momskitchen.momskitchen.model.Order;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 9/27/2016.
 */
public class MealAdapter extends RecyclerView.Adapter<MealAdapter.ViewHolder> {

    public static final int TYPE_LUNCH = 0;
    public static final int TYPE_DESSERT = 1;
    public static final int TYPE_COMPLIMENT = 2;

    DatabaseReference ref;
    private List<MealItem> mMealList;
    Context mContext;
    static String currentDate;
    int type;

    public MealAdapter(Context context, String currDate, DatabaseReference ref, final int type) {
        this.ref = ref;
        mMealList = new ArrayList<>();
        this.mContext = context;
        currentDate = currDate;
        this.type = type;
        this.ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMealList = new ArrayList<MealItem>();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                    MealItem mealItem = dataSnapshot1.getValue(MealItem.class);
                    if (mealItem.dates.contains(currentDate)) {
                        mMealList.add(mealItem);
                        if(type == TYPE_LUNCH) {
                            CustomerMenuFragment.anyLunch = true;
                        }else if(type == TYPE_DESSERT){
                            CustomerMenuFragment.anyDessert = true;
                        }else if(type == TYPE_COMPLIMENT){
                            CustomerMenuFragment.anyCompliment = true;
                        }
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public MealAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_customer_menu_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mView.setVisibility(View.VISIBLE);
        holder.mMealNameTV.setText(getItem(position).name);
        holder.mMealPriceTV.setText(""+getItem(position).pricePerUnit+" PKR");
        Picasso.with(mContext).load(getItem(position).thumbnailURL).into(holder.mMealThumbnailIV);
        holder.mMealDescriptionTV.setText(getItem(position).description);
        OrderHandler.getQuantityOfMealItem(getItem(position).id,currentDate,holder);
        final MealAdapter mealAdapter = this;
        holder.mIncrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean inProcess = false;
                if(OrderHandler.inProcess.get(mMealList.get(position).id)!=null){
                    inProcess = OrderHandler.inProcess.get(mMealList.get(position).id);
                }
                if(!inProcess) {
                    holder.mQuantityTV.setText("" + (holder.mCurrentQuantity + 1));
                    OrderHandler.incrementQuantityInCart(currentDate, mealAdapter, holder.mCurrentQuantity, mMealList, position);
                }
            }
        });
        holder.mDecrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean inProcess = false;
                if(OrderHandler.inProcess.get(mMealList.get(position).id)!=null){
                    inProcess = OrderHandler.inProcess.get(mMealList.get(position).id);
                }
                if(!inProcess) {
                    if (holder.mCurrentQuantity >= 1) {
                        holder.mQuantityTV.setText("" + (holder.mCurrentQuantity - 1));
                    }
                    OrderHandler.decrementQuantityInCart(currentDate, mealAdapter, holder.mCurrentQuantity, mMealList, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMealList.size();
    }

    public MealItem getItem(int position) {
        return mMealList.get(position);
    }

    public void addAll(List<MealItem> mealList) {
        mMealList.addAll(mealList);
        notifyDataSetChanged();
    }

    public int getSize(){
        return mMealList.size();
    }

    public void removeFirstItems(int count) {
        for (int i=0; i<count; i++) mMealList.remove(0);
        notifyDataSetChanged();
    }

    public void clear() {
        mMealList.clear();
        notifyDataSetChanged();
    }

    public void refreshData(){
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMealList = new ArrayList<MealItem>();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                    MealItem mealItem = dataSnapshot1.getValue(MealItem.class);
                    if (mealItem.dates.contains(currentDate)) {
                        mMealList.add(mealItem);
                        if(type == TYPE_LUNCH) {
                            CustomerMenuFragment.anyLunch = true;
                        }else if(type == TYPE_DESSERT){
                            CustomerMenuFragment.anyDessert = true;
                        }else if(type == TYPE_COMPLIMENT){
                            CustomerMenuFragment.anyCompliment = true;
                        }
                    }
                }

                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public final ImageView mMealThumbnailIV;
        public final TextView mMealNameTV;
        public final TextView mMealDescriptionTV;
        public final TextView mMealPriceTV;
        public final ImageButton mIncrementButton;
        public final ImageButton mDecrementButton;
        public final TextView mQuantityTV;
        public int mCurrentQuantity;
        public ViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;
            this.mMealThumbnailIV = (ImageView) mView.findViewById(R.id.meal_thumbnail_image_view);
            this.mMealNameTV = (TextView) mView.findViewById(R.id.meal_name_text_view);
            this.mMealDescriptionTV = (TextView) mView.findViewById(R.id.meal_description_text_view);
            this.mMealPriceTV = (TextView) mView.findViewById(R.id.meal_price_text_view);
            this.mIncrementButton = (ImageButton) mView.findViewById(R.id.increment_quantity_button);
            this.mDecrementButton = (ImageButton) mView.findViewById(R.id.decrement_quantity_button);
            this.mQuantityTV = (TextView) mView.findViewById(R.id.quantity_text_view);
            mCurrentQuantity = Integer.valueOf(this.mQuantityTV.getText().toString());
        }
    }
}