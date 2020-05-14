package evanh.fitnesspro.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseManager {

    private DatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DatabaseManager(Context c) {
        context = c;
    }

    public DatabaseManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insertIntoWorkoutTable(String name, double weight, int reps) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.NAME, name);
        contentValues.put(DatabaseHelper.WEIGHT, weight);
        contentValues.put(DatabaseHelper.REPS, reps);
        database.insert(DatabaseHelper.WORKOUT_DATA_TABLE_NAME, null, contentValues);
    }

    public Cursor fetchFromWorkoutTable(String exerciseName) {
        String[] columns = new String[] { DatabaseHelper.ID, DatabaseHelper.NAME, DatabaseHelper.WEIGHT, DatabaseHelper.REPS, DatabaseHelper.TIMESTAMP};
        Cursor cursor = database.query(DatabaseHelper.WORKOUT_DATA_TABLE_NAME, columns, "name = \"" + exerciseName + "\"", null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int updateWorkoutTable(long id, String name, double weight, int reps) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.NAME, name);
        contentValues.put(DatabaseHelper.WEIGHT, weight);
        contentValues.put(DatabaseHelper.REPS, reps);
        int i = database.update(DatabaseHelper.WORKOUT_DATA_TABLE_NAME, contentValues, DatabaseHelper.ID + " = " + id, null);
        return i;
    }

    public void deleteFromWorkoutTable(long id) {
        database.delete(DatabaseHelper.WORKOUT_DATA_TABLE_NAME, DatabaseHelper.ID + "=" + id, null);
    }

    public Cursor fetchDistinctExerciseNames() {
        String[] columns = new String[] {DatabaseHelper.NAME};
        Cursor cursor = database.query(true, DatabaseHelper.WORKOUT_DATA_TABLE_NAME, columns, null, null, null , null, DatabaseHelper.NAME + " ASC", null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public void insertIntoBodyWeightTable(double bodyWeight) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.BODY_WEIGHT, bodyWeight);
        database.insert(DatabaseHelper.BODY_WEIGHT_TABLE_NAME, null, contentValues);
    }

    public Cursor fetchFromBodyWeightTable() {
        String[] columns = new String[] { DatabaseHelper.ID, DatabaseHelper.BODY_WEIGHT, DatabaseHelper.TIMESTAMP};
        Cursor cursor = database.query(DatabaseHelper.BODY_WEIGHT_TABLE_NAME, columns,null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int updateBodyWeightTable(long id, double bodyWeight) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.BODY_WEIGHT, bodyWeight);
        int i = database.update(DatabaseHelper.BODY_WEIGHT_TABLE_NAME, contentValues, DatabaseHelper.ID + " = " + id, null);
        return i;
    }

    public void deleteFromBodyWeightTable(long id) {
        database.delete(DatabaseHelper.BODY_WEIGHT_TABLE_NAME, DatabaseHelper.ID + "=" + id, null);
    }

    public void insertIntoCardioTable(double distance, long time) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.DISTANCE, distance);
        contentValues.put(DatabaseHelper.TIME, time);
        database.insert(DatabaseHelper.CARDIO_TABLE_NAME, null, contentValues);
    }

    public Cursor fetchFromCardioTable() {
        String[] columns = new String[] { DatabaseHelper.ID, DatabaseHelper.DISTANCE, DatabaseHelper.TIME, DatabaseHelper.TIMESTAMP};
        Cursor cursor = database.query(DatabaseHelper.CARDIO_TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int updateCardioTable(long id, double distance, long time) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.DISTANCE, distance);
        contentValues.put(DatabaseHelper.TIME, time);
        int i = database.update(DatabaseHelper.CARDIO_TABLE_NAME, contentValues, DatabaseHelper.ID + " = " + id, null);
        return i;
    }

    public void deleteFromCardioTable(long id) {
        database.delete(DatabaseHelper.CARDIO_TABLE_NAME, DatabaseHelper.ID + "=" + id, null);
    }

    public void insertIntoOneRepMaxTable(String name, double weight) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.NAME, name);
        contentValues.put(DatabaseHelper.WEIGHT, weight);
        database.insert(DatabaseHelper.ONE_REP_MAX_TABLE_NAME, null, contentValues);
    }

    public Cursor fetchFromOneRepMaxTable(String exerciseName) {
        String[] columns = new String[] { DatabaseHelper.ID, DatabaseHelper.NAME, DatabaseHelper.WEIGHT, DatabaseHelper.TIMESTAMP};
        Cursor cursor = database.query(DatabaseHelper.ONE_REP_MAX_TABLE_NAME, columns, "name = \"" + exerciseName + "\"", null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int updateOneRepMaxTable(long id, String name, double weight) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.NAME, name);
        contentValues.put(DatabaseHelper.WEIGHT, weight);
        int i = database.update(DatabaseHelper.ONE_REP_MAX_TABLE_NAME, contentValues, DatabaseHelper.ID + " = " + id, null);
        return i;
    }

    public void deleteFromOneRepMaxTable(long id) {
        database.delete(DatabaseHelper.ONE_REP_MAX_TABLE_NAME, DatabaseHelper.ID + "=" + id, null);
    }

    public void insertIntoGoalTable(String type, String exercise, Double weight, Double currentBodyWeight, Double bodyWeight, Double distance, Long time) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.TYPE, type);
        contentValues.put(DatabaseHelper.EXERCISE, exercise);
        contentValues.put(DatabaseHelper.WEIGHT, weight);
        contentValues.put(DatabaseHelper.CURRENT_BODY_WEIGHT, currentBodyWeight);
        contentValues.put(DatabaseHelper.BODY_WEIGHT, bodyWeight);
        contentValues.put(DatabaseHelper.DISTANCE, distance);
        contentValues.put(DatabaseHelper.TIME, time);

        database.insert(DatabaseHelper.GOAL_TABLE_NAME, null, contentValues);
    }

    public Cursor fetchFromGoalTable() {
        String[] columns = new String[] { DatabaseHelper.ID, DatabaseHelper.TYPE, DatabaseHelper.EXERCISE, DatabaseHelper.WEIGHT, DatabaseHelper.CURRENT_BODY_WEIGHT, DatabaseHelper.BODY_WEIGHT, DatabaseHelper.DISTANCE, DatabaseHelper.TIME, DatabaseHelper.TIMESTAMP};
        Cursor cursor = database.query(DatabaseHelper.GOAL_TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int updateGoalTable(long id, String type, String exercise, double weight, double currentBodyWeight, double bodyWeight, double distance, long time) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.TYPE, type);
        contentValues.put(DatabaseHelper.EXERCISE, exercise);
        contentValues.put(DatabaseHelper.WEIGHT, weight);
        contentValues.put(DatabaseHelper.CURRENT_BODY_WEIGHT, currentBodyWeight);
        contentValues.put(DatabaseHelper.BODY_WEIGHT, bodyWeight);
        contentValues.put(DatabaseHelper.DISTANCE, distance);
        contentValues.put(DatabaseHelper.TIME, time);
        int i = database.update(DatabaseHelper.GOAL_TABLE_NAME, contentValues, DatabaseHelper.ID + " = " + id, null);
        return i;
    }

    public void deleteFromGoalTable(long id) {
        database.delete(DatabaseHelper.GOAL_TABLE_NAME, DatabaseHelper.ID + "=" + id, null);
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }
}
