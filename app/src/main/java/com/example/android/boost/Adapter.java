package com.example.android.boost;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.AdapterViewHolder>{

    private ArrayList<Long> mData;

    public Adapter(ArrayList<Long> arrayList) {
        mData = arrayList;
    }

    // inner class ViewHolder
    public class AdapterViewHolder extends RecyclerView.ViewHolder {
        TextView tempText;

        public AdapterViewHolder(View v) {
            super(v);
            tempText = v.findViewById(R.id.temp_text);
        }
    }

    // creates new views (invoked by the layout manager)\
    // creates cardview, creates a new viewholder and returns it
    @Override
    public AdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card, parent, false);

        AdapterViewHolder adapterViewHolder = new AdapterViewHolder(itemView);
        return adapterViewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(AdapterViewHolder holder, int position) {

        // changes the tempText
        holder.tempText.setText(Long.toString(mData.get(position)));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mData.size();
    }


}
