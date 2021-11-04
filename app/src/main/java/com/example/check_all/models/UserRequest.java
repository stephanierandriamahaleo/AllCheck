package com.example.check_all.models;

import com.example.check_all.constant.Data;

public class UserRequest {
    String login;
    String password;
    String role;


    public UserRequest(String login, String password, String role) {
        this.login = login;
        this.password = password;
        if (role.compareTo(Data.NAME_ACTION_BTN[6]) == 0 || role.compareTo(Data.NAME_ACTION_BTN[7]) == 0) {
            this.role = "ROLE_USER_SHOP";
        } else {
            this.role = "ROLE_CHECKER";
        }

    }

    public String getEmail() {
        return login;
    }

    public void setEmail(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
