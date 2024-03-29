package com.thundersoft.hospital.util;

public class HttpUrl {
    public static final String HOSPITAL = "http://47.100.187.5:8080/hospital";
    /**
     * 客户端用户操作
     */
    public static final String CLIENT_SIGN_UP = "/client/clientUserSignUp";
    public static final String CLIENT_ACCOUNT_LOGIN = "/client/clientUserLogin";
    public static final String PHONE_LOGIN = "";
    /**
     * 疾病信息操作
     */
    public static final String DISEASE_INSERT = "/disease/addInfo";
    public static final String DISEASE_UPDATE = "/disease/updateInfo";
    public static final String DISEASE_QUERY_USERNAME = "/disease/findInfoByUserName";
    public static final String DISEASE_QUERY_ID = "/disease/findInfoById";
    public static final String DISEASE_DELETE = "/disease/deleteInfo";

    /**
     * 好友信息操作
     */
    public static final String FRIEND_QUERY_USERNAME = "/friend/findFriendList";
    public static final String FRIEND_QUERY_ID = "/friend/findFriend";
    public static final String FRIEND_INSERT = "/friend/insertFriend";
    public static final String FRIEND_UPDATE = "/friend/updateFriend";
    public static final String FRIEND_UPDATE_CLOSE = "/friend/updateFriendClose";
    public static final String FRIEND_DELETE = "/friend/deleteFriend";

    /**
     * 急救信息操作
     */
    public static final String FIRST_AID_QUERY_USERNAME = "/firstAid/findFirstAidList";
    public static final String FIRST_AID_INSERT = "/firstAid/insertFirstAid";
    public static final String FIRST_AID_DELETE = "/firstAid/deleteFirstAid";

}
