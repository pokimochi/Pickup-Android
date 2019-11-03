package com.usf.pickup.ui.profile;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.usf.pickup.BottomNav;
import com.usf.pickup.R;
import com.usf.pickup.api.models.User;
import com.usf.pickup.search.SearchAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        final ImageView profilePicture = root.findViewById(R.id.profile_pic);
        final TextView profileName = root.findViewById(R.id.display_name);

        profileViewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null){
                    profileName.setText(user.getDisplayName());
                }
            }
        });

        JSONArray mockSearchResults = new JSONArray();

        for(int i = 0; i < 10; i++) {
            // Create mock data
            JSONObject searchEntry = new JSONObject();
            try {
                searchEntry.put("host", "John Doe");
                searchEntry.put("title", "Casual 1-on-1");
                searchEntry.put("sport", "Tennis");
                searchEntry.put("currentPlayers", "2");
                searchEntry.put("totalPlayers", "4");
                searchEntry.put("location", "Varsity Tennis Court");
                searchEntry.put("date", "10/31/2019");
                searchEntry.put("startTime", "10:00 AM");
                searchEntry.put("endTime", "12:00 PM");
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            mockSearchResults.put(searchEntry);
        }

        ListView searchList = root.findViewById(R.id.current_games_list_view);
        searchList.setDivider(null);
        searchList.setDividerHeight(0);
        searchList.setAdapter(new SearchAdapter(root.getContext(), mockSearchResults));

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((BottomNav) Objects.requireNonNull(getActivity())).getSupportActionBar()).show();
    }
}