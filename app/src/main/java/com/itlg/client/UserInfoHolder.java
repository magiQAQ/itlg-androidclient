package com.itlg.client;

import com.itlg.client.bean.UserInfo;
import com.itlg.client.utils.SharedPreferencesUtils;

public class UserInfoHolder {
    private static UserInfoHolder instance = new UserInfoHolder();
    private UserInfo userInfo;
    private static final String KEY_ID = "key_userId";
    private static final String KEY_USERNAME = "key_username";
    private static final String KEY_PASSWORD = "key_password";
    private static final String KEY_NAME = "key_name";
    private static final String KEY_EMAIL = "key_email";
    private static final String KEY_CELLPHONE = "key_cellphone";
    private static final String KEY_USER_IMG = "key_userImg";
    private static final String KEY_RECORD_TIME = "key_recordTime";
    private static final String KEY_PRIVILEGE = "key_privilege";

    public static UserInfoHolder getInstance() {
        return instance;
    }

    public UserInfo getUserInfo() {
        if (userInfo == null) {
            SharedPreferencesUtils instance = SharedPreferencesUtils.getInstance();
            int id = (int) instance.get(KEY_ID,0);
            String username = (String) instance.get(KEY_USERNAME,"");
            String password = (String) instance.get(KEY_PASSWORD, "");
            String name = (String) instance.get(KEY_NAME,"");
            String email = (String) instance.get(KEY_EMAIL,"");
            String cellphone = (String) instance.get(KEY_CELLPHONE,"");
            String userImg = (String) instance.get(KEY_USER_IMG, "");
            long recordTime = (long) instance.get(KEY_RECORD_TIME, 0L);
            int privilege = (int) instance.get(KEY_PRIVILEGE, 0);
            userInfo = new UserInfo();
            userInfo.setId(id);
            userInfo.setUsername(username);
            userInfo.setPassword(password);
            userInfo.setName(name);
            userInfo.setEmail(email);
            userInfo.setCellphone(cellphone);
            userInfo.setUserImg(userImg);
            userInfo.setRecordTime(recordTime);
            userInfo.setPrivilege(privilege);
        }
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
        if (userInfo != null) {
            SharedPreferencesUtils instance = SharedPreferencesUtils.getInstance();
            instance.put(KEY_ID, userInfo.getId());
            instance.put(KEY_USERNAME, userInfo.getUsername());
            instance.put(KEY_PASSWORD, userInfo.getPassword());
            instance.put(KEY_NAME, userInfo.getName());
            instance.put(KEY_EMAIL, userInfo.getEmail());
            instance.put(KEY_CELLPHONE, userInfo.getCellphone());
            instance.put(KEY_USER_IMG, userInfo.getUserImg());
            instance.put(KEY_RECORD_TIME, userInfo.getRecordTime());
            instance.put(KEY_PRIVILEGE, userInfo.getPrivilege());
        }
    }

    public void clearUser() {
        if (userInfo != null) {
            userInfo = null;
        }
        //先把sp中的用户信息全部清除掉
        SharedPreferencesUtils.getInstance().clear();
    }
}
