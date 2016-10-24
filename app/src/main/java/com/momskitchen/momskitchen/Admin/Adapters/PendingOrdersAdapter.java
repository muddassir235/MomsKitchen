package com.momskitchen.momskitchen.Admin.Adapters;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.momskitchen.momskitchen.Admin.ViewHolders.OrderViewHolder;
import com.momskitchen.momskitchen.R;
import com.momskitchen.momskitchen.messaging.MyFirebaseMessagingService;
import com.momskitchen.momskitchen.model.MealItem;
import com.momskitchen.momskitchen.model.Order;
import com.momskitchen.momskitchen.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 10/22/2016.
 */

public class PendingOrdersAdapter extends RecyclerView.Adapter<OrderViewHolder> {
    public static final String TAG = PendingOrdersAdapter.class.getName()+": ";
    public static final int TYPE_ADMIN_PENDING_ORDERS_LIST = 0;
    public static final int TYPE_CUSTOMER_PENDING_ORDERS_LIST = 1;

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_PACKAGED = 1;
    public static final int STATUS_RECIEVED = 2;

    int type;
    int status;
    List<Order> pendingOrders;
    List<Order> packagedOrders;
    List<Order> recievedOrders;
    DataLoadedListener dataLoadedListener;

    List<Order> currOrdersList;

    public interface DataLoadedListener{
        public void dataLoaded(int size);
    }
    public PendingOrdersAdapter(int type, int status) {
        super();
        this.type = type;
        this.status = status;
        loadDataInitailly();
    }

    public void setDataLoadedListener(DataLoadedListener loadedListener){
        this.dataLoadedListener = loadedListener;
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pending_order_layout,parent,false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final OrderViewHolder holder, final int position) {

        if(position == (currOrdersList.size()-1)){
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.mView.getLayoutParams();
            params.bottomMargin=((int)(holder.mView.getResources().getDisplayMetrics().density*5));
        }
        holder.mOrder = currOrdersList.get(position);
        int numberOfMeals = 0;
        int totalCost = 0;
        for(int i = 0;i<holder.mOrder.mealItemList.size();i++){
            numberOfMeals+=holder.mOrder.quantities.get(i);
            totalCost+=holder.mOrder.mealItemList.get(i).pricePerUnit*holder.mOrder.quantities.get(i);
        }
        holder.mTotalCostTV.setText(totalCost+" PKR");
        holder.mTotalItemsTV.setText(""+numberOfMeals);
        holder.mDeliveryPointAddressTV.setText(holder.mOrder.deliveryPoint);
        if(holder.mOrder.userId!=null){
            FirebaseDatabase.getInstance().getReference().child("Users").child(holder.mOrder.userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot!=null){
                        User user = dataSnapshot.getValue(User.class);
                        if(type == TYPE_CUSTOMER_PENDING_ORDERS_LIST){
                            holder.mPersonNameTV.setText("Me");
                        }else{
                            holder.mPersonNameTV.setText(user.name);
                        }

                        holder.mPersonPhoneTV.setText(user.phone);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        OrderContentAdapter orderContentAdapter = new OrderContentAdapter(
                holder.mOrder.mealItemList,holder.mOrder.quantities,holder.mOrder.dates,holder.mOrder.times);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(holder.mView.getContext());
        manager.setAutoMeasureEnabled(true);
        holder.mOrderContentListRV.setLayoutManager(manager);
        holder.mOrderContentListRV.setAdapter(orderContentAdapter);
        holder.mExpandListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.mOrderContentListRV.getVisibility()==View.VISIBLE){
                    holder.mOrderContentListRV.setVisibility(View.GONE);
                    holder.mOrderContentSchemaLL.setVisibility(View.GONE);
                    holder.mExpandListIconIV.setRotation(0);
                }else{
                    holder.mOrderContentListRV.setVisibility(View.VISIBLE);
                    holder.mOrderContentSchemaLL.setVisibility(View.VISIBLE);
                    holder.mExpandListIconIV.setRotation(180);
                }
            }
        });
        holder.mExpandListIconIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.mOrderContentListRV.getVisibility()==View.VISIBLE){
                    holder.mOrderContentListRV.setVisibility(View.GONE);
                    holder.mOrderContentSchemaLL.setVisibility(View.GONE);
                    holder.mExpandListIconIV.setRotation(0);
                }else{
                    holder.mOrderContentListRV.setVisibility(View.VISIBLE);
                    holder.mOrderContentSchemaLL.setVisibility(View.VISIBLE);
                    holder.mExpandListIconIV.setRotation(180);
                }
            }
        });

        if(type != TYPE_CUSTOMER_PENDING_ORDERS_LIST && status != STATUS_RECIEVED) {
            ItemTouchHelper.SimpleCallback swipeHandler = new ItemTouchHelper.SimpleCallback(
                    0, ItemTouchHelper.RIGHT
            ) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    switch (status){
                        case STATUS_PENDING:{
                            Order pendingOrder = pendingOrders.get(position);
                            Order packagedOrder = null;

                            MealItem meal = pendingOrder.mealItemList.get(viewHolder.getAdapterPosition());
                            Long quantity = pendingOrder.quantities.get(viewHolder.getAdapterPosition());
                            String date = pendingOrder.dates.get(viewHolder.getAdapterPosition());
                            String time = pendingOrder.times.get(viewHolder.getAdapterPosition());

                            boolean alreadyInPackaged = containsId(packagedOrders, pendingOrder.mainListId);
                            if(!alreadyInPackaged){

                                List<MealItem> newMealsList = new ArrayList<>();
                                List<Long> newQuantitiesList = new ArrayList<>();
                                List<String> newDatesList = new ArrayList<>();
                                List<String> newTimesList = new ArrayList<>();

                                newMealsList.add(meal);
                                newQuantitiesList.add(quantity);
                                newDatesList.add(date);
                                newTimesList.add(time);

                                packagedOrder = new Order(
                                        pendingOrder.mainListId,
                                        pendingOrder.userListId,
                                        newMealsList,
                                        newTimesList,
                                        newDatesList,
                                        newQuantitiesList,
                                        pendingOrder.userId,
                                        pendingOrder.deliveryPoint,
                                        pendingOrder.status
                                );
                            }else{
                                packagedOrder = packagedOrders.get(getIndex(packagedOrders,pendingOrder.mainListId));
                                packagedOrder.mealItemList.add(meal);
                                packagedOrder.quantities.add(quantity);
                                packagedOrder.dates.add(date);
                                packagedOrder.times.add(time);
                            }

                            pendingOrder.mealItemList.remove(viewHolder.getAdapterPosition());
                            pendingOrder.quantities.remove(viewHolder.getAdapterPosition());
                            pendingOrder.dates.remove(viewHolder.getAdapterPosition());
                            pendingOrder.times.remove(viewHolder.getAdapterPosition());

                            DatabaseReference mainListOrderDBR = FirebaseDatabase.getInstance().getReference()
                                    .child("Orders")
                                    .child(pendingOrder.mainListId);

                            DatabaseReference mainListPackgedRef = FirebaseDatabase.getInstance().getReference()
                                    .child("Packaged")
                                    .child(pendingOrder.mainListId);

                            DatabaseReference userListOrderRef = FirebaseDatabase.getInstance().getReference()
                                    .child("Users")
                                    .child(pendingOrder.userId)
                                    .child("Orders")
                                    .child(pendingOrder.userListId);

                            DatabaseReference userListPackagedRef = FirebaseDatabase.getInstance().getReference()
                                    .child("Users")
                                    .child(pendingOrder.userId)
                                    .child("Packaged")
                                    .child(pendingOrder.userListId);

                            if(pendingOrder.mealItemList.size()>0) {
                                mainListOrderDBR.setValue(pendingOrder);
                                userListOrderRef.setValue(pendingOrder);
                            }else {
                                mainListOrderDBR.setValue(null);
                                userListOrderRef.setValue(null);
                            }

                            mainListPackgedRef.setValue(packagedOrder);
                            userListPackagedRef.setValue(packagedOrder);
                        }break;
                        case STATUS_PACKAGED:{
                            Order packagedOrder = packagedOrders.get(position);
                            Order recievedOrder = null;

                            MealItem meal = packagedOrder.mealItemList.get(viewHolder.getAdapterPosition());
                            Long quantity = packagedOrder.quantities.get(viewHolder.getAdapterPosition());
                            String date = packagedOrder.dates.get(viewHolder.getAdapterPosition());
                            String time = packagedOrder.times.get(viewHolder.getAdapterPosition());

                            boolean alreadyInRecieved = containsId(recievedOrders, packagedOrder.mainListId);
                            if(!alreadyInRecieved){

                                List<MealItem> newMealsList = new ArrayList<>();
                                List<Long> newQuantitiesList = new ArrayList<>();
                                List<String> newDatesList = new ArrayList<>();
                                List<String> newTimesList = new ArrayList<>();

                                newMealsList.add(meal);
                                newQuantitiesList.add(quantity);
                                newDatesList.add(date);
                                newTimesList.add(time);

                                recievedOrder = new Order(
                                        packagedOrder.mainListId,
                                        packagedOrder.userListId,
                                        newMealsList,
                                        newTimesList,
                                        newDatesList,
                                        newQuantitiesList,
                                        packagedOrder.userId,
                                        packagedOrder.deliveryPoint,
                                        packagedOrder.status
                                );
                            }else{
                                recievedOrder = recievedOrders.get(getIndex(recievedOrders,packagedOrder.mainListId));
                                recievedOrder.mealItemList.add(meal);
                                recievedOrder.quantities.add(quantity);
                                recievedOrder.dates.add(date);
                                recievedOrder.times.add(time);
                            }

                            packagedOrder.mealItemList.remove(viewHolder.getAdapterPosition());
                            packagedOrder.quantities.remove(viewHolder.getAdapterPosition());
                            packagedOrder.dates.remove(viewHolder.getAdapterPosition());
                            packagedOrder.times.remove(viewHolder.getAdapterPosition());

                            DatabaseReference mainListPackagedDBR = FirebaseDatabase.getInstance().getReference()
                                    .child("Packaged")
                                    .child(packagedOrder.mainListId);

                            DatabaseReference mainListRecievedRef = FirebaseDatabase.getInstance().getReference()
                                    .child("Recieved")
                                    .child(recievedOrder.mainListId);

                            DatabaseReference userListPackagedRef = FirebaseDatabase.getInstance().getReference()
                                    .child("Users")
                                    .child(packagedOrder.userId)
                                    .child("Packaged")
                                    .child(packagedOrder.userListId);

                            DatabaseReference userListRecievedRef = FirebaseDatabase.getInstance().getReference()
                                    .child("Users")
                                    .child(packagedOrder.userId)
                                    .child("Recieved")
                                    .child(packagedOrder.userListId);

                            if(packagedOrder.mealItemList.size()>0) {
                                mainListPackagedDBR.setValue(packagedOrder);
                                userListPackagedRef.setValue(packagedOrder);
                            }else {
                                mainListPackagedDBR.setValue(null);
                                userListPackagedRef.setValue(null);
                            }

                            mainListRecievedRef.setValue(recievedOrder);
                            userListRecievedRef.setValue(recievedOrder);
                        }break;
                        default:{
                            Log.v(TAG, " invalid operation");
                        }
                    }

                }
            };

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeHandler);
            itemTouchHelper.attachToRecyclerView(holder.mOrderContentListRV);
        }
    }

    @Override
    public int getItemCount() {
        if(currOrdersList !=null) {
            return currOrdersList.size();
        }else {
            return 0;
        }
    }

    public void loadDataInitailly(){
        DatabaseReference ordersRef = null;
        DatabaseReference packagedRef = null;
        DatabaseReference recivedRef = null;

        if(type==TYPE_ADMIN_PENDING_ORDERS_LIST){
            ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
            packagedRef = FirebaseDatabase.getInstance().getReference().child("Packaged");
            recivedRef = FirebaseDatabase.getInstance().getReference().child("Recieved");
        }else{
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user!=null) {
                ordersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Orders");
                packagedRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Packaged");
                recivedRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Recieved");
            }
        }

        if(ordersRef!=null){
            ordersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    pendingOrders = new ArrayList<>();
                    if(dataSnapshot.exists()){
                        for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                            pendingOrders.add(dataSnapshot1.getValue(Order.class));
                            Log.v(TAG, " order added");
                            Log.v(TAG, pendingOrders.get(0).deliveryPoint);
                        }
                    }
                    if(status == STATUS_PENDING) {
                        pendingOrders = getReverse(pendingOrders);
                        currOrdersList = pendingOrders;
                        notifyDataSetChanged();
                        if (dataLoadedListener != null) {
                            dataLoadedListener.dataLoaded(pendingOrders.size());
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            packagedRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    packagedOrders = new ArrayList<>();
                    if(dataSnapshot.exists()){
                        for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                            packagedOrders.add(dataSnapshot1.getValue(Order.class));
                            Log.v(TAG, " order added");
                            Log.v(TAG, packagedOrders.get(0).deliveryPoint);
                        }
                    }
                    if(status == STATUS_PACKAGED) {
                        packagedOrders = getReverse(packagedOrders);
                        currOrdersList = packagedOrders;
                        notifyDataSetChanged();
                        if (dataLoadedListener != null) {
                            dataLoadedListener.dataLoaded(packagedOrders.size());
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        recivedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recievedOrders = new ArrayList<>();
                if(dataSnapshot.exists()){
                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                        recievedOrders.add(dataSnapshot1.getValue(Order.class));
                        Log.v(TAG, " order added");
                        Log.v(TAG, recievedOrders.get(0).deliveryPoint);
                    }
                }
                if(status == STATUS_RECIEVED) {
                    recievedOrders = getReverse(recievedOrders);
                    currOrdersList = recievedOrders;
                    notifyDataSetChanged();
                    if (dataLoadedListener != null) {
                        dataLoadedListener.dataLoaded(recievedOrders.size());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void loadData(){
        DatabaseReference ordersRef = null;
        DatabaseReference packagedRef = null;
        DatabaseReference recivedRef = null;

        if(type==TYPE_ADMIN_PENDING_ORDERS_LIST){
            ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
            packagedRef = FirebaseDatabase.getInstance().getReference().child("Packaged");
            recivedRef = FirebaseDatabase.getInstance().getReference().child("Recieved");
        }else{
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user!=null) {
                ordersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Orders");
                packagedRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Packaged");
                recivedRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Recieved");
            }
        }

        if(ordersRef!=null){
            ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    pendingOrders = new ArrayList<>();
                    if(dataSnapshot.exists()){
                        for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                            pendingOrders.add(dataSnapshot1.getValue(Order.class));
                            Log.v(TAG, " order added");
                            Log.v(TAG, pendingOrders.get(0).deliveryPoint);
                        }
                    }
                    if(status == STATUS_PENDING) {
                        pendingOrders = getReverse(pendingOrders);
                        currOrdersList = pendingOrders;
                        notifyDataSetChanged();
                        if (dataLoadedListener != null) {
                            dataLoadedListener.dataLoaded(pendingOrders.size());
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            packagedRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    packagedOrders = new ArrayList<>();
                    if(dataSnapshot.exists()){
                        for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                            packagedOrders.add(dataSnapshot1.getValue(Order.class));
                            Log.v(TAG, " order added");
                            Log.v(TAG, packagedOrders.get(0).deliveryPoint);
                        }
                    }
                    if(status == STATUS_PACKAGED) {
                        packagedOrders = getReverse(packagedOrders);
                        currOrdersList = packagedOrders;
                        notifyDataSetChanged();
                        if (dataLoadedListener != null) {
                            dataLoadedListener.dataLoaded(packagedOrders.size());
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        recivedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recievedOrders = new ArrayList<>();
                if(dataSnapshot.exists()){
                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                        recievedOrders.add(dataSnapshot1.getValue(Order.class));
                        Log.v(TAG, " order added");
                        Log.v(TAG, recievedOrders.get(0).deliveryPoint);
                    }
                }
                if(status == STATUS_RECIEVED) {
                    recievedOrders = getReverse(recievedOrders);
                    currOrdersList = recievedOrders;
                    notifyDataSetChanged();
                    if (dataLoadedListener != null) {
                        dataLoadedListener.dataLoaded(recievedOrders.size());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    boolean containsId(List<Order> orders, String id){
        for(Order order:orders){
            if(order.mainListId.equals(id)){
                return true;
            }
        }
        return false;
    }

    int getIndex(List<Order> orders, String id){
        for(Order order:orders){
            if(order.mainListId.equals(id)){
                return orders.indexOf(order);
            }
        }
        return -1;
    }

    List<Order> getReverse(List<Order> list){
        int length = list.size();
        if(length <= 1){
            return list;
        }
        List<Order> reversedList = new ArrayList<>();
        for(int i=length-1;i>=0;i--){
            reversedList.add(list.get(i));
        }
        return reversedList;
    }

    public void dismissOrderNotifications(Context context){
        if(context!=null) {
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (type == TYPE_ADMIN_PENDING_ORDERS_LIST) {
                notificationManager.cancelAll();
            } else {
                List<Integer> notificationIDs = MyFirebaseMessagingService.getNotificationIDsfromOrderIDs(context, currOrdersList);
                for (Integer id : notificationIDs) {
                    notificationManager.cancel(id);
                }
            }
        }
    }

}
