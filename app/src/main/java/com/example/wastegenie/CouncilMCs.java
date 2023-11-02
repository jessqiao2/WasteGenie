package com.example.wastegenie;

public class CouncilMCs {
    String councilName;
    Integer monthlyContams;

    public CouncilMCs(String councilName, Integer monthlyContams){
        this.councilName = councilName;
        this.monthlyContams = monthlyContams;
    }

    public String getCouncilName() {
        return councilName;
    }

    public void setCouncilName(String councilName) {
        this.councilName = councilName;
    }

    public Integer getMonthlyContams() {
        return monthlyContams;
    }

    public void setMonthlyContams(Integer monthlyContams) {
        this.monthlyContams = monthlyContams;
    }
}
