package com.hotel.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "exercise_log")
public class ExerciseLog {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "date")
    private String date;

    @Column(name = "time")
    private String time;

    @Column(name = "duration")
    private int duration; // Duration in minutes

    @Column(name = "exerciseType")
    private String exerciseType;

    @Column(name = "intensity")
    private String intensity;

    @Column(name = "caloriesBurned")
    private int caloriesBurned;

    @Column(name = "BMR")
    private int BMR;

    public ExerciseLog() {
        // Default constructor
    }

    // Constructor with parameters
    public ExerciseLog(String email, String date, String time, int duration, String exerciseType, String intensity) {
        this.email = email;
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.exerciseType = exerciseType;
        this.intensity = intensity;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }

    public String getIntensity() {
        return intensity;
    }

    public void setIntensity(String intensity) {
        this.intensity = intensity;
    }

    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(int caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public int getBMR() {
        return BMR;
    }

    public void setBMR(int BMR) {
        this.BMR = BMR;
    }

    // toString method
    @Override
    public String toString() {
        return "ExerciseLog{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", duration=" + duration +
                ", exerciseType='" + exerciseType + '\'' +
                ", intensity='" + intensity + '\'' +
                ", caloriesBurned=" + caloriesBurned +
                ", BMR=" + BMR +
                '}';
    }
}