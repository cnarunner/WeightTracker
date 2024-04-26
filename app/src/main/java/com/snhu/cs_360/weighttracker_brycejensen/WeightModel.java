package com.snhu.cs_360.weighttracker_brycejensen;

import java.util.ArrayList;

/**
 * The WeightModel class represents a weight entry in the weight tracking application.
 * It contains information about the weight, date, plus/minus value, and the username.
 */
public class WeightModel {
    private int weight_Id;
    private String weight_Weight;
    private String weight_Date;
    private String weight_PlusMinus;
    private String weight_Username;

    /**
     * Constructs a WeightModel object with the given weight, date, plus/minus value, and username.
     *
     * @param weight_Weight   the weight value as a string
     * @param weight_Date     the date of the weight entry
     * @param weight_PlusMinus the plus/minus value compared to the previous weight entry
     * @param weight_Username the username associated with the weight entry
     */
    public WeightModel(String weight_Weight, String weight_Date, String weight_PlusMinus, String weight_Username) {
        this.weight_Weight = weight_Weight;
        this.weight_Date = weight_Date;
        this.weight_PlusMinus = "";
        this.weight_Username = weight_Username;
    }

    /**
     * Returns the weight value.
     *
     * @return the weight value as a string
     */
    public String getWeight_Weight() {
        return weight_Weight;
    }

    /**
     * Returns the date of the weight entry.
     *
     * @return the date as a string
     */
    public String getWeight_Date() {
        return weight_Date;
    }

    /**
     * Returns the plus/minus value compared to the previous weight entry.
     *
     * @return the plus/minus value as a string
     */
    public String getWeight_PlusMinus() {
        return weight_PlusMinus;
    }

    /**
     * Returns the weight entry ID.
     *
     * @return the weight entry ID
     */
    public int getWeight_Id() {
        return weight_Id;
    }

    /**
     * Returns the username associated with the weight entry.
     *
     * @return the username as a string
     */
    public String getWeight_Username() {
        return weight_Username;
    }

    /**
     * Sets the weight entry ID.
     *
     * @param weight_Id the weight entry ID
     */
    public void setWeight_Id(int weight_Id) {
        this.weight_Id = weight_Id;
    }

    /**
     * Sets the weight value.
     *
     * @param weight_Weight the weight value as a double
     */
    public void setWeight_Weight(double weight_Weight) {
        this.weight_Weight = String.valueOf(weight_Weight);
    }

    /**
     * Sets the date of the weight entry.
     *
     * @param weight_Date the date as a string
     */
    public void setWeight_Date(String weight_Date) {
        this.weight_Date = weight_Date;
    }

    /**
     * Sets the plus/minus value compared to the previous weight entry.
     *
     * @param weight_PlusMinus the plus/minus value as a string
     */
    public void setWeight_PlusMinus(String weight_PlusMinus) {
        this.weight_PlusMinus = weight_PlusMinus;
    }

    /**
     * Sets the username associated with the weight entry.
     *
     * @param weight_Username the username as a string
     */
    public void setWeight_Username(String weight_Username) {
        this.weight_Username = weight_Username;
    }

    /**
     * Calculates the plus/minus value compared to the previous weight entry in the given list
     * of weight models.
     *
     * @param weightModels the list of weight models
     * @param currentIndex the index of the current weight model in the list
     */
    public void calculatePlusMinus(ArrayList<WeightModel> weightModels, int currentIndex) {
        if (currentIndex > 0) {
            // Get the previous weight model from the list
            WeightModel previousWeight = weightModels.get(currentIndex - 1);
            
            // Convert the current and previous weight values to doubles
            double currentWeightValue = Double.parseDouble(this.weight_Weight);
            double previousWeightValue = Double.parseDouble(previousWeight.weight_Weight);
            // Calculate the difference between the current and previous weight values
            double difference = currentWeightValue - previousWeightValue;
            
            // Set the plus/minus value as a formatted string with one decimal place
            this.weight_PlusMinus = String.format("%.1f", difference);
        } else {
            // If this is the first weight entry, set the plus/minus value to "+0.0"
            this.weight_PlusMinus = "+0.0";
        }
    }

}

