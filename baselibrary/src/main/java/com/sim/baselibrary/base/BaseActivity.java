package com.sim.baselibrary.base;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.sim.baselibrary.views.DialogBuilder;
import com.sim.baselibrary.views.DialogInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther Sim
 * @Time 2020/12/9 11:13
 * @Description 封装权限请求、dialog。项目模块BaseActivity继承此类
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected int screenWidth;//屏幕宽度
    protected int heightPixels;//屏幕高度

    private int REQUEST_CODE_PERMISSION = 0x00000;//权限请求码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getScreenSize();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void getScreenSize() {
        //获取屏幕宽高相关
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;         // 屏幕宽度（像素）
        int height = dm.heightPixels;       // 屏幕高度（像素）
        float density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        screenWidth = (int) (width / density);  // 屏幕宽度(dp)
        heightPixels = (int) (height / density);// 屏幕高度(dp)
    }

    /**
     * 请求权限
     *
     * @param permissions 请求的权限
     * @param requestCode 请求权限的请求码
     */
    public void requestPermission(String[] permissions, int requestCode) {
        this.REQUEST_CODE_PERMISSION = requestCode;
        if (checkPermissions(permissions)) {
            permissionSuccess(REQUEST_CODE_PERMISSION);
        } else {
            List<String> needPermissions = getDeniedPermissions(permissions);
            ActivityCompat.requestPermissions(this, needPermissions.toArray(new String[needPermissions.size()]), REQUEST_CODE_PERMISSION);
        }
    }

    /**
     * 检测所有的权限是否都已授权
     *
     * @param permissions
     * @return
     */
    private boolean checkPermissions(String[] permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     */
    private List<String> getDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) !=
                    PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                needRequestPermissionList.add(permission);
            }
        }
        return needRequestPermissionList;
    }

    /**
     * 系统请求权限回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (verifyPermissions(grantResults)) {
                permissionSuccess(REQUEST_CODE_PERMISSION);
            } else {
                permissionFail(REQUEST_CODE_PERMISSION);
            }
        }
    }

    /**
     * 确认所有的权限是否都已授权
     *
     * @param grantResults
     * @return
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    //无权限提示跳转到当前应用设置页面
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    //获取权限成功
    public void permissionSuccess(int requestCode) {

    }

    //权限获取失败
    public void permissionFail(int requestCode) {
        //无权限显示提示对话框,需自行修改
        showDialog("提示信息", "缺少必要权限软件无法正常使用!如若恢复正常使用，请单击【确定】按钮前往设置进行权限授权",
                "确定", "取消", new DialogInterface() {
                    @Override
                    public void sureOnClick() {
                        startAppSettings();
                    }

                    @Override
                    public void cancelOnClick() {

                    }
                });
    }


    /**
     * dialog显示
     *
     * @param title           标题
     * @param message         提示信息
     * @param sureText        确认按钮
     * @param cancelText      取消按钮
     * @param dialogInterface 点击事件监听
     */
    public void showDialog(String title, String message, String sureText, String cancelText,
                           final com.sim.baselibrary.views.DialogInterface dialogInterface) {
        DialogBuilder dialogBuilder;
        dialogBuilder = new DialogBuilder(this);
        if (title != null) {
            dialogBuilder.title(title);
        }
        if (message != null) {
            dialogBuilder.message(message);
        }
        if (sureText != null) {
            dialogBuilder.sureText(sureText);
        }
        if (cancelText != null) {
            dialogBuilder.cancelText(cancelText);
        }
        if (dialogInterface != null) {
            dialogBuilder.setSureOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogInterface.sureOnClick();
                }
            });
            dialogBuilder.setCancelOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogInterface.cancelOnClick();
                }
            });
        }
        dialogBuilder.build().show();
    }

}