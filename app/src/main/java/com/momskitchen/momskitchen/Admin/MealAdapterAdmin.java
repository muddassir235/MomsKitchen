package com.momskitchen.momskitchen.Admin;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.momskitchen.momskitchen.R;
import com.momskitchen.momskitchen.model.MealItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class MealAdapterAdmin extends RecyclerView.Adapter<MealAdapterAdmin.ViewHolder> {

    public static final int TYPE_LUNCH = 0;
    public static final int TYPE_DESSERT = 1;
    public static final int TYPE_COMPLIMENT = 2;

    DatabaseReference ref;
    private List<MealItem> mMealList;
    Context mContext;
    static String currentDate;
    int type;

    public MealAdapterAdmin(Context context, String currDate, DatabaseReference ref, final int type) {
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
    public MealAdapterAdmin.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_admin_menu_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mView.setVisibility(View.VISIBLE);
        holder.mMealNameTV.setText(getItem(position).name);
        holder.mMealPriceTV.setText(""+getItem(position).pricePerUnit+" PKR");
        Picasso.with(mContext).load(getItem(position).thumbnailURL).into(holder.mMealThumbnailIV);
        holder.mMealDescriptionTV.setText(getItem(position).description);
    }

    @Override
    public int getItemCount() {
        return mMealList.size();
    }

    public MealItem getItem(int position) {
        return mMealList.get(position);
    }

    public int getSize(){
        return mMealList.size();
    }

    public void addAll(List<MealItem> mealList) {
        mMealList.addAll(mealList);
        notifyDataSetChanged();
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
        public ViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;
            this.mMealThumbnailIV = (ImageView) mView.findViewById(R.id.meal_thumbnail_image_view);
            this.mMealNameTV = (TextView) mView.findViewById(R.id.meal_name_text_view);
            this.mMealDescriptionTV = (TextView) mView.findViewById(R.id.meal_description_text_view);
            this.mMealPriceTV = (TextView) mView.findViewById(R.id.meal_price_text_view);
        }
    }
}