package com.example.khrak.wordgame.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.khrak.wordgame.Model.ScoreboardItem;
import com.example.khrak.wordgame.R;

import java.util.ArrayList;

/**
 * Created by khrak on 8/6/17.
 */

public class ScoreboardAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ScoreboardItem> list;

    public ScoreboardAdapter(Context context, ArrayList<ScoreboardItem> list) {
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
        ViewHolder holder;

        if(convertView != null){
            listItem = convertView;
            holder = (ViewHolder) listItem.getTag();
        } else {
            listItem = View.inflate(context, R.layout.scoreboard_listviewitem, null);

            TextView guessedWord = (TextView) listItem.findViewById(R.id.guessed_word_view);
            TextView scoreView = (TextView) listItem.findViewById(R.id.score_view);
            ImageView icon = (ImageView) listItem.findViewById(R.id.scoreboard_item_image);

            holder = new ViewHolder();

            holder.guessedword = guessedWord;
            holder.profileImage = icon;
            holder.score = scoreView;

            listItem.setTag(holder);
        }

        ScoreboardItem item = (ScoreboardItem) getItem(position);

        holder.guessedword.setText(item.guessedWord);
        holder.profileImage.setImageResource(avatars[item.userimage]);
        holder.score.setText("" + item.score);

        try {
            if (item.winner) {
                listItem.setBackgroundColor(Color.parseColor("#DCA32B"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return listItem;
    }

    public class ViewHolder {
        TextView guessedword, score;
        ImageView profileImage;
    }

    private int[] avatars = new int[] {
            R.drawable.avatar_icon1,
            R.drawable.avatar_icon2,
            R.drawable.avatar_icon3,
            R.drawable.avatar_icon4,
            R.drawable.avatar_icon5,
            R.drawable.avatar_icon6,
            R.drawable.avatar_icon7,
            R.drawable.avatar_icon8,
            R.drawable.avatar_icon9,
            R.drawable.avatar_icon10
    };
}
