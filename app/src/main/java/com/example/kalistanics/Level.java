package com.example.kalistanics;



public class Level {
    private String name;
     private Training[] Trainings;
     private boolean isComplete;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Training[] getTrainings() {
        return Trainings;
    }

    public void setTrainings(Training[] trainings) {
        Trainings = trainings;
    }

    public Level(String name, Training[] trainings) {
        this.name = name;
        Trainings = trainings;
        this.isComplete = false;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }


}
