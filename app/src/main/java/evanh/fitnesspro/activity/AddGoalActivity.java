package evanh.fitnesspro.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import evanh.fitnesspro.R;
import evanh.fitnesspro.database.DatabaseHelper;
import evanh.fitnesspro.database.DatabaseManager;

public class AddGoalActivity extends AppCompatActivity {

    private Spinner goalSpinner;

    private TableRow exerciseRow;
    private Spinner exerciseSpinner;

    private TableRow cardioGoalRow;
    private EditText minutesField;
    private EditText secondsField;

    private TableRow goalRow;
    private EditText goalField;
    private Button saveButton;

    private String selectedExercise;

    private DatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);

        dbManager = new DatabaseManager(this);

        goalRow = findViewById(R.id.goalRow);
        goalField = findViewById(R.id.goalField);
        goalField.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        exerciseRow = findViewById(R.id.exerciseRow);
        cardioGoalRow = findViewById(R.id.cardioGoalRow);
        minutesField = findViewById(R.id.minutesField);
        secondsField = findViewById(R.id.secondsField);

        initSpinner();
        initExerciseSpinner();
        initSaveButton();
    }

    private void initSpinner() {
        goalSpinner = findViewById(R.id.goalSpinner);

        String[] spinnerSelections = new String[] {"Body Weight", "Lift PR", "Cardio PR"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerSelections);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        goalSpinner.setAdapter(adapter);
        goalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (adapterView.getItemAtPosition(i).toString()) {
                    case "Body Weight":
                        goalField.setHint("Body Weight (lbs)");
                        exerciseRow.setVisibility(View.GONE);
                        cardioGoalRow.setVisibility(View.GONE);
                        goalRow.setVisibility(View.VISIBLE);
                        break;
                    case "Cardio PR":
                        goalField.setHint("Distance (miles)");
                        exerciseRow.setVisibility(View.GONE);
                        cardioGoalRow.setVisibility(View.VISIBLE);
                        break;
                    case "Lift PR":
                        goalField.setHint("Weight (lbs)");
                        exerciseRow.setVisibility(View.VISIBLE);
                        cardioGoalRow.setVisibility(View.GONE);
                        goalRow.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initExerciseSpinner() {
        exerciseSpinner = findViewById(R.id.exerciseSpinner);
        exerciseSpinner.setPadding(0, 40, 0 , 0);

        ArrayList<String> categories = new ArrayList<>();
        categories.add("Select Category");

        dbManager.open();

        Cursor cursor = dbManager.fetchDistinctExerciseNames();

        final int nameIndex = cursor.getColumnIndex(DatabaseHelper.NAME);

        if (cursor.moveToFirst()) {
            do {
                String category = cursor.getString(nameIndex);
                category = Character.toUpperCase(category.charAt(0)) + category.substring(1);
                categories.add(category);
            } while (cursor.moveToNext());
        }

        dbManager.close();

        String[] spinnerSelections = categories.toArray(new String[categories.size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerSelections);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exerciseSpinner.setAdapter(adapter);
        exerciseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedExercise = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initSaveButton() {
        saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!goalField.getText().toString().isEmpty()) {

                    dbManager.open();
                    String type = goalSpinner.getSelectedItem().toString();
                    switch (type) {
                        case "Body Weight":
                            type = "body_weight";
                            double bodyWeight = Double.parseDouble(goalField.getText().toString());

                            Cursor cursor = dbManager.fetchFromBodyWeightTable();
                            if (cursor.moveToLast()) {

                                final int bodyWeightIndex = cursor.getColumnIndex(DatabaseHelper.BODY_WEIGHT);

                                double currentBodyWeight = cursor.getDouble(bodyWeightIndex);

                                dbManager.insertIntoGoalTable(type, null, null, currentBodyWeight, bodyWeight, null, null);

                            } else {
                                Toast toast = Toast.makeText(AddGoalActivity.this, "Please weigh in first", Toast.LENGTH_SHORT);
                                View toastView = toast.getView();

                                toastView.setBackgroundColor(ContextCompat.getColor(AddGoalActivity.this, R.color.colorAccent));
                                TextView toastText = (TextView) toastView.findViewById(android.R.id.message);

                                toastText.setTextColor(Color.WHITE);
                                toast.show();
                                return;
                            }

                            break;
                        case "Lift PR":
                            type = "lift";
                            String exerciseName = exerciseSpinner.getSelectedItem().toString().toLowerCase();

                            double weight = Double.parseDouble(goalField.getText().toString());

                            if (exerciseName.equals("select category")) {
                                return;
                            }

                            dbManager.insertIntoGoalTable(type, exerciseName, weight, null,null, null, null);
                            break;
                        case "Cardio PR":
                            type = "cardio";
                            double distance = Double.parseDouble(goalField.getText().toString());

                            double minutes = 0;
                            double seconds = 0;
                            if (minutesField.getText().toString().isEmpty() && secondsField.getText().toString().isEmpty()) {
                                return;
                            }

                            if (!minutesField.getText().toString().isEmpty()) {
                                minutes = Double.parseDouble(minutesField.getText().toString());
                            }

                            if (!secondsField.getText().toString().isEmpty()) {
                                seconds = Double.parseDouble(secondsField.getText().toString());
                            }

                            long convertedSeconds = (long)((minutes * 60) + seconds);

                            dbManager.insertIntoGoalTable(type, null, null, null,null, distance, convertedSeconds);
                            break;
                    }

                    dbManager.close();
                    
                    Toast toast = Toast.makeText(AddGoalActivity.this, "Goal Added", Toast.LENGTH_SHORT);
                    View toastView = toast.getView();

                    toastView.setBackgroundColor(ContextCompat.getColor(AddGoalActivity.this, R.color.colorAccent));
                    TextView toastText = (TextView) toastView.findViewById(android.R.id.message);

                    toastText.setTextColor(Color.WHITE);
                    toast.show();

                    Intent intent = new Intent(AddGoalActivity.this, OverviewActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
