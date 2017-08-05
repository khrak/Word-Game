package com.example.khrak.wordgame.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.khrak.wordgame.Game.Card;
import com.example.khrak.wordgame.R;

import java.util.ArrayList;

/**
 * Created by khrak on 8/5/17.
 */

public class GridViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Card> list;

    public GridViewAdapter(Context context, ArrayList<Card> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = null;
        GridViewHolder holder = null;

        try {
            if (convertView != null) {
                listItem = convertView;
                holder = (GridViewHolder) listItem.getTag();
            } else {
                listItem = View.inflate(context, R.layout.gridview_item, null);

                Button symbol = (Button) listItem.findViewById(R.id.letter_view);
                TextView score = (TextView) listItem.findViewById(R.id.symbol_score_view);

                holder = new GridViewHolder();

                holder.symbol = symbol;
                holder.score = score;

                listItem.setTag(holder);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Card card = (Card) getItem(position);

        holder.symbol.setText(card.symbol);
        holder.score.setText("" + card.score);

        holder.symbol.setTag("" + card.score);

        return listItem;
    }

    public class GridViewHolder {
        Button symbol;
        TextView score;
    }
}
