package com.thundersoft.hospital.model;

public class Disease {
    private int id;

    private String username;
    private String name;
    private String age;
    private String phone;
    private String gender;
    private String address;
    private String diseaseName;
    private String diseaseInfo;

    public Disease(String username, String name, String age,
                   String phone, String gender, String address,
                   String diseaseName, String diseaseInfo) {
        this.username = username;
        this.name = name;
        this.age = age;
        this.phone = phone;
        this.gender = gender;
        this.address = address;
        this.diseaseName = diseaseName;
        this.diseaseInfo = diseaseInfo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public String getDiseaseInfo() {
        return diseaseInfo;
    }

    public void setDiseaseInfo(String diseaseInfo) {
        this.diseaseInfo = diseaseInfo;
    }
}
