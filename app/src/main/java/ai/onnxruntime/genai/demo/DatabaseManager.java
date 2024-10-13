package ai.onnxruntime.genai.demo;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;

public class DatabaseManager {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public DatabaseManager(Context context) {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public long insertUserInfo(String height, String weight) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_HEIGHT, height);
        values.put(DatabaseHelper.COLUMN_WEIGHT, weight);
        return database.insert(DatabaseHelper.TABLE_NAME, null, values);
    }

    public void insertDynamicTexts(long userId, String[] texts) {
        database.beginTransaction();
        try {
            for (String text : texts) {
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.DYNAMIC_COLUMN_USER_ID, userId);
                values.put(DatabaseHelper.DYNAMIC_COLUMN_NAME, text);
                database.insert(DatabaseHelper.DYNAMIC_TABLE_NAME, null, values);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public Cursor getAllDynamicText() {
        return database.query(DatabaseHelper.DYNAMIC_TABLE_NAME, null, null, null, null, null, null);
    }

    public void close() {
        dbHelper.close();
    }
}