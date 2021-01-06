package com.sim.traveltool.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sim.baselibrary.base.BaseActivity;
import com.sim.baselibrary.bean.EventMessage;
import com.sim.baselibrary.callback.DialogInterface;
import com.sim.baselibrary.utils.LogUtil;
import com.sim.baselibrary.utils.SPUtil;
import com.sim.baselibrary.utils.ToastUtil;
import com.sim.traveltool.AppHelper;
import com.sim.traveltool.R;
import com.sim.traveltool.db.bean.User;
import com.sim.traveltool.ui.view.TitleView;

import org.greenrobot.eventbus.EventBus;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @Auther Sim
 * @Time 2020/4/29 1:05
 * @Description 显示用户信息的页面
 */
public class UserUpdateActivity extends BaseActivity {

    private TitleView titleView;
    private LinearLayout parent;

    private RelativeLayout rlUserName;
    private RelativeLayout rlPassword;
    private RelativeLayout rlMobilePhoneNumber;
    private RelativeLayout rlEmail;

    private TextView tvUserName;
    //    private TextView tvPassword;
    private TextView tvMobilePhoneNumber;
    private TextView tvEmail;
    private Button btnLogOut;

    //修改密码弹窗
    private PopupWindow updatePasswordPopupWindow;//弹窗
    private View updatePasswordLayout;//布局
    private EditText etOldPassword;
    private EditText etNewPassword;
    private EditText etNewPasswordAgain;
    private Button btnPasswordCancel;
    private Button btnPasswordConfirm;

    private User user;


    @Override
    protected int getLayoutRes() {
        return R.layout.activity_user_update;
    }

    @Override
    protected void bindViews(Bundle savedInstanceState) {
        titleView = findViewById(R.id.titleView);
        parent = findViewById(R.id.parent);
        rlUserName = findViewById(R.id.rl_user_name);
        rlPassword = findViewById(R.id.rl_password);
        rlMobilePhoneNumber = findViewById(R.id.rl_mobile_phone_number);
        rlEmail = findViewById(R.id.rl_email);
        tvUserName = findViewById(R.id.tv_user_name);
        tvMobilePhoneNumber = findViewById(R.id.tv_mobile_phone_number);
        tvEmail = findViewById(R.id.tv_email);
        btnLogOut = findViewById(R.id.btn_log_out);
        setViewClick(rlUserName, rlPassword, rlMobilePhoneNumber, rlEmail, btnLogOut);
        titleView.setLeftClickListener(new TitleView.LeftClickListener() {
            @Override
            public void onClick(View leftView) {
                finish();
            }
        });
    }

    @Override
    protected void initData() {
        if (BmobUser.isLogin()) {
            user = BmobUser.getCurrentUser(User.class);
        }
    }

    @Override
    protected void initView() {
        if (user != null) {
            tvUserName.setText(user.getUsername());
            tvMobilePhoneNumber.setText(user.getMobilePhoneNumber());
            tvEmail.setText(user.getEmail());
        } else {
            finish();
        }

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        updatePasswordLayout = inflater.inflate(R.layout.view_popup_update_password, null);
        updatePasswordPopupWindow = showPopupWindow(updatePasswordLayout, 350, 380);
        etOldPassword = updatePasswordLayout.findViewById(R.id.et_old_password);
        etNewPassword = updatePasswordLayout.findViewById(R.id.et_new_password);
        etNewPasswordAgain = updatePasswordLayout.findViewById(R.id.et_new_password_again);
        btnPasswordCancel = updatePasswordLayout.findViewById(R.id.btn_password_cancel);
        btnPasswordConfirm = updatePasswordLayout.findViewById(R.id.btn_password_confirm);
        setViewClick(btnPasswordCancel, btnPasswordConfirm);
    }

    @Override
    public void onMultiClick(View view) {
        if (view == btnLogOut) {
            showDialog(getString(R.string.logout), getString(R.string.confirm_logout), getString(R.string.ok), getString(R.string.cancel),
                    new DialogInterface() {
                        @Override
                        public void sureOnClick() {
                            BmobUser.logOut();
                            EventBus.getDefault().post(new EventMessage(AppHelper.USER_noLogIn));
                            SPUtil.put(UserUpdateActivity.this, AppHelper.userSpName, AppHelper.userSpStateKey, false);
                            finish();
                        }

                        @Override
                        public void cancelOnClick() {

                        }
                    });
        } else if (view == rlUserName) {

        } else if (view == rlPassword) {
            updatePasswordPopupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
        } else if (view == rlMobilePhoneNumber) {

        } else if (view == rlEmail) {

        } else if (view == btnPasswordCancel) {
            updatePasswordPopupWindow.dismiss();
        } else if (view == btnPasswordConfirm) {
            if (etOldPassword.getText().length() > 0 && etNewPassword.getText().length() > 0 && etNewPasswordAgain.getText().length() > 0) {
                if (etNewPassword.getText().toString().equals(etNewPasswordAgain.getText().toString())) {
                    updatePassword(etOldPassword.getText().toString(), etNewPassword.getText().toString());
                } else {
                    ToastUtil.T_Info(UserUpdateActivity.this, getString(R.string.inconsistent));
                }
            } else {
                ToastUtil.T_Info(UserUpdateActivity.this, getString(R.string.password_no_null));
            }
        } else {
            super.onMultiClick(view);
        }
    }

    /**
     * 提供旧密码修改密码
     */
    private void updatePassword(String oldPassword, String newPassword) {
        BmobUser.updateCurrentUserPassword(oldPassword, newPassword, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    ToastUtil.T_Success(UserUpdateActivity.this, getString(R.string.update_success));
                    updatePasswordPopupWindow.dismiss();
                } else {
                    if (e.getMessage().contains("old password incorrect")) {
                        ToastUtil.T_Error(UserUpdateActivity.this, getString(R.string.login_fail_password));
                    } else {
                        ToastUtil.T_Error(UserUpdateActivity.this, getString(R.string.update_fail));
                        LogUtil.e(this.getClass(), "修改用户信息失败---code:" + e.getErrorCode() + ";message:" + e.getMessage());
                    }
                }
            }
        });
    }

    /**
     * 更新用户操作并同步更新本地的用户信息
     */
    private void updateUserInfo() {
        User user = BmobUser.getCurrentUser(User.class);
//        user.setMobilePhoneNumber("");
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    ToastUtil.T_Success(UserUpdateActivity.this, getString(R.string.update_success));
                } else {
                    ToastUtil.T_Error(UserUpdateActivity.this, getString(R.string.update_fail));
                    LogUtil.e(this.getClass(), "修改用户信息失败---code:" + e.getErrorCode() + ";message:" + e.getMessage());
                }
            }
        });
    }

}
