package com.usf.pickup.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.usf.pickup.BottomNav;
import com.usf.pickup.R;
import com.usf.pickup.search.SearchAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class SearchFragment extends Fragment {
    private final String DIALOG_FRAGMENT_TAG = "com.usf.pickup.ui.search.dialog";
    private SearchViewModel searchViewModel;
    private FilterBottomSheetDialogFragment filterBottomSheetDialogFragment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        View root = inflater.inflate(R.layout.fragment_search, container, false);

        ImageButton imageButton = root.findViewById(R.id.filter_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilterOptions();
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

        ListView searchList = root.findViewById(R.id.search_list_view);
        searchList.setDivider(null);
        searchList.setDividerHeight(0);
        searchList.setAdapter(new SearchAdapter(root.getContext(), mockSearchResults));

        return root;
    }

    private void openFilterOptions() {
        filterBottomSheetDialogFragment = new FilterBottomSheetDialogFragment();
        filterBottomSheetDialogFragment.show(Objects.requireNonNull(getFragmentManager()), DIALOG_FRAGMENT_TAG);
    }

    @Override
    public void onResume() {
        super.onResume();
        BottomNav bottomNav = ((BottomNav) getActivity());
        Objects.requireNonNull(Objects.requireNonNull(bottomNav).getSupportActionBar()).hide();
    }
}