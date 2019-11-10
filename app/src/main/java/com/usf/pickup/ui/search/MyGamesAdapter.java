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

public class MyGamesAdapter extends SearchAdapter {

    public MyGamesAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final Pickup pickup = (Pickup) context.getApplicationContext();
        final Game entry = searchResults.get(i);

        view = drawRow(view, entry);

        final ImageButton infoBtn = view.findViewById(R.id.add_game_btn);
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

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailBottomSheetDialogFragment detailBottomSheetDialogFragment = new DetailBottomSheetDialogFragment(entry);
                detailBottomSheetDialogFragment.show(((FragmentActivity) context).getSupportFragmentManager(), DIALOG_FRAGMENT_TAG);
            }
        });

        return view;
    }
}
