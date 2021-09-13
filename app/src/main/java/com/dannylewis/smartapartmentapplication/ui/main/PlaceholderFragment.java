package com.dannylewis.smartapartmentapplication.ui.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import androidx.lifecycle.ViewModelProvider;

import com.dannylewis.smartapartmentapplication.R;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_light_scheduler, container, false);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        //On Create Code Here:
        LinearLayout linLay = getView().findViewById(R.id.relLay);

        //Check for existing actions for current day of week
        int index = -1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);

            switch (index) {
                case 1: //Sunday
                    setActionText('U', linLay);
                    break;
                case 2: //Monday
                    setActionText('M', linLay);
                    break;
                case 3: //Tuesday
                    setActionText('T', linLay);
                    break;
                case 4: //Wednesday
                    setActionText('W', linLay);
                    break;
                case 5: //Thursday
                    setActionText('R', linLay);
                    break;
                case 6: //Friday
                    setActionText('F', linLay);
                    break;
                case 7: //Saturday
                    setActionText('A', linLay);
                    break;

            }
        }
        //IF NONE -- Display none message
        //IF YES -- Populate screen with each action
        //
    }

    private void setActionText(char weekDay, LinearLayout linLay) {
        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("ACTIONS", Context.MODE_PRIVATE);

        ArrayList<String> actionList = new ArrayList<String>();

        Map<String, ?> allEntries = sharedPref.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().charAt(0) == '$') {
                String actionCode = entry.getValue().toString();
                if (actionCode.charAt(0) == weekDay)
                    actionList.add(entry.getKey() +"$" + hourToString(actionCode.substring(1,3)) + ":" + actionCode.substring(3,5) + AMorPM(actionCode.substring(1,3)) + "\nSet brightness to: " + Integer.parseInt(actionCode.substring(6, 9)) + "%" + "\nSet warmth to: " + Integer.parseInt(actionCode.substring(9)) + "%");
            }
        }

        if (actionList.size() == 0) {
            TextView TV = new TextView(this.getActivity());
            TV.setText("There are no scheduled actions on this day.\n\nCreate one by clicking the (+) icon below!\n\nTap on a created action to delete it.");
            TV.setTextSize(20);
            TV.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            TV.setTextColor(Color.parseColor("#FFFFFF"));

            linLay.addView(TV);
        }
        else {

            //TODO: Create a comparator and sort the arraylist in chronological order

            for( int i = 0; i < actionList.size(); i++ )
            {
                TextView textView = new TextView(this.getActivity());

                String s = actionList.get(i);

                textView.setText(s.substring(s.lastIndexOf("$")+1));

                textView.setPadding(8, 16, 8, 16);
                textView.setTextSize(20);
                textView.setTextColor(Color.parseColor("#FFFFFF"));


                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);


                textView.setId(7777777 + Integer.parseInt(s.substring(1, s.lastIndexOf("$"))));

                textView.setOnClickListener(view -> {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
                    builder.setMessage("Do you want to delete this action?");

                    builder.setPositiveButton("Delete", (dialog, id) -> {
                        // User clicked OK button
                        sharedPref.edit().remove("$" + ((int)view.getId() - 7777777)).commit();
                        FragmentTransaction ftr = getFragmentManager().beginTransaction();
                        ftr.detach(PlaceholderFragment.this).attach(PlaceholderFragment.this).commit();


                    });
                    builder.setNegativeButton("Keep", (dialog, id) -> {
                        // User cancelled the dialog
                    });

                   builder.create().show();

                });

                linLay.addView(textView);

            }
        }
    }

    private String hourToString(String hour) {
        int h = Integer.parseInt(hour);
        if (h > 11) {
            h -= 12;
        }
        h++;

        return "" + h;
    }

    private String AMorPM(String hour) {
        if (Integer.parseInt(hour) > 11)
            return "AM";
        else
            return "PM";
    }
}