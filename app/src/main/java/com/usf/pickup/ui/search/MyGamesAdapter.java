package com.usf.pickup.ui.search;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.ArrayUtils;
import com.squareup.picasso.Picasso;
import com.usf.pickup.Pickup;
import com.usf.pickup.R;
import com.usf.pickup.api.ApiClient;
import com.usf.pickup.api.ApiResult;
import com.usf.pickup.api.models.Game;
import com.usf.pickup.api.models.User;
import com.usf.pickup.helpers.RoundedCornersTransform;

public class MyGamesAdapter extends SearchAdapter {

    public MyGamesAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View row = view;

        if(row == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.search_result_layout, null);
        }

        TextView gameTitle = row.findViewById(R.id.result_title);
        TextView location = row.findViewById(R.id.result_location);
        TextView playerCount = row.findViewById(R.id.result_players);
        final ImageButton infoBtn = row.findViewById(R.id.add_game_btn);
        final Pickup pickup = (Pickup) context.getApplicationContext();
        final Game entry = searchResults.get(i);
        final User organizer = entry.getOrganizer();
        ImageView avatar = row.findViewById(R.id.avatar);

        if(organizer.getProfilePicture() != null && !organizer.getProfilePicture().isEmpty()){
            Picasso.get().load(context.getString(R.string.api_url) +
                    context.getString(R.string.static_files) + "/" + organizer.getProfilePicture())
                    .centerCrop()
                    .transform(new RoundedCornersTransform(20))
                    .fit()
                    .into(avatar);
        }

        gameTitle.setText(entry.getTitle().length() > 25 ? entry.getTitle().substring(0, 22) + "..." : entry.getTitle());
        location.setText(entry.getLocationName());
        String playerCountText = '(' + entry.getPlayerCount().toString() + '/' + entry.getNumberOfPlayers() + " Players)" ;
        playerCount.setText(playerCountText);
        infoBtn.setBackgroundResource(R.drawable.ic_cancel_black_24dp);

        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                ApiClient.getInstance(pickup).leaveGame(pickup.getJwt(), entry.get_id(), new ApiResult.Listener<Game>() {
                                    @Override
                                    public void onResponse(ApiResult<Game> response) {
                                        if(response.isSuccess()) {
                                            Toast.makeText(pickup, R.string.leave_success, Toast.LENGTH_SHORT).show();
                                            searchResults.remove(i);
                                            notifyDataSetChanged();
                                        }
                                        else {
                                            Toast.makeText(pickup, response.getErrorMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //Do nothing
                                break;
                        }
                    }
                };


                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.leave_confirmation).setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

        return row;
    }
}
