package com.example.truekeycar;

public class ClassUser {
    private String NumberPhone,Password,token;

    public ClassUser(String numberPhone, String password, String token) {
        NumberPhone = numberPhone;
        Password = password;
        this.token = token;
    }

    public ClassUser() {

    }

    public String getNumberPhone() {
        return NumberPhone;
    }

    public void setNumberPhone(String numberPhone) {
        NumberPhone = numberPhone;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
