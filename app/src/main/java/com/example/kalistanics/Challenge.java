package com.example.kalistanics;

public class Challenge {
    private String   title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private Level[] levels;

    public Challenge(String title, Level[] levels) {
        this.title = title;
        this.levels = levels;
    }

    public Level[] getLevels() {
        return levels;
    }

    public void setLevels(Level[] levels) {
        this.levels = levels;
    }
}
