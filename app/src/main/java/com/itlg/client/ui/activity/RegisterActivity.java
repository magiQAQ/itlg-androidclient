package com.itlg.client.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.itlg.client.R;
import com.itlg.client.bean.UserInfo;
import com.itlg.client.biz.UserBiz;
import com.itlg.client.utils.MyUtils;
import com.itlg.client.utils.RegexUtil;
import com.itlg.client.utils.ToastUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import okhttp3.Call;

public class RegisterActivity extends BaseActivity {

    private static final String TAG = "RegisterActivity";

    public static final int SYSTEM_ALBUM_REQUEST_CODE = 101;
    public static final int CROP_AVATAR_REQUEST_CODE = 102;
    private boolean hasAvatar = false;
    @BindView(R.id.userImg_imageView)
    AppCompatImageView userImgImageView;
    @BindView(R.id.name_editText)
    TextInputEditText nameEditText;
    @BindView(R.id.name_layout)
    TextInputLayout nameLayout;
    @BindView(R.id.username_editText)
    TextInputEditText usernameEditText;
    @BindView(R.id.username_layout)
    TextInputLayout usernameLayout;
    @BindView(R.id.password_editText)
    TextInputEditText passwordEditText;
    @BindView(R.id.password_layout)
    TextInputLayout passwordLayout;
    @BindView(R.id.password2_editText)
    TextInputEditText password2EditText;
    @BindView(R.id.password2_layout)
    TextInputLayout password2Layout;
    @BindView(R.id.cellphone_editText)
    TextInputEditText cellphoneEditText;
    @BindView(R.id.cellphone_layout)
    TextInputLayout cellphoneLayout;
    @BindView(R.id.email_editText)
    TextInputEditText emailEditText;
    @BindView(R.id.email_layout)
    TextInputLayout emailLayout;
    private UserBiz userBiz = new UserBiz();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);
        setupCommonToolbar();
        setStatusBarColor(R.color.colorTheme, true);
        setTitle("新用户注册");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == SYSTEM_ALBUM_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            //得到系统传来的一次性的图片路径
            Uri contentUri = data.getData();
            //将Content类型的Uri转化为文件类型的Uri,并将原图和旋转修正后的图片保存
            String avatarPhotoPath = MyUtils.convertUri(this, contentUri);
            Log.e(TAG, avatarPhotoPath);
            File photoFile = new File(avatarPhotoPath);
            if (!photoFile.exists()){
                Log.e(TAG,"图片文件不存在");
                return;
            }
            //打开系统裁剪
            startImageZoom(photoFile);
        } else if (requestCode == CROP_AVATAR_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            //得到裁剪后的图片
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap bitmap = extras.getParcelable("data");
                //将处理完的图片显示出来
                Glide.with(this).load(bitmap).circleCrop().into(userImgImageView);
                //将图片缓存到本地
                MyUtils.saveBitmap(this, bitmap, MyUtils.USER_AVATAR_FILENAME);
                hasAvatar = true;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        userBiz.onDestroy();
        super.onDestroy();
    }

    @OnClick(R.id.chooseAvatar_button)
    public void chooseAvatar() {
        //打开相册选择照片
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, SYSTEM_ALBUM_REQUEST_CODE);
    }

    @OnTextChanged(R.id.name_editText)
    public void nameTextChanged(CharSequence s){
        if (!RegexUtil.checkChinese(s.toString())) {
            nameLayout.setError("请输入中文人名");
            nameLayout.setErrorEnabled(true);
        } else if (s.length()<2||s.length()>5){
            nameLayout.setError("姓名应由2~5个中文汉字组成");
            nameLayout.setErrorEnabled(true);
        } else if (RegexUtil.checkChinese(s.toString())){
            nameLayout.setErrorEnabled(false);
        }

    }

    @OnTextChanged(R.id.username_editText)
    public void usernameTextChanged(CharSequence s){
        if (RegexUtil.checkUsername(s.toString())){
            usernameLayout.setErrorEnabled(false);
        } else {
            usernameLayout.setErrorEnabled(true);
            usernameLayout.setError("用户名必须是由字母开头的4~20位字母数字下划线组成");
        }
    }

    @OnTextChanged(R.id.password_editText)
    public void passwordTextChanged(CharSequence s){
        if (RegexUtil.checkPassword(s.toString())){
            passwordLayout.setErrorEnabled(false);
        } else {
            passwordLayout.setErrorEnabled(true);
            passwordLayout.setError("密码必须是由字母开头的6~20位字母数字下划线组成");
        }
    }

    @OnTextChanged(R.id.password2_editText)
    public void password2TextChanged(CharSequence s){
        String password = passwordEditText.getEditableText().toString();
        if (password.equals(s.toString())){
            password2Layout.setErrorEnabled(false);
        } else {
            password2Layout.setErrorEnabled(true);
            password2Layout.setError("两次密码输入不一致");
        }
    }

    @OnTextChanged(R.id.cellphone_editText)
    public void cellphoneTextChanged(CharSequence s){
        if (RegexUtil.checkCellPhone(s.toString())){
            cellphoneLayout.setErrorEnabled(false);
        } else {
            cellphoneLayout.setErrorEnabled(true);
            cellphoneLayout.setError("请输入正确格式的手机号码");
        }
    }

    @OnTextChanged(R.id.email_editText)
    public void emailTextChanged(CharSequence s){
        if (RegexUtil.checkEmail(s.toString())){
            emailLayout.setErrorEnabled(false);
        } else {
            emailLayout.setErrorEnabled(true);
            emailLayout.setError("请输入正确格式的邮箱");
        }
    }

    /**
     * 注册信息提交
     */
    @OnClick(R.id.register_button)
    public void registerSubmit() {
        //检查各控件内的内容是否经过校验
        if (nameLayout.isErrorEnabled()||usernameLayout.isErrorEnabled()||passwordLayout.isErrorEnabled()
        ||password2Layout.isErrorEnabled()||cellphoneLayout.isErrorEnabled()||emailLayout.isErrorEnabled()){
            ToastUtils.showToast("请修改错误的内容");
            return;
        }

        String name = nameEditText.getEditableText().toString();
        String username = usernameEditText.getEditableText().toString();
        String password = passwordEditText.getEditableText().toString();
        String password2 = password2EditText.getEditableText().toString();
        String cellphone = cellphoneEditText.getEditableText().toString();
        String email = emailEditText.getEditableText().toString();
        //检查是否有没有填写的项目
        if (name.equals("")||username.equals("")||password.equals("")
        ||password2.equals("")||cellphone.equals("")||email.equals("")){
            ToastUtils.showToast("所有项目都必须被填写");
            return;
        }
        //所有内容确认后,开始构造user对象
        UserInfo userInfo = new UserInfo();
        userInfo.setId(0);//新建用户id则为0
        userInfo.setName(name);
        userInfo.setUsername(username);
        userInfo.setPassword(password);
        userInfo.setCellphone(cellphone);
        userInfo.setEmail(email);
        userInfo.setPrivilege(50);
        StringCallback stringCallback = new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtils.showToast(e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("succ")){
                        String data = jsonObject.getString("data");
                        ToastUtils.showToast("注册成功，" + data);

                        //这册成功后将账号密码传回
                        Intent intent = new Intent();
                        intent.putExtra(MyUtils.KEY_USERNAME,username);
                        intent.putExtra(MyUtils.KEY_PASSWORD,password);
                        setResult(RESULT_OK,intent);
                        finish();
                    } else {
                        ToastUtils.showToast(jsonObject.getString("stmt"));
                    }
                } catch (JSONException e) {
                    ToastUtils.showToast(e.getMessage());
                    e.printStackTrace();
                }

            }
        };
        if (hasAvatar){
            userBiz.register(userInfo, password2, new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), MyUtils.USER_AVATAR_FILENAME), stringCallback);
        } else {
            userBiz.register(userInfo, password2, stringCallback);
        }
    }

    /**
     * 通过Uri传递图像信息以供裁剪
     *
     * @param photoFile 图片文件
     */
    private void startImageZoom(File photoFile) {
        //构建隐式Intent来启动裁剪程序
        Intent intent = new Intent("com.android.camera.action.CROP");
        Uri uri = Uri.fromFile(photoFile);
        //Android7.0及以后Uri需要通过FileProvider来分享,同时需要为裁剪程序添加临时的读取权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri = FileProvider.getUriForFile(this, getApplication().getPackageName() + ".fileprovider", photoFile);
        }
        //设置数据uri和类型为图片类型
        intent.setDataAndType(uri, "image/*");
        //显示View为可裁剪的
        intent.putExtra("crop", true);
        //裁剪的宽高的比例为1:1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //输出图片的宽高均为150
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        //裁剪之后的数据是通过Intent返回
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_AVATAR_REQUEST_CODE);
    }



}
