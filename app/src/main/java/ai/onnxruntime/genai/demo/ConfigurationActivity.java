package ai.onnxruntime.genai.demo;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

public class ConfigurationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

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

            // ... (Save data, e.g., using SharedPreferences or a database) ...
            // ... (Consider input validation and error handling here) ...
        });

        returnButton.setOnClickListener(view -> {
            finish();
        });
    }
}