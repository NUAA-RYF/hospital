package com.thundersoft.hospital.util;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.thundersoft.hospital.model.BMI;
import com.thundersoft.hospital.model.Constellation;
import com.thundersoft.hospital.model.GenderMonth;
import com.thundersoft.hospital.model.JieQi;
import com.thundersoft.hospital.model.MonthGender;
import com.thundersoft.hospital.model.Solarterm;
import com.thundersoft.hospital.model.Table;
import com.thundersoft.hospital.model.gson.Fortune.Fortune;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoadJsonUtil {

    private static final String TAG = "LoadJsonUtil";


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

                        Log.i(TAG, "SolarTerm: " + i);
                        Log.i(TAG, "SolarTerm: " + jieqiId);
                        Log.i(TAG, "SolarTerm: " + name);
                        Log.i(TAG, "SolarTerm: " + imgUrl);
                        Log.i(TAG, "SolarTerm: " + date);
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

                    Log.i(TAG, "JieQi: " + jieqiId);
                    Log.i(TAG, "JieQi: " + name);
                    Log.i(TAG, "JieQi: " + date);
                    Log.i(TAG, "JieQi: " + jianjie);
                    Log.i(TAG, "JieQi: " + youlai);
                    Log.i(TAG, "JieQi: " + xisu);
                    Log.i(TAG, "JieQi: " + yangsheng);

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

                        Log.i(TAG, "Constellation: " + i);
                        Log.i(TAG, "Constellation: " + astroId);
                        Log.i(TAG, "Constellation: " + astroName);
                        Log.i(TAG, "Constellation: " + astroDate);
                        Log.i(TAG, "Constellation: " + imgUrl);

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


}
