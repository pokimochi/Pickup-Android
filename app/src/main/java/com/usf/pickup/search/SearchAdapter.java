package com.usf.pickup.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.usf.pickup.R;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;


public class SearchAdapter extends BaseAdapter {

    private Context context;
    private JSONArray searchResults;
    private static LayoutInflater inflater = null;

    public SearchAdapter(Context context, JSONArray searchResults) {
        this.context = context;
        this.searchResults = searchResults;
    }

    @Override
    public int getCount() {
        return searchResults.length();
    }

    @Override
    public Object getItem(int i) {
        return getItemId(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row = view;

        if(row == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            row = inflater.inflate(R.layout.search_result_layout, null);
        }

        ImageView avatar = row.findViewById(R.id.avatar);
        TextView gameTitle = row.findViewById(R.id.result_title);
        TextView location = row.findViewById(R.id.result_location);
        TextView playerCount = row.findViewById(R.id.result_players);
        ImageButton infoBtn = row.findViewById(R.id.add_game_btn);

        try {
            JSONObject entry = searchResults.getJSONObject(i);
//            I'm not sure how the image will be stored
//            avatar.setImageResource(searchResults.getJSONObject(i).getString("avatar"));
            gameTitle.setText(entry.getString("title"));
            location.setText(entry.getString("location"));
            String playerCountText = '(' + entry.getString("currentPlayers") + '/' + entry.getString("totalPlayers") + " Players)" ;
            playerCount.setText(playerCountText);
            infoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return row;
    }
}
