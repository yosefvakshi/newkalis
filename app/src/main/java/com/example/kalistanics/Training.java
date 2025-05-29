package com.example.kalistanics;

public class Training
{
    private int image;
    private String explain;

    public Training(int image, String explain) {
        this.image = image;
        this.explain = explain;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }
}
