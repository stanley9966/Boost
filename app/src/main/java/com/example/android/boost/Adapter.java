package com.example.android.boost;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Adapter extends RecyclerView.Adapter<Adapter.AdapterViewHolder> {

    private ArrayList<Long> mData;
    private HashMap<Long, Boolean> mMapData;

    public Adapter(ArrayList<Long> arrayList, HashMap<Long, Boolean> map) {
        // getting rid of the nulls and removing correspoding arraylist elements
        Iterator<Long> itr = arrayList.iterator();
        while(itr.hasNext()) {
            Long key = itr.next();
            if (map.get(key) == null) {
                map.remove(key);
                itr.remove();
            }
        }
        mData = arrayList;
        mMapData = map;
    }

    // inner class ViewHolder
    public class AdapterViewHolder extends RecyclerView.ViewHolder {
        TextView winloss;
        TextView tempText;

        public AdapterViewHolder(View v) {
            super(v);
            tempText = v.findViewById(R.id.temp_text);
            winloss = v.findViewById(R.id.winloss);
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
    // DON'T EVEN SHOW IF THE VALUE OF THE BOOLEAN IS NULL, MEANING THAT ONLY SUMMONER1 IS IN THAT GAME
    @Override
    public void onBindViewHolder(AdapterViewHolder holder, int position) {
        Long matchKey = mData.get(position);
        holder.tempText.setText(Long.toString(matchKey));


        Boolean winLoss = mMapData.get(matchKey);
        if (winLoss == null) {
            holder.winloss.setText(".");
        } else if (winLoss) {
            holder.winloss.setText("W");
        } else {
            holder.winloss.setText("L");
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mData.size();
    }


}
