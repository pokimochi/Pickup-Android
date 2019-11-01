package com.usf.pickup.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.usf.pickup.BottomNav;
import com.usf.pickup.R;
import com.usf.pickup.search.SearchAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import java.util.List;

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

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        final Spinner sportSpinner = root.findViewById(R.id.search_spinner);

        searchViewModel.getAvailableSports().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                if(strings != null){
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                            R.layout.spinner_text_color, strings.toArray(new String[0]));
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown);
                    sportSpinner.setAdapter(adapter);
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