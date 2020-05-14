package evanh.fitnesspro.ui.cardio;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import evanh.fitnesspro.database.DatabaseManager;
import evanh.fitnesspro.activity.OverviewActivity;
import evanh.fitnesspro.R;

import static evanh.fitnesspro.activity.OverviewActivity.hideKeyboardFrom;

public class CardioFragment extends Fragment {
    private View root;

    private Chronometer chronometer;
    private long pauseOffset;
    private boolean running;

    private Button startButton;
    private Button pauseButton;
    private Button resetButton;

    private EditText distanceField;

    private Button finishButton;

    private DatabaseManager dbManager;

    public static CardioFragment newInstance() {
        return new CardioFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_cardio, container, false);

        dbManager = new DatabaseManager(getContext());

        initChrono();
        initDistanceInput();
        initFinishButton();

        return root;
    }

    private void initChrono() {
        chronometer = root.findViewById(R.id.chronometer);
        chronometer.setBase(SystemClock.elapsedRealtime());

        startButton = root.findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!running) {
                    chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                    chronometer.start();
                    running = true;
                }
                Log.v("debug", "start button clicked");
            }
        });

        pauseButton = root.findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (running) {
                    chronometer.stop();
                    pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
                    running = false;
                }
            }
        });

        resetButton = root.findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chronometer.setBase(SystemClock.elapsedRealtime());
                pauseOffset = 0;
            }
        });
    }

    private void initDistanceInput() {
        distanceField = root.findViewById(R.id.distanceField);
        distanceField.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        distanceField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboardFrom(root.getContext(), root);
                }
            }
        });
    }

    private void initFinishButton() {
        finishButton = root.findViewById(R.id.finishCardioButton);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!distanceField.getText().toString().isEmpty()) {
                    Toast toast = Toast.makeText(getActivity(), "Cardio completed", Toast.LENGTH_SHORT);
                    View toastView = toast.getView();
                    toastView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    TextView toastText = (TextView) toastView.findViewById(android.R.id.message);
                    toastText.setTextColor(Color.WHITE);
                    toast.show();

                    double distance = Double.parseDouble(distanceField.getText().toString());

                    long stoppedMilliseconds = 0;
                    String chronoText = chronometer.getText().toString();
                    String array[] = chronoText.split(":");
                    if (array.length == 2) {
                        stoppedMilliseconds = Long.parseLong(array[0]) * 60 * 1000 + Long.parseLong(array[1]) * 1000;
                    } else if (array.length == 3) {
                        stoppedMilliseconds = Long.parseLong(array[0]) * 60 * 60 * 1000 + Long.parseLong(array[1]) * 60 * 1000 + Long.parseLong(array[2]) * 1000;
                    }

                    long stoppedSeconds = stoppedMilliseconds/1000;
                    dbManager.open();
                    dbManager.insertIntoCardioTable(distance, stoppedSeconds);
                    dbManager.close();

                    Intent intent = new Intent(getActivity(), OverviewActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
