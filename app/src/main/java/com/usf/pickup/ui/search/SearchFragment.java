package com.usf.pickup.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.usf.pickup.BottomNav;
import com.usf.pickup.R;

import java.util.List;

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

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        final Spinner sportSpinner = root.findViewById(R.id.search_spinner);

        searchViewModel.getAvailableSports().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                if(strings != null){
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                            android.R.layout.simple_spinner_item, strings.toArray(new String[0]));
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sportSpinner.setAdapter(adapter);
                }
            }
        });

        return root;
    }

    public void openFilterOptions() {
        filterBottomSheetDialogFragment = new FilterBottomSheetDialogFragment();
        filterBottomSheetDialogFragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
    }
}