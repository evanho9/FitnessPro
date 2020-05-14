package evanh.fitnesspro.ui.weighin;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import evanh.fitnesspro.database.DatabaseHelper;
import evanh.fitnesspro.database.DatabaseManager;
import evanh.fitnesspro.activity.OverviewActivity;
import evanh.fitnesspro.R;

import static evanh.fitnesspro.activity.OverviewActivity.hideKeyboardFrom;

public class WeighInFragment extends Fragment {
    private View root;

    private EditText weightField;
    private Button weighInButton;
    private TextView weightView;

    private DatabaseManager dbManager;

    public static WeighInFragment newInstance() {
        return new WeighInFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_weigh_in, container, false);

        dbManager = new DatabaseManager(getContext());

        initWeighInInput();
        initWeightView();

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initWeightView() {
        weightView  = root.findViewById(R.id.weightView);

        weightView.setText("Placeholder weight");

        dbManager.open();
        Cursor cursor = dbManager.fetchFromBodyWeightTable();

        final int bodyWeightIndex = cursor.getColumnIndex(DatabaseHelper.BODY_WEIGHT);

        if (cursor.moveToLast()) {
            double lastRecordedWeight = cursor.getDouble(bodyWeightIndex);
            weightView.setText("Last Recorded Body Weight: " + lastRecordedWeight);
        } else {
            weightView.setText("No Recorded Body Weight Found");
        }
        dbManager.close();
    }

    private void initWeighInInput() {
        weightField = root.findViewById(R.id.weightField);
        weighInButton = root.findViewById(R.id.weighInButton);

        weightField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboardFrom(root.getContext(), root);
                }
            }
        });

        weighInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!weightField.getText().toString().isEmpty()) {
                    double weight = Double.parseDouble(weightField.getText().toString());

                    dbManager.open();
                    dbManager.insertIntoBodyWeightTable(weight);
                    dbManager.close();

                    Toast toast = Toast.makeText(getActivity(), "Weighed in at: " + weight, Toast.LENGTH_SHORT);
                    View toastView = toast.getView();
                    toastView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    TextView toastText = (TextView) toastView.findViewById(android.R.id.message);
                    toastText.setTextColor(Color.WHITE);
                    toast.show();

                    Intent intent = new Intent(getActivity(), OverviewActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
