package com.thundersoft.hospital.model;

import org.litepal.crud.DataSupport;

public class IllnessInfo extends DataSupport {
    private int id;

    private String userId;
    private String userAge;
    private String userName;
    private String userPhone;
    private String userGender;
    private String userAddress;
    private String userIllnessName;
    private String userIllnessInfo;


    public IllnessInfo() {
        super();
    }

    public IllnessInfo(String userId, String userAge, String userName,
                       String userPhone, String userGender, String userAddress,
                       String userIllnessName, String userIllnessInfo) {
        this.userId = userId;
        this.userAge = userAge;
        this.userName = userName;
        this.userPhone = userPhone;
        this.userGender = userGender;
        this.userAddress = userAddress;
        this.userIllnessName = userIllnessName;
        this.userIllnessInfo = userIllnessInfo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserIllnessName() {
        return userIllnessName;
    }

    public String getUserAge() {
        return userAge;
    }

    public void setUserAge(String userAge) {
        this.userAge = userAge;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public void setUserIllnessName(String userIllnessName) {
        this.userIllnessName = userIllnessName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserIllnessInfo() {
        return userIllnessInfo;
    }

    public void setUserIllnessInfo(String userIllnessInfo) {
        this.userIllnessInfo = userIllnessInfo;
    }
}
