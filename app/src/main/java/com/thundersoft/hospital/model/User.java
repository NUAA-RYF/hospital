package com.thundersoft.hospital.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;

public class User extends DataSupport implements Parcelable {

    private int id;
    private String userName;
    private String userPassword;
    private String phone;

    public User() {
        super();
    }

    public User(int id, String userName, String userPassword, String phone) {
        this.id = id;
        this.userName = userName;
        this.userPassword = userPassword;
        this.phone = phone;
    }

    protected User(Parcel in) {
        id = in.readInt();
        userName = in.readString();
        userPassword = in.readString();
        phone = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(userName);
        parcel.writeString(userPassword);
        parcel.writeString(phone);
    }
}
