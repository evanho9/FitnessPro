package evanh.fitnesspro.ui.alarm;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import evanh.fitnesspro.activity.OverviewActivity;
import evanh.fitnesspro.R;

public class AlarmFragment extends Fragment {
    private View root;

    private EditText hourField;
    private EditText minuteField;
    private Spinner AMPMSpinner;

    private Button setAlarmButton;

    public static AlarmFragment newInstance() {
        return new AlarmFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_alarm, container, false);

        hourField = root.findViewById(R.id.hourField);
        minuteField = root.findViewById(R.id.minutesField);

        initSpinner();
        initSetAlarmButton();

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initSpinner() {
        AMPMSpinner = root.findViewById(R.id.AMPMSpinner);

        String[] spinnerSelections = {"AM", "PM"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerSelections);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AMPMSpinner.setAdapter(adapter);
    }

    private void initSetAlarmButton() {
        setAlarmButton = root.findViewById(R.id.alarmButton);
        setAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String spinnerSelection = AMPMSpinner.getSelectedItem().toString();
                int hour = Integer.parseInt(hourField.getText().toString());
                int minute = Integer.parseInt(minuteField.getText().toString());
                if ((hour >= 0 && hour <= 12) && (minute >= 0 && minute <= 60)) {

                    if (spinnerSelection.equals("AM")) {
                        OverviewActivity.startAlarmReceiver(getContext(), hour, minute);
                    } else {
                        OverviewActivity.startAlarmReceiver(getContext(), hour + 12, minute);
                    }

                    Toast toast = Toast.makeText(getActivity(), "Alarm set at " + hour + ":" + minute + " " + spinnerSelection.toString(), Toast.LENGTH_SHORT);
                    View toastView = toast.getView();
                    toastView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    TextView toastText = (TextView) toastView.findViewById(android.R.id.message);
                    toastText.setTextColor(Color.WHITE);
                    toast.show();

                    Intent intent = new Intent(getActivity(), OverviewActivity.class);
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(getActivity(), "Invalid time", Toast.LENGTH_SHORT);
                    View toastView = toast.getView();
                    toastView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    TextView toastText = (TextView) toastView.findViewById(android.R.id.message);
                    toastText.setTextColor(Color.WHITE);
                    toast.show();
                }
            }
        });
    }

}
