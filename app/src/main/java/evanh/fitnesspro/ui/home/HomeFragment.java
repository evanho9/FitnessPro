package evanh.fitnesspro.ui.home;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import evanh.fitnesspro.activity.AddGoalActivity;
import evanh.fitnesspro.database.DatabaseHelper;
import evanh.fitnesspro.database.DatabaseManager;
import evanh.fitnesspro.R;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class HomeFragment extends Fragment {
    private View root;

    private TableRow progressCircleContainer;
    private CircularProgressBar circularProgressBar;
    private TextView progressText;

    private LinearLayout goalsContainer;

    private Button addGoalButton;

    private DatabaseManager dbManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);

        progressCircleContainer = root.findViewById(R.id.progressCircleContainer);

        dbManager = new DatabaseManager(getContext());

        initGoalsContainer();
        initCircleProgressBar();
        initAddGoalButton();

        return root;
    }

    private void initCircleProgressBar() {
        progressText = root.findViewById(R.id.progressText);
        progressText.setText("Select a goal");

        // circle progress bar
        circularProgressBar = root.findViewById(R.id.circularProgressBar);

        circularProgressBar.setProgress(0f);
        circularProgressBar.setProgressWithAnimation(100f, 1500L); // =1s

        circularProgressBar.setProgressMax(100f);

        circularProgressBar.setProgressBarColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        circularProgressBar.setBackgroundProgressBarColor(Color.LTGRAY);

        circularProgressBar.setProgressBarWidth(20f); // in DP
        circularProgressBar.setBackgroundProgressBarWidth(5f); // in DP

        circularProgressBar.setStartAngle(0f);
        circularProgressBar.setProgressDirection(CircularProgressBar.ProgressDirection.TO_RIGHT);

        circularProgressBar.setRoundBorder(true);

        circularProgressBar.setOnIndeterminateModeChangeListener(new Function1<Boolean, Unit>() {
            @Override
            public Unit invoke(Boolean isEnable) {
                return Unit.INSTANCE;
            }
        });

        circularProgressBar.setOnProgressChangeListener(new Function1<Float, Unit>() {
            @Override
            public Unit invoke(Float progress) {
                return Unit.INSTANCE;
            }
        });
    }

    private void initGoalsContainer() {
        goalsContainer = root.findViewById(R.id.goalsContainer);

        dbManager.open();

        Cursor cursor = dbManager.fetchFromGoalTable();

        final int idIndex = cursor.getColumnIndex(DatabaseHelper.ID);
        final int typeIndex = cursor.getColumnIndex(DatabaseHelper.TYPE);
        final int exerciseIndex = cursor.getColumnIndex(DatabaseHelper.EXERCISE);
        final int weightIndex = cursor.getColumnIndex(DatabaseHelper.WEIGHT);
        final int currentBodyWeightIndex = cursor.getColumnIndex(DatabaseHelper.CURRENT_BODY_WEIGHT);
        final int bodyWeightIndex = cursor.getColumnIndex(DatabaseHelper.BODY_WEIGHT);
        final int distanceIndex = cursor.getColumnIndex(DatabaseHelper.DISTANCE);
        final int timeIndex = cursor.getColumnIndex(DatabaseHelper.TIME);
        final int timeStampIndex = cursor.getColumnIndex(DatabaseHelper.TIMESTAMP);

        if (cursor.moveToFirst()) {
            do {
                final Button goal = new Button(getContext());
                goal.setPadding(50,15,0,15);
                goal.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                goal.setBackgroundColor(Color.TRANSPARENT);
                goal.setTransformationMethod(null);
                goal.setGravity(Gravity.NO_GRAVITY);

                final String type = cursor.getString(typeIndex);

                long id = -1;
                double bodyWeightAtGoalInsertTime = -1;
                double bodyWeight = -1;
                String exerciseName = "";
                double weight = -1;
                double distance = -1;
                int timeInSeconds = -1;
                switch (type){
                    case "body_weight":
                        id = cursor.getLong(idIndex);
                        bodyWeightAtGoalInsertTime = cursor.getDouble(currentBodyWeightIndex);
                        bodyWeight = cursor.getDouble(bodyWeightIndex);

                        goal.setText("Body Weight: " + bodyWeight + " lbs");
                        break;
                    case "lift":
                        id = cursor.getLong(idIndex);
                        exerciseName = cursor.getString(exerciseIndex);
                        exerciseName = Character.toUpperCase(exerciseName.charAt(0)) + exerciseName.substring(1);
                        weight = cursor.getDouble(weightIndex);

                        goal.setText(exerciseName + " Lift PR: " + weight + " lbs");
                        break;
                    case "cardio":
                        id = cursor.getLong(idIndex);
                        distance = cursor.getDouble(distanceIndex);
                        timeInSeconds = cursor.getInt(timeIndex);

                        int seconds = timeInSeconds % 60;
                        int minutes = timeInSeconds / 60;

                        goal.setText("Cardio: " + distance + " miles in " + minutes + " minutes and " + seconds + " seconds");
                        break;
                }

                final long goalId = id;
                final double goalBodyWeightAtGoalInsertTime = bodyWeightAtGoalInsertTime;
                final double goalBodyWeight = bodyWeight;
                final String goalExerciseName = exerciseName;
                final double goalOneRepMax = weight;
                final double goalDistance = distance;
                final int goalTime = timeInSeconds;
                goal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dbManager.open();

                        if (type.equals("body_weight")) {
                            Cursor cursor = dbManager.fetchFromBodyWeightTable();
                            if (cursor.moveToLast()) {

                                final int bodyWeightIndex = cursor.getColumnIndex(DatabaseHelper.BODY_WEIGHT);

                                float currentBodyWeight = cursor.getFloat(bodyWeightIndex);

                                // lose weight case
                                if (goalBodyWeightAtGoalInsertTime > goalBodyWeight) {
                                    float progress = (float) (goalBodyWeight/ currentBodyWeight) * 100;

                                    progressText.setText(Float.toString(progress) + "%\n" + "Current: " + Float.toString(currentBodyWeight) + " lbs");
                                    progressText.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));

                                    if (progress >= 100) {
                                        dbManager.deleteFromGoalTable(goalId);
                                        circularProgressBar.setProgressWithAnimation(progress, 1500L);
                                        progressText.setText("Goal achieved!");
                                        progressText.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
                                        goalsContainer.removeView(goal);
                                        return;
                                    }

                                    circularProgressBar.setProgressWithAnimation(progress, 1500L);
                                } else {
                                    float progress = (float) (currentBodyWeight / goalBodyWeight) * 100;

                                    progressText.setText(Float.toString(progress) + "%\n" + "Current: " + Float.toString(currentBodyWeight) + " lbs");
                                    progressText.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));

                                    if (progress >= 100) {
                                        dbManager.deleteFromGoalTable(goalId);
                                        circularProgressBar.setProgressWithAnimation(progress, 1500L);
                                        progressText.setText("Goal achieved!");
                                        progressText.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
                                        goalsContainer.removeView(goal);
                                        return;
                                    }

                                    circularProgressBar.setProgressWithAnimation(progress, 1500L);
                                }
                            } else {
                                Toast toast = Toast.makeText(getActivity(), "No body weight data found", Toast.LENGTH_SHORT);
                                View toastView = toast.getView();

                                toastView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                                TextView toastText = (TextView) toastView.findViewById(android.R.id.message);

                                toastText.setTextColor(Color.WHITE);
                                toast.show();
                            }

                        } else if (type.equals("lift")) {
                            Cursor cursor = dbManager.fetchFromOneRepMaxTable(goalExerciseName.toLowerCase());
                            if (cursor.moveToLast()) {

                                final int oneRepMaxIndex = cursor.getColumnIndex(DatabaseHelper.WEIGHT);

                                float currentOneRepMax = cursor.getFloat(oneRepMaxIndex);

                                float progress = (float) (currentOneRepMax / goalOneRepMax) * 100;
                                Log.d("debug", "current one rep max: " + currentOneRepMax + " goal one rep max: " + goalOneRepMax + " progess: " + progress);

                                progressText.setText(Float.toString(progress) + "%\n" + "Current: " + Float.toString(currentOneRepMax) + " lbs");
                                progressText.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));

                                if (progress >= 100) {
                                    dbManager.deleteFromGoalTable(goalId);
                                    circularProgressBar.setProgressWithAnimation(progress, 1500L);
                                    progressText.setText("Goal achieved!");
                                    progressText.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
                                    goalsContainer.removeView(goal);
                                    return;
                                }

                                circularProgressBar.setProgressWithAnimation(progress, 1500L);
                            }  else {
                                Toast toast = Toast.makeText(getActivity(), "No data found for " + goalExerciseName.toLowerCase(), Toast.LENGTH_SHORT);
                                View toastView = toast.getView();

                                toastView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                                TextView toastText = (TextView) toastView.findViewById(android.R.id.message);

                                toastText.setTextColor(Color.WHITE);
                                toast.show();
                            }

                        } else if (type.equals("cardio")) {
                            Cursor cursor = dbManager.fetchFromCardioTable();
                            if (cursor.moveToLast()) {
                                final int distanceIndex = cursor.getColumnIndex(DatabaseHelper.DISTANCE);
                                final int timeIndex = cursor.getColumnIndex(DatabaseHelper.TIME);

                                float currentDistance = cursor.getFloat(distanceIndex);
                                float currentTime = cursor.getFloat(timeIndex);

                                float currentSpeed = (currentDistance / currentTime) ;

                                Log.d("debug", "currentDistance: " + currentDistance + " currentTime: " + currentTime + " currentSpeed: " + currentSpeed);

                                float goalSpeed = (float)(goalDistance / goalTime);

                                Log.d("debug", "goalDistance: " + goalDistance + " goalTime: " + goalTime + " goalSpeed: " + goalSpeed);

                                float progress = (currentSpeed / goalSpeed) * 100;
                                Log.d("debug", "current avg speed: " + (currentDistance / currentTime) + " goal avg speed: " + (goalDistance / goalTime) + " progess: " + progress);

                                progressText.setText(Float.toString(progress) + "%\n" + "Current: " + Float.toString(currentSpeed * 60 * 60) + " mph");
                                progressText.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));

                                if (progress >= 100) {
                                    dbManager.deleteFromGoalTable(goalId);
                                    circularProgressBar.setProgressWithAnimation(progress, 1500L);
                                    progressText.setText("Goal achieved!");
                                    progressText.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
                                    goalsContainer.removeView(goal);
                                    return;
                                }

                                circularProgressBar.setProgressWithAnimation(progress, 1500L);
                            } else {
                                Toast toast = Toast.makeText(getActivity(), "No cardio data found", Toast.LENGTH_SHORT);
                                View toastView = toast.getView();

                                toastView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                                TextView toastText = (TextView) toastView.findViewById(android.R.id.message);

                                toastText.setTextColor(Color.WHITE);
                                toast.show();
                            }
                        }

                        dbManager.close();
                    }
                });

                /*
                String timeStamp = cursor.getString(timeStampIndex);
                timeStamp = timeStamp.substring(timeStamp.indexOf('-')+1, timeStamp.indexOf(' '));


                 */

                goalsContainer.addView(goal);

            } while (cursor.moveToNext());
        }

        dbManager.close();
    }

    private void initAddGoalButton() {
        addGoalButton = root.findViewById(R.id.addGoalButton);

        addGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddGoalActivity.class);
                startActivity(intent);
            }
        });
    }
}
