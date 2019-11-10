package com.usf.pickup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.usf.pickup.api.ApiClient;
import com.usf.pickup.api.ApiResult;
import com.usf.pickup.api.models.Game;
import com.usf.pickup.api.models.Weather;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DetailBottomSheetDialogFragment extends BottomSheetDialogFragment {
    private Game game;
    private SimpleDateFormat dateformat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
    private SimpleDateFormat timeformat = new SimpleDateFormat("h:mm a", Locale.US);

    public DetailBottomSheetDialogFragment(Game game) {
        this.game = game;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.bottom_sheet_game_details, container, false);

        TextView titleDetailTextView = view.findViewById(R.id.titleDetailTextView);
        TextView sportDetailTextView = view.findViewById(R.id.sportDetailTextView);
        TextView totalPlayersDetailTextView = view.findViewById(R.id.totalPlayersDetailTextView);
        TextView descriptionDetailTextView = view.findViewById(R.id.descriptionDetailTextView);
        TextView locationDetailTextView = view.findViewById(R.id.locationDetailTextView);
        TextView dateDetailTextView = view.findViewById(R.id.dateDetailTextView);
        TextView startsAtDetailTextView = view.findViewById(R.id.startsAtDetailTextView);
        TextView endsAtDetailTextView = view.findViewById(R.id.endsAtDetailTextView);
        final TextView weatherDetailTextView = view.findViewById(R.id.weatherDetailTextView);
        final TextView tempDetailTextView = view.findViewById(R.id.tempDetailTextView);
        final TextView humidityDetailTextView = view.findViewById(R.id.humidityDetailTextView);

        titleDetailTextView.setText(game.getTitle());
        sportDetailTextView.setText(game.getSport());
        totalPlayersDetailTextView.setText(game.getPlayerCount() + "/" + game.getNumberOfPlayers());
        descriptionDetailTextView.setText(game.getDescription());
        locationDetailTextView.setText(game.getAddress());
        dateDetailTextView.setText(dateformat.format(game.getStartTime()));
        startsAtDetailTextView.setText(timeformat.format(game.getStartTime()));
        endsAtDetailTextView.setText(timeformat.format(game.getEndTime()));

        ApiClient.getInstance(getContext()).getWeather(game, new ApiResult.Listener<Weather>() {
            @Override
            public void onResponse(ApiResult<Weather> response) {
                if(response.isSuccess()){
                    weatherDetailTextView.setText(getContext().getString(R.string.weather) + " " + response.getData().getDescription());
                    tempDetailTextView.setText(Double.toString((response.getData().gettemp())));
                    humidityDetailTextView.setText(Double.toString((response.getData().getHumidity())));
                }else {
                    weatherDetailTextView.setText(getContext().getString(R.string.weather) + " (Unavailable)");
                    tempDetailTextView.setText("Unknown");
                    humidityDetailTextView.setText("Unknown");
                }
            }
        });

        return view;
    }
}
