package com.example.truekeycar;

public class ClassCars {
    String Name, Code;

    public ClassCars() {
    }

    public ClassCars(String name, String code) {
        Name = name;
        Code = code;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }
}
