package com.willme.topactivity;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

/**
 * Created by Wen on 1/14/15.
 */
public class WatchingAccessibilityService extends AccessibilityService {

    @SuppressLint("NewApi")
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        if(SPHelper.isShowWindow(this)){
            TasksWindow.show(this, event.getPackageName() + "\n" + event.getClassName());
        }

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        TasksWindow.dismiss(this);
        return super.onUnbind(intent);
    }

}
