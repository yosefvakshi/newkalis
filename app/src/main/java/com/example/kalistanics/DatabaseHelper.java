package com.example.kalistanics;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteException;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "kalistanics.db";
    private static final int DATABASE_VERSION = 2;

    // שמות הטבלאות
    public static final String TABLE_CHALLENGES = "challenges";
    public static final String TABLE_LEVELS = "levels";
    public static final String TABLE_TRAININGS = "trainings";

    // שמות העמודות בטבלת אתגרים
    public static final String COLUMN_CHALLENGE_ID = "challenge_id";
    public static final String COLUMN_CHALLENGE_NAME = "challenge_name";

    // שמות העמודות בטבלת שלבים
    public static final String COLUMN_LEVEL_ID = "level_id";
    public static final String COLUMN_LEVEL_NAME = "level_name";
    public static final String COLUMN_LEVEL_CHALLENGE_ID = "level_challenge_id";

    // שמות העמודות בטבלת תרגילים
    public static final String COLUMN_TRAINING_ID = "training_id";
    public static final String COLUMN_TRAINING_DESCRIPTION = "description";
    public static final String COLUMN_TRAINING_IMAGE = "image";
    public static final String COLUMN_TRAINING_LEVEL_ID = "training_level_id";

    // מערך מאורגן של כל האתגרים
    private static final String[][] ALL_CHALLENGES = {
        // Muscle Up (ID: 1)
        {"Muscle Up", "1"},
        
        // Pull Up (ID: 2)
        {"Pull Up", "2"},
        
        // Push Up (ID: 3)
        {"Push Up", "3"},
        
        // Dip (ID: 4)
        {"Dip", "4"}
    };

    // יצירת טבלת אתגרים
    private static final String CREATE_CHALLENGES_TABLE = "CREATE TABLE " + TABLE_CHALLENGES + "("
            + COLUMN_CHALLENGE_ID + " INTEGER PRIMARY KEY,"
            + COLUMN_CHALLENGE_NAME + " TEXT NOT NULL"
            + ")";

    // יצירת טבלת שלבים
    private static final String CREATE_LEVELS_TABLE = "CREATE TABLE " + TABLE_LEVELS + "("
            + COLUMN_LEVEL_ID + " INTEGER PRIMARY KEY,"
            + COLUMN_LEVEL_NAME + " TEXT,"
            + COLUMN_LEVEL_CHALLENGE_ID + " INTEGER,"
            + "FOREIGN KEY(" + COLUMN_LEVEL_CHALLENGE_ID + ") REFERENCES " + TABLE_CHALLENGES + "(" + COLUMN_CHALLENGE_ID + ")"
            + ")";

    // יצירת טבלת תרגילים
    private static final String CREATE_TRAININGS_TABLE = "CREATE TABLE " + TABLE_TRAININGS + "("
            + COLUMN_TRAINING_ID + " INTEGER PRIMARY KEY,"
            + COLUMN_TRAINING_DESCRIPTION + " TEXT,"
            + COLUMN_TRAINING_IMAGE + " TEXT,"
            + COLUMN_TRAINING_LEVEL_ID + " INTEGER,"
            + "FOREIGN KEY(" + COLUMN_TRAINING_LEVEL_ID + ") REFERENCES " + TABLE_LEVELS + "(" + COLUMN_LEVEL_ID + ")"
            + ")";

    // מערך מאורגן של כל התרגילים
    private static final String[][][] ALL_TRAININGS = {
        // Muscle Up (muscle_upp_X_Y)
        {
            // Level 1
            {"3 Sets X15", "muscle_upp_1_1", "1"},
            {"3 Sets X13", "muscle_upp_1_2", "1"},
            {"3 Sets X5", "muscle_upp_1_3", "1"},
            {"3 Sets X12", "muscle_upp_1_4", "1"},
            
            // Level 2
            {"3 Sets X4", "muscle_upp_2_1", "2"},
            {"3 Sets X7", "muscle_upp_2_2", "2"},
            {"3 Sets X3", "muscle_upp_2_3", "2"},
            {"3 Sets X15 ", "muscle_upp_2_4", "2"},
            
            // Level 3
            {"3 Sets X5", "muscle_upp_3_1", "3"},
            {"3 Sets X3", "muscle_upp_3_2", "3"},
            {"3 Sets X5", "muscle_upp_3_3", "3"},
            {"3 Sets X7", "muscle_upp_3_4", "3"},
            
            // Level 4
            {"3 Sets X2", "muscle_upp_4_1", "4"},
            {"3 Sets X7", "muscle_upp_4_2", "4"},
            {"3 Sets X5", "muscle_upp_4_3", "4"},
            {"3 Sets X8", "muscle_upp_4_4", "4"},
            
            // Level 5
            {"3 Sets X1", "muscle_upp_5_1", "5"},
            {"3 Sets X4", "muscle_upp_5_2", "5"},
            {"3 Sets X8", "muscle_upp_5_3", "5"},
            {"3 Sets X10", "muscle_upp_5_4", "5"}
        },
        
        // Pull Up (pull_upp_X_Y)
        {
            // Level 1
            {"3 Sets X3", "pull_upp_1_1", "6"},
            {"3 Sets X6", "pull_upp_1_2", "6"},
            {"3 Sets X8", "pull_upp_1_3", "6"},
            {"3 Sets X8", "pull_upp_1_4", "6"},
            
            // Level 2
            {"3 Sets X5", "pull_upp_2_1", "7"},
            {"3 Sets X5", "pull_upp_2_2", "7"},
            {"3 Sets X8", "pull_upp_2_3", "7"},
            {"3 Sets X10", "pull_upp_2_4", "7"},
            
            // Level 3
            {"3 Sets 5s", "pull_upp_3_1", "8"},
            {"3 Sets 10s", "pull_upp_3_2", "8"},
            {"3 Sets X6", "pull_upp_3_3", "8"},
            {"3 Sets X15", "pull_upp_3_4", "8"},
            
            // Level 4
            {"3 Sets 5s", "pull_upp_4_1", "9"},
            {"3 Sets 10s", "pull_upp_4_2", "9"},
            {"3 Sets X6", "pull_upp_4_3", "9"},
            {"3 Sets X6", "pull_upp_4_4", "9"},
            
            // Level 5
            {"3 Sets 3s", "pull_upp_5_1", "10"},
            {"3 Sets 10s", "pull_upp_5_2", "10"},
            {"3 Sets X6", "pull_upp_5_3", "10"},
            {"3 Sets X10", "pull_upp_5_4", "10"}
        },
        
        // Push Up (push_upp_X_Y)
        {
            // Level 1
            {"3 Sets 6s", "push_upp_1_1", "11"},
            {"3 Sets X5", "push_upp_1_2", "11"},
            {"3 Sets X6", "push_upp_1_3", "11"},
            {"3 Sets X10", "push_upp_1_4", "11"},
            
            // Level 2
            {"3 Sets 8s", "push_upp_2_1", "12"},
            {"3 Sets 20s", "push_upp_2_2", "12"},
            {"3 Sets X6", "push_upp_2_3", "12"},
            {"3 Sets X10", "push_upp_2_4", "12"},
            
            // Level 3
            {"3 Sets 8s", "push_upp_3_1", "13"},
            {"3 Sets 10s", "push_upp_3_2", "13"},
            {"3 Sets X3", "push_upp_3_3", "13"},
            {"3 Sets X10", "push_upp_3_4", "13"},
            
            // Level 4
            {"3 Sets 8s", "push_upp_4_1", "14"},
            {"3 Sets 15s", "push_upp_4_2", "14"},
            {"3 Sets X5", "push_upp_4_3", "14"},
            {"3 Sets X10", "push_upp_4_4", "14"},
            
            // Level 5
            {"3 Sets 3s", "push_upp_5_1", "15"},
            {"3 Sets 10s", "push_upp_5_2", "15"},
            {"3 Sets X5", "push_upp_5_3", "15"},
            {"1 Set X8", "push_upp_5_4", "15"}
        },
        
        // Dip (dipp_X_Y)
        {
            // Level 1
            {"3 Sets X5", "dipp_1_1", "16"},
            {"3 Sets 5s", "dipp_1_2", "16"},
            {"3 Sets X8", "dipp_1_3", "16"},
            {"3 Sets X10", "dipp_1_4", "16"},
            
            // Level 2
            {"3 Sets 10s", "dipp_2_1", "17"},
            {"3 Sets X5", "dipp_2_2", "17"},
            {"3 Sets 10s", "dipp_2_3", "17"},
            {"3 Sets X10", "dipp_2_4", "17"},
            
            // Level 3
            {"3 Sets 6s", "dipp_3_1", "18"},
            {"3 Sets 20s", "dipp_3_2", "18"},
            {"3 Sets X8", "dipp_3_3", "18"},
            {"3 Sets X12", "dipp_3_4", "18"},
            
            // Level 4
            {"3 Sets 6s", "dipp_4_1", "19"},
            {"3 Sets X5", "dipp_4_2", "19"},
            {"3 Sets X8", "dipp_4_3", "19"},
            {"3 Sets X15", "dipp_4_4", "19"},
            
            // Level 5
            {"3 Sets 2s", "dipp_5_1", "20"},
            {"3 Sets 10s", "dipp_5_2", "20"},
            {"3 Sets X5", "dipp_5_3", "20"},
            {"3 Sets 10s", "dipp_5_4", "20"}
        }
    };

    // מערך מאורגן של כל השלבים
    private static final String[][] ALL_LEVELS = {
        // Muscle Up Levels
        {"שלב 1 - מתחילים", "1", "1"},  // level_id: 1, challenge_id: 1
        {"שלב 2 - ביניים", "1", "2"},   // level_id: 2, challenge_id: 1
        {"שלב 3 - מתקדמים", "1", "3"},  // level_id: 3, challenge_id: 1
        {"שלב 4 - מומחים", "1", "4"},   // level_id: 4, challenge_id: 1
        {"שלב 5 - מאסטר", "1", "5"},    // level_id: 5, challenge_id: 1
        
        // Pull Up Levels
        {"שלב 1 - מתחילים", "2", "6"},  // level_id: 6, challenge_id: 2
        {"שלב 2 - ביניים", "2", "7"},   // level_id: 7, challenge_id: 2
        {"שלב 3 - מתקדמים", "2", "8"},  // level_id: 8, challenge_id: 2
        {"שלב 4 - מומחים", "2", "9"},   // level_id: 9, challenge_id: 2
        {"שלב 5 - מאסטר", "2", "10"},   // level_id: 10, challenge_id: 2
        
        // Push Up Levels
        {"שלב 1 - מתחילים", "3", "11"}, // level_id: 11, challenge_id: 3
        {"שלב 2 - ביניים", "3", "12"},  // level_id: 12, challenge_id: 3
        {"שלב 3 - מתקדמים", "3", "13"}, // level_id: 13, challenge_id: 3
        {"שלב 4 - מומחים", "3", "14"},  // level_id: 14, challenge_id: 3
        {"שלב 5 - מאסטר", "3", "15"},   // level_id: 15, challenge_id: 3
        
        // Dip Levels
        {"שלב 1 - מתחילים", "4", "16"}, // level_id: 16, challenge_id: 4
        {"שלב 2 - ביניים", "4", "17"},  // level_id: 17, challenge_id: 4
        {"שלב 3 - מתקדמים", "4", "18"}, // level_id: 18, challenge_id: 4
        {"שלב 4 - מומחים", "4", "19"},  // level_id: 19, challenge_id: 4
        {"שלב 5 - מאסטר", "4", "20"}    // level_id: 20, challenge_id: 4
    };

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            // מחיקת הטבלאות הקיימות אם הן קיימות
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAININGS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LEVELS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHALLENGES);

            // יצירת הטבלאות מחדש
            db.execSQL(CREATE_CHALLENGES_TABLE);
            db.execSQL(CREATE_LEVELS_TABLE);
            db.execSQL(CREATE_TRAININGS_TABLE);

            // הוספת כל האתגרים
            db.beginTransaction();
            try {
                for (String[] challenge : ALL_CHALLENGES) {
                    ContentValues values = new ContentValues();
                    values.put(COLUMN_CHALLENGE_ID, Integer.parseInt(challenge[1]));
                    values.put(COLUMN_CHALLENGE_NAME, challenge[0]);
                    long result = db.insert(TABLE_CHALLENGES, null, values);
                    if (result == -1) {
                        throw new SQLiteException("Failed to insert challenge: " + challenge[0]);
                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }

            // הוספת כל השלבים
            db.beginTransaction();
            try {
                for (String[] level : ALL_LEVELS) {
                    ContentValues values = new ContentValues();
                    values.put(COLUMN_LEVEL_ID, Integer.parseInt(level[2]));
                    values.put(COLUMN_LEVEL_NAME, level[0]);
                    values.put(COLUMN_LEVEL_CHALLENGE_ID, Integer.parseInt(level[1]));
                    long result = db.insert(TABLE_LEVELS, null, values);
                    if (result == -1) {
                        throw new SQLiteException("Failed to insert level: " + level[0]);
                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }

            // הוספת כל התרגילים
            db.beginTransaction();
            try {
                for (String[][] challengeTrainings : ALL_TRAININGS) {
                    for (String[] training : challengeTrainings) {
                        ContentValues values = new ContentValues();
                        values.put(COLUMN_TRAINING_DESCRIPTION, training[0]);
                        values.put(COLUMN_TRAINING_IMAGE, training[1]);
                        values.put(COLUMN_TRAINING_LEVEL_ID, Integer.parseInt(training[2]));
                        long result = db.insert(TABLE_TRAININGS, null, values);
                        if (result == -1) {
                            throw new SQLiteException("Failed to insert training: " + training[0]);
                        }
                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database: " + e.getMessage());
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // מחיקת הטבלאות הקיימות
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAININGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LEVELS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHALLENGES);
        
        // יצירה מחדש
        onCreate(db);
    }

    // מתודות חדשות להכנסת מידע בצורה נוחה

    // הכנסת אתגר חדש
    public void addChallenge(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CHALLENGE_NAME, name);
        db.insert(TABLE_CHALLENGES, null, values);
    }

    // הכנסת שלב חדש
    public void addLevel(String name, long challengeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LEVEL_NAME, name);
        values.put(COLUMN_LEVEL_CHALLENGE_ID, challengeId);
        db.insert(TABLE_LEVELS, null, values);
    }

    // הכנסת תרגיל חדש
    public void addTraining(String description, String image, long levelId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TRAINING_DESCRIPTION, description);
        values.put(COLUMN_TRAINING_IMAGE, image);
        values.put(COLUMN_TRAINING_LEVEL_ID, levelId);
        db.insert(TABLE_TRAININGS, null, values);
    }

    // הכנסת מערך של אתגרים
    public void addChallenges(String[] challengeNames) {
        for (String name : challengeNames) {
            addChallenge(name);
        }
    }

    // הכנסת מערך של שלבים
    public void addLevels(String[][] levels) {
        for (String[] level : levels) {
            addLevel(level[0], Long.parseLong(level[1]));
        }
    }

    // הכנסת מערך של תרגילים
    public void addTrainings(String[][] trainings) {
        for (String[] training : trainings) {
            addTraining(training[0], training[1], Long.parseLong(training[2]));
        }
    }

    // פונקציה להוספת כל האתגרים
    public void addAllChallenges() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            for (String[] challenge : ALL_CHALLENGES) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_CHALLENGE_ID, Integer.parseInt(challenge[1]));
                values.put(COLUMN_CHALLENGE_NAME, challenge[0]);
                long result = db.insert(TABLE_CHALLENGES, null, values);
                if (result == -1) {
                    throw new SQLiteException("Failed to insert challenge: " + challenge[0]);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    // פונקציה להוספת כל השלבים
    public void addAllLevels() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // מחיקת כל הנתונים הקיימים
            db.delete(TABLE_LEVELS, null, null);
            
            for (String[] level : ALL_LEVELS) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_LEVEL_ID, Integer.parseInt(level[2]));
                values.put(COLUMN_LEVEL_NAME, level[0]);
                values.put(COLUMN_LEVEL_CHALLENGE_ID, Integer.parseInt(level[1]));
                long result = db.insert(TABLE_LEVELS, null, values);
                if (result == -1) {
                    throw new SQLiteException("Failed to insert level: " + level[0]);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to add levels: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    // פונקציה להוספת כל התרגילים
    public void addAllTrainings() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // מחיקת כל הנתונים הקיימים
            db.delete(TABLE_TRAININGS, null, null);
            
            for (String[][] challengeTrainings : ALL_TRAININGS) {
                for (String[] training : challengeTrainings) {
                    ContentValues values = new ContentValues();
                    values.put(COLUMN_TRAINING_DESCRIPTION, training[0]);
                    values.put(COLUMN_TRAINING_IMAGE, training[1]);
                    values.put(COLUMN_TRAINING_LEVEL_ID, Integer.parseInt(training[2]));
                    long result = db.insert(TABLE_TRAININGS, null, values);
                    if (result == -1) {
                        throw new SQLiteException("Failed to insert training: " + training[0]);
                    }
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to add trainings: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    // קבלת כל האתגרים
    public Cursor getAllChallenges() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_CHALLENGE_ID, COLUMN_CHALLENGE_NAME};
        return db.query(TABLE_CHALLENGES, columns, null, null, null, null, COLUMN_CHALLENGE_ID + " ASC");
    }

    // קבלת כל השלבים של אתגר מסוים
    public Cursor getLevelsForChallenge(long challengeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_LEVELS, null, COLUMN_LEVEL_CHALLENGE_ID + "=?",
                new String[]{String.valueOf(challengeId)}, null, null, null);
    }

    // קבלת כל התרגילים של שלב מסוים
    public Cursor getTrainingsForLevel(long levelId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_TRAININGS, null, COLUMN_TRAINING_LEVEL_ID + "=?",
                new String[]{String.valueOf(levelId)}, null, null, null);
    }

    // פונקציה לבדיקת תקינות מסד הנתונים
    public void validateDatabase() {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            if (db != null && db.isOpen()) {
                // בדיקת טבלת אתגרים
                Cursor challengesCursor = db.query(TABLE_CHALLENGES, null, null, null, null, null, null);
                if (challengesCursor != null) {
                    android.util.Log.d("DatabaseHelper", "Found " + challengesCursor.getCount() + " challenges");
                    challengesCursor.close();
                }

                // בדיקת טבלת שלבים
                Cursor levelsCursor = db.query(TABLE_LEVELS, null, null, null, null, null, null);
                if (levelsCursor != null) {
                    android.util.Log.d("DatabaseHelper", "Found " + levelsCursor.getCount() + " levels");
                    levelsCursor.close();
                }

                // בדיקת טבלת תרגילים
                Cursor trainingsCursor = db.query(TABLE_TRAININGS, null, null, null, null, null, null);
                if (trainingsCursor != null) {
                    android.util.Log.d("DatabaseHelper", "Found " + trainingsCursor.getCount() + " trainings");
                    trainingsCursor.close();
                }

                db.close();
            }
        } catch (Exception e) {
            android.util.Log.e("DatabaseHelper", "Error validating database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}