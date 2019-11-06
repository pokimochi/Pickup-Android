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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.common.util.ArrayUtils;
import com.usf.pickup.BottomNav;
import com.usf.pickup.R;
import com.usf.pickup.api.ApiResult;
import com.usf.pickup.api.models.Game;
import com.usf.pickup.api.models.MyGames;
import com.usf.pickup.api.models.User;
import com.usf.pickup.ui.search.SearchAdapter;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private boolean editMode;
    private SearchAdapter gamesAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        final ImageView profilePicture = root.findViewById(R.id.profile_pic);
        final EditText profileName = root.findViewById(R.id.display_name);
        final EditText profileDesc = root.findViewById(R.id.profile_description);
        final ImageButton editBtn = root.findViewById(R.id.edit_btn);
        final ListView myGamesList = root.findViewById(R.id.current_games_list_view);

        profileViewModel.searchMyGames();

        gamesAdapter = new SearchAdapter(root.getContext());
        profileViewModel.getMyGames().observe(this, new Observer<ApiResult<MyGames>>() {

            @Override
            public void onChanged(ApiResult<MyGames> apiResult) {
                gamesAdapter.updateResults(ArrayUtils.concat(apiResult.getData().getGames(), apiResult.getData().getOrganizedGames()));
            }
        });

        editMode = false;

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
                    profileDesc.setText(user.getProfileDescription());
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

        myGamesList.setDivider(null);
        myGamesList.setDividerHeight(0);
        myGamesList.setAdapter(gamesAdapter);

        editBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(editMode) {
                    profileViewModel.updateDisplayName(profileName.getText().toString());
                    profileViewModel.updateProfileDescription(profileDesc.getText().toString());
                    Toast.makeText(getContext(), "Profile changes saved!", Toast.LENGTH_SHORT).show();
                    editBtn.setImageResource(R.drawable.ic_edit_black_24dp);
                    profileName.setEnabled(false);
                    profileDesc.setEnabled(false);

                }
                else {
                    editBtn.setImageResource(R.drawable.ic_done_black_24dp);
                    profileName.setEnabled(true);
                    profileDesc.setEnabled(true);
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