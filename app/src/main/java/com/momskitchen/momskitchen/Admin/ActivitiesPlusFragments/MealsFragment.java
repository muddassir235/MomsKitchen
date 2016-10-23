package com.momskitchen.momskitchen.Admin.ActivitiesPlusFragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.momskitchen.momskitchen.Admin.MealsListFragmentsPagerAdapter;
import com.momskitchen.momskitchen.R;
import com.momskitchen.momskitchen.model.MealItem;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MealsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MealsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MealsFragment extends Fragment implements MealListFragment.OnListFragmentInteractionListener{

    public static Context context;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "MealsFragment: " ;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ViewPager mealsListViewPager;
    private TabLayout tabLayout;
    private MealsListFragmentsPagerAdapter mealsListFragmentsPagerAdapter;

    private OnFragmentInteractionListener mListener;

    public MealsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MealsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MealsFragment newInstance(String param1, String param2) {
        MealsFragment fragment = new MealsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_meals, container, false);
        context = getActivity();
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        mealsListFragmentsPagerAdapter = new MealsListFragmentsPagerAdapter(getChildFragmentManager(),MealsListFragmentsPagerAdapter.ALL_MEALS_LIST_FRAGMENT);
        // Set up the ViewPager with the sections adapter.
        mealsListViewPager = (ViewPager) rootView.findViewById(R.id.meal_container);
        if (mealsListViewPager != null) {
            mealsListViewPager.setAdapter(mealsListFragmentsPagerAdapter);
            mealsListViewPager.setCurrentItem(1);
        }

        tabLayout.setupWithViewPager(mealsListViewPager);
        FloatingActionButton addNewMealButton = (FloatingActionButton) rootView.findViewById(R.id.add_new_meal_fab);
        addNewMealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),NewMealActivity.class);
                startActivity(intent);
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

    @Override
    public void onListFragmentInteraction(MealItem item) {

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

    @Override
    public void onStart() {
        super.onStart();
        context = getActivity();
    }
}
