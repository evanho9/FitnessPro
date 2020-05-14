package evanh.fitnesspro.ui.progress;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

import evanh.fitnesspro.database.DatabaseHelper;
import evanh.fitnesspro.database.DatabaseManager;
import evanh.fitnesspro.R;

public class ProgressFragment extends Fragment {
    private View root;

    private Spinner spinner;
    private String selectedItem;

    private TextView optionalView;
    private TableRow optionalRow;

    private TableRow chartContainer;
    private LineChart chart;

    private LinearLayout cardioDataContainer;

    private DatabaseManager dbManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_progress, container, false);

        dbManager = new DatabaseManager(getContext());

        initOptionalRow();
        initChart();
        initSpinner();
        initCardioData();

        return root;
    }

    private void initOptionalRow() {
        optionalRow = root.findViewById(R.id.exerciseRow);
        optionalView = root.findViewById(R.id.calculated1RMView);
    }

    private void initSpinner() {
        spinner = root.findViewById(R.id.progressSpinner);

        ArrayList<String> categories = new ArrayList<>();
        categories.add("Select Category");
        categories.add("Body Weight");
        categories.add("Cardio");

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

        final String[] data = categories.toArray(new String[categories.size()]);

        selectedItem = new String();
        ArrayAdapter<String> ad=new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, data);
        spinner.setAdapter(ad);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String val = spinner.getSelectedItem().toString();
                selectedItem = data[i];

                ((TextView) adapterView.getChildAt(0)).setTextSize(16);

                if (!selectedItem.equals("Select Category")) {
                    //Toast.makeText(getActivity(), selectedItem, Toast.LENGTH_SHORT).show();
                }

                if (!(selectedItem.equals("Select Category") || selectedItem.equals("Cardio"))) {
                    optionalRow.setVisibility(View.VISIBLE);
                } else {
                    optionalRow.setVisibility(View.GONE);
                }

                if (selectedItem.equals("Cardio")) {
                    chartContainer.setVisibility(View.GONE);
                    cardioDataContainer.setVisibility(View.VISIBLE);
                } else {
                    cardioDataContainer.setVisibility(View.GONE);
                    changeChart();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initChart() {
        chartContainer = root.findViewById(R.id.chartContainer);

        chart = new LineChart(root.getContext());
        Description description = new Description();
        description.setText("");
        chart.setDescription(description);

        ArrayList<Point> points = new ArrayList<>();

        List<Entry> entries = new ArrayList<Entry>();
        for (Point data : points) {
            entries.add(new Entry(data.x, data.y));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Label");

        dataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        dataSet.setCircleColor(Color.DKGRAY);
        dataSet.setValueTextColor(Color.BLACK);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        chart.invalidate();

        chart.setLayoutParams(new TableRow.LayoutParams(0, 500, 1.0f));

        chartContainer.addView(chart);
        chartContainer.setVisibility(View.GONE);
    }

    private void changeChart() {
        if (selectedItem.equals("Select Category")) {
            chart.clearValues();
            chartContainer.setVisibility(View.GONE);
            return;
        }

        dbManager.open();
        Cursor cursor = null;
        if (selectedItem.equals("Body Weight")) {
            cursor = dbManager.fetchFromBodyWeightTable();

            final int bodyWeightIndex = cursor.getColumnIndex(DatabaseHelper.BODY_WEIGHT);
            final int timeStampIndex = cursor.getColumnIndex(DatabaseHelper.TIMESTAMP);

            int xIndex = 0;

            double minBodyWeight = Double.MAX_VALUE;
            double maxBodyWeight = -1;
            ArrayList<PointF> points = new ArrayList<>();

            if (cursor.moveToFirst()) {
                do {
                    double bodyWeight = cursor.getDouble(bodyWeightIndex);
                    String timeStamp = cursor.getString(timeStampIndex);
                    timeStamp = timeStamp.substring(timeStamp.indexOf('-')+1, timeStamp.indexOf(' '));

                    if (bodyWeight > maxBodyWeight) {
                        maxBodyWeight = bodyWeight;
                    }

                    if (bodyWeight < minBodyWeight) {
                        minBodyWeight = bodyWeight;
                    }

                    PointF p = new PointF(xIndex, (float) bodyWeight);
                    xIndex++;
                    points.add(p);

                    //xAxisLables.add(timeStamp);

                } while (cursor.moveToNext());
            }

            StringBuilder sb = new StringBuilder();
            if (minBodyWeight != Double.MAX_VALUE ) {
                sb.append("Max Body Weight: " + maxBodyWeight + " ");
            }
            if (maxBodyWeight != -1) {
                sb.append("Min Body Weight: " + minBodyWeight + " ");
            }
            if (sb.toString().isEmpty()) {
                optionalView.setText("No Data Found");
            } else {
                optionalView.setText(sb.toString());
            }

            List<Entry> entries = new ArrayList<Entry>();
            for (PointF data : points) {
                entries.add(new Entry(data.x, data.y));
            }

            LineDataSet dataSet = new LineDataSet(entries, selectedItem + " (lbs)");
            dataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            dataSet.setCircleColor(Color.argb(100,2,119,189));
            dataSet.setValueTextColor(Color.BLACK); // styling

            chart.getAxisLeft().setAxisMinimum((float) (minBodyWeight * 0.9));
            chart.getAxisLeft().setAxisMaximum((float) (maxBodyWeight * 1.1));

            LineData lineData = new LineData(dataSet);
            chart.setData(lineData);

        } else {
            cursor = dbManager.fetchFromOneRepMaxTable(selectedItem.toLowerCase());

            final int weightIndex = cursor.getColumnIndex(DatabaseHelper.WEIGHT);
            final int timeStampIndex = cursor.getColumnIndex(DatabaseHelper.TIMESTAMP);

            int xIndex = 0;
            //ArrayList<String> xAxisLables = new ArrayList();

            double min1RM = Double.MAX_VALUE;
            double max1RM = -1;
            ArrayList<PointF> points = new ArrayList<>();
            if (cursor.moveToFirst()) {
                do {
                    double weight = cursor.getDouble(weightIndex);
                    String timeStamp = cursor.getString(timeStampIndex);
                    timeStamp = timeStamp.substring(timeStamp.indexOf('-')+1, timeStamp.indexOf(' '));

                    if (weight > max1RM) {
                        max1RM = weight;
                    }

                    if (weight < min1RM) {
                        min1RM = weight;
                    }

                    PointF p = new PointF(xIndex, (float) weight);
                    xIndex++;

                    points.add(p);

                    //xAxisLables.add(timeStamp);

                } while (cursor.moveToNext());
            }

            if (max1RM != -1) {
                optionalView.setText("Calculated Theoretical 1RM: " + max1RM);
            } else {
                optionalView.setText("Calculated Theoretical 1RM: No Data Found");
            }

            List<Entry> entries = new ArrayList<Entry>();
            for (PointF data : points) {
                entries.add(new Entry(data.x, data.y));
            }

            LineDataSet dataSet = new LineDataSet(entries, selectedItem + " (lbs)");
            dataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            dataSet.setCircleColor(Color.argb(100, 2, 119, 189));
            dataSet.setValueTextColor(Color.BLACK); // styling

            //chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLables));

            chart.getAxisLeft().setAxisMinimum((float) (min1RM * 0.9));
            chart.getAxisLeft().setAxisMaximum((float) (max1RM * 1.1));

            LineData lineData = new LineData(dataSet);
            chart.setData(lineData);
        }
        dbManager.close();

        chart.getXAxis().setDrawLabels(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);

        chart.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorBackground));

        chart.invalidate();
        chartContainer.setVisibility(View.VISIBLE);
        chartContainer.setPadding(30,10,30,10);
    }

    private void initCardioData() {
        cardioDataContainer = root.findViewById(R.id.cardioDataContainer);
        dbManager.open();
        Cursor cursor = dbManager.fetchFromCardioTable();

        final int distanceIndex = cursor.getColumnIndex(DatabaseHelper.DISTANCE);
        final int timeIndex = cursor.getColumnIndex(DatabaseHelper.TIME);

        ArrayList<PointF> points = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                double distance = cursor.getDouble(distanceIndex);
                int timeInSeconds = cursor.getInt(timeIndex);

                long seconds = timeInSeconds;
                long minutes = seconds / 60;

                String time = minutes % 60 + " minutes and " + seconds % 60 + " seconds";

                TextView cardioData = new TextView(getContext());
                cardioData.setText(distance + " miles in " + time);
                cardioData.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
                cardioData.setPadding(0,0,0,40);

                cardioDataContainer.addView(cardioData);

            } while (cursor.moveToNext());
        } else {
            TextView cardioData = new TextView(getContext());
            cardioData.setText("No data found");
            cardioData.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
            cardioData.setPadding(0,0,0,40);

            cardioDataContainer.addView(cardioData);
        }
        dbManager.close();
    }

}
