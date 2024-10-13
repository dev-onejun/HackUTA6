package ai.onnxruntime.genai.demo;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;


import androidx.appcompat.app.AppCompatActivity;

public class ConfigurationActivity extends AppCompatActivity {
    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        databaseManager = new DatabaseManager(this);

        EditText heightEditText = findViewById(R.id.edit_text_height);
        EditText weightEditText = findViewById(R.id.edit_text_weight);
        Button saveButton = findViewById(R.id.button_save);
        Button returnButton = findViewById(R.id.button_return);
        LinearLayout dynamicEditTextContainer = findViewById(R.id.dynamic_edit_text_container);

        TextView wardrobeTextView = new TextView(this);
        wardrobeTextView.setText("Wardrobe");
        wardrobeTextView.setGravity(Gravity.CENTER);
        dynamicEditTextContainer.addView(wardrobeTextView);

        Button addTextFieldButton = new Button(this);
        addTextFieldButton.setText("Add Text Field");
        dynamicEditTextContainer.addView(addTextFieldButton);

        addTextFieldButton.setOnClickListener(view -> {
            EditText newEditText = new EditText(this);
            newEditText.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            newEditText.setHint("New Text Field");
            dynamicEditTextContainer.addView(newEditText);
        });

        saveButton.setOnClickListener(view -> {
            String height = heightEditText.getText().toString();
            String weight = weightEditText.getText().toString();

            databaseManager.insertUserInfo(height, weight);

//            Cursor cursor = databaseManager.getUserInfoById(1); // Assuming the ID is 1 for demonstration
//            if (cursor.moveToFirst()) {
//                String retrievedHeight = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HEIGHT));
//                String retrievedWeight = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WEIGHT));
//                Log.d("ConfigurationActivity", "Retrieved Height: " + retrievedHeight + ", Retrieved Weight: " + retrievedWeight);
//                // Pop up the data
//                new AlertDialog.Builder(this)
//                        .setTitle("User Info")
//                        .setMessage("Height: " + retrievedHeight + "\nWeight: " + retrievedWeight)
//                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
//                            // Close the dialog and finish the activity
//                            finish();
//                        })
//                        .show();
//            }
//            cursor.close();

            databaseManager.close();

            // ... (Consider input validation and error handling here) ...
            finish();
        });

        returnButton.setOnClickListener(view -> {
            finish();
        });
    }
}