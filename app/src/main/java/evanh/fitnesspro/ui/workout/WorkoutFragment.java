package evanh.fitnesspro.ui.workout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;

import evanh.fitnesspro.database.DatabaseHelper;
import evanh.fitnesspro.database.DatabaseManager;
import evanh.fitnesspro.activity.OverviewActivity;
import evanh.fitnesspro.R;

public class WorkoutFragment extends Fragment {
    private View root;

    private ArrayList<Spinner> spinners;
    private ArrayList<TableLayout> workoutTables;

    private Button addRowButton;
    private Button addTableButton;
    private int tableIndex;

    private DatabaseManager dbManager;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_workout, container, false);

        dbManager = new DatabaseManager(getContext());

        spinners = new ArrayList<>();
        workoutTables = new ArrayList<>();
        tableIndex = 0;

        addWorkoutTable(tableIndex);
        tableIndex++;

        initAddExerciseButton();
        initFinishButton();

        return root;
    }

    private void addWorkoutTable(final int index) {
        TableRow tableContainerRow = new TableRow(this.getContext());
        tableContainerRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));

        final TableLayout workoutTable = new TableLayout(this.getContext());
        workoutTable.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT,1.0f));

        TableRow firstRow = new TableRow(this.getContext());
        final Spinner exerciseSpinner = new Spinner(this.getContext());

        final ArrayList<String> categories = new ArrayList<>();
        categories.add("Select an exercise");
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

        categories.add("Add a new exercise");

        String[] spinnerSelections = categories.toArray(new String[categories.size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerSelections);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exerciseSpinner.setAdapter(adapter);
        exerciseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> adapterView, View view, int i, long l) {
                switch (adapterView.getItemAtPosition(i).toString()) {
                    case "Add a new exercise":
                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        alert.setTitle("Add a new exercise");

                        final EditText input = new EditText(getContext());
                        alert.setView(input);

                        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dbManager.open();
                                dbManager.insertIntoWorkoutTable(input.getText().toString().toLowerCase(), -1, -1);
                                dbManager.close();

                                String inputCategory = input.getText().toString();
                                inputCategory = Character.toUpperCase(inputCategory.charAt(0)) + inputCategory.substring(1);
                                categories.add(categories.size()-1, inputCategory);

                                String[] spinnerSelections = categories.toArray(new String[categories.size()]);
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, spinnerSelections);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                for (Spinner spinner : spinners) {
                                    spinner.setAdapter(adapter);
                                    spinner.refreshDrawableState();
                                }

                            }
                        });

                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                adapterView.setSelection(0);
                            }
                        });

                        alert.show();
                        break;
                    default:
                        workoutTable.setTag(adapterView.getItemAtPosition(i).toString());
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        exerciseSpinner.setTag("exerciseSpinner");
        spinners.add(exerciseSpinner);
        firstRow.addView(exerciseSpinner);

        TableRow secondRow = new TableRow(this.getContext());
        secondRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        secondRow.setGravity(Gravity.CENTER_HORIZONTAL);
        EditText weight = new EditText(this.getContext());
        weight.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.4f));
        weight.setHint("Weight");
        weight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        TextView xTextView = new TextView(this.getContext());
        xTextView.setText("X");
        EditText reps = new EditText(this.getContext());
        reps.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.4f));
        reps.setHint("Reps");
        reps.setInputType(InputType.TYPE_CLASS_NUMBER);
        CheckBox done = new CheckBox(this.getContext());
        done.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.1f));
        weight.setTag("weight");
        reps.setTag("reps");
        done.setTag("done");
        secondRow.addView(weight);
        secondRow.addView(xTextView);
        secondRow.addView(reps);
        secondRow.addView(done);

        TableRow thirdRow = new TableRow(this.getContext());
        thirdRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        thirdRow.setGravity(Gravity.CENTER_HORIZONTAL);
        Button addSetButton = new Button(this.getContext());
        addSetButton.setTag("addSetButton");
        addSetButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        addSetButton.setText("Add Set");
        addSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TableRow addRow = new TableRow(view.getContext());
                addRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
                addRow.setGravity(Gravity.CENTER_HORIZONTAL);
                EditText addWeight = new EditText(view.getContext());
                addWeight.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.4f));
                addWeight.setHint("Weight");
                addWeight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                TextView addXTextView = new TextView(view.getContext());
                addXTextView.setText("X");
                EditText addReps = new EditText(view.getContext());
                addReps.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.4f));
                addReps.setHint("Reps");
                addReps.setInputType(InputType.TYPE_CLASS_NUMBER);
                CheckBox addDone = new CheckBox(view.getContext());
                addDone.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.1f));

                addWeight.setTag("weight");
                addReps.setTag("reps");
                addDone.setTag("done");

                addRow.addView(addWeight);
                addRow.addView(addXTextView);
                addRow.addView(addReps);
                addRow.addView(addDone);

                TableLayout tableLayout = workoutTables.get(index);
                tableLayout.addView(addRow, tableLayout.getChildCount()-1);
            }
        });
        thirdRow.addView(addSetButton);

        workoutTable.addView(firstRow);
        workoutTable.addView(secondRow);
        workoutTable.addView(thirdRow);

        workoutTables.add(workoutTable);

        tableContainerRow.addView(workoutTable);

        TableLayout tableContainer = root.findViewById(R.id.tableContainer);
        tableContainer.addView(tableContainerRow, index);
    }

    private void initAddExerciseButton() {

        TableRow addExerciseRow = new TableRow(this.getContext());
        addExerciseRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        addExerciseRow.setGravity(Gravity.CENTER);
        Button addExerciseButton = new Button(this.getContext());
        addExerciseButton.setTag("addExercise");
        addExerciseButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        addExerciseButton.setText("Add Exercise");
        addExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addWorkoutTable(tableIndex);
                tableIndex++;
            }
        });
        addExerciseRow.addView(addExerciseButton);
        TableLayout tableContainer = root.findViewById(R.id.tableContainer);
        tableContainer.addView(addExerciseRow);
    }

    private void initFinishButton() {
        TableRow finishButtonRow = new TableRow(this.getContext());
        finishButtonRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        finishButtonRow.setGravity(Gravity.CENTER);
        Button finishButton = new Button(this.getContext());
        finishButton.setTag("finish");
        finishButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        finishButton.setText("Finish");
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dbManager.open();

                for (TableLayout workoutTable : workoutTables) {
                    String exercise = workoutTable.getTag().toString().toLowerCase();
                    ArrayList<View> weightFields = getViewsByTag(workoutTable, "weight");
                    ArrayList<View> repsFields = getViewsByTag(workoutTable, "reps");
                    ArrayList<View> doneFields = getViewsByTag(workoutTable, "done");

                    double maxOneRepMax = Double.MIN_VALUE;
                    for (int i = 0; i < weightFields.size(); i++) {
                        if (!exercise.equals("select an exercise") && !((EditText)weightFields.get(i)).getText().toString().isEmpty() && !((EditText)repsFields.get(i)).getText().toString().isEmpty()) {
                            double weight = Double.parseDouble(((EditText) weightFields.get(i)).getText().toString());
                            int reps = Integer.parseInt(((EditText) repsFields.get(i)).getText().toString());
                            boolean done = ((CheckBox) doneFields.get(i)).isChecked();

                            if (done) {
                                double oneRepMax = calculate1RM(weight, reps);
                                if (oneRepMax > maxOneRepMax) {
                                    maxOneRepMax = oneRepMax;
                                }

                                dbManager.insertIntoWorkoutTable(exercise, weight, reps);

                            }
                        }
                    }
                    dbManager.insertIntoOneRepMaxTable(exercise, maxOneRepMax);
                }
                dbManager.close();

                Toast toast = Toast.makeText(getActivity(), "Workout completed", Toast.LENGTH_SHORT);
                View toastView = toast.getView();
                toastView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                TextView toastText = (TextView) toastView.findViewById(android.R.id.message);
                toastText.setTextColor(Color.WHITE);
                toast.show();

                Intent intent = new Intent(getActivity(), OverviewActivity.class);
                startActivity(intent);
            }
        });
        finishButtonRow.addView(finishButton);

        TableLayout tableContainer = root.findViewById(R.id.tableContainer);
        tableContainer.addView(finishButtonRow);
    }

    private static ArrayList<View> getViewsByTag(ViewGroup root, String tag){
        ArrayList<View> views = new ArrayList<View>();
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                views.addAll(getViewsByTag((ViewGroup) child, tag));
            }

            final Object tagObj = child.getTag();

            if (tagObj != null && tagObj.toString().equals(tag)) {
                views.add(child);
            }

        }
        return views;
    }

    public static double calculate1RM(double weight, int reps) {
        return (weight * 36)/(37 - reps);
    }
}
