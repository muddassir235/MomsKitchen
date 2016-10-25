package com.momskitchen.momskitchen.Admin.ActivitiesPlusFragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.momskitchen.momskitchen.R;
import com.momskitchen.momskitchen.Tools;
import com.momskitchen.momskitchen.model.MealItem;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AdminMealDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AdminMealDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminMealDetailsFragment extends Fragment {
    private static ProgressDialog mProgressDialog;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    static Activity activity;

    static boolean thumbnailDeleted;
    static boolean posterDelelted;

    static MealItem mealItem;
    ImageView mealCatagoryIcon;

    private OnFragmentInteractionListener mListener;

    public AdminMealDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminMealDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminMealDetailsFragment newInstance(String param1, String param2) {
        AdminMealDetailsFragment fragment = new AdminMealDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_admin_meal_details, container, false);
        this.activity = getActivity();
        ((LinearLayout) rootView.findViewById(R.id.detail_content_layout)).setBackgroundColor(Color.parseColor("#FFFFFF"));
        mealItem =(MealItem) getArguments().getSerializable("Meal");
        // Inflate the layout for this fragment
        TextView nameTV = (TextView) rootView.findViewById(R.id.details_meal_name);
        TextView priceTV = (TextView) rootView.findViewById(R.id.details_meal_price);
        TextView catagory = (TextView) rootView.findViewById(R.id.details_meal_catagory);
        TextView descriptionTV = (TextView) rootView.findViewById(R.id.details_description);
        mealCatagoryIcon = (ImageView) rootView.findViewById(R.id.details_catagory_icon);

        if(mealItem.catagory.equals("dessert")){
            mealCatagoryIcon.setImageResource(R.drawable.dessert_icon);
        }else if(mealItem.catagory.equals("lunch")){
            mealCatagoryIcon.setImageResource(R.drawable.lunch_icon);
        }else if(mealItem.catagory.equals("compliment")){
            mealCatagoryIcon.setImageResource(R.drawable.compliment_icon);
        }

        nameTV.setText(mealItem.name);
        priceTV.setText("Price: "+mealItem.pricePerUnit+" PKR");
        catagory.setText(mealItem.catagory.toUpperCase());
        descriptionTV.setText(mealItem.description);

        if(MealDetailsActivityAdmin.posterPalette != null){
            ((LinearLayout) rootView.findViewById(R.id.detail_content_layout))
                    .setBackgroundColor(Tools.brightenColor(
                            MealDetailsActivityAdmin
                                    .posterPalette
                                    .getLightVibrantColor(Color.parseColor("#FFFFFF")))
                    );
            if(MealDetailsActivityAdmin.posterPalette.getLightVibrantSwatch()!=null) {
                ((LinearLayout) rootView.findViewById(R.id.details_meal_info_layout))
                        .setBackgroundColor(
                                MealDetailsActivityAdmin
                                        .posterPalette
                                        .getLightVibrantSwatch().getTitleTextColor()
                        );
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mealCatagoryIcon.setImageTintList(ColorStateList.valueOf(MealDetailsActivityAdmin.posterPalette.getDarkVibrantColor(getActivity().getResources().getColor(R.color.colorAccent))));
            }
        }
        MealDetailsActivityAdmin.setPosterLoadedListeners(new MealDetailsActivityAdmin.OnPosterLoadedListener() {
            @Override
            public void onPosterLoad() {
                ((LinearLayout) rootView.findViewById(R.id.detail_content_layout))
                        .setBackgroundColor(Tools.brightenColor(
                                MealDetailsActivityAdmin
                                        .posterPalette
                                        .getLightVibrantColor(Color.parseColor("#FFFFFF")))
                        );
                if(MealDetailsActivityAdmin.posterPalette.getLightVibrantSwatch()!=null) {
                    ((LinearLayout) rootView.findViewById(R.id.details_meal_info_layout))
                            .setBackgroundColor(
                                    MealDetailsActivityAdmin
                                            .posterPalette
                                            .getLightVibrantSwatch().getTitleTextColor()
                            );
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if(MealDetailsActivityAdmin.posterPalette!=null) {
                        if(getActivity()!=null) {
                            mealCatagoryIcon.setImageTintList(ColorStateList.valueOf(MealDetailsActivityAdmin.posterPalette.getDarkVibrantColor(getActivity().getResources().getColor(R.color.colorAccent))));
                        }
                    }
                }
            }
        });

        ((Button) rootView.findViewById(R.id.delete_meal)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DeleteMealDialog();
                newFragment.show(getChildFragmentManager(), "missiles");
            }
        });
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    @SuppressLint("ValidFragment")
    public static class DeleteMealDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Delete Meal?");
            builder.setMessage("Delete this meal forever.")
                    .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            showLoadingDialog(activity,"Deleting meal...");
                            //StorageReference mealFolderRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://kitchen-28900.appspot.com/meals").child(mealItem.catagory).child(mealItem.id);
                            StorageReference thumbnailRef = FirebaseStorage.getInstance().getReferenceFromUrl(mealItem.thumbnailURL);
                            StorageReference posterRef = FirebaseStorage.getInstance().getReferenceFromUrl(mealItem.posterURL);
                            thumbnailRef.delete().addOnSuccessListener(getActivity(), new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    thumbnailDeleted = true;
                                    if(posterDelelted) {
                                        dismissDialog();
                                        FirebaseDatabase.getInstance().getReference().child("Meals").child(mealItem.catagory).child(mealItem.id).setValue(null);
                                        Toast.makeText(activity, "Meal deleted successfully!", Toast.LENGTH_SHORT).show();
                                        activity.finish();
                                    }
                                }
                            }).addOnFailureListener(getActivity(), new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    thumbnailDeleted = false;
                                    dismissDialog();
                                    Toast.makeText(activity,"The delete has failed please retry!",Toast.LENGTH_SHORT).show();
                                    //getActivity().finish();
                                }
                            });

                            posterRef.delete().addOnSuccessListener(getActivity(), new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    posterDelelted = true;
                                    if(thumbnailDeleted) {
                                        dismissDialog();
                                        FirebaseDatabase.getInstance().getReference().child("Meals").child(mealItem.catagory).child(mealItem.id).setValue(null);
                                        Toast.makeText(activity, "Meal deleted successfully!", Toast.LENGTH_SHORT).show();
                                        activity.finish();
                                    }
                                }
                            }).addOnFailureListener(getActivity(), new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    posterDelelted = false;
                                    dismissDialog();
                                    Toast.makeText(activity,"The delete has failed please retry!",Toast.LENGTH_SHORT).show();
                                    //getActivity().finish();
                                }
                            });
                        }
                    });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // DO NOTHING
                }
            });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    public static void showLoadingDialog(Activity context,String message) {
        dismissDialog();
        mProgressDialog = ProgressDialog.show(context, "", message, true);
    }

    public static void dismissDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

}
