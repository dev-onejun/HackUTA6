package ai.onnxruntime.genai.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import org.json.JSONObject;
import org.json.JSONArray;// Make sure to include a JSON library for parsing the API response
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ai.onnxruntime.genai.GenAIException;
import ai.onnxruntime.genai.Generator;
import ai.onnxruntime.genai.GeneratorParams;
import ai.onnxruntime.genai.Sequences;
import ai.onnxruntime.genai.TokenizerStream;
import ai.onnxruntime.genai.demo.databinding.ActivityMainBinding;
import ai.onnxruntime.genai.Model;
import ai.onnxruntime.genai.Tokenizer;

public class MainActivity extends AppCompatActivity implements Consumer<String> {

    private ActivityMainBinding binding;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Model model;
    private Tokenizer tokenizer;
    private ImageButton sendMsgIB;
    private TextView generatedTV;
    private TextView progressText;
    private ImageButton settingsButton;
    private static final String TAG = "genai.demo.MainActivity";
    private int maxLength = 100;
    private float lengthPenalty = 1.0f;

    double lat;
    double lng;

    private static boolean fileExists(Context context, String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        return file.exists();
    }


    private static final String WEATHER_API_KEY = "1ab286ec641a41dd0fd542ab19beff5d"; // Replace with your API key


    private String getWeather(double lat, double lon) {


        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + WEATHER_API_KEY + "&units=metric";
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            InputStreamReader inputStream = new InputStreamReader(urlConnection.getInputStream());
            reader = new BufferedReader(inputStream);

            StringBuilder buffer = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                return null; // No data retrieved
            }

            // Parse JSON response
            JSONObject jsonResponse = new JSONObject(buffer.toString());
            JSONObject mainObject = jsonResponse.getJSONObject("main");
            JSONArray weatherArray = jsonResponse.getJSONArray("weather");
            JSONObject weatherObject = weatherArray.getJSONObject(0);

            // Extract temperature and weather description
            double temp = mainObject.getDouble("temp");
            String weatherDescription = weatherObject.getString("description");

            // Return the formatted weather data
            return "The current temperature is " + temp + "°C and the weather is " + weatherDescription + ".";

        } catch (Exception e) {
            Log.e(TAG, "Error fetching weather data: ", e);
            return "Error retrieving weather data.";
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    Log.e(TAG, "Error closing stream: ", e);
                }
            }
        }
    }

    public String gatAllClothes() {
        DatabaseManager databaseManager = new DatabaseManager(this);
        List<String> clothesList = databaseManager.getAllClothes();
        databaseManager.close();
        return clothesList.toString();
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(gfgPolicy);
        super.onCreate(savedInstanceState);

        this.maxLength = 1000;
        this.lengthPenalty = 0.5f;

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                // Get latitude and longitude
                 lat = location.getLatitude();
                 lng = location.getLongitude();

                // Log the location data
                Log.i("AAA", "Latitude: " + lat);
                Log.i("AAA", "Longitude: " + lng);
            }
        };
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Request location permissions
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        } else {
            // If permissions are granted, request location updates
            requestLocationUpdates();
        }
    }

    private void requestLocationUpdates() {
        try {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    3000, 10, // Update every 3 seconds or when moved by 10 meters
                    locationListener
            );
        } catch (SecurityException e) {
            Log.e("AAA", "Location permission missing", e);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, request location updates
                requestLocationUpdates();
            } else {
                // Permission denied, show a message or handle the case appropriately
                Log.e("AAA", "Location permission denied");
            }
        }

        sendMsgIB = findViewById(R.id.idIBSend);
        generatedTV = findViewById(R.id.sample_text);
        progressText = findViewById(R.id.progress_text);
        settingsButton = findViewById(R.id.configuration_button);

        // Trigger the download operation when the application is created
        try {
            downloadModels(
                    getApplicationContext());
        } catch (GenAIException e) {
            throw new RuntimeException(e);
        }

        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ConfigurationActivity.class);
            startActivity(intent);
        });


        Consumer<String> tokenListener = this;

        //enable scrolling and resizing of text boxes
        generatedTV.setMovementMethod(new ScrollingMovementMethod());
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        // adding on click listener for send message button.
        sendMsgIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tokenizer == null) {
                    // if user tries to submit prompt while model is still downloading, display a toast message.
                    Toast.makeText(MainActivity.this, "Model not loaded yet, please wait...", Toast.LENGTH_SHORT).show();
                    return;
                }

                double latitude = lat;
                double longitude = lng;
                String weatherInfo = getWeather(latitude, longitude);

                String clothesItem = gatAllClothes();
                String promptQuestion_formatted = "<system>You are a helpful AI assistant. Answer in two or three words. Please list 3 fashion item based on this conditions<|end|><|user|>"+weatherInfo+"<|end|><|user|>"+clothesItem+"This is list of fashion items that I have. Please recommend only from here.<|end|>\n<assistant|>";
                Log.i("GenAI: prompt question", promptQuestion_formatted);
                setVisibility();

                // Disable send button while responding to prompt.
//                sendMsgIB.setEnabled(false);
//                sendMsgIB.setAlpha(0.5f);
                sendMsgIB.setEnabled(true);
                sendMsgIB.setAlpha(1.0f);

                // Clear Edit Text or prompt question.
                generatedTV.setText("");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        TokenizerStream stream = null;
                        GeneratorParams generatorParams = null;
                        Generator generator = null;
                        Sequences encodedPrompt = null;
                        try {
                            stream = tokenizer.createStream();

                            generatorParams = model.createGeneratorParams();
                            //examples for optional parameters to format AI response
                            // https://onnxruntime.ai/docs/genai/reference/config.html
                            generatorParams.setSearchOption("length_penalty", lengthPenalty);
                            generatorParams.setSearchOption("max_length", maxLength);

                            encodedPrompt = tokenizer.encode(promptQuestion_formatted);
                            generatorParams.setInput(encodedPrompt);

                            generator = new Generator(model, generatorParams);

                            // try to measure average time taken to generate each token.
                            long startTime = System.currentTimeMillis();
                            long firstTokenTime = startTime;
                            long currentTime = startTime;
                            int numTokens = 0;
                            while (!generator.isDone()) {
                                generator.computeLogits();
                                generator.generateNextToken();

                                int token = generator.getLastTokenInSequence(0);

                                if (numTokens == 0) { //first token
                                    firstTokenTime = System.currentTimeMillis();
                                }

                                tokenListener.accept(stream.decode(token));


                                Log.i(TAG, "Generated token: " + token + ": " +  stream.decode(token));
                                Log.i(TAG, "Time taken to generate token: " + (System.currentTimeMillis() - currentTime)/ 1000.0 + " seconds");
                                currentTime = System.currentTimeMillis();
                                numTokens++;
                            }
                            long totalTime = System.currentTimeMillis() - firstTokenTime;

                            float promptProcessingTime = (firstTokenTime - startTime)/ 1000.0f;
                            float tokensPerSecond = (1000 * (numTokens -1)) / totalTime;

                            runOnUiThread(() -> {
                                sendMsgIB.setEnabled(true);
                                sendMsgIB.setAlpha(1.0f);

                                // Display the token generation rate in a dialog popup
                                showTokenPopup(promptProcessingTime, tokensPerSecond);
                            });

                            Log.i(TAG, "Prompt processing time (first token): " + promptProcessingTime + " seconds");
                            Log.i(TAG, "Tokens generated per second (excluding prompt processing): " + tokensPerSecond);
                        }
                        catch (GenAIException e) {
                            Log.e(TAG, "Exception occurred during model query: " + e.getMessage());
                        }
                        finally {
                            if (generator != null) generator.close();
                            if (encodedPrompt != null) encodedPrompt.close();
                            if (stream != null) stream.close();
                            if (generatorParams != null) generatorParams.close();
                        }

                        runOnUiThread(() -> {
                            sendMsgIB.setEnabled(true);
                            sendMsgIB.setAlpha(1.0f);
                        });
                    }
                }).start();
            }
        });
    }





    @Override
    protected void onDestroy() {
        tokenizer.close();
        tokenizer = null;
        model.close();
        model = null;
        super.onDestroy();
    }

    private void downloadModels(Context context) throws GenAIException {

        final String baseUrl = "https://huggingface.co/microsoft/Phi-3-mini-4k-instruct-onnx/resolve/main/cpu_and_mobile/cpu-int4-rtn-block-32-acc-level-4/";
        List<String> files = Arrays.asList(
                "added_tokens.json",
                "config.json",
                "configuration_phi3.py",
                "genai_config.json",
                "phi3-mini-4k-instruct-cpu-int4-rtn-block-32-acc-level-4.onnx",
                "phi3-mini-4k-instruct-cpu-int4-rtn-block-32-acc-level-4.onnx.data",
                "special_tokens_map.json",
                "tokenizer.json",
                "tokenizer.model",
                "tokenizer_config.json");

        List<Pair<String, String>> urlFilePairs = new ArrayList<>();
        for (String file : files) {
            if (!fileExists(context, file)) {
                urlFilePairs.add(new Pair<>(
                        baseUrl + file,
                        file));
            }
        }
        if (urlFilePairs.isEmpty()) {
            // Display a message using Toast
            Toast.makeText(this, "All files already exist. Skipping download.", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "All files already exist. Skipping download.");
            model = new Model(getFilesDir().getPath());
            tokenizer = model.createTokenizer();
            return;
        }

        progressText.setText("Downloading...");
        progressText.setVisibility(View.VISIBLE);

        Toast.makeText(this,
                "Downloading model for the app... Model Size greater than 2GB, please allow a few minutes to download.",
                Toast.LENGTH_SHORT).show();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            ModelDownloader.downloadModel(context, urlFilePairs, new ModelDownloader.DownloadCallback() {
                @Override
                public void onProgress(long lastBytesRead, long bytesRead, long bytesTotal) {
                    long lastPctDone = 100 * lastBytesRead / bytesTotal;
                    long pctDone = 100 * bytesRead / bytesTotal;
                    if (pctDone > lastPctDone) {
                        Log.d(TAG, "Downloading files: " + pctDone + "%");
                        runOnUiThread(() -> {
                            progressText.setText("Downloading: " + pctDone + "%");
                        });
                    }
                }
                @Override
                public void onDownloadComplete() {
                    Log.d(TAG, "All downloads completed.");

                    // Last download completed, create SimpleGenAI
                    try {
                        model = new Model(getFilesDir().getPath());
                        tokenizer = model.createTokenizer();
                        runOnUiThread(() -> {
                            Toast.makeText(context, "All downloads completed", Toast.LENGTH_SHORT).show();
                            progressText.setVisibility(View.INVISIBLE);
                        });
                    } catch (GenAIException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }

                }
            });
        });
        executor.shutdown();
    }

    @Override
    public void accept(String token) {
        runOnUiThread(() -> {
            // Update and aggregate the generated text and write to text box.
            CharSequence generated = generatedTV.getText();
            generatedTV.setText(generated + token);
            generatedTV.invalidate();
            final int scrollAmount = generatedTV.getLayout().getLineTop(generatedTV.getLineCount()) - generatedTV.getHeight();
            generatedTV.scrollTo(0, Math.max(scrollAmount, 0));
        });
    }

    public void setVisibility() {
        TextView botView = (TextView) findViewById(R.id.sample_text);
        botView.setVisibility(View.VISIBLE);
    }

    private void showTokenPopup(float promptProcessingTime, float tokenRate) {

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.info_popup);

        TextView promptProcessingTimeTv = dialog.findViewById(R.id.prompt_processing_time_tv);
        TextView tokensPerSecondTv = dialog.findViewById(R.id.tokens_per_second_tv);
        Button closeBtn = dialog.findViewById(R.id.close_btn);

        promptProcessingTimeTv.setText(String.format("Prompt processing time: %.2f seconds", promptProcessingTime));
        tokensPerSecondTv.setText(String.format("Tokens per second: %.2f", tokenRate));

        closeBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }


}
