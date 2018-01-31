package de.repictures.stromberg.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Arrays;

import de.repictures.stromberg.Features.EditEmployeeActivity;
import de.repictures.stromberg.POJOs.Account;
import de.repictures.stromberg.R;

public class EditEmployeeWorkTimeDialogFragment extends DialogFragment{

    public static String ARG_TITLE = "title";
    private int titleId = R.string.title;
    private int startTimeMinutes, endTimeMinutes, position;
    private DecimalFormat decimalFormat = new DecimalFormat("00");
    private Spinner startHourSpinner, endHourSpinner;

    private String[] days = {"Mo", "Di", "Mi", "Do", "Fr", "Sa"};
    private Integer[] momiStartHours = {8, 9, 10, 11, 12, 13, 14};
    private Integer[] momiEndHours = {9, 10, 11, 12, 13, 14, 15};
    private Integer[] doStartHours = {8, 9, 10, 11, 12};
    private Integer[] doEndHours = {9, 10, 11, 12, 13};
    private Integer[] frStartHours = {9, 10, 11, 12, 13, 14, 15};
    private Integer[] frEndHours = {10, 11, 12, 13, 14, 15, 16};
    private Integer[] saStartHours = {9, 10, 11, 12, 13};
    private Integer[] saEndHours = {10, 11, 12, 13, 14};
    private String[] minutes = {"00", "30"};
    private EditEmployeeActivity editEmployeeActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_TITLE)){
            titleId = getArguments().getInt(ARG_TITLE);
        }
        if (getArguments().containsKey("start_time_minutes")){
            startTimeMinutes = getArguments().getInt("start_time_minutes");
        }
        if (getArguments().containsKey("end_time_minutes")){
            endTimeMinutes = getArguments().getInt("end_time_minutes");
        }
        if (getArguments().containsKey("position")){
            position = getArguments().getInt("position");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Build the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(getActivity().getResources().getString(titleId))
                .setCancelable(false);

        //Put ProgressBar in the DialogLayout
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View parent = layoutInflater.inflate(R.layout.fragment_edit_employee_work_time_dialog, null);

        Spinner daySpinner = (Spinner) parent.findViewById(R.id.fragment_edit_employee_work_time_dialog_day_spinner);
        startHourSpinner = (Spinner) parent.findViewById(R.id.fragment_edit_employee_work_time_dialog_start_hour_spinner);
        Spinner startMinuteSpinner = (Spinner) parent.findViewById(R.id.fragment_edit_employee_work_time_dialog_start_minute_spinner);
        endHourSpinner = (Spinner) parent.findViewById(R.id.fragment_edit_employee_work_time_dialog_end_hour_spinner);
        Spinner endMinuteSpinner = (Spinner) parent.findViewById(R.id.fragment_edit_employee_work_time_dialog_end_minute_spinner);
        TextView errorTextView = (TextView) parent.findViewById(R.id.fragment_edit_employee_work_time_dialog_error_message);

        ArrayAdapter<String> stringsAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, days);
        daySpinner.setAdapter(stringsAdapter);
        stringsAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, minutes);
        startMinuteSpinner.setAdapter(stringsAdapter);
        endMinuteSpinner.setAdapter(stringsAdapter);

        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                changeHourSpinnerAdapters(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        int startDays = Account.getDaysFromMinutes(startTimeMinutes);
        int startHours = Account.getHoursFromMinutes(startTimeMinutes);
        int endHours = Account.getHoursFromMinutes(endTimeMinutes);
        int startMinutesIndex = Account.getMinutesOfHourFromMinutes(startTimeMinutes)/30;
        int endMinutesIndex = Account.getMinutesOfHourFromMinutes(endTimeMinutes)/30;

        daySpinner.setSelection(startDays);
        changeHourSpinnerAdapters(startDays);
        startMinuteSpinner.setSelection(startMinutesIndex);
        endMinuteSpinner.setSelection(endMinutesIndex);
        int index;

        switch (startDays){
            case 3:
                index = Arrays.asList(doStartHours).indexOf(startHours);
                startHourSpinner.setSelection(index);
                index = Arrays.asList(doEndHours).indexOf(endHours);
                endHourSpinner.setSelection(index);
                break;
            case 4:
                index = Arrays.asList(frStartHours).indexOf(startHours);
                startHourSpinner.setSelection(index);
                index = Arrays.asList(frEndHours).indexOf(endHours);
                endHourSpinner.setSelection(index);
                break;
            case 5:
                index = Arrays.asList(saStartHours).indexOf(startHours);
                startHourSpinner.setSelection(index);
                index = Arrays.asList(saEndHours).indexOf(endHours);
                endHourSpinner.setSelection(index);
                break;
            default:
                index = Arrays.asList(momiStartHours).indexOf(startHours);
                startHourSpinner.setSelection(index);
                index = Arrays.asList(momiStartHours).indexOf(endHours);
                endHourSpinner.setSelection(index);
        }

        builder.setView(parent);
        builder.setPositiveButton(R.string.ok, null);
        builder.setNegativeButton(R.string.cancel, (dialogInterface, i) -> dismiss());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {

            Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                startTimeMinutes = Integer.valueOf(minutes[startMinuteSpinner.getSelectedItemPosition()]);
                startTimeMinutes += daySpinner.getSelectedItemPosition()*1440;
                endTimeMinutes = Integer.valueOf(minutes[endMinuteSpinner.getSelectedItemPosition()]);
                endTimeMinutes += daySpinner.getSelectedItemPosition()*1440;

                if (daySpinner.getSelectedItemPosition() == 3){
                    startTimeMinutes += doStartHours[startHourSpinner.getSelectedItemPosition()]*60;
                    endTimeMinutes += doEndHours[endHourSpinner.getSelectedItemPosition()]*60;
                }
                else if (daySpinner.getSelectedItemPosition() == 4){
                    startTimeMinutes += frStartHours[startHourSpinner.getSelectedItemPosition()]*60;
                    endTimeMinutes += frEndHours[endHourSpinner.getSelectedItemPosition()]*60;
                }
                else if (daySpinner.getSelectedItemPosition() == 5){
                    startTimeMinutes += saStartHours[startHourSpinner.getSelectedItemPosition()]*60;
                    endTimeMinutes += saEndHours[endHourSpinner.getSelectedItemPosition()]*60;
                }
                else {
                    startTimeMinutes += momiStartHours[startHourSpinner.getSelectedItemPosition()]*60;
                    endTimeMinutes += momiEndHours[endHourSpinner.getSelectedItemPosition()]*60;
                }
                if (startTimeMinutes >= endTimeMinutes || endTimeMinutes-startTimeMinutes < 60){
                    errorTextView.setText(R.string.start_time_after_end_time);
                    errorTextView.setVisibility(View.VISIBLE);
                } else {
                    errorTextView.setVisibility(View.INVISIBLE);
                    editEmployeeActivity.editWorkingTimes(startTimeMinutes, endTimeMinutes, position);
                    dismiss();
                }
            });
        });
        return dialog;
    }

    private void changeHourSpinnerAdapters(int position) {
        ArrayAdapter<Integer> startHoursAdapter;
        ArrayAdapter<Integer> endHoursAdapter;
        if (position == 3){
            startHoursAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, doStartHours);
            endHoursAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, doEndHours);
        }
        else if (position == 4){
            startHoursAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, frStartHours);
            endHoursAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, frEndHours);
        }
        else if (position == 5){
            startHoursAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, saStartHours);
            endHoursAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, saEndHours);
        }
        else {
            startHoursAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, momiStartHours);
            endHoursAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, momiEndHours);
        }
        startHourSpinner.setAdapter(startHoursAdapter);
        endHourSpinner.setAdapter(endHoursAdapter);
    }

    public void setEditEmployeeActivity(EditEmployeeActivity editEmployeeActivity) {
        this.editEmployeeActivity = editEmployeeActivity;
    }
}