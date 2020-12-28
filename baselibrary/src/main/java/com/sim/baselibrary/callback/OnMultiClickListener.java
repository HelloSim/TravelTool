package com.sim.baselibrary.callback;

import android.view.View;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @Auther Sim
 * @Time 2020/12/28 11:35
 * @Description 防止多次点击
 */
public abstract class OnMultiClickListener implements View.OnClickListener {

    private static final int MIN_CLICK_DELAY_TIME = 1000;// 两次点击按钮之间的点击间隔不能少于1000毫秒
    private static AtomicLong lastClickTime = new AtomicLong(0);

    /**
     * @return true 为没有快速点击
     */
    public static boolean isNoFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime.get()) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime.set(curClickTime);
        return flag;
    }

    @Override
    public void onClick(View v) {
        if (isNoFastClick()) {
            // 进行点击事件后的逻辑操作
            onMultiClick(v);
        }
    }

    public abstract void onMultiClick(View v);

}