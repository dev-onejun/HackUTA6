package ai.onnxruntime.genai.demo;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

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

    public List<String> getAllClothes() {
        List<String> clothesList = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.DYNAMIC_TABLE_NAME,
                new String[]{DatabaseHelper.DYNAMIC_COLUMN_NAME},
                DatabaseHelper.DYNAMIC_COLUMN_USER_ID + " = (SELECT MAX(" + DatabaseHelper.DYNAMIC_COLUMN_USER_ID + ") FROM " + DatabaseHelper.DYNAMIC_TABLE_NAME + ")",
                null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                String clothes = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.DYNAMIC_COLUMN_NAME));
                clothesList.add(clothes);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return clothesList;
    }

    public void close() {
        dbHelper.close();
    }
}