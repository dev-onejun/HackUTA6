package ai.onnxruntime.genai.demo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "user_data.db";
    private static final int DATABASE_VERSION = 6;

    public static final String TABLE_NAME = "user_info";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_HEIGHT = "height";
    public static final String COLUMN_WEIGHT = "weight";

    public static final String DYNAMIC_TABLE_NAME = "wardrobe";
    public static final String DYNAMIC_COLUMN_ID = "_id";
    public static final String DYNAMIC_COLUMN_USER_ID = "user_id";
    public static final String DYNAMIC_COLUMN_NAME = "clothes";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_HEIGHT + " TEXT, " +
                    COLUMN_WEIGHT + " TEXT);";

    private static final String DYNAMIC_TABLE_CREATE =
            "CREATE TABLE " + DYNAMIC_TABLE_NAME + " (" +
                    DYNAMIC_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DYNAMIC_COLUMN_USER_ID + " INTEGER, " +
                    DYNAMIC_COLUMN_NAME + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        db.execSQL(DYNAMIC_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DYNAMIC_TABLE_NAME);
        onCreate(db);
    }
}