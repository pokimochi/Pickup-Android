package com.usf.pickup.ui.profile;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.usf.pickup.BottomNav;
import com.usf.pickup.R;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        final ImageView profilePicture = root.findViewById(R.id.profile_pic);
        final TextView profileName = root.findViewById(R.id.display_name);

        profileViewModel.getDisplayName().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                profileName.setText(s);
            }
        });

        // Uncomment this once uri's are being set for the profile picture
//        profileViewModel.getProfilePicturePath().observe(this, new Observer<Uri>() {
//            @Override
//            public void onChanged(Uri s) {
//                profilePicture.setImageURI(s);
//            }
//        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((BottomNav) Objects.requireNonNull(getActivity())).getSupportActionBar()).show();
    }
}