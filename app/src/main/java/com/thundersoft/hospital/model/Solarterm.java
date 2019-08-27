package com.thundersoft.hospital.model;

import org.litepal.crud.DataSupport;

public class Solarterm extends DataSupport {

    private int id;
    private String JieqiId;
    private String JieqiName;
    private String imgUrl;
    private String JieqiDate;


    public Solarterm(String jieqiId, String jieqiName, String imgUrl, String jieqiDate) {
        JieqiId = jieqiId;
        JieqiName = jieqiName;
        this.imgUrl = imgUrl;
        JieqiDate = jieqiDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJieqiId() {
        return JieqiId;
    }

    public void setJieqiId(String jieqiId) {
        JieqiId = jieqiId;
    }

    public String getJieqiName() {
        return JieqiName;
    }

    public void setJieqiName(String jieqiName) {
        JieqiName = jieqiName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getJieqiDate() {
        return JieqiDate;
    }

    public void setJieqiDate(String jieqiDate) {
        JieqiDate = jieqiDate;
    }
}
