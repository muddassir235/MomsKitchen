package com.momskitchen.momskitchen.backend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.momskitchen.momskitchen.Constants;
import com.momskitchen.momskitchen.model.MealItem;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;

/**
 * Created by hp on 8/29/2016.
 */
public class FirebaseOperations {
    public static FirebaseOperations object;
    public interface ImageUploadEventListener {
        // These methods are the different events and
        // need to pass relevant arguments related to the event triggered
        public void onFailure(Exception exception);
        // or when data has been loaded
        public void onSuccess(Uri uri);
    }

    Target posterTarget;
    Target thumbnailTarget;

    public interface ThumnailUploadedListener {
        public void onFailure(Exception exception);
        public void onSuccess(Uri uri);
    }

    public interface PosterUploadedListener {
        public void onFailure(Exception exception);
        public void onSuccess(Uri uri);
    }

    public FirebaseOperations(){
        listeners= new ArrayList<ImageUploadEventListener>();
        thumnailUploadedListeners = new ArrayList<>();
        posterUploadedListeners = new ArrayList<>();
        activity=new Activity();
    }
    public interface LocalImageEventListener{
        public void onSuccess(Uri uri);
    }
    public ArrayList<ImageUploadEventListener> listeners;
    public ArrayList<ThumnailUploadedListener> thumnailUploadedListeners;
    public ArrayList<PosterUploadedListener> posterUploadedListeners;
    public ArrayList<LocalImageEventListener> localImageListeners;
    public DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
    public  Activity activity;

    public void addNewMealItem(MealItem mealItem){
        DatabaseReference pushRef = firebaseDatabase.child("Meals").push();
        String pushKey = pushRef.getKey();
        HashMap<String, Object> dateCreatedObj = new HashMap<String, Object>();
        dateCreatedObj.put("date", ServerValue.TIMESTAMP);
        mealItem.timeStampCreated = dateCreatedObj;
        mealItem.id = pushKey;
        pushRef.setValue(mealItem);
    }

    public void updateMealItem(String key, MealItem mealItem){
        //TODO: Define this function
    }

    public void deleteMealItem(String key){
        //TODO: Define this function
    }

    public MealItem getMealItem(String key){
        //TODO: Implement this function some how
        return null;
    }



    public FirebaseOperations uploadImage(Activity activity, int requestCode){

        this.activity=activity;

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent,"Select Picture"), requestCode);
        return this;
    }


    //http://stackoverflow.com/questions/21780252/how-to-use-onactivityresult-method-from-other-than-activity-class
    //PLease go through upper given link to find out how to use this method
    public void activityResult(int requestCode, int resultCode, Intent data){
       if(resultCode==Activity.RESULT_OK) {
           if (requestCode == Constants.FIREBASE_OPERATION_SELECT_PICTURE) {
               Uri selectedImageUri = data.getData();
               for(LocalImageEventListener listen : localImageListeners ){
                   listen.onSuccess(selectedImageUri);
                   localImageListeners.remove(listen);
               }
           }
       }
    }


    public String getImagePath(Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }

    private void firebaseUploadThumbnail(final StorageReference storageReference, Uri selectedImagePath){
        thumbnailTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Log.e("onBitmapLoaded","Thumbnail");
                ByteArrayOutputStream bit= new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,bit);
                byte[] bits=bit.toByteArray();
                UploadTask task= storageReference.putBytes(bits);
                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        int index = 0;
                        for(ListIterator<ThumnailUploadedListener> it = thumnailUploadedListeners.listIterator(); it.hasNext(); ){
                            ThumnailUploadedListener aListener = it.next();
                            aListener.onFailure(exception);
                            thumnailUploadedListeners.remove(index);
                            index++;
                        }

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        int index = 0;
                        for(ListIterator<ThumnailUploadedListener> it = thumnailUploadedListeners.listIterator(); it.hasNext(); ){
                            ThumnailUploadedListener aListener = it.next();
                            aListener.onSuccess(downloadUrl);
                            thumnailUploadedListeners.remove(index);
                            index ++;
                        }

                        Log.e("ui", downloadUrl.toString());
                    }
                });

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.e("Error","bitmapFailed\\");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Log.e("onPrepared","I am here thumbnail");

            }
        };
        Picasso.with(activity.getApplicationContext()).load(selectedImagePath).resize(500,500).centerCrop().into(thumbnailTarget);
    }

    private void firebaseUploadPoster(final StorageReference storageReference, Uri selectedImagePath){
        posterTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Log.e("onBitmap Loaded","Poster");
                ByteArrayOutputStream bit= new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,bit);
                byte[] bits=bit.toByteArray();
                UploadTask task= storageReference.putBytes(bits);
                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        int index = 0;
                        for(ListIterator<PosterUploadedListener> it = posterUploadedListeners.listIterator(); it.hasNext(); ){
                            PosterUploadedListener aListener = it.next();
                            aListener.onFailure(exception);
                            posterUploadedListeners.remove(index);
                            index++;
                        }
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        int index = 0;
                        for(ListIterator<PosterUploadedListener> it = posterUploadedListeners.listIterator(); it.hasNext(); ){
                            PosterUploadedListener aListener = it.next();
                            aListener.onSuccess(downloadUrl);
                            posterUploadedListeners.remove(index);
                            index++;
                        }
                        Log.e("ui", downloadUrl.toString());
                    }
                });

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.e("Error",errorDrawable.toString());

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Log.e("onPrepared: ","I am here poster");
            }
        };
        Picasso.with(activity).load(selectedImagePath).resize(1280, 720).centerCrop().into(posterTarget);
    }

    private void firebaseUploadTask(final StorageReference ref, Uri selectedImagePath,int operation){
        Log.e("in FirebaseUpload",ref.toString()+"::"+selectedImagePath.toString());
        if(operation==Constants.FIREBASE_OPERATION_UPLOAD_THUMBNAIL) {
            Picasso.with(activity).load(selectedImagePath).resize(500,500).centerCrop().into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    Log.e("onBitmapLoaded","Thumbnail");
                    ByteArrayOutputStream bit= new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,bit);
                    byte[] bits=bit.toByteArray();
                    UploadTask task= ref.putBytes(bits);
                    task.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            for(ThumnailUploadedListener listen:thumnailUploadedListeners){
                                listen.onFailure(exception);
                                thumnailUploadedListeners.remove(listen);
                            }
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            for(ThumnailUploadedListener listen:thumnailUploadedListeners){
                                listen.onSuccess(downloadUrl);
                                thumnailUploadedListeners.remove(listen);
                            }
                            Log.e("ui", downloadUrl.toString());
                        }
                    });

                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    Log.e("Error","bitmapFailed\\");
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    Log.e("onPrepared","I am here");

                }
            });
        }
        else if(operation==Constants.FIREBASE_OPERATION_UPLOAD_POSTER){
            Picasso.with(activity).load(selectedImagePath).resize(1280, 720).centerCrop().into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    Log.e("onBitmap Loaded","Poster");
                    ByteArrayOutputStream bit= new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,bit);
                    byte[] bits=bit.toByteArray();
                    UploadTask task= ref.putBytes(bits);
                    task.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            int index=0;
                            for(ListIterator<PosterUploadedListener> it = posterUploadedListeners.listIterator(); it.hasNext(); ){
                                PosterUploadedListener aListener = it.next();
                                aListener.onFailure(exception);
                                posterUploadedListeners.remove(index);
                                index++;
                            }
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            int index = 0;
                            for(ListIterator<PosterUploadedListener> it = posterUploadedListeners.listIterator(); it.hasNext(); ){
                                PosterUploadedListener aListener = it.next();
                                aListener.onSuccess(downloadUrl);
                                posterUploadedListeners.remove(index);
                                index++;
                            }
                            Log.e("ui", downloadUrl.toString());
                        }
                    });

                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    Log.e("Error",errorDrawable.toString());

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });

        }


    }



    public void setLocalImageListener(LocalImageEventListener listen){
        this.localImageListeners.add(listen);
    }

    public void setThumnailUploadedListener(ThumnailUploadedListener listener){
        this.thumnailUploadedListeners.add(listener);
    }

    public void setPosterUploadedListeners(PosterUploadedListener listener){
        this.posterUploadedListeners.add(listener);
    }

    public void setImageUploadEventListener(ImageUploadEventListener listen){
        this.listeners.add(listen);
    }

    public FirebaseOperations uploadThumbnailTask(String id, Uri imageUri){
        StorageReference thumbnailRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://kitchen-28900.appspot.com/meals/");
        firebaseUploadThumbnail(thumbnailRef.child(id).child("thumbnail"),imageUri);
        return this;
    }

    public FirebaseOperations uploadPosterTask(String id, Uri imageUri){
        StorageReference posterReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://kitchen-28900.appspot.com/meals/");
        firebaseUploadPoster(posterReference.child(id).child("poster"),imageUri);
        return this;
    }


    public FirebaseOperations uploadTask(String id, Uri imageUri,int operation){

        if(operation==Constants.FIREBASE_OPERATION_UPLOAD_POSTER) {

        }
        else if(operation==Constants.FIREBASE_OPERATION_UPLOAD_THUMBNAIL){
            StorageReference thumbnailReference = FirebaseStorage.getInstance().getReference("gs://kitchen-28900.appspot.com/meals/").child(id).child("thumbnail");
            firebaseUploadTask(thumbnailReference,imageUri,operation);
        }
        return this;
    }

    public String getPath(Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }
    public static FirebaseOperations getInstance(){

        if(object==null){
           object= new FirebaseOperations();
        }
        return object;
    }

}
