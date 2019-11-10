package com.itlg.client.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.itlg.client.R;
import com.itlg.client.UserInfoHolder;
import com.itlg.client.bean.User;
import com.itlg.client.biz.UserBiz;
import com.itlg.client.net.CommonCallback;
import com.itlg.client.utils.MyUtils;
import com.itlg.client.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    public static final int REGISTER_ACTIVITY_REQUEST_CODE = 1;

    @BindView(R.id.username_editText)
    EditText usernameEditText;
    @BindView(R.id.password_editText)
    EditText passwordEditText;


    private UserInfoHolder userInfoHolder = UserInfoHolder.getInstance();
    private UserBiz userBiz = new UserBiz();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        setStatusBarColor(R.color.transparent, false);

        User user = userInfoHolder.getUser();
        //如果用户已经登录过,则自动登录并跳转到主界面
        if (user.getId() > 0) {
            userBiz.login(user.getUsername(), user.getPassword(), new CommonCallback<User>() {
                @Override
                public void onFail(Exception e) {
                    ToastUtils.showToast(e.getMessage());
                }

                @Override
                public void onSuccess(User response) {
                    if (response.getPrivilege() > 1) {
                        toNormalUserActivity(userInfoHolder.getUser());
                    } else if (response.getPrivilege() == 1) {
                        toOperatorActivity(userInfoHolder.getUser());
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userBiz.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REGISTER_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            //得到注册界面返回的用户名和密码,不用用户二次填写
            String username = data.getStringExtra(MyUtils.KEY_USERNAME);
            String password = data.getStringExtra(MyUtils.KEY_PASSWORD);
            usernameEditText.setText(username);
            passwordEditText.setText(password);
        }
    }

    @OnClick(R.id.login_button)
    public void login(){
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            ToastUtils.showToast("用户名和密码不能为空");
            return;
        }

        userBiz.login(username, password, new CommonCallback<User>() {
            @Override
            public void onFail(Exception e) {
                ToastUtils.showToast(e.getMessage());
            }

            @Override
            public void onSuccess(User response) {
                ToastUtils.showToast("欢迎回来," + response.getName());
                //返回的密码会被MD5加密,所以保存原来的密码
                response.setPassword(password);
                //保存用户信息
                userInfoHolder.setUser(response);
                //判断用户身份
                if (response.getPrivilege() == 50 || response.getPrivilege() == 70) {
                    //是普通用户或农场主
                    toNormalUserActivity(response);
                } else if (response.getPrivilege() == 1){
                    //是操作员
                    toOperatorActivity(response);
                }
            }
        });
    }

    @OnClick(R.id.register_button)
    public void register(){
        toRegisterActivity();
    }


    private void toNormalUserActivity(User user) {
        Intent intent = new Intent(this,NormalUserActivity.class);
        intent.putExtra("user",user);
        startActivity(intent);
        finish();
    }

    private void toOperatorActivity(User user) {
        Intent intent = new Intent(this,OperatorActivity.class);
        intent.putExtra("user",user);
        startActivity(intent);
        finish();
    }

    private void toRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivityForResult(intent, REGISTER_ACTIVITY_REQUEST_CODE);
    }

}
