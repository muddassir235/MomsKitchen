package com.momskitchen.momskitchen.backend;

import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.momskitchen.momskitchen.Customer.ActivitiesPlusFragments.CustomerMenu;
import com.momskitchen.momskitchen.Customer.MealAdapter;
import com.momskitchen.momskitchen.model.MealItem;
import com.momskitchen.momskitchen.model.Order;
import com.momskitchen.momskitchen.model.UserCartEntry;

import java.util.HashMap;
import java.util.List;

public class OrderHandler{

	private static final String TAG = "OrderHandler: ";

	public static HashMap<String,Boolean> inProcess = new HashMap<>();
	public static HashMap<String,Integer> deltas = new HashMap<>();

	public static void incrementQuantityInCart(String date, final MealAdapter adapter, int currentQuantity, final List<MealItem> mealItems, final int position){
		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
		if(user!=null){
			if(currentQuantity!=0){
				inProcess.put(mealItems.get(position).id,true);
				FirebaseDatabase.getInstance().
						getReference().
						child("Users").
						child(user.getUid()).
						child("Cart").
						child(date).
						child(mealItems.get(position).id).
						child("quantity").
						setValue(currentQuantity+1).addOnSuccessListener(new OnSuccessListener<Void>() {
					@Override
					public void onSuccess(Void aVoid) {
						inProcess.put(mealItems.get(position).id,false);
						calculateTotal();
						adapter.refreshData();
					}
				});

			}else{
				inProcess.put(mealItems.get(position).id,true);
				UserCartEntry userCartEntry = new UserCartEntry(user.getUid(),mealItems.get(position),date,currentQuantity+1);
				FirebaseDatabase.getInstance().getReference().
						child("Users").
						child(user.getUid()).
						child("Cart").
						child(date).
						child(mealItems.get(position).id).setValue(userCartEntry).addOnSuccessListener(new OnSuccessListener<Void>() {
					@Override
					public void onSuccess(Void aVoid) {
						inProcess.put(mealItems.get(position).id,false);
						calculateTotal();
						adapter.refreshData();
					}
				});
			}
		}else{
			try {
				throw new Exception("the user is not signed in");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void decrementQuantityInCart(String date, final MealAdapter adapter, int currentQuantity, final List<MealItem> mealItems, final int position){
		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
		if(user!=null){
			if(currentQuantity>1){
				inProcess.put(mealItems.get(position).id,true);
				FirebaseDatabase.getInstance().
						getReference().
						child("Users").
						child(user.getUid()).
						child("Cart").
						child(date).
						child(mealItems.get(position).id).
						child("quantity").
						setValue(currentQuantity-1).addOnSuccessListener(new OnSuccessListener<Void>() {
					@Override
					public void onSuccess(Void aVoid) {
						calculateTotal();
						inProcess.put(mealItems.get(position).id,false);
						adapter.refreshData();
					}
				});
			}else{
				inProcess.put(mealItems.get(position).id,true);
				FirebaseDatabase.getInstance().
						getReference().
						child("Users").
						child(user.getUid()).
						child("Cart").
						child(date).
						child(mealItems.get(position).id).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
					@Override
					public void onSuccess(Void aVoid) {
						calculateTotal();
						inProcess.put(mealItems.get(position).id,false);
						adapter.refreshData();
					}
				});
			}
		}else{
			try {
				throw new Exception("the user in not logged in");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void getQuantityOfMealItem(String mealId,String date, final MealAdapter.ViewHolder viewHolder){
		FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
		if(firebaseUser!=null) {
			FirebaseDatabase.getInstance().
					getReference().
					child("Users").
					child(firebaseUser.getUid()).
					child("Cart").
					child(date).
					child(mealId).
					child("quantity").addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					if(dataSnapshot.exists()){
						viewHolder.mQuantityTV.setText(""+dataSnapshot.getValue());
						viewHolder.mCurrentQuantity = Integer.valueOf(String.valueOf((Long)dataSnapshot.getValue()));
					}else{
						viewHolder.mQuantityTV.setText("0");
						viewHolder.mCurrentQuantity = 0;
					}
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {
					viewHolder.mCurrentQuantity=Integer.valueOf(viewHolder.mQuantityTV.getText().toString());
				}
			});
		}else{
			try {
				throw new Exception("The user is not logged in");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void calculateTotal(){
		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
		if(user!=null){
			FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Cart").addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {

					if(dataSnapshot.exists()){
						Long total= 0L;
						if(dataSnapshot.hasChildren()){
							for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
								if(dataSnapshot1.hasChildren()){
									for(DataSnapshot dataSnapshot2:dataSnapshot1.getChildren()){
										Log.v(TAG," "+dataSnapshot2.getValue());
										UserCartEntry cartEntry = dataSnapshot2.getValue(UserCartEntry.class);
										total+=(Long.valueOf(String.valueOf(cartEntry.quantity))*cartEntry.mealItem.pricePerUnit);
									}
								}
							}
						}
						if(CustomerMenu.totalLayout!=null) {CustomerMenu.totalLayout.setVisibility(View.VISIBLE);}
                        if(CustomerMenu.totalTV!=null){CustomerMenu.totalTV.setText("" + total);}

					}else{
                        if(CustomerMenu.totalLayout!=null){CustomerMenu.totalLayout.setVisibility(View.GONE);}
					}
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {

				}
			});
		}
	}

	public static void setupWeeksCart(final List<String> currentWeekDates){
		final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
		if(user!=null){
			FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Cart").addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {

					if(dataSnapshot.exists()){
						Long total= 0L;
						if(dataSnapshot.hasChildren()){
							for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
								if(!currentWeekDates.contains(dataSnapshot1.getKey())){
									FirebaseDatabase.getInstance().getReference().
											child("Users").
											child(user.getUid()).
											child("Cart")
											.child(dataSnapshot1.getKey()).setValue(null);
								}
							}
						}
						CustomerMenu.totalLayout.setVisibility(View.VISIBLE);
						CustomerMenu.totalTV.setText(""+total);
					}else{
						CustomerMenu.totalLayout.setVisibility(View.GONE);
					}
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {

				}
			});
		}
	}

	public static void placeOrder(){
		// move items from current cart to orders
	}

	public static void moveToPackaged(Order... orders){
		// move items from order into packaged
	}

	public static void moveToReceived(Order... orders){
		// move item from packaged to recieved
	}

}