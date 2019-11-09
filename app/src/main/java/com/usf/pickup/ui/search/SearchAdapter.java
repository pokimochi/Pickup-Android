package com.usf.pickup.ui.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.usf.pickup.R;
import com.usf.pickup.api.models.Game;
import com.usf.pickup.api.models.User;
import com.usf.pickup.helpers.RoundedCornersTransform;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;


public class SearchAdapter extends BaseAdapter {

    private Context context;
    private Game[] searchResults;
    private static LayoutInflater inflater = null;

    public SearchAdapter(Context context) {
        this.context = context;
        this.searchResults = new Game[]{};
    }

    public void updateResults(Game[] searchResults){
        this.searchResults = searchResults;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return searchResults.length;
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

        Game entry = searchResults[i];
        User user = entry.getOrganizer();
        if(user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()){
            Picasso.get().load(context.getString(R.string.api_url) +
                    context.getString(R.string.static_files) + "/" + user.getProfilePicture())
                    .centerCrop()
                    .transform(new RoundedCornersTransform(20))
                    .fit()
                    .into(avatar);
        }
        gameTitle.setText(entry.getTitle());
        location.setText(entry.getLocationName());
        String playerCountText = '(' + entry.getPlayerCount().toString() + '/' + entry.getNumberOfPlayers() + " Players)" ;
        playerCount.setText(playerCountText);
        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return row;
    }
}
