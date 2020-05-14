package evanh.fitnesspro.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String WORKOUT_DATA_TABLE_NAME = "workout_data";

    // Table columns
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String WEIGHT = "weight";
    public static final String REPS = "reps";
    public static final String TIMESTAMP = "timestamp";

    // Table Name
    public static final String BODY_WEIGHT_TABLE_NAME = "body_weight_data";

    // Table columns
    //public static final String ID = "id";
    public static final String BODY_WEIGHT = "body_weight";
    //public static final String TIMESTAMP = "timestamp";

    // Table Name
    public static final String CARDIO_TABLE_NAME = "cardio_data";

    // Table columns
    //public static final String ID = "id";
    public static final String DISTANCE = "distance";
    public static final String TIME = "time";
    //public static final String TIMESTAMP = "timestamp";

    // Table Name
    public static final String ONE_REP_MAX_TABLE_NAME = "one_rep_max_data";

    // Table columns
    //public static final String ID = "id";
    //public static final String NAME = "name";
    //public static final String WEIGHT = "weight";
    //public static final String TIMESTAMP = "timestamp";

    // Table Name
    public static final String GOAL_TABLE_NAME = "goal_data";

    // Table columns
    //public static final String ID = "id";
    public static final String TYPE = "type";
    public static final String EXERCISE = "exercise_name";
    //public static final String WEIGHT = "weight";
    public static final String CURRENT_BODY_WEIGHT = "current_body_weight";
    //public static final String BODY_WEIGHT = "body_weight";
    //public static final String DISTANCE = "distance";
    //public static final String TIME = "time";
    //public static final String TIMESTAMP = "timestamp";

    // Database Information
    static final String DB_NAME = "fitnesspro.DB";

    // Database version
    static final int DB_VERSION = 1;

    // Creating table query
    private static final String CREATE_WORKOUT_TABLE = "CREATE TABLE " + WORKOUT_DATA_TABLE_NAME + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME + " TEXT NOT NULL, " + WEIGHT + " REAL NOT NULL, " + REPS + " INTEGER NOT NULL," + TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";
    private static final String CREATE_BODY_WEIGHT_TABLE = "CREATE TABLE " + BODY_WEIGHT_TABLE_NAME + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + BODY_WEIGHT + " REAL NOT NULL, " + TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";
    private static final String CREATE_CARDIO_TABLE = "CREATE TABLE " + CARDIO_TABLE_NAME + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DISTANCE + " REAL NOT NULL, " + TIME + " INTEGER NOT NULL, " + TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";
    private static final String CREATE_ONE_REP_MAX_TABLE = "CREATE TABLE " + ONE_REP_MAX_TABLE_NAME + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME + " TEXT NOT NULL, " + WEIGHT + " REAL NOT NULL, " +  TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";
    private static final String CREATE_GOAL_TABLE = "CREATE TABLE " + GOAL_TABLE_NAME + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TYPE + " TEXT NOT NULL, " + EXERCISE + " TEXT, " + WEIGHT + " REAL, " + CURRENT_BODY_WEIGHT + " REAL, " + BODY_WEIGHT + " REAL, "+ DISTANCE + " REAL, " + TIME + " INTEGER, " + TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_WORKOUT_TABLE);
        db.execSQL(CREATE_BODY_WEIGHT_TABLE);
        db.execSQL(CREATE_CARDIO_TABLE);
        db.execSQL(CREATE_ONE_REP_MAX_TABLE);
        db.execSQL(CREATE_GOAL_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WORKOUT_DATA_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + BODY_WEIGHT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CARDIO_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ONE_REP_MAX_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GOAL_TABLE_NAME);
        onCreate(db);
    }
}
