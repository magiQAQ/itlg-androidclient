package com.itlg.client.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.exifinterface.media.ExifInterface;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyUtils {

    private static final String TAG = "MyUtils";

    public static final String KEY_FARM_INFO_MODEL = "key_farmInfo";
    public static final String USER_AVATAR_FILENAME = "avatar.jpg";
    public static final String KEY_USERNAME = "key_username";
    public static final String KEY_PASSWORD = "key_password";
    private static final String ORIGIN_AVATAR_FILENAME = "origin_avatar.jpg";

    /**
     * 通过相册返回的contentUri得到图片和旋转信息,修正后保存
     *
     * @param context    上下文
     * @param contentUri 相册返回的contentUri
     * @return 保存的图片路径
     */
    public static String convertUri(Context context, Uri contentUri) {
        InputStream inputStream;
        try {
            inputStream = context.getContentResolver().openInputStream(contentUri);
            if (inputStream == null) {
                Log.e(TAG, "相册传回的uri有误");
                return null;
            }
            //这里inputStream需要用两次,先转化为ByteArrayOutputStream
            ByteArrayOutputStream outputStream = copyStream(inputStream);
            if (outputStream == null) {
                return null;
            }
            InputStream inputStream1 = new ByteArrayInputStream(outputStream.toByteArray());
            //InputStream --> Bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream1);
            inputStream1.close();
            InputStream inputStream2 = new ByteArrayInputStream(outputStream.toByteArray());
            //有偏转的图片会自动转回来
            bitmap = toTurn(bitmap, getPictureDegree(inputStream2));
            inputStream2.close();
            //全部流使用完毕后把先转化为ByteArrayOutputStream也关闭
            outputStream.close();
            //将图片保存并返回图片路径
            return saveBitmap(context, bitmap, ORIGIN_AVATAR_FILENAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将图片保存到外部的私有文件夹,不需要文件读写权限
     *
     * @param context  上下文
     * @param bitmap   位图
     * @param fileName 文件名
     * @return 该文件的路径
     */
    public static String saveBitmap(Context context, Bitmap bitmap, String fileName) {
        File imageFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);
        if (imageFile.exists()) {
            //如果文件已经存在,尝试删除原来的文件
            if (!imageFile.delete()) {
                Log.e(TAG, "新头像保存异常" + "imageFile.delete()执行失败");
                return null;
            }
        }
        if (!imageFile.getParentFile().exists()) {
            imageFile.getParentFile().mkdirs();
        }
        try {
            imageFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "保存新头像失败");
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "文件输出流刷新失败");
            return null;
        }
        return imageFile.getAbsolutePath();
    }


    /**
     * 得到图片的旋转角度
     *
     * @param path 图片的文件路径
     * @return 图片的角度
     */
    private static int getPictureDegree(InputStream path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                case ExifInterface.ORIENTATION_UNDEFINED:
                    Log.e(TAG, "图片方向没有被定义");
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }


    /**
     * 将图片旋转
     *
     * @param bitmap 旋转前的原图
     * @param angle  旋转的角度
     * @return 旋转后的图
     */
    private static Bitmap toTurn(Bitmap bitmap, int angle) {
        if (angle == 0) {
            return bitmap;
        }
        Bitmap newBitmap;
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (newBitmap == null) {
            newBitmap = bitmap;
        }
        if (bitmap != newBitmap) {
            bitmap.recycle();
        }
        return newBitmap;
    }

    /**
     * 输入流不支持mark和reset方法时,就需要通过此方法来复制
     *
     * @param inputStream 要复制的输入流
     * @return 复制完的输出流
     */
    private static ByteArrayOutputStream copyStream(InputStream inputStream) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) > -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
            inputStream.close();
            return outputStream;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * yyyy-MM-dd HH:mm:ss或yyyy年MM月dd日
     *
     * @param time long型的时间值
     * @return 转化完成的字符串
     */
    public static String longTypeTimeToString(long time) {
        Date date = new Date(time);
        //date.getYear()方法已过时 calendar一开始默认是系统时间
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        //将系统时间取出后,将其赋值为传入的时间
        calendar.setTime(date);
        SimpleDateFormat simpleDateFormat;
        if (calendar.get(Calendar.YEAR) != year) {
            //不是同一年需要显示年份
            simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        } else if (calendar.get(Calendar.MONTH) != month) {
            //不是同一月需要显示月份
            simpleDateFormat = new SimpleDateFormat("MM月dd日", Locale.CHINA);
        } else if (calendar.get(Calendar.DAY_OF_MONTH) != day) {
            //不是同一天显示日和几点几分
            simpleDateFormat = new SimpleDateFormat("dd日 hh:mm", Locale.CHINA);
        } else if (calendar.get(Calendar.HOUR_OF_DAY) != hour) {
            //不是同一小时显示几点几分
            simpleDateFormat = new SimpleDateFormat("hh:mm", Locale.CHINA);
        } else {
            //同一小时的话就显示多少分钟以前
            return String.format(Locale.CHINA, "%d分钟前", minute - calendar.get(Calendar.MINUTE));
        }
        return simpleDateFormat.format(date);
    }

}
