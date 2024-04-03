package com.snhu.cs_360.weighttracker_brycejensen;

public class WeightModel {
    String weight_Weight;
    String weight_Date;
    String weight_PlusMinus;

    public WeightModel(String weight_Weight, String weight_Date, String weight_PlusMinus) {
        this.weight_Weight = weight_Weight;
        this.weight_Date = weight_Date;
        this.weight_PlusMinus = weight_PlusMinus;
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
}
