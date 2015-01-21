package com.pongodev.dailyworkout.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.pongodev.dailyworkout.R;
import com.pongodev.dailyworkout.adapters.AdapterWorkouts;
import com.pongodev.dailyworkout.utils.DBHelperWorkouts;
import com.pongodev.dailyworkout.utils.OnTapListener;
import com.pongodev.dailyworkout.utils.Utils;

import java.util.ArrayList;

public class FragmentTabWorkouts extends Fragment {
    private OnSelectedListener mCallback;

    // Declare view objects
    private RecyclerView recyclerView;
    private ProgressBarCircularIndeterminate prgLoading;
    private TextView lblNoResult;

    //Declare instace of Utils class
    private Utils utils;
    private DBHelperWorkouts dbWorkouts;

    // create object of custom adapter
    private AdapterWorkouts la;

    // create arraylist variables to store data
    private ArrayList<String> workoutIds = new ArrayList<String>();
    private ArrayList<String> workoutName = new ArrayList<String>();
    private ArrayList<String> workoutImage = new ArrayList<String>();
    private ArrayList<String> workoutTotal = new ArrayList<String>();

    // create arraylist variable to store object data from database
    private ArrayList<ArrayList<Object>> data;

    // create interface for listener method
    public interface OnSelectedListener {
        public void onSelected(String selectedID, String selectedName);
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FragmentTabWorkouts newInstance() {
        FragmentTabWorkouts fragment = new FragmentTabWorkouts();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        setRetainInstance(true);

        // Connect view objects and view id on xml.
        recyclerView= (RecyclerView) v.findViewById(R.id.recycler_view);
        prgLoading  = (ProgressBarCircularIndeterminate) v.findViewById(R.id.prgLoading);
        lblNoResult = (TextView) v.findViewById(R.id.lblNoResult);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // Check database
        dbWorkouts = new DBHelperWorkouts(getActivity());
        dbWorkouts.checkDBWorkouts();

        // call asynctask class to get data from database
        new getDataList().execute();

        la = new AdapterWorkouts(getActivity());

        la.setOnTapListener(new OnTapListener() {
            @Override
            public void onTapView(String selectedIds, String selectedName) {
                mCallback.onSelected(selectedIds, selectedName);
            }
        });

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (OnSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    // asynctask class that is used to fetch data from database in background
    private class getDataList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            // show progress dialog when fetching data from database
            super.onPreExecute();
            prgLoading.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            getDataFromDatabase();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // TODO Auto-generated method stub
            // when finishing fetching data, close progress dialog and show data on listview
            // if available, otherwise show toast message
            super.onPostExecute(aVoid);
            prgLoading.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            if(workoutIds.size() != 0){
                lblNoResult.setVisibility(View.GONE);
                la.updateList(workoutIds, workoutName, workoutImage, workoutTotal);
                recyclerView.setAdapter(la);
            } else {
                lblNoResult.setVisibility(View.VISIBLE);
            }
        }
    }

    // method to fetch data from database
    public void getDataFromDatabase() {

        data = dbWorkouts.getAllCategories();

        for (int i = 0; i < data.size(); i++) {
            ArrayList<Object> row = data.get(i);

            workoutIds.add(row.get(0).toString());
            workoutName.add(row.get(1).toString());
            workoutImage.add(row.get(2).toString());
            workoutTotal.add(row.get(3).toString());
        }

    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        dbWorkouts.close();
        super.onDestroy();
    }

}