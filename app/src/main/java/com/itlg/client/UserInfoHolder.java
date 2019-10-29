package com.itlg.client;

import com.itlg.client.bean.User;
import com.itlg.client.utils.SharedPreferencesUtils;

public class UserInfoHolder {
    private static UserInfoHolder instance = new UserInfoHolder();
    private User user;
    private static final String KEY_ID = "key_userId";
    private static final String KEY_USERNAME = "key_username";
    private static final String KEY_PASSWORD = "key_password";
    private static final String KEY_NAME = "key_name";
    private static final String KEY_EMAIL = "key_email";
    private static final String KEY_CELLPHONE = "key_cellphone";
    private static final String KEY_USERIMG = "key_userImg";
    private static final String KEY_RECORDTIME = "key_recordTime";
    private static final String KEY_PRIVILEGE = "key_privilege";

    public static UserInfoHolder getInstance() {
        return instance;
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            SharedPreferencesUtils instance = SharedPreferencesUtils.getInstance();
            instance.put(KEY_ID, user.getId());
            instance.put(KEY_USERNAME, user.getUsername());
            instance.put(KEY_PASSWORD, user.getPassword());
            instance.put(KEY_NAME, user.getName());
            instance.put(KEY_EMAIL,user.getEmail());
            instance.put(KEY_CELLPHONE,user.getCellphone());
            instance.put(KEY_USERIMG,user.getUserImg());
            instance.put(KEY_RECORDTIME,user.getRecordTime());
            instance.put(KEY_PRIVILEGE,user.getPrivilege());
        }
    }

    public User getUser() {
        if (user == null){
            SharedPreferencesUtils instance = SharedPreferencesUtils.getInstance();
            int id = (int) instance.get(KEY_ID,0);
            String username = (String) instance.get(KEY_USERNAME,"");
            String password = (String) instance.get(KEY_PASSWORD, "");
            String name = (String) instance.get(KEY_NAME,"");
            String email = (String) instance.get(KEY_EMAIL,"");
            String cellphone = (String) instance.get(KEY_CELLPHONE,"");
            String userImg = (String) instance.get(KEY_USERIMG,"");
            long recordTime = (long) instance.get(KEY_RECORDTIME, 0L);
            int privilege = (int) instance.get(KEY_PRIVILEGE, 0);
            user = new User();
            user.setId(id);
            user.setUsername(username);
            user.setPassword(password);
            user.setName(name);
            user.setEmail(email);
            user.setCellphone(cellphone);
            user.setUserImg(userImg);
            user.setRecordTime(recordTime);
            user.setPrivilege(privilege);
        }
        return user;
    }
}
