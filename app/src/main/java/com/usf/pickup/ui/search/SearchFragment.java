package com.usf.pickup.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.usf.pickup.BottomNav;
import com.usf.pickup.R;

public class SearchFragment extends Fragment {
    private final String DIALOG_FRAGMENT_TAG = "com.usf.pickup.ui.search.dialog";
    private SearchViewModel searchViewModel;
    private FilterBottomSheetDialogFragment filterBottomSheetDialogFragment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        searchViewModel =
                ViewModelProviders.of(this).get(SearchViewModel.class);
        View root = inflater.inflate(R.layout.fragment_search, container, false);

        ImageButton imageButton = (ImageButton) root.findViewById(R.id.filter_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilterOptions();
            }
        });

        return root;
    }

    public void openFilterOptions() {
        filterBottomSheetDialogFragment = new FilterBottomSheetDialogFragment();
        filterBottomSheetDialogFragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((BottomNav) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((BottomNav) getActivity()).getSupportActionBar().show();
    }
}