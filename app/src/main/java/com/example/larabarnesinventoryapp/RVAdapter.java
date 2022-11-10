package com.example.larabarnesinventoryapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.RecyclerViewHolder>{  //recyclerview adapter to initialize recyclerview
    private ArrayList<itemData> myArrayList;
    DatabaseHelper myDb;

    public RVAdapter(ArrayList<itemData> myArrayList){
        this.myArrayList = myArrayList;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_layout, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        itemData myItem = myArrayList.get(position);
        holder.viewItemName.setText(myArrayList.get(position).getItemName());
        holder.viewItemQty.setText(String.valueOf(myArrayList.get(position).getItemQty()));
        holder.itemData = myItem;
    }

    @Override
    public int getItemCount() {
        return myArrayList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView viewItemName;
        private TextView viewItemQty;
        View rootView;
        itemData itemData;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView;
            viewItemName = itemView.findViewById(R.id.textItemName);
            viewItemQty = itemView.findViewById(R.id.textViewQty);
            myDb = new DatabaseHelper(rootView.getContext());

            itemView.findViewById(R.id.buttonAddQty).setOnClickListener(new View.OnClickListener() { //add qty button
                @Override
                public void onClick(View view) {
                    if(myDb.updateItem(itemData.getId(), 1)){
                        viewItemQty.setText(String.valueOf(Integer.parseInt(viewItemQty.getText().toString()) + 1));
                    }
                }
            });
            itemView.findViewById(R.id.buttonSubtractQty).setOnClickListener(new View.OnClickListener() { //subtract qty button
                @Override
                public void onClick(View view) {
                    if(myDb.updateItem(itemData.getId(), -1)){
                        viewItemQty.setText(String.valueOf(Integer.parseInt(viewItemQty.getText().toString()) + -1));
                    }
                }
            });
        }
    }
}
