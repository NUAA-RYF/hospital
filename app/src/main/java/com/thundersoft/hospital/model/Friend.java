package com.thundersoft.hospital.model;

public class Friend {
    private int id;
    private String username;
    private String name;
    private String phone;
    private String relation;
    private boolean close;

    public Friend(String username, String name, String phone, String relation) {
        this.username = username;
        this.name = name;
        this.phone = phone;
        this.relation = relation;
        this.close = false;
    }

    public Friend(int id, String username, String name, String phone, String relation, boolean close) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.phone = phone;
        this.relation = relation;
        this.close = close;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public boolean isClose() {
        return close;
    }

    public void setClose(boolean close) {
        this.close = close;
    }
}
