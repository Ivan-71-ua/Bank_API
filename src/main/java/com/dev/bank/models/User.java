package com.dev.bank.models;

import java.time.LocalDate;

public class User {
    private String username;
    private String password;
    private String email;
    private LocalDate birthday;
    private String phoneNumber;

    public User() {}

    public User(String username, String password, String email, LocalDate birthday, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public String getEmail() {
        return email;
    }
    public LocalDate getBirthday() {
        return birthday;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}