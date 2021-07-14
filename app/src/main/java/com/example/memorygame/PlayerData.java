package com.example.memorygame;

public class PlayerData{
    private int second;
    private double accuracy;

    public PlayerData(int second, double accuracy) {
        this.second = second;
        this.accuracy = accuracy;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }
}
