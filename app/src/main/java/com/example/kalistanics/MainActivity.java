package com.example.kalistanics;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.WHITE);  // משנה את צבע הרקע של ה־Status Bar ללבן
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            // משנה את הטקסט והאייקונים של ה־Status Bar לכהים (שיהיו נראים על רקע לבן)
        }


        // יצירת מסד הנתונים
        dbHelper = new DatabaseHelper(this);

        // הגדרת כפתורים
        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);
        Button button3 = findViewById(R.id.button3);
        Button button4 = findViewById(R.id.button4);

        // הגדרת מאזיני לחיצה
        button1.setOnClickListener(v -> openChallenge(1));
        button2.setOnClickListener(v -> openChallenge(2));
        button3.setOnClickListener(v -> openChallenge(3));
        button4.setOnClickListener(v -> openChallenge(4));
    }

    private void openChallenge(int challengeNumber) {
        // קבלת מזהה האתגר מהמסד
        Cursor cursor = null;
        try {
            // בדיקה שהמספר חוקי
            if (challengeNumber < 1 || challengeNumber > 4) {
                Toast.makeText(this, "שגיאה: מספר אתגר לא חוקי", Toast.LENGTH_SHORT).show();
            return;
        }

            // פתיחת דף השלבים עם מזהה האתגר
            Intent intent = new Intent(this, LevelMuscleUpp.class);
            intent.putExtra("challenge_id", (long)challengeNumber);  // המרה מפורשת ל-Long
                startActivity(intent);

        } catch (Exception e) {
            android.util.Log.e("MainActivity", "Error opening challenge: " + e.getMessage());
            Toast.makeText(this, "שגיאה: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
