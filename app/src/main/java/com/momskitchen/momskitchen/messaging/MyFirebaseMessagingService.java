package com.momskitchen.momskitchen.messaging;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.momskitchen.momskitchen.Admin.ActivitiesPlusFragments.AdminMainActivity;
import com.momskitchen.momskitchen.R;
import com.momskitchen.momskitchen.model.Order;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 10/23/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        if(remoteMessage.getData().get("title").equals("Orders")) {

            sendOrderNotification(remoteMessage.getData().get("body"));

        }else if(remoteMessage.getData().get("title").equals("Packaged")){

            sendPackagedNotification(remoteMessage.getData().get("body"));

        }else if(remoteMessage.getData().get("title").equals("Recieved")){

            sendRecievedNotification(remoteMessage.getData().get("body"));

        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void sendPackagedNotification(String messageBody){
        Intent intent = new Intent(this, AdminMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        final Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        FirebaseDatabase.getInstance().getReference().child("Packaged").child(messageBody).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Order order = dataSnapshot.getValue(Order.class);
                    int numberOfmeals = order.mealItemList.size();

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.packaged_icon)
                            .setContentTitle("Orders Packaged")
                            .setContentText("Your "+numberOfmeals+" meals have been packaged")
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);

                    NotificationCompat.InboxStyle inboxStyle =
                            new NotificationCompat.InboxStyle();

                    inboxStyle.setBigContentTitle("The following meals have been packaged:");

                    for (int i=0; i < order.mealItemList.size(); i++){
                        if(order.quantities.get(i)==1){
                            inboxStyle.addLine("1 "+order.mealItemList.get(i).name);
                        }else{
                            inboxStyle.addLine(""+order.quantities.get(i)+" "+order.mealItemList.get(i).name+"s");
                        }
                    }

                    mBuilder.setStyle(inboxStyle);

                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    int uniqueNotificationID = getNotificationIDFromOrderID(getApplicationContext(),order.mainListId);

                    notificationManager.notify(uniqueNotificationID, mBuilder.build());

                    pairNotificationIDwithOrderID(getApplicationContext(),order.mainListId,uniqueNotificationID);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        try {
//            Intent intent = new Intent(this, AdminMainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                    PendingIntent.FLAG_ONE_SHOT);
//
//            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//            JSONObject jsonObject = new JSONObject(messageBody);
//            String id = jsonObject.getString("mainListId");
//            JSONArray mealsJsonArray = jsonObject.getJSONArray("mealItemList");
//            int numberOfMealItems = mealsJsonArray.length();
//            String deliveryAddress = jsonObject.getString("deliveryPoint");
//            List<String> mealNames = new ArrayList<>();
//            for(int i = 0; i<mealsJsonArray.length(); i++){
//                JSONObject mealObject = mealsJsonArray.getJSONObject(i);
//                mealNames.add(mealObject.getString("name"));
//            }
//            List<Long> quantities = new ArrayList<>();
//            JSONArray quantitiesArray =jsonObject.getJSONArray("quantities");
//            for(int i = 0; i<quantitiesArray.length(); i++){
//                quantities.add(quantitiesArray.getLong(i));
//            }
//
//            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
//                    .setSmallIcon(R.drawable.cart_icon)
//                    .setContentTitle("Orders Packaged")
//                    .setContentText("Your "+numberOfMealItems+" meals have been packaged")
//                    .setAutoCancel(true)
//                    .setSound(defaultSoundUri)
//                    .setContentIntent(pendingIntent);;
//
//            NotificationCompat.InboxStyle inboxStyle =
//                    new NotificationCompat.InboxStyle();
//
//            inboxStyle.setBigContentTitle("The following meals have been packaged:");
//
//            for (int i=0; i < mealNames.size(); i++){
//                if(quantities.get(i)==1){
//                    inboxStyle.addLine("1 "+mealNames.get(i));
//                }else{
//                    inboxStyle.addLine(""+quantities.get(i)+" "+mealNames.get(i)+"s");
//                }
//            }
//
//            mBuilder.setStyle(inboxStyle);
//
//            NotificationManager notificationManager =
//                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//            int uniqueNotificationID = getNotificationIDFromOrderID(getApplicationContext(),id);
//
//            notificationManager.notify(uniqueNotificationID, mBuilder.build());
//
//            pairNotificationIDwithOrderID(getApplicationContext(),id,uniqueNotificationID);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }


    private void sendRecievedNotification(String messageBody){
        Intent intent = new Intent(this, AdminMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        final Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        FirebaseDatabase.getInstance().getReference().child("Recieved").child(messageBody).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Order order = dataSnapshot.getValue(Order.class);
                    int numberOfmeals = order.mealItemList.size();

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.delivered_icon)
                            .setContentTitle("Orders Received")
                            .setContentText("You have received "+numberOfmeals+" meals.")
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);

                    NotificationCompat.InboxStyle inboxStyle =
                            new NotificationCompat.InboxStyle();

                    inboxStyle.setBigContentTitle("You have received the following meals:");

                    for (int i=0; i < order.mealItemList.size(); i++){
                        if(order.quantities.get(i)==1){
                            inboxStyle.addLine("1 "+order.mealItemList.get(i).name);
                        }else{
                            inboxStyle.addLine(""+order.quantities.get(i)+" "+order.mealItemList.get(i).name+"s");
                        }
                    }

                    mBuilder.setStyle(inboxStyle);

                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    int uniqueNotificationID = getNotificationIDFromOrderID(getApplicationContext(),order.mainListId);

                    notificationManager.notify(uniqueNotificationID, mBuilder.build());

                    pairNotificationIDwithOrderID(getApplicationContext(),order.mainListId,uniqueNotificationID);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //JSONObject jsonObject = new JSONObject(messageBody);
        //String id = jsonObject.getString("mainListId");
        //JSONArray mealsJsonArray = jsonObject.getJSONArray("mealItemList");
        //int numberOfMealItems = mealsJsonArray.length();
        //String deliveryAddress = jsonObject.getString("deliveryPoint");
        //List<String> mealNames = new ArrayList<>();
//        for(int i = 0; i<mealsJsonArray.length(); i++){
//            JSONObject mealObject = mealsJsonArray.getJSONObject(i);
//            mealNames.add(mealObject.getString("name"));
//        }
        //List<Long> quantities = new ArrayList<>();
        //JSONArray quantitiesArray =jsonObject.getJSONArray("quantities");
        //for(int i = 0; i<quantitiesArray.length(); i++){
          //  quantities.add(quantitiesArray.getLong(i));
        //}

//        try {
//            Intent intent = new Intent(this, AdminMainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                    PendingIntent.FLAG_ONE_SHOT);
//
//            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//            JSONObject jsonObject = new JSONObject(messageBody);
//            String id = jsonObject.getString("mainListId");
//            JSONArray mealsJsonArray = jsonObject.getJSONArray("mealItemList");
//            int numberOfMealItems = mealsJsonArray.length();
//            String deliveryAddress = jsonObject.getString("deliveryPoint");
//            List<String> mealNames = new ArrayList<>();
//            for(int i = 0; i<mealsJsonArray.length(); i++){
//                JSONObject mealObject = mealsJsonArray.getJSONObject(i);
//                mealNames.add(mealObject.getString("name"));
//            }
//            List<Long> quantities = new ArrayList<>();
//            JSONArray quantitiesArray =jsonObject.getJSONArray("quantities");
//            for(int i = 0; i<quantitiesArray.length(); i++){
//                quantities.add(quantitiesArray.getLong(i));
//            }
//
//            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
//                    .setSmallIcon(R.drawable.cart_icon)
//                    .setContentTitle("Orders Packaged")
//                    .setContentText("You have received "+numberOfMealItems+" meals.")
//                    .setAutoCancel(true)
//                    .setSound(defaultSoundUri)
//                    .setContentIntent(pendingIntent);;
//
//            NotificationCompat.InboxStyle inboxStyle =
//                    new NotificationCompat.InboxStyle();
//
//            inboxStyle.setBigContentTitle("You have received the following meals:");
//
//            for (int i=0; i < mealNames.size(); i++){
//                if(quantities.get(i)==1){
//                    inboxStyle.addLine("1 "+mealNames.get(i));
//                }else{
//                    inboxStyle.addLine(""+quantities.get(i)+" "+mealNames.get(i)+"s");
//                }
//            }
//
//            mBuilder.setStyle(inboxStyle);
//
//            NotificationManager notificationManager =
//                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//            int uniqueNotificationID = getNotificationIDFromOrderID(getApplicationContext(),id);
//
//            notificationManager.notify(uniqueNotificationID, mBuilder.build());
//
//            pairNotificationIDwithOrderID(getApplicationContext(),id,uniqueNotificationID);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendOrderNotification(String messageBody) {
        Intent intent = new Intent(this, AdminMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        final Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        FirebaseDatabase.getInstance().getReference().child("Orders").child(messageBody).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Order order = dataSnapshot.getValue(Order.class);
                    int numberOfmeals = order.mealItemList.size();

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.cart_icon)
                            .setContentTitle("New Orders")
                            .setContentText(numberOfmeals+" new orders from "+order.deliveryPoint)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);

                    NotificationCompat.InboxStyle inboxStyle =
                            new NotificationCompat.InboxStyle();

                    inboxStyle.setBigContentTitle("New Orders from "+order.deliveryPoint+":");

                    for (int i=0; i < order.mealItemList.size(); i++){
                        if(order.quantities.get(i)==1){
                            inboxStyle.addLine("1 "+order.mealItemList.get(i).name);
                        }else{
                            inboxStyle.addLine(""+order.quantities.get(i)+" "+order.mealItemList.get(i).name+"s");
                        }
                    }

                    mBuilder.setStyle(inboxStyle);

                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    int uniqueNotificationID = getNotificationIDFromOrderID(getApplicationContext(),order.mainListId);

                    notificationManager.notify(uniqueNotificationID, mBuilder.build());

                    pairNotificationIDwithOrderID(getApplicationContext(),order.mainListId,uniqueNotificationID);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        try {
//            Intent intent = new Intent(this, AdminMainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                    PendingIntent.FLAG_ONE_SHOT);
//
//            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//            JSONObject jsonObject = new JSONObject(messageBody);
//            String id = jsonObject.getString("mainListId");
//            JSONArray mealsJsonArray = jsonObject.getJSONArray("mealItemList");
//            int numberOfMealItems = mealsJsonArray.length();
//            String deliveryAddress = jsonObject.getString("deliveryPoint");
//            List<String> mealNames = new ArrayList<>();
//            for(int i = 0; i<mealsJsonArray.length(); i++){
//                JSONObject mealObject = mealsJsonArray.getJSONObject(i);
//                mealNames.add(mealObject.getString("name"));
//            }
//            List<Long> quantities = new ArrayList<>();
//            JSONArray quantitiesArray =jsonObject.getJSONArray("quantities");
//            for(int i = 0; i<quantitiesArray.length(); i++){
//                quantities.add(quantitiesArray.getLong(i));
//            }
//
//            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
//                    .setSmallIcon(R.drawable.cart_icon)
//                    .setContentTitle("New Orders")
//                    .setContentText(numberOfMealItems+" new orders from "+deliveryAddress)
//                    .setAutoCancel(true)
//                    .setSound(defaultSoundUri)
//                    .setContentIntent(pendingIntent);;
//
//            NotificationCompat.InboxStyle inboxStyle =
//                    new NotificationCompat.InboxStyle();
//
//            inboxStyle.setBigContentTitle("New Orders from "+deliveryAddress+":");
//
//            for (int i=0; i < mealNames.size(); i++){
//                if(quantities.get(i)==1){
//                    inboxStyle.addLine("1 "+mealNames.get(i));
//                }else{
//                    inboxStyle.addLine(""+quantities.get(i)+" "+mealNames.get(i)+"s");
//                }
//            }
//
//            mBuilder.setStyle(inboxStyle);
//
//            NotificationManager notificationManager =
//                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            int uniqueNotificationID = getUniqueNotificationID(getApplicationContext());
//            notificationManager.notify(uniqueNotificationID, mBuilder.build());
//            pairNotificationIDwithOrderID(getApplicationContext(),id,uniqueNotificationID);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        Intent intent = new Intent(this, AdminMainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.cart_icon)
//                .setContentTitle("FCM Message")
//                .setContentText(messageBody)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
//

//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(this)
//                        .setSmallIcon(R.drawable.notification_icon)
//                        .setContentTitle("My notification")
//                        .setContentText("Hello World!");
//// Creates an explicit intent for an Activity in your app
//        Intent resultIntent = new Intent(this, ResultActivity.class);
//
//// The stack builder object will contain an artificial back stack for the
//// started Activity.
//// This ensures that navigating backward from the Activity leads out of
//// your application to the Home screen.
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//// Adds the back stack for the Intent (but not the Intent itself)
//        stackBuilder.addParentStack(ResultActivity.class);
//// Adds the Intent that starts the Activity to the top of the stack
//        stackBuilder.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent =
//                stackBuilder.getPendingIntent(
//                        0,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
//        mBuilder.setContentIntent(resultPendingIntent);
//        NotificationManager mNotificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//// mId allows you to update the notification later on.
//        mNotificationManager.notify(mId, mBuilder.build());
    }

    int getUniqueNotificationID(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int id = sharedPreferences.getInt("ID",0);
        if(id == Integer.MAX_VALUE){
            editor.putInt("ID",0);
        }else{
            editor.putInt("ID",id+1);
        }
        editor.commit();
        return id;
    }

    int getNotificationIDFromOrderID(Context context, String orderID){
        String number="";
        for(int i=0;i<orderID.length();i++){
            number = number + String.valueOf((int) orderID.charAt(i));
        }
        int sizeOfNumber = number.length();
        String integerNumber = number.substring(sizeOfNumber-8,sizeOfNumber-1);
        return Integer.valueOf(integerNumber);
    }

    void pairNotificationIDwithOrderID(Context context,String orderID,int notificationID){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putInt(orderID,notificationID);
        editor.commit();
    }

    static public List<Integer> getNotificationIDsfromOrderIDs(Context context,List<Order> orders){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        List<Integer> notificationIDs = new ArrayList<>();
        for(Order order:orders){
            int id = sharedPreferences.getInt(order.mainListId,-1);
            if(id!=-1){
                notificationIDs.add(id);
            }
        }
        return notificationIDs;
    }
}