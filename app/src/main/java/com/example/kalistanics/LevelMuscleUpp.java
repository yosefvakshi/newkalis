package com.example.kalistanics;

import static androidx.core.app.ActivityCompat.finishAffinity;
import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class LevelMuscleUpp extends AppCompatActivity {
    private HorizontalScrollView scrollView1, scrollView2, scrollView3, scrollView4, scrollView5;
    private Button button1, button2, button3, button4, button5;
    private Button completeButton1, completeButton2, completeButton3, completeButton4, completeButton5;
    private boolean isLevel1Open = false;
    private boolean isLevel2Open = false;
    private boolean isLevel3Open = false;
    private boolean isLevel4Open = false;
    private boolean isLevel5Open = false;
    private boolean[] isCompleted = new boolean[5];
    private DatabaseHelper dbHelper;
    private long currentChallengeId;
    // --- Music ---
    private MediaPlayer player;
    private boolean musicOn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_muscle_upp);
// --- init music ---
        player = MediaPlayer.create(this, R.raw.workout_track); // ‎res/raw/workout_track.mp3
        player.setLooping(true);

        // קבלת מזהה האתגר מה-Intent
        currentChallengeId = getIntent().getLongExtra("challenge_id", -1);
        if (currentChallengeId == -1) {
            Toast.makeText(this, "שגיאה בטעינת האתגר", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        android.util.Log.d("LevelMuscleUpp", "Current challenge ID: " + currentChallengeId);

        // אתחול כפתורים
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);

        // אתחול כפתורי הצלחה
        completeButton1 = findViewById(R.id.completeButton1);
        completeButton2 = findViewById(R.id.completeButton2);
        completeButton3 = findViewById(R.id.completeButton3);
        completeButton4 = findViewById(R.id.completeButton4);
        completeButton5 = findViewById(R.id.completeButton5);

        // אתחול אזורי גלילה
        scrollView1 = findViewById(R.id.scrollView1);
        scrollView2 = findViewById(R.id.scrollView2);
        scrollView3 = findViewById(R.id.scrollView3);
        scrollView4 = findViewById(R.id.scrollView4);
        scrollView5 = findViewById(R.id.scrollView5);

        // הגדרת מאזיני לחיצה לכפתורים
        button1.setOnClickListener(v -> {
            android.util.Log.d("LevelMuscleUpp", "Button 1 clicked");
            toggleLevel(1);
        });
        button2.setOnClickListener(v -> {
            android.util.Log.d("LevelMuscleUpp", "Button 2 clicked");
            toggleLevel(2);
        });
        button3.setOnClickListener(v -> {
            android.util.Log.d("LevelMuscleUpp", "Button 3 clicked");
            toggleLevel(3);
        });
        button4.setOnClickListener(v -> {
            android.util.Log.d("LevelMuscleUpp", "Button 4 clicked");
            toggleLevel(4);
        });
        button5.setOnClickListener(v -> {
            android.util.Log.d("LevelMuscleUpp", "Button 5 clicked");
            toggleLevel(5);
        });

        // הגדרת מאזיני לחיצה לכפתורי הצלחה
        completeButton1.setOnClickListener(v -> handleLevelCompletion(1));
        completeButton2.setOnClickListener(v -> handleLevelCompletion(2));
        completeButton3.setOnClickListener(v -> handleLevelCompletion(3));
        completeButton4.setOnClickListener(v -> handleLevelCompletion(4));
        completeButton5.setOnClickListener(v -> handleLevelCompletion(5));

        // אתחול מסד הנתונים וטעינת נתונים
        dbHelper = new DatabaseHelper(this);

        // טעינת נתונים
        loadLevelData();
            }

            @Override
    protected void onResume() {
        super.onResume();
        try {
            // בדיקת תקינות מסד הנתונים
            dbHelper.validateDatabase();
        } catch (Exception e) {
            android.util.Log.e("LevelMuscleUpp", "Error validating database: " + e.getMessage());
        }
    }

    private void loadLevelData() {
        try {
            android.util.Log.d("LevelMuscleUpp", "Starting to load level data for challenge " + currentChallengeId);

            // טעינת נתונים לכל שלב
            loadTrainingsForLevel(1, scrollView1);
            loadTrainingsForLevel(2, scrollView2);
            loadTrainingsForLevel(3, scrollView3);
            loadTrainingsForLevel(4, scrollView4);
            loadTrainingsForLevel(5, scrollView5);

            android.util.Log.d("LevelMuscleUpp", "Finished loading level data");
        } catch (Exception e) {
            android.util.Log.e("LevelMuscleUpp", "Error loading level data: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "שגיאה בטעינת הנתונים", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadTrainingsForLevel(int levelNumber, HorizontalScrollView scrollView) {
        try {
            android.util.Log.d("LevelMuscleUpp", "Loading trainings for level " + levelNumber);

            // יצירת מיכל לתרגילים
            LinearLayout container = scrollView.findViewById(getResources().getIdentifier("container" + levelNumber, "id", getPackageName()));
            if (container == null) {
                android.util.Log.e("LevelMuscleUpp", "Container not found for level " + levelNumber);
                return;
            }

            // ניקוי המיכל לפני טעינת תרגילים חדשים
            container.removeAllViews();
            android.util.Log.d("LevelMuscleUpp", "Cleared container for level " + levelNumber);

            // חישוב ה-level_id הנכון בהתאם לאתגר הנוכחי
            long levelId;
            if (currentChallengeId == 1) { // Muscle Up
                levelId = levelNumber;
            } else if (currentChallengeId == 2) { // Pull Up
                levelId = levelNumber + 5;
            } else if (currentChallengeId == 3) { // Push Up
                levelId = levelNumber + 10;
            } else if (currentChallengeId == 4) { // Dip
                levelId = levelNumber + 15;
            } else {
                android.util.Log.e("LevelMuscleUpp", "Invalid challenge ID: " + currentChallengeId);
                return;
            }

            android.util.Log.d("LevelMuscleUpp", "Loading trainings for level " + levelNumber + " (level_id: " + levelId + ", challenge_id: " + currentChallengeId + ")");

            // קבלת התרגילים מהמסד
            Cursor cursor = dbHelper.getTrainingsForLevel(levelId);
            if (cursor != null) {
                android.util.Log.d("LevelMuscleUpp", "Cursor returned with " + cursor.getCount() + " rows");

                // קבלת אינדקסי העמודות
                int descriptionIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TRAINING_DESCRIPTION);
                int imageIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TRAINING_IMAGE);

                if (descriptionIndex == -1 || imageIndex == -1) {
                    android.util.Log.e("LevelMuscleUpp", "Column not found: description=" + descriptionIndex + ", image=" + imageIndex);
                    cursor.close();
                    return;
                }

                int count = 0;
                while (cursor.moveToNext()) {
                    try {
                        String description = cursor.getString(descriptionIndex);
                        String imageName = cursor.getString(imageIndex);

                        android.util.Log.d("LevelMuscleUpp", "Found training: " + description + " with image: " + imageName);

                        // יצירת תצוגת תרגיל
                        View exerciseView = getLayoutInflater().inflate(R.layout.exercise_cards, container, false);

                        ImageView imageView = exerciseView.findViewById(R.id.image1);
                        TextView textView = exerciseView.findViewById(R.id.text1);

                        // הגדרת תמונה וטקסט
                        android.util.Log.d("LevelMuscleUpp", "Found image name: " + imageName);

                        // ניסיון לטעון את התמונה
                        try {
                            // המרת שם התמונה לפורמט הנכון
                            String formattedImageName = imageName;
                            if (currentChallengeId == 1) { // Muscle Up
                                formattedImageName = imageName.replace("_upp_", "upp");
                            } else if (currentChallengeId == 2) { // Pull Up
                                formattedImageName = "back" + imageName.split("_")[2] + "_" + imageName.split("_")[3];
                            } else if (currentChallengeId == 3) { // Push Up
                                formattedImageName = "front" + imageName.split("_")[2] + "_" + imageName.split("_")[3];
                            } else if (currentChallengeId == 4) { // Dip
                                formattedImageName = "planche" + imageName.split("_")[1] + "_" + imageName.split("_")[2];
                            }

                            android.util.Log.d("LevelMuscleUpp", "Original image name: " + imageName);
                            android.util.Log.d("LevelMuscleUpp", "Formatted image name: " + formattedImageName);

                            // נסה לטעון את התמונה עם סיומת PNG
                            int imageResource = getResources().getIdentifier(formattedImageName, "drawable", getPackageName());
                            android.util.Log.d("LevelMuscleUpp", "Resource ID for " + formattedImageName + ": " + imageResource);

                            if (imageResource != 0) {
                                imageView.setImageResource(imageResource);
                                android.util.Log.d("LevelMuscleUpp", "Successfully loaded image using resource ID");
                            } else {
                                android.util.Log.e("LevelMuscleUpp", "Image resource not found: " + formattedImageName);

                                // נסה לטעון את התמונה עם סיומת JPG
                                try {
                                    String imagePath = formattedImageName + ".jpg";
                                    android.util.Log.d("LevelMuscleUpp", "Trying to load image: " + imagePath);

                                    // נסה לטעון מתיקיית drawable
                                    try {
                                        java.io.InputStream is = getResources().openRawResource(
                                            getResources().getIdentifier(formattedImageName, "raw", getPackageName())
                                        );
                                        android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeStream(is);
                                        imageView.setImageBitmap(bitmap);
                                        is.close();
                                        android.util.Log.d("LevelMuscleUpp", "Successfully loaded image from raw resources");
                                    } catch (Exception e) {
                                        android.util.Log.e("LevelMuscleUpp", "Failed to load from raw resources: " + e.getMessage());

                                        // נסה לטעון מתיקיית assets
                                        try {
                                            java.io.InputStream is = getAssets().open(imagePath);
                                            android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeStream(is);
                                            imageView.setImageBitmap(bitmap);
                                            is.close();
                                            android.util.Log.d("LevelMuscleUpp", "Successfully loaded image from assets");
                                        } catch (Exception e2) {
                                            android.util.Log.e("LevelMuscleUpp", "Failed to load from assets: " + e2.getMessage());

                                            // נסה לטעון מהקובץ ישירות
                                            try {
                                                java.io.File file = new java.io.File(getFilesDir(), imagePath);
                                                if (file.exists()) {
                                                    android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeFile(file.getAbsolutePath());
                                                    imageView.setImageBitmap(bitmap);
                                                    android.util.Log.d("LevelMuscleUpp", "Successfully loaded image from file");
                                                } else {
                                                    android.util.Log.e("LevelMuscleUpp", "Image file not found: " + file.getAbsolutePath());
                                                }
                                            } catch (Exception e3) {
                                                android.util.Log.e("LevelMuscleUpp", "Failed to load from file: " + e3.getMessage());
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    android.util.Log.e("LevelMuscleUpp", "Error loading image: " + e.getMessage());
                                }
                            }
                        } catch (Exception e) {
                            android.util.Log.e("LevelMuscleUpp", "Error loading image: " + e.getMessage());
                        }
                        textView.setText(description);

                        container.addView(exerciseView);
                        count++;
                    } catch (Exception e) {
                        android.util.Log.e("LevelMuscleUpp", "Error processing training: " + e.getMessage());
                    }
                }
                android.util.Log.d("LevelMuscleUpp", "Loaded " + count + " exercises for level " + levelNumber);
                cursor.close();
            } else {
                android.util.Log.e("LevelMuscleUpp", "No cursor returned for level " + levelNumber);
            }
        } catch (Exception e) {
            android.util.Log.e("LevelMuscleUpp", "Error loading trainings for level " + levelNumber + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void toggleLevel(int level) {
        android.util.Log.d("LevelMuscleUpp", "Toggling level " + level);
        HorizontalScrollView scrollView = null;
        boolean isOpen = false;

        switch (level) {
            case 1:
                scrollView = scrollView1;
                isOpen = isLevel1Open;
                isLevel1Open = !isLevel1Open;
                break;
            case 2:
                scrollView = scrollView2;
                isOpen = isLevel2Open;
                isLevel2Open = !isLevel2Open;
                break;
            case 3:
                scrollView = scrollView3;
                isOpen = isLevel3Open;
                isLevel3Open = !isLevel3Open;
                break;
            case 4:
                scrollView = scrollView4;
                isOpen = isLevel4Open;
                isLevel4Open = !isLevel4Open;
                break;
            case 5:
                scrollView = scrollView5;
                isOpen = isLevel5Open;
                isLevel5Open = !isLevel5Open;
                break;
        }

        if (scrollView != null) {
            android.util.Log.d("LevelMuscleUpp", "Setting visibility for level " + level + " to " + (!isOpen ? "VISIBLE" : "GONE"));
            scrollView.setVisibility(!isOpen ? View.VISIBLE : View.GONE);

            // בדיקה שהתרגילים נטענו
            LinearLayout container = scrollView.findViewById(getResources().getIdentifier("container" + level, "id", getPackageName()));
            if (container != null) {
                android.util.Log.d("LevelMuscleUpp", "Container " + level + " has " + container.getChildCount() + " children");
                if (container.getChildCount() == 0) {
                    android.util.Log.d("LevelMuscleUpp", "Reloading trainings for level " + level);
                    loadTrainingsForLevel(level, scrollView);
                }
            } else {
                android.util.Log.e("LevelMuscleUpp", "Container " + level + " not found");
            }
        } else {
            android.util.Log.e("LevelMuscleUpp", "ScrollView is null for level " + level);
        }
    }

    private void handleLevelCompletion(int level) {
        Button completeButton;
        String message;
        int levelIndex = level - 1;

        // בחירת הכפתור וההודעה המתאימים לפי השלב
        switch (level) {
            case 1:
                completeButton = completeButton1;
                message = "מעולה! הצלחת! עכשיו תעבור לשלב השני";
                break;
            case 2:
                completeButton = completeButton2;
                message = "מעולה! הצלחת! עכשיו תעבור לשלב השלישי";
                break;
            case 3:
                completeButton = completeButton3;
                message = "מעולה! הצלחת! עכשיו תעבור לשלב הרביעי";
                break;
            case 4:
                completeButton = completeButton4;
                message = "מעולה! הצלחת! עכשיו תעבור לשלב החמישי";
                break;
            case 5:
                completeButton = completeButton5;
                message = "מעולה! הצלחת! סיימת את כל השלבים!";
                break;
            default:
                return;
        }

        // החלפת מצב ההשלמה - כמו מתג (toggle)
        // כל לחיצה מחליפה בין "הושלם" ל"לא הושלם"
        isCompleted[levelIndex] = !isCompleted[levelIndex];

        // שינוי הטקסט והצבע בהתאם למצב
        if (isCompleted[levelIndex]) {
            // מצב "הושלם":
            // 1. משנה את הטקסט לסימן וי (✓)
            // 2. משנה את צבע הרקע לבן
            // 3. משנה את צבע הטקסט ללבן
            // 4. מציג הודעת הצלחה
            completeButton.setText("✓");
            completeButton.setTextColor(getResources().getColor(android.R.color.white));
            completeButton.setBackgroundTintList(getResources().getColorStateList(android.R.color.holo_green_dark));
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        } else {
            // מצב "לא הושלם":
            // 1. מחזיר את הטקסט ל"הצלחתי"
            // 2. מחזיר את צבע הרקע לאפור המקורי
            // 3. מחזיר את צבע הטקסט לשחור
            completeButton.setText("הצלחתי");
            completeButton.setTextColor(getResources().getColor(android.R.color.black));
            completeButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E0E0E0")));
        }
    }
// פונקצית מניו
@Override
public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu, menu);  // טוען את הקובץ menu_main.xml
    return true;
}

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

            int id = item.getItemId();

            if (id == R.id.action_search_park) {
                // כאן מפעילים את ה-ParksMapActivity
                Intent intent = new Intent(this, ParksMapActivity.class);
                startActivity(intent);
                return true;
            }



        if (id == R.id.action_exit) {          // יציאה
            finishAffinity();
            return true;

        } else if (id == R.id.action_main) {   // חזרה
            finish();
            return true;

        } else if (id == R.id.action_setting) { // סרטון חימום
            String videoUrl = "https://www.youtube.com/watch?v=uTV-sR7_QgY";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl)));
            return true;

        } else if (id == R.id.action_music) {   // כפתור המוזיקה החדש
            toggleMusic(item);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /** מפעיל/מפסיק מוזיקה ומעדכן אייקון וטקסט */
    private void toggleMusic(MenuItem item) {
        if (musicOn) {
            player.pause();
            item.setIcon(R.drawable.ic_music_note); // אייקון "כבוי"
            item.setTitle(R.string.music_on);       // "הפעל מוזיקה"
        } else {
            player.start();
            item.setIcon(R.drawable.ic_music_off);  // אייקון "דולק"
            item.setTitle(R.string.music_off);      // "כבה מוזיקה"
        }
        musicOn = !musicOn;
    }

}


