package com.momskitchen.momskitchen.Admin.ActivitiesPlusFragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.momskitchen.momskitchen.Admin.Adapters.OrderItem;
import com.momskitchen.momskitchen.Admin.Adapters.PendingOrdersAdapter;
import com.momskitchen.momskitchen.Admin.ViewHolders.OrderHolder;
import com.momskitchen.momskitchen.Admin.ViewHolders.OrderItemWrapper;
import com.momskitchen.momskitchen.Admin.ViewHolders.OrderWrapper;
import com.momskitchen.momskitchen.R;
import com.momskitchen.momskitchen.model.Order;
import com.zaihuishou.expandablerecycleradapter.adapter.BaseExpandableAdapter;
import com.zaihuishou.expandablerecycleradapter.viewholder.AbstractAdapterItem;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AdminOrderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AdminOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminOrderFragment extends Fragment {

    private final int ITEM_TYPE_ORDER = 1;
    private final int ITEM_TYPE_ORDER_CONTENT = 2;

    private OnFragmentInteractionListener mListener;

    public AdminOrderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment
     * @return A new instance of fragment AdminOrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminOrderFragment newInstance() {
        AdminOrderFragment fragment = new AdminOrderFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_admin_order, container, false);
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_meals_grid);
        final ProgressBar loadingProgress= (ProgressBar) rootView.findViewById(R.id.progress_meals_grid);
        final LinearLayout emptyListLayout = (LinearLayout) rootView.findViewById(R.id.empty_list_layout);
        final ImageView emptyListImageView = (ImageView) rootView.findViewById(R.id.empty_list_image_view);
        final TextView emptyListTextView = (TextView) rootView.findViewById(R.id.empty_list_text_view);

        final RecyclerView allOrdersList = (RecyclerView) rootView.findViewById(R.id.pending_orders_recycler_view);
        allOrdersList.setLayoutManager(new LinearLayoutManager(getActivity()));
        final PendingOrdersAdapter adapter = new PendingOrdersAdapter(PendingOrdersAdapter.TYPE_ADMIN_PENDING_ORDERS_LIST,PendingOrdersAdapter.STATUS_PENDING);
        allOrdersList.setAdapter(adapter);

        adapter.setDataLoadedListener(new PendingOrdersAdapter.DataLoadedListener() {
            @Override
            public void dataLoaded(int size) {
                adapter.dismissOrderNotifications(getActivity());
                loadingProgress.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                if(size == 0){
                    emptyListLayout.setVisibility(View.VISIBLE);
                    emptyListImageView.setImageResource(R.drawable.sun_icon);
                    emptyListTextView.setText("Hurray! no orders to be packaged :)");
                    rootView.setBackgroundColor(Color.parseColor("#B0D3FE"));
                }else{
                    emptyListLayout.setVisibility(View.GONE);
                    rootView.setBackgroundColor(Color.parseColor("#00FFFFFF"));
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.loadData();
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
