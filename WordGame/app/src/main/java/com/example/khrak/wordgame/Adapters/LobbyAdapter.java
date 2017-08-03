package com.example.khrak.wordgame.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.khrak.wordgame.Model.Room;
import com.example.khrak.wordgame.R;

import java.util.ArrayList;

/**
 * Created by khrak on 8/2/17.
 */

public class LobbyAdapter extends BaseAdapter {

    private ArrayList<Room> rooms;
    private Context context;

    public LobbyAdapter(ArrayList<Room> rooms, Context context) {
        this.rooms = rooms;
        this.context = context;
    }

    @Override
    public int getCount() {
        return rooms.size();
    }

    @Override
    public Object getItem(int position) {
        return rooms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView;
        ViewHolder viewHolder = null;

        if(convertView == null){
            rootView = View.inflate(context, R.layout.roomslist_item, null);

            TextView roomName = (TextView) rootView.findViewById(R.id.room_name);
            TextView numPlayers = (TextView) rootView.findViewById(R.id.num_players);
            TextView privacy = (TextView) rootView.findViewById(R.id.privacy_tag);
            TextView id = (TextView) rootView.findViewById(R.id.room_id);

            viewHolder = new ViewHolder();

            viewHolder.name = roomName;
            viewHolder.numPlayers = numPlayers;
            viewHolder.privacy = privacy;
            viewHolder.id = id;

            rootView.setTag(viewHolder);
        }else{
            rootView = convertView;
            viewHolder = (ViewHolder) rootView.getTag();
        }

        Room room = (Room) getItem(position);

        viewHolder.name.setText(room.getName());
        viewHolder.privacy.setText(room.getPrivacy());
        viewHolder.numPlayers.setText("" + room.getNumPlayers());
        viewHolder.id.setText(room.getId());

        return rootView;
    }

    private class ViewHolder {
        private TextView name, numPlayers, privacy, id;
    }
}
