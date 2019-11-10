package com.usf.pickup.ui.search;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.opengl.Visibility;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.squareup.picasso.Picasso;
import com.usf.pickup.DetailBottomSheetDialogFragment;
import com.usf.pickup.Pickup;
import com.usf.pickup.R;
import com.usf.pickup.api.ApiClient;
import com.usf.pickup.api.ApiResult;
import com.usf.pickup.api.models.Game;
import com.usf.pickup.api.models.User;
import com.usf.pickup.helpers.RoundedCornersTransform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SearchAdapter extends BaseAdapter {

    protected Context context;
    protected List<Game> searchResults;
    protected static LayoutInflater inflater = null;
    protected final String DIALOG_FRAGMENT_TAG = "com.usf.pickup.ui.search.detail";

    public SearchAdapter(Context context) {
        this.context = context;
        this.searchResults = new ArrayList<>();
    }

    public void updateResults(Game[] searchResults){
        this.searchResults = new ArrayList<>(Arrays.asList(searchResults));
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return searchResults.size();
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
        final Pickup pickup = (Pickup) context.getApplicationContext();
        final Game entry = searchResults.get(i);

        final View row = drawRow(view, entry);

        final ImageButton infoBtn = row.findViewById(R.id.add_game_btn);
        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(entry.hasJoined()) {
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
                                                entry.setHasJoined(response.getData().hasJoined());
                                                entry.setUsers(response.getData().getUsers());
                                                drawRow(row, entry);
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
                else {
                    ApiClient.getInstance(pickup).joinGame(pickup.getJwt(), entry.get_id(), new ApiResult.Listener<Game>() {
                        @Override
                        public void onResponse(ApiResult<Game> response) {
                            if(response.isSuccess()) {
                                Toast.makeText(pickup, R.string.join_success, Toast.LENGTH_SHORT).show();
                                entry.setHasJoined(response.getData().hasJoined());
                                entry.setUsers(response.getData().getUsers());
                                drawRow(row, entry);
                            }
                            else {
                                Toast.makeText(pickup, response.getErrorMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        return row;
    }

    protected View drawRow(View row, final Game entry){
        if(row == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.search_result_layout, null);
        }

        TextView gameTitle = row.findViewById(R.id.result_title);
        TextView location = row.findViewById(R.id.result_location);
        TextView playerCount = row.findViewById(R.id.result_players);
        final ImageButton infoBtn = row.findViewById(R.id.add_game_btn);
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
        String playerCountText = '(' + entry.getPlayerCount().toString() + '/' + entry.getNumberOfPlayers().toString() + " Players)" ;
        playerCount.setText(playerCountText);
        infoBtn.setBackgroundResource(entry.hasJoined() ? R.drawable.ic_cancel_black_24dp : R.drawable.ic_add_black_24dp);
        infoBtn.setVisibility(entry.isOrganizer() || (entry.getPlayerCount().equals(entry.getNumberOfPlayers()) && !entry.hasJoined()) ? View.INVISIBLE : View.VISIBLE);

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailBottomSheetDialogFragment detailBottomSheetDialogFragment = new DetailBottomSheetDialogFragment(entry);
                detailBottomSheetDialogFragment.show(((FragmentActivity) context).getSupportFragmentManager(), DIALOG_FRAGMENT_TAG);
            }
        });

        return row;
    }
}
