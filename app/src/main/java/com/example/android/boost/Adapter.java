package com.example.android.boost;

import android.content.Context;
import android.graphics.Color;
import android.icu.text.IDNA;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class Adapter extends RecyclerView.Adapter<Adapter.AdapterViewHolder> {
    private static final String TAG = Adapter.class.toString();

    Context context;

    // inner class ViewHolder
    public class AdapterViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView winloss;
        TextView tempText;
        ImageView imageView;

        public AdapterViewHolder(View v) {
            super(v);
            tempText = v.findViewById(R.id.temp_text);
            winloss = v.findViewById(R.id.winloss);
            cardView = v.findViewById(R.id.card_view);
            imageView = v.findViewById(R.id.champion_splash_art);
        }
    }

    // creates new views (invoked by the layout manager)\
    // creates cardview, creates a new viewholder and returns it
    @Override
    public AdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card, parent, false);

        AdapterViewHolder adapterViewHolder = new AdapterViewHolder(itemView);
        return adapterViewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    // DON'T EVEN SHOW IF THE VALUE OF THE BOOLEAN IS NULL, MEANING THAT ONLY SUMMONER1 IS IN THAT GAME
    @Override
    public void onBindViewHolder(AdapterViewHolder holder, int position) {
        Long matchKey = MainScreenActivity.mMatchArrayList.get(position);
        holder.tempText.setText(Long.toString(matchKey));

        MainScreenActivity.Info player1Info = MainScreenActivity.infoArrayList.get(position*2);
        MainScreenActivity.Info player2Info = MainScreenActivity.infoArrayList.get((position*2) + 1);
        int champ1Id = player1Info.championId;
        String champ1String = StaticData.idToString(champ1Id);
        Log.d(TAG, "onBindViewHolder: " + player1Info.championId);

        Boolean winLoss = MainScreenActivity.mGameIdsAndWinLossMap.get(matchKey);
        if (winLoss == null) {
            holder.winloss.setText(".");
        } else if (winLoss) {
            //cannot set individual view theme dynamically, can only do so with activities before onCreate
            holder.winloss.setText("W");
            holder.cardView.setBackgroundColor(Color.GREEN);
            //holder.cardView.setBackgroundColor(ContextCompat.getColor(context, R.color.lighterGreen));
            holder.winloss.setTextColor(ContextCompat.getColor(context, R.color.darkerGreen));
            holder.tempText.setTextColor(ContextCompat.getColor(context, R.color.darkerGreen));
        } else {
            holder.winloss.setText("L");
            holder.cardView.setBackgroundColor(ContextCompat.getColor(context, R.color.lightBackgroundColorRed));
            holder.winloss.setTextColor(ContextCompat.getColor(context, R.color.lightBackgroundColorTextRed));
            holder.tempText.setTextColor(ContextCompat.getColor(context, R.color.lightBackgroundColorTextRed));
        }
        Picasso.with(context).load("http://ddragon.leagueoflegends.com/cdn/8.1.1/img/champion/" + champ1String + ".png")
                .into(holder.imageView);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return MainScreenActivity.mMatchArrayList.size();
    }


}
