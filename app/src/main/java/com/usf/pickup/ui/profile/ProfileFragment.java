package com.usf.pickup.ui.profile;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.usf.pickup.BottomNav;
import com.usf.pickup.R;
import com.usf.pickup.api.models.User;
import com.usf.pickup.ui.search.SearchAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private boolean editMode = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        final ImageView profilePicture = root.findViewById(R.id.profile_pic);
        final EditText profileName = root.findViewById(R.id.display_name);
        final EditText profileDesc = root.findViewById(R.id.profile_description);
        final ImageButton editBtn = root.findViewById(R.id.edit_btn);
        final ListView searchList = root.findViewById(R.id.current_games_list_view);

        final JSONArray mockSearchResults = new JSONArray();

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

        profileName.setEnabled(false);
        profileDesc.setEnabled(false);

        profileViewModel.getProfileFormState().observe(this, new Observer<ProfileFormState>() {
            @Override
            public void onChanged(@Nullable ProfileFormState profileFormState) {
                if (profileFormState == null) {
                    return;
                }
                editBtn.setEnabled(profileFormState.isDataValid());

                // Clear existing errors
                profileName.setError(null);
                profileDesc.setError(null);

                if (profileFormState.getNameError() != null) {
                    profileName.setError(getString(profileFormState.getNameError()));
                }
                if (profileFormState.getDescriptionError() != null) {
                    profileDesc.setError(getString(profileFormState.getDescriptionError()));
                }
            }
        });

        profileViewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null){
                    profileName.setText(user.getDisplayName());
                }
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                profileViewModel.profileDataChanged(profileName.getText().toString(), profileDesc.getText().toString());
            }
        };

        profileName.addTextChangedListener(afterTextChangedListener);
        profileDesc.addTextChangedListener(afterTextChangedListener);

        searchList.setDivider(null);
        searchList.setDividerHeight(0);
        searchList.setAdapter(new SearchAdapter(root.getContext()));

        editBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(editMode) {
                    editBtn.setImageResource(R.drawable.ic_done_black_24dp);
                    profileName.setEnabled(true);
                    profileDesc.setEnabled(true);
                }
                else {
                    editBtn.setImageResource(R.drawable.ic_edit_black_24dp);
                    profileName.setEnabled(false);
                    profileDesc.setEnabled(false);
                }

                editMode = !editMode;
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((BottomNav) Objects.requireNonNull(getActivity())).getSupportActionBar()).show();
    }
}