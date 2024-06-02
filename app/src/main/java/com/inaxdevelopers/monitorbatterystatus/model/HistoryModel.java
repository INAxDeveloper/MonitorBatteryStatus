package com.inaxdevelopers.monitorbatterystatus.model;


public class HistoryModel {
    String health;
    int historyId;
    String level;
    String plugged;
    String startEndDate;
    String startEndTime;
    String status;
    float voltage;

    public HistoryModel(int i, String str, String str2, String str3, String str4, String str5, String str6, float f) {
        this.historyId = i;
        this.startEndDate = str;
        this.startEndTime = str2;
        this.plugged = str3;
        this.health = str4;
        this.status = str5;
        this.level = str6;
        this.voltage = f;
    }

    public int getHistoryId() {
        return this.historyId;
    }

    public void setHistoryId(int i) {
        this.historyId = i;
    }

    public String getStartEndDate() {
        return this.startEndDate;
    }

    public void setStartEndDate(String str) {
        this.startEndDate = str;
    }

    public String getStartEndTime() {
        return this.startEndTime;
    }

    public void setStartEndTime(String str) {
        this.startEndTime = str;
    }

    public String getPlugged() {
        return this.plugged;
    }

    public void setPlugged(String str) {
        this.plugged = str;
    }

    public String getHealth() {
        return this.health;
    }

    public void setHealth(String str) {
        this.health = str;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String str) {
        this.status = str;
    }

    public String getLevel() {
        return this.level;
    }

    public void setLevel(String str) {
        this.level = str;
    }

    public float getVoltage() {
        return this.voltage;
    }

    public void setVoltage(float f) {
        this.voltage = f;
    }
}
