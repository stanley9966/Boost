package com.example.android.boost;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
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

    private StaticData sd = new StaticData();
    Context context;

    // inner class ViewHolder
    public class AdapterViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;

//        TextView winloss;
//        TextView tempText;

        ImageView summoner1ChampImageView;
        ImageView summoner1Spell1;
        ImageView summoner1Spell2;

        ImageView summoner2ChampImageView;
        ImageView summoner2Spell1;
        ImageView summoner2Spell2;

        TextView sum1kda;
        TextView sum1damage;

        TextView sum2kda;
        TextView sum2damage;

        public AdapterViewHolder(View v) {
            super(v);
            cardView = v.findViewById(R.id.card_view);
            summoner1ChampImageView = v.findViewById(R.id.champion_splash_art);
            summoner2ChampImageView = v.findViewById(R.id.champion2_splash_art);
            summoner1Spell1 = v.findViewById(R.id.summoner1_spell1);
            summoner1Spell2 = v.findViewById(R.id.summoner1_spell2);
            summoner2Spell1 = v.findViewById(R.id.summoner2_spell1);
            summoner2Spell2 = v.findViewById(R.id.summoner2_spell2);
            sum1kda = v.findViewById(R.id.kda);
            sum1damage = v.findViewById(R.id.damage);
            sum2kda = v.findViewById(R.id.kda2);
            sum2damage = v.findViewById(R.id.damage2);
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
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(AdapterViewHolder holder, int position) {
        Long matchKey = MainScreenActivity.mMatchArrayList.get(position);
//        holder.tempText.setText(Long.toString(matchKey));

        MainScreenActivity.Info player1Info = MainScreenActivity.infoArrayList.get(position*2);
        MainScreenActivity.Info player2Info = MainScreenActivity.infoArrayList.get((position*2) + 1);

        String champ1String = StaticData.idToString(player1Info.championId);
        String summoner1Spell1String = StaticData.mSummonerSpellsHashMap.get(player1Info.spell1Id);
        String summoner1Spell2String = StaticData.mSummonerSpellsHashMap.get(player1Info.spell2Id);
        // summoner2
        String champ2String = StaticData.idToString(player2Info.championId);
        String summoner2Spell1String = StaticData.mSummonerSpellsHashMap.get(player2Info.spell1Id);
        String summoner2Spell2String = StaticData.mSummonerSpellsHashMap.get(player2Info.spell2Id);

        // loading in summoner1 images
        Picasso.with(context).load("http://ddragon.leagueoflegends.com/cdn/8.1.1/img/champion/" + champ1String + ".png")
                .into(holder.summoner1ChampImageView);
        Picasso.with(context).load("http://ddragon.leagueoflegends.com/cdn/8.1.1/img/spell/" + summoner1Spell1String + ".png")
                .into(holder.summoner1Spell1);
        Picasso.with(context).load("http://ddragon.leagueoflegends.com/cdn/8.1.1/img/spell/" + summoner1Spell2String + ".png")
                .into(holder.summoner1Spell2);
//        // summoner2 images
        Picasso.with(context).load("http://ddragon.leagueoflegends.com/cdn/8.1.1/img/champion/" + champ2String + ".png")
                .into(holder.summoner2ChampImageView);
        Picasso.with(context).load("http://ddragon.leagueoflegends.com/cdn/8.1.1/img/spell/" + summoner2Spell1String + ".png")
                .into(holder.summoner2Spell1);
        Picasso.with(context).load("http://ddragon.leagueoflegends.com/cdn/8.1.1/img/spell/" + summoner2Spell2String + ".png")
                .into(holder.summoner2Spell2);

        String sum1KillsString = Integer.toString(player1Info.kills);
        String sum1AssistsString = Integer.toString(player1Info.assists);
        String sum1DeathsString = Integer.toString(player1Info.deaths);
        String sum1Damage = Integer.toString(player1Info.totalDamageDealt);

        int sum2KillsString = player2Info.kills;
        int sum2AssistsString = player2Info.assists;
        int sum2DeathsString = player2Info.deaths;
        int sum2Damage = player2Info.totalDamageDealt;

        // writing sum1 two textviews
        holder.sum1kda.setText(sum1KillsString + "/" + sum1AssistsString + "/" + sum1DeathsString);
        holder.sum1damage.setText(sum1Damage + " dam");
        // writing sum2 two textviews
        holder.sum2kda.setText(sum2KillsString + "/" + sum2AssistsString + "/" + sum2DeathsString);
        holder.sum2damage.setText(sum2Damage + " dam");

        // changing color scheme of each card
        Boolean winLoss = MainScreenActivity.mGameIdsAndWinLossMap.get(matchKey);
        if (winLoss == null) {
//            holder.winloss.setText(".");
        } else if (winLoss) {
            //cannot set individual view theme dynamically, can only do so with activities before onCreate
//            holder.winloss.setText("W");
            holder.cardView.setCardBackgroundColor(Color.GREEN);
            //holder.cardView.setBackgroundColor(ContextCompat.getColor(context, R.color.lighterGreen));
            holder.sum1kda.setTextColor(ContextCompat.getColor(context, R.color.darkerGreen));
            holder.sum1damage.setTextColor(ContextCompat.getColor(context, R.color.darkerGreen));
        } else {
//            holder.winloss.setText("L");
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.lightBackgroundColorRed));
            holder.sum1kda.setTextColor(ContextCompat.getColor(context, R.color.lightBackgroundColorTextRed));
            holder.sum2kda.setTextColor(ContextCompat.getColor(context, R.color.lightBackgroundColorTextRed));
            holder.sum1damage.setTextColor(ContextCompat.getColor(context, R.color.lightBackgroundColorTextRed));
            holder.sum2damage.setTextColor(ContextCompat.getColor(context, R.color.lightBackgroundColorTextRed));

        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return MainScreenActivity.mMatchArrayList.size();
    }


}
