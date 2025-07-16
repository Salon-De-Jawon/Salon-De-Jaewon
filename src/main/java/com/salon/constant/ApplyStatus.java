package com.salon.constant;

public enum ApplyStatus {
    WAITING(""), INPROGRESS(""), APPROVED(""), REJECTED("");

    private final String label;

    ApplyStatus(String label){
        this.label = label;
    }

    public String getLabel(){
        return this.label;
    }
}
