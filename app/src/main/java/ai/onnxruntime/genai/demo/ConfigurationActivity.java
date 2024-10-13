package ai.onnxruntime.genai.demo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class ConfigurationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        EditText heightEditText = findViewById(R.id.edit_text_height);
        EditText weightEditText = findViewById(R.id.edit_text_weight);
        Button saveButton = findViewById(R.id.button_save);

        saveButton.setOnClickListener(view -> {
            String height = heightEditText.getText().toString();
            String weight = weightEditText.getText().toString();
            // ... (Save data, e.g., using SharedPreferences or a database) ...
            // ... (Consider input validation and error handling here) ...
        });
    }
}