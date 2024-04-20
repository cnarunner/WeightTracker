package com.snhu.cs_360.weighttracker_brycejensen;

import java.util.ArrayList;

public class WeightModel {
    int weight_Id;
    String weight_Weight;
    String weight_Date;
    String weight_PlusMinus;
    String weight_Username;

    public WeightModel(String weight_Weight, String weight_Date, String weight_PlusMinus, String weight_Username) {
        this.weight_Weight = weight_Weight;
        this.weight_Date = weight_Date;
        this.weight_PlusMinus = "";
        this.weight_Username = weight_Username;
    }

    public String getWeight_Weight() {
        return weight_Weight;
    }

    public String getWeight_Date() {
        return weight_Date;
    }

    public String getWeight_PlusMinus() {
        return weight_PlusMinus;
    }

    public int getWeight_Id() {
        return weight_Id;
    }

    public String getWeight_Username() {
        return weight_Username;
    }


    public void setWeight_Id(int weight_Id) {
        this.weight_Id = weight_Id;
    }

    public void setWeight_Weight(double weight_Weight) {
        this.weight_Weight = String.valueOf(weight_Weight);
    }

    public void setWeight_Date(String weight_Date) {
        this.weight_Date = weight_Date;
    }

    public void setWeight_PlusMinus(String weight_PlusMinus) {
        this.weight_PlusMinus = weight_PlusMinus;
    }

    public void setWeight_Username(String weight_Username) {
        this.weight_Username = weight_Username;
    }

    public void calculatePlusMinus(ArrayList<WeightModel> weightModels, int currentIndex) {
        if (currentIndex > 0) {
            WeightModel previousWeight = weightModels.get(currentIndex - 1);
            double currentWeightValue = Double.parseDouble(this.weight_Weight);
            double previousWeightValue = Double.parseDouble(previousWeight.weight_Weight);
            double difference = currentWeightValue - previousWeightValue;
            this.weight_PlusMinus = String.format("%.1f", difference);
        } else {
            this.weight_PlusMinus = "+0.0";
        }

    }

}
