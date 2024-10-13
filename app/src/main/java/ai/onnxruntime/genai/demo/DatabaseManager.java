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

    public void insertUserInfo(String height, String weight) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_HEIGHT, height);
        values.put(DatabaseHelper.COLUMN_WEIGHT, weight);
        database.insert(DatabaseHelper.TABLE_NAME, null, values);
    }

    public Cursor getUserInfoById(long id) {
        String[] columns = {DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_HEIGHT, DatabaseHelper.COLUMN_WEIGHT};
        String orderBy = DatabaseHelper.COLUMN_ID + " DESC";
        String limit = "1";
        return database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, orderBy, limit);
    }

    public void close() {
        dbHelper.close();
    }
}