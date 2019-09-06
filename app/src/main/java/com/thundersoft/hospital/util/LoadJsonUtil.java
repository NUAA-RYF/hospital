package com.thundersoft.hospital.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.thundersoft.hospital.model.BMI;
import com.thundersoft.hospital.model.Constellation;
import com.thundersoft.hospital.model.Disease;
import com.thundersoft.hospital.model.GenderMonth;
import com.thundersoft.hospital.model.JieQi;
import com.thundersoft.hospital.model.MonthGender;
import com.thundersoft.hospital.model.Solarterm;
import com.thundersoft.hospital.model.gson.Fortune.Fortune;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadJsonUtil {

    /**
     * 从JSON格式中解析二十四节气
     * @param response JSON格式字符串
     * @return 成功返回true
     */
    public static boolean SolarTerm(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject msgAll = new JSONObject(response);
                if (msgAll.getString("msg").equals("ok")) {
                    JSONArray SolarTermList = msgAll.getJSONObject("result").getJSONArray("list");
                    for (int i = 0; i < SolarTermList.length(); i++) {
                        JSONObject object = SolarTermList.getJSONObject(i);
                        String jieqiId = object.getString("jieqiid");
                        String name = object.getString("name");
                        String imgUrl = object.getString("pic");
                        String date = object.getString("time");

                        Solarterm solarterm = new Solarterm(jieqiId, name, imgUrl, date);
                        solarterm.save();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }


    /**
     * 从JSON格式中解析节气具体信息
     * @param response JSON格式字符串
     * @return 成功返回true
     */
    public static boolean JieQi(String response){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONObject msgAll = new JSONObject(response);
                if (msgAll.getString("msg").equals("ok")){
                    JSONObject object = msgAll.getJSONObject("result");
                    String jieqiId = object.getString("jieqiid");
                    String name = object.getString("name");
                    String date = object.getString("date");
                    String jianjie = object.getString("jianjie");
                    String youlai = object.getString("youlai");
                    String xisu = object.getString("xisu");
                    String yangsheng = object.getString("yangsheng");
                    String imgUrl = object.getString("pic");

                    JieQi jieQi = new JieQi(jieqiId,name,date,jianjie,youlai,xisu,yangsheng,imgUrl);
                    jieQi.save();
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    /**
     * 从JSON中解析十二星座
     *
     * @param response JSON格式字符串
     * @return 成功返回true
     */
    public static boolean Constellation(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                //获得所有信息
                JSONObject msgAll = new JSONObject(response);
                if (msgAll.getString("msg").equals("ok")) {
                    //获得JSON数组
                    JSONArray constellationList = msgAll.getJSONArray("result");
                    //获得具体数据
                    for (int i = 0; i < constellationList.length(); i++) {
                        JSONObject object = constellationList.getJSONObject(i);
                        String astroId = object.getString("astroid");
                        String astroName = object.getString("astroname");
                        String astroDate = object.getString("date");
                        String imgUrl = object.getString("pic");

                        Constellation constellation = new Constellation(astroId, astroName, astroDate, imgUrl);
                        constellation.save();
                    }
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    /**
     * 从JSON中解析十二星座运势
     * 使用GSON获得对象
     * @param response JSON格式字符串
     * @return 成功返回Fortune对象
     */
    public static Fortune Fortune(String response){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONObject msgAll = new JSONObject(response);
                String fortuneContent = msgAll.toString();
                if (msgAll.getString("msg").equals("ok")){
                    return new Gson().fromJson(fortuneContent,Fortune.class);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 从JSON中解析BMI
     * 使用GSON获得对象
     * @param response JSON格式字符串
     * @return 成功返回BMI对象
     */
    public static BMI BMI(String response){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONObject msgAll = new JSONObject(response);
                String BMIContent = msgAll.toString();
                if (msgAll.getString("msg").equals("ok")){
                    return new Gson().fromJson(BMIContent,BMI.class);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    /**
     * 按照月份查询性别
     * 使用GSON获得对象
     * @param response JSON格式字符串
     * @return 成功返回MonthGender对象
     */
    public static MonthGender MonthGender(String response){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONObject msgAll = new JSONObject(response);
                if (msgAll.getString("msg").equals("ok")){
                    String monthGenderContent = msgAll.toString();
                    return new Gson().fromJson(monthGenderContent,MonthGender.class);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }


    /**
     * 按照性别查月份
     * 使用GSON获得对象
     * @param response JSON格式字符串
     * @return 成功返回GenderMonth对象
     */
    public static GenderMonth GenderMonth(String response){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONObject msgAll = new JSONObject(response);
                if (msgAll.getString("msg").equals("ok")){
                    String genderMonthContent = msgAll.toString();
                    return new Gson().fromJson(genderMonthContent,GenderMonth.class);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }


    /**
     * 解析登录成功后返回的用户信息
     * @param response 用户信息
     * @return         返回用户
     */
    public static Map<String,String> getUser(String response){
        Map<String,String> ret = new HashMap<>();
        if (!TextUtils.isEmpty(response)){
            try {
                JSONObject msgAll = new JSONObject(response);
                if (msgAll.getString("type").equals("success")){
                    ret.put("type","success");
                    ret.put("id",msgAll.getString("id"));
                    ret.put("username",msgAll.getString("username"));
                    ret.put("password",msgAll.getString("password"));
                    ret.put("phone",msgAll.getString("phone"));
                    return ret;
                }else {
                    ret.put("type",msgAll.getString("type"));
                    ret.put("msg",msgAll.getString("msg"));
                    return ret;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 注册获取ID
     * @param response 返回信息
     * @return 返回ID
     */
    public static Map<String,String> signUpAndGetIDORMsg(String response){
        Map<String,String> ret = new HashMap<>();
        if (!TextUtils.isEmpty(response)){
            try {
                JSONObject msgAll = new JSONObject(response);
                if (msgAll.getString("type").equals("success")){
                    ret.put("type","success");
                    ret.put("id",msgAll.getString("id"));
                    return ret;
                }else {
                    ret.put("type","error");
                    ret.put("msg",msgAll.getString("msg"));
                    return ret;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ret.put("type","error");
        ret.put("msg",null);
        return ret;
    }


    /**
     * 获取疾病信息
     * @param response 疾病信息
     * @return         返回疾病列表
     */
    public static List<Disease> getDisease(String response){
        List<Disease> diseaseList = new ArrayList<>();
        if (!TextUtils.isEmpty(response)){
            try {
                JSONObject msgAll = new JSONObject(response);
                if (msgAll.getString("type").equals("success")){
                    JSONArray result = msgAll.getJSONArray("result");
                    for (int i = 0; i < result.length(); i++) {
                        Disease disease = new Gson().fromJson(result.getString(i),Disease.class);
                        diseaseList.add(disease);
                    }
                    return diseaseList;
                }else {
                    return diseaseList;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return diseaseList;
    }

    /**
     * 删除疾病信息
     * @param response 返回信息
     * @return         是否成功
     */
    public static boolean deleteInfo(String response){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONObject msgAll = new JSONObject(response);
                return msgAll.getString("type").equals("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 按疾病ID获取疾病信息
     * @param response 疾病信息
     * @return         疾病信息
     */
    public static Disease getDiseaseById(String response){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONObject msgAll = new JSONObject(response);
                if (msgAll.getString("type").equals("success")){
                    return new Gson().fromJson(msgAll.getString("result"),Disease.class);
                }else {
                    return null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 检查获取的信息是否成功
     * @param response 返回信息
     * @return         返回成功与否
     */
    public static boolean messageIsSuccess(String response){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONObject msgAll = new JSONObject(response);
                return msgAll.getString("type").equals("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
