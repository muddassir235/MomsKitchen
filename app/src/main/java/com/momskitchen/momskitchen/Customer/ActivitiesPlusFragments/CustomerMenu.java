package com.momskitchen.momskitchen.Customer.ActivitiesPlusFragments;

import android.app.Activity;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.momskitchen.momskitchen.Admin.MealsListFragmentsPagerAdapter;
import com.momskitchen.momskitchen.R;
import com.momskitchen.momskitchen.backend.MenuCreator;
import com.momskitchen.momskitchen.backend.OrderHandler;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CustomerMenu.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CustomerMenu#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerMenu extends Fragment implements CustomerMenuFragment.OnListFragmentInteractionListener {
    public static final int SHOPPING_CART_ORDER_REQUEST = 0;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    View rootView;

    private ViewPager mealsListViewPager;
    private TabLayout tabLayout;
    private MealsListFragmentsPagerAdapter mealsListFragmentsPagerAdapter;

    public static RelativeLayout totalLayout;
    public static TextView totalTV;

    private OnFragmentInteractionListener mListener;

    public CustomerMenu() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CustomerMenu.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomerMenu newInstance(String param1, String param2) {
        CustomerMenu fragment = new CustomerMenu();
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
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_customer_menu, container, false);

        totalLayout = (RelativeLayout) rootView.findViewById(R.id.total_layout);
        totalTV = (TextView) rootView.findViewById(R.id.total_text_view);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        mealsListFragmentsPagerAdapter = new MealsListFragmentsPagerAdapter(getChildFragmentManager(),MealsListFragmentsPagerAdapter.CUSTOMER_MENU_LIST);
        // Set up the ViewPager with the sections adapter.
        mealsListViewPager = (ViewPager) rootView.findViewById(R.id.meal_container);
        if (mealsListViewPager != null) {
            mealsListViewPager.setAdapter(mealsListFragmentsPagerAdapter);
            mealsListViewPager.setCurrentItem(1);
        }

        OrderHandler.setupWeeksCart(MenuCreator.getInstance().
                getAllWeekDates(
                        MenuCreator.getInstance().getDateFromCalendar(CustomerMainActivity.calendar))
        );
        OrderHandler.calculateTotal();

        tabLayout.setupWithViewPager(mealsListViewPager);
        FloatingActionButton floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.shopping_cart_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ShoppingCartActivity.class);
                startActivityForResult(intent,SHOPPING_CART_ORDER_REQUEST);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SHOPPING_CART_ORDER_REQUEST){
            if(resultCode == Activity.RESULT_OK){

            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListFragmentInteraction() {

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
}
