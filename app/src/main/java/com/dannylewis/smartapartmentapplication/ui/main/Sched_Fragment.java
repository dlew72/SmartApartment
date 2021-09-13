package com.dannylewis.smartapartmentapplication.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
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

import com.dannylewis.smartapartmentapplication.LightSchedulerActivity;
import com.dannylewis.smartapartmentapplication.R;

import com.dannylewis.smartapartmentapplication.ShadeSchedulerActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class Sched_Fragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    public static Sched_Fragment newInstance(int index) {
        Sched_Fragment fragment = new Sched_Fragment();
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
        View root = inflater.inflate(R.layout.fragment_scheduler, container, false);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (getActivity() instanceof LightSchedulerActivity) {
            handleLightScheduler();
        }

        if (getActivity() instanceof ShadeSchedulerActivity) {
            handleShadeScheduler();
        }
    }

    private void handleLightScheduler() {
        //On Create Code Here:
        LinearLayout linLay = getView().findViewById(R.id.relLay);

        //Check for existing actions for current day of week
        int index = -1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);

            switch (index) {
                case 1: //Sunday
                    displayLightActions('U', linLay);
                    break;
                case 2: //Monday
                    displayLightActions('M', linLay);
                    break;
                case 3: //Tuesday
                    displayLightActions('T', linLay);
                    break;
                case 4: //Wednesday
                    displayLightActions('W', linLay);
                    break;
                case 5: //Thursday
                    displayLightActions('R', linLay);
                    break;
                case 6: //Friday
                    displayLightActions('F', linLay);
                    break;
                case 7: //Saturday
                    displayLightActions('A', linLay);
                    break;
            }
        }
    }

    private void handleShadeScheduler() {
        //On Create Code Here:
        LinearLayout linLay = getView().findViewById(R.id.relLay);

        //Check for existing actions for current day of week
        int index = -1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);

            switch (index) {
                case 1: //Sunday
                    displayShadeActions('U', linLay);
                    break;
                case 2: //Monday
                    displayShadeActions('M', linLay);
                    break;
                case 3: //Tuesday
                    displayShadeActions('T', linLay);
                    break;
                case 4: //Wednesday
                    displayShadeActions('W', linLay);
                    break;
                case 5: //Thursday
                    displayShadeActions('R', linLay);
                    break;
                case 6: //Friday
                    displayShadeActions('F', linLay);
                    break;
                case 7: //Saturday
                    displayShadeActions('A', linLay);
                    break;
            }
        }
    }

    private void displayLightActions(char weekDay, LinearLayout linLay) {
        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("ACTIONS", Context.MODE_PRIVATE);

        ArrayList<String> actionList = new ArrayList<String>();

        Map<String, ?> allEntries = sharedPref.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().charAt(0) == '$') {
                String actionCode = entry.getValue().toString();
                if (actionCode.charAt(0) == weekDay && actionCode.charAt(5) == 'L') //ensure it is a light action and on the proper day
                    actionList.add(entry.getKey() +"$" + hourToString(actionCode.substring(1,3)) + ":" + actionCode.substring(3,5) + AMorPM(actionCode.substring(1,3)) + "\nSet brightness to: " + Integer.parseInt(actionCode.substring(6, 9)) + "%" + "\nSet warmth to: " + Integer.parseInt(actionCode.substring(9)) + "%");
            }
        }
        if (actionList.size() == 0) {
            TextView TV = new TextView(this.getActivity());
            TV.setText("There are no scheduled actions on this day.\n\nCreate one by clicking the (+) icon below!\n\nTap on a created action to delete it.");
            TV.setTextSize(20);
            TV.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            TV.setTextColor(Color.parseColor("#FFFFFF"));

            TV.setPadding(0, 24, 0, 24);
            TV.setElevation(6);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(16,32,16,32);
            TV.setLayoutParams(params);

            linLay.addView(TV);

        }
        else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && actionList.size() > 1) {
                Comparator<String> actionListComparator = new Comparator<String>(){

                    @Override
                    public int compare(String s1, String t1) {

                        int hour1 = Integer.parseInt(s1.substring(s1.lastIndexOf("$")+1, s1.indexOf(":")));
                        int hour2 = Integer.parseInt(t1.substring(t1.lastIndexOf("$")+1, t1.indexOf(":")));


                        if (s1.charAt(s1.indexOf(":")+3) == 'P')
                            hour1 += 12;

                        if (t1.charAt(t1.indexOf(":")+3) == 'P')
                            hour2 += 12;

                        if (hour1 < hour2)
                            return -1;
                        if (hour1 > hour2)
                            return 1;

                        //Hours must be the same, check minutes
                        int min1 = Integer.parseInt(s1.substring(s1.indexOf(':')+1, s1.indexOf(':') + 3));
                        int min2 = Integer.parseInt(t1.substring(t1.indexOf(':')+1, t1.indexOf(':') + 3));


                        if (min1 < min2)
                            return -1;
                        if (min1 > min2)
                            return 1;

                        //minutes are same, equal
                        return 0;
                    }

                };

                actionList.sort(actionListComparator);

            }

            for( int i = 0; i < actionList.size(); i++ )
            {
                TextView textView = new TextView(this.getActivity());

                String s = actionList.get(i);

                textView.setText(s.substring(s.lastIndexOf("$")+1));

                textView.setPadding(8, 16, 8, 16);
                textView.setTextSize(20);
                textView.setTextColor(Color.parseColor("#FFFFFF"));
                textView.setBackgroundColor(Color.parseColor("#03A9F4"));
                textView.setElevation(6);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(64,32,64,32);
                textView.setLayoutParams(params);


                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);


                textView.setId(7777777 + Integer.parseInt(s.substring(1, s.lastIndexOf("$"))));

                textView.setOnClickListener(view -> {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
                    builder.setMessage("Do you want to delete this action?");

                    builder.setPositiveButton("Delete", (dialog, id) -> {
                        // User clicked OK button
                        sharedPref.edit().remove("$" + ((int)view.getId() - 7777777)).commit();
                        FragmentTransaction ftr = getFragmentManager().beginTransaction();
                        ftr.detach(Sched_Fragment.this).attach(Sched_Fragment.this).commit();


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

    private void displayShadeActions(char weekDay, LinearLayout linLay) {
        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("ACTIONS", Context.MODE_PRIVATE);

        ArrayList<String> actionList = new ArrayList<>();

        Map<String, ?> allEntries = sharedPref.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().charAt(0) == '$') {
                String actionCode = entry.getValue().toString();
                if (actionCode.charAt(0) == weekDay && actionCode.charAt(5) == 'S') //ensure it is a shade action and on the proper day
                    actionList.add(entry.getKey() +"$" + hourToString(actionCode.substring(1,3)) + ":" + actionCode.substring(3,5) + AMorPM(actionCode.substring(1,3)) + "\nSet shade position to: " + Integer.parseInt(actionCode.substring(6, 9)) + "%");
            }
        }
        if (actionList.size() == 0) {
            TextView TV = new TextView(this.getActivity());
            TV.setText("There are no scheduled actions on this day.\n\nCreate one by clicking the (+) icon below!\n\nTap on a created action to delete it.");
            TV.setTextSize(20);
            TV.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            TV.setTextColor(Color.parseColor("#FFFFFF"));

            TV.setPadding(0, 24, 0, 24);
            TV.setElevation(6);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(16,32,16,32);
            TV.setLayoutParams(params);

            linLay.addView(TV);

        }
        else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && actionList.size() > 1) {
                Comparator<String> actionListComparator = new Comparator<String>(){

                    @Override
                    public int compare(String s1, String t1) {

                        int hour1 = Integer.parseInt(s1.substring(s1.lastIndexOf("$")+1, s1.indexOf(":")));
                        int hour2 = Integer.parseInt(t1.substring(t1.lastIndexOf("$")+1, t1.indexOf(":")));


                        if (s1.charAt(s1.indexOf(":")+3) == 'P')
                            hour1 += 12;

                        if (t1.charAt(t1.indexOf(":")+3) == 'P')
                            hour2 += 12;

                        if (hour1 < hour2)
                            return -1;
                        if (hour1 > hour2)
                            return 1;

                        //Hours must be the same, check minutes
                        int min1 = Integer.parseInt(s1.substring(s1.indexOf(':')+1, s1.indexOf(':') + 3));
                        int min2 = Integer.parseInt(t1.substring(t1.indexOf(':')+1, t1.indexOf(':') + 3));


                        if (min1 < min2)
                            return -1;
                        if (min1 > min2)
                            return 1;

                        //minutes are same, equal
                        return 0;
                    }

                };

                actionList.sort(actionListComparator);

            }

            for( int i = 0; i < actionList.size(); i++ )
            {
                TextView textView = new TextView(this.getActivity());

                String s = actionList.get(i);

                textView.setText(s.substring(s.lastIndexOf("$")+1));

                textView.setPadding(8, 16, 8, 16);
                textView.setTextSize(20);
                textView.setTextColor(Color.parseColor("#FFFFFF"));
                textView.setBackgroundColor(Color.parseColor("#03A9F4"));
                textView.setElevation(6);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(64,32,64,32);
                textView.setLayoutParams(params);


                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);


                textView.setId(6777777 + Integer.parseInt(s.substring(1, s.lastIndexOf("$"))));

                textView.setOnClickListener(view -> {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
                    builder.setMessage("Do you want to delete this action?");

                    builder.setPositiveButton("Delete", (dialog, id) -> {
                        // User clicked OK button
                        sharedPref.edit().remove("$" + ((int)view.getId() - 6777777)).commit();
                        FragmentTransaction ftr = getFragmentManager().beginTransaction();
                        ftr.detach(Sched_Fragment.this).attach(Sched_Fragment.this).commit();


                    });
                    builder.setNegativeButton("Keep", (dialog, id) -> {
                        // User cancelled the dialog
                    });

                    builder.create().show();

                });

                linLay.addView(textView);

            }
        }    }

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
            return "PM";
        else
            return "AM";
    }
}