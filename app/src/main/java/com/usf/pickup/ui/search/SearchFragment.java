package com.usf.pickup.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

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
        final TextView textView = root.findViewById(R.id.text_dashboard);
        searchViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        // Opens Filter
        filterBottomSheetDialogFragment = new FilterBottomSheetDialogFragment();
        filterBottomSheetDialogFragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);

        return root;
    }
}