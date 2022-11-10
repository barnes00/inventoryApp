package com.example.larabarnesinventoryapp;

import android.view.LayoutInflater;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Toast;
import java.util.ArrayList;

public class HomeFragment extends Fragment{ //item table screen
    private RecyclerView recyclerView;
    private DatabaseHelper myDb;
    private ArrayList<itemData> myArrayList = new ArrayList<itemData>();
    private RVAdapter rvAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_home, container, false); //set up recyclerview
        recyclerView = view.findViewById(R.id.itemRV);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        myDb = new DatabaseHelper(getContext());
        myDb.fillArray(myArrayList); //populate arraylist from item db
        rvAdapter = new RVAdapter(myArrayList);
        recyclerView.setAdapter(rvAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT  ) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                int position = viewHolder.getAdapterPosition();
                myDb.deleteItem(myArrayList.get(position).getId());
                myArrayList.remove(position);
                rvAdapter.notifyItemRemoved(position);
                rvAdapter.notifyItemRangeChanged(position, myArrayList.size());
                Toast.makeText(getContext(), "Item Deleted", Toast.LENGTH_SHORT).show();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(view.findViewById(R.id.itemRV));

        return view;
    }

}
