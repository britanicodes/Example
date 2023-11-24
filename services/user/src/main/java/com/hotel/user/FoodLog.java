package com.hotel.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table; 

@Entity
@Table(name = "foodLogs")
    public class FoodLog {
        
        @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
        
        @Column(name = "foodName")
        private String foodName;

        @Column(name = "email")
        private String email;

        @Column(name = "date")
        private String date;    

        @Column(name = "servings")
        private int servings;

        @Column(name = "calories")
        private int calories;

        @Column(name = "protein")
        private int protein;

        @Column(name = "carbs")
        private int carbs;

        @Column(name = "fat")
        private int fat;

        @Column(name = "mealType")
        private String mealType; 
        

    public FoodLog() {
        // Default constructor
    }

    public FoodLog(long foodLogID, String email, String date, String foodName, int servings) {
        // this.foodLogID = foodLogID;
        this.email = email;
        this.date = date;
        // this.foodName = foodName;
        this.servings = servings;
    }

    // Setters
    // public void setFoodLogID(long foodLogID){
    //     this.foodLogID = foodLogID;
    // }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public void setEmail(String email){
        this.email = email;
    }
    
    public void setDate(String date){
        this.date = date;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    // public void setFoodName(String foodName){
    //     this.foodName = foodName;
    // }

    public void setServings(int servings){
        this.servings = servings;
    }

    public void setCalories(int calories){
        this.calories = calories;
    }

    public void setProtein(int protein){
        this.protein = protein;
    }

    public void setCarbs(int carbs){
        this.carbs = carbs;
    }

    public void setFat(int fat){
        this.fat = fat;
    }


    // Getters
    // public long getFoodLogID(){
    //     return foodLogID;
    // }

    public String getFoodName() {
        return this.foodName;
    }    
    
    public String getMealType() {
        return this.mealType;
    }

    public String getEmail(){
        return email;
    }

    public String getDate(){
        return date;
    }

    // public String getFoodName(){
    //     return foodName;
    // }

    public int getServings(){
        return servings;
    }

    public int getCalories(){
        return calories;
    }

    public int getProtein(){
        return protein;
    }

    public int getCarbs(){
        return carbs;
    }

    public int getFat(){
        return fat;
    }

    @Override
    public String toString() {
        return "FoodLog{" +
                // "foodLogID=" + foodLogID +
                "email='" + email + '\'' +
                ", date='" + date + '\'' +
                // ", foodName='" + foodName + '\'' +
                ", servings=" + servings +
                ", calories=" + calories +
                ", protein=" + protein +
                ", carbs=" + carbs +
                ", fat=" + fat +
                '}';
    }
}
