package com.thundersoft.hospital.model;

import org.litepal.crud.DataSupport;

public class Constellation extends DataSupport {

    private int id;
    private String astroId;
    private String astroName;
    private String astroDate;
    private String imgUrl;


    public Constellation(String astroId, String astroName, String astroDate, String imgUrl) {
        this.astroId = astroId;
        this.astroName = astroName;
        this.astroDate = astroDate;
        this.imgUrl = imgUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAstroDate() {
        return astroDate;
    }

    public void setAstroDate(String astroDate) {
        this.astroDate = astroDate;
    }

    public String getAstroId() {
        return astroId;
    }

    public void setAstroId(String astroId) {
        this.astroId = astroId;
    }

    public String getAstroName() {
        return astroName;
    }

    public void setAstroName(String astroName) {
        this.astroName = astroName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
