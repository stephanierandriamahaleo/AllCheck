package com.example.check_all.models;

import com.example.check_all.constant.Data;

public class ChoiceChecker {
    private final String[] tools = {"Camera","Netum","Zebra"};
    private final String[] action = Data.NAME_ACTION_BTN;
    private final Class[] activities = Data.ACTIVITY_LIST;

    private int choiceToolsIndex;
    private int choiceActionIndex;

    private String email;
    private String password;

    public String getChoiceToolsName(){
        return this.tools[this.getChoiceToolsIndex()];
    }

    public String getChoiceActionName(){
        return this.action[this.getChoiceActionIndex()];
    }

    public Class getClassActivity(){
        return this.activities[this.getChoiceToolsIndex()];
    }

    public int getChoiceToolsIndex() {
        return choiceToolsIndex;
    }

    public void setChoiceToolsIndex(int choiceToolsIndex) {
        this.choiceToolsIndex = choiceToolsIndex;
    }

    public int getChoiceActionIndex() {
        return choiceActionIndex;
    }

    public void setChoiceActionIndex(int choiceActionIndex) {
        this.choiceActionIndex = choiceActionIndex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "ChoiceChecker{" +
                "tools=" + this.getChoiceToolsName()+
                ", action=" + this.getChoiceActionName() +
                ", choiceToolsIndex=" + choiceToolsIndex +
                ", choiceActionIndex=" + choiceActionIndex +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
