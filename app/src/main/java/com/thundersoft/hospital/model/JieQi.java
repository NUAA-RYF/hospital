package com.thundersoft.hospital.model;

import org.litepal.crud.DataSupport;

public class JieQi extends DataSupport {

    private int id;
    private String jieqiId;
    private String name;
    private String date;
    private String jianjie;
    private String youlai;
    private String xisu;
    private String yangsheng;
    private String imgUrl;

    public JieQi(String jieqiId, String name, String date,
                 String jianjie, String youlai, String xisu,
                 String yangsheng, String imgUrl) {
        this.jieqiId = jieqiId;
        this.name = name;
        this.date = date;
        this.jianjie = jianjie;
        this.youlai = youlai;
        this.xisu = xisu;
        this.yangsheng = yangsheng;
        this.imgUrl = imgUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJieqiId() {
        return jieqiId;
    }

    public void setJieqiId(String jieqiId) {
        this.jieqiId = jieqiId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getJianjie() {
        return jianjie;
    }

    public void setJianjie(String jianjie) {
        this.jianjie = jianjie;
    }

    public String getYoulai() {
        return youlai;
    }

    public void setYoulai(String youlai) {
        this.youlai = youlai;
    }

    public String getXisu() {
        return xisu;
    }

    public void setXisu(String xisu) {
        this.xisu = xisu;
    }

    public String getYangsheng() {
        return yangsheng;
    }

    public void setYangsheng(String yangsheng) {
        this.yangsheng = yangsheng;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
