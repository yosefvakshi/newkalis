package com.example.kalistanics;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CalorieActivity extends AppCompatActivity {

    // הגדרת רכיבי ממשק
    private EditText etAge, etWeight, etHeight, etWeightChangeRate;
    private RadioGroup rgGender, rgWeightGoal;
    private Spinner spActivityLevel;
    private Button btnCalculate;
    private TextView tvResult;

    // משתנה שישמור את רמת הפעילות שנבחרה
    private String selectedActivityLevel;

    // מודל של Gemini
    private GenerativeModelFutures modelFutures;
    private final Executor executor = Executors.newSingleThreadExecutor();

    // זיכרון לתשובות חוזרות (cache)
    private Map<String, String> geminiCache = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorie_chat); // טען את מסך המחשבון

        // אתחול חיבור ל-Gemini API
        initializeGeminiApi();

        // אתחול רכיבי הממשק
        initializeViews();

        // הגדרת התנהגות של תפריט הבחירה לפעילות גופנית
        setupActivityLevelSpinner();

        // מאזין ללחיצה על כפתור החישוב
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateCalorieNeeds(); // בצע חישוב
            }
        });
    }

    // אתחול מודל Gemini עם מפתח ה־API
    private void initializeGeminiApi() {
        GenerativeModel gModel = new GenerativeModel(
                "gemini-1.5-flash",
                getString(R.string.gemini_api_key)
        );
        modelFutures = GenerativeModelFutures.from(gModel);
    }

    // קישור רכיבי הממשק לפי ה־ID שלהם בקובץ ה־XML
    private void initializeViews() {
        etAge = findViewById(R.id.etAge);
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        etWeightChangeRate = findViewById(R.id.etWeightChangeRate);
        rgGender = findViewById(R.id.rgGender);
        rgWeightGoal = findViewById(R.id.rgWeightGoal);
        spActivityLevel = findViewById(R.id.spActivityLevel);
        btnCalculate = findViewById(R.id.btnCalculate);
        tvResult = findViewById(R.id.tvResult);
    }

    // קביעת אפשרויות הספינר לרמות פעילות גופנית
    private void setupActivityLevelSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.activity_levels,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spActivityLevel.setAdapter(adapter);

        spActivityLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedActivityLevel = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedActivityLevel = "פעילות מתונה";
            }
        });
    }

    // פונקציית חישוב הקלוריות – מתבצע לאחר לחיצה על כפתור
    private void calculateCalorieNeeds() {
        // קריאת ערכים מהמשתמש
        String ageStr = etAge.getText().toString();
        String weightStr = etWeight.getText().toString();
        String heightStr = etHeight.getText().toString();
        String weightChangeRateStr = etWeightChangeRate.getText().toString();

        // בדיקה אם כל השדות מולאו
        if (ageStr.isEmpty() || weightStr.isEmpty() || heightStr.isEmpty() || weightChangeRateStr.isEmpty()) {
            Toast.makeText(this, "נא למלא את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }

        // המרת הטקסט למספרים
        int age = Integer.parseInt(ageStr);
        double weight = Double.parseDouble(weightStr);
        double height = Double.parseDouble(heightStr);
        double weightChangeRate = Double.parseDouble(weightChangeRateStr);

        // קביעת מגדר
        String gender = rgGender.getCheckedRadioButtonId() == R.id.rbMale ? "זכר" : "נקבה";

        // קביעת מטרת משקל
        String weightGoal = rgWeightGoal.getCheckedRadioButtonId() == R.id.rbLoseWeight ?
                "ירידה במשקל" : "עלייה במשקל";

        // יצירת פרומפט לג’מיני
        String prompt = createGeminiPrompt(age, weight, height, gender, selectedActivityLevel, weightGoal, weightChangeRate);

        // הצגת הודעת "מחשב..."
        tvResult.setText("מחשב...");

        // בדיקה אם התוצאה כבר קיימת בזיכרון
        if (geminiCache.containsKey(prompt)) {
            tvResult.setText(geminiCache.get(prompt));
            return;
        }

        // קריאה למודל של ג'מיני
        queryGeminiApi(prompt);
    }

    // יצירת פרומפט לשאילתת Gemini
    private String createGeminiPrompt(int age, double weight, double height,
                                      String gender, String activityLevel,
                                      String weightGoal, double weightChangeRate) {

        String template =
                "חשב את צריכת הקלוריות היומית המומלצת עבור אדם בעל המאפיינים הבאים:\n" +
                        "- גיל: %AGE% שנים\n" +
                        "- משקל: %WEIGHT% ק\"ג\n" +
                        "- גובה: %HEIGHT% ס\"מ\n" +
                        "- מגדר: %GENDER%\n" +
                        "- רמת פעילות גופנית: %ACTIVITY_LEVEL%\n" +
                        "- מטרת משקל: %WEIGHT_GOAL%\n" +
                        "- קצב %CHANGE_DIRECTION% במשקל מבוקש: %WEIGHT_CHANGE_RATE% ק\"ג בשבוע\n\n" +
                        "תן רק את מס הקלוריות ליום שיש לצרוך";

        return template
                .replace("%AGE%", String.valueOf(age))
                .replace("%WEIGHT%", String.valueOf(weight))
                .replace("%HEIGHT%", String.valueOf(height))
                .replace("%GENDER%", gender)
                .replace("%ACTIVITY_LEVEL%", activityLevel)
                .replace("%WEIGHT_GOAL%", weightGoal)
                .replace("%WEIGHT_CHANGE_RATE%", String.valueOf(weightChangeRate))
                .replace("%CHANGE_DIRECTION%", weightGoal.equals("ירידה במשקל") ? "ירידה" : "עלייה");
    }

    // קריאה ל-Gemini API ושליחת הפרומפט
    private void queryGeminiApi(String prompt) {
        Content content = new Content.Builder()
                .addText(prompt)
                .build();

        ListenableFuture<GenerateContentResponse> response = modelFutures.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                final String responseText = result.getText();
                geminiCache.put(prompt, responseText); // שמור בזיכרון
                runOnUiThread(() -> tvResult.setText(responseText)); // הצג תוצאה במסך
            }

            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(() -> {
                    Log.e("GeminiAPI", "שגיאה", t);
                    tvResult.setText("שגיאה: " + t.getMessage());
                    Toast.makeText(CalorieActivity.this, "שגיאה בקריאה ל-Gemini API", Toast.LENGTH_LONG).show();
                });
            }
        }, executor);
    }

    // --- תפריט עליון (ActionBar) ---

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu); // טען את תפריט האפשרויות מהתיקייה res/menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_home) {
            // אם המשתמש לחץ על "בית" — נווט למסך הראשי
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
