package com.willme.topactivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class MainActivity extends Activity implements OnCheckedChangeListener {

    public static final String EXTRA_FROM_QS_TILE = "from_qs_tile";
    public static final String ACTION_STATE_CHANGED = "com.willme.topactivity.ACTION_STATE_CHANGED";
    CompoundButton mWindowSwitch, mNotificationSwitch;
    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWindowSwitch = (CompoundButton) findViewById(R.id.sw_window);
        mWindowSwitch.setOnCheckedChangeListener(this);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            if(!getResources().getBoolean(R.bool.qs_tile_service_availability)){
                findViewById(R.id.useNotificationPref).setVisibility(View.GONE);
                findViewById(R.id.divider_useNotificationPref).setVisibility(View.GONE);
            }
        }
        mNotificationSwitch = (CompoundButton) findViewById(R.id.sw_notification);
        if(mNotificationSwitch != null){
            mNotificationSwitch.setOnCheckedChangeListener(this);
        }
        if(getResources().getBoolean(R.bool.use_watching_service)){
            TasksWindow.show(this, "");
            startService(new Intent(this, WatchingService.class));
        }
        if(getIntent().getBooleanExtra(EXTRA_FROM_QS_TILE, false)){
            mWindowSwitch.setChecked(true);
        }
        mReceiver = new UpdateSwitchReceiver();
        registerReceiver(mReceiver, new IntentFilter(ACTION_STATE_CHANGED));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(getIntent().getBooleanExtra(EXTRA_FROM_QS_TILE, false)){
            mWindowSwitch.setChecked(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshWindowSwitch();
        refreshNotificationSwitch();
        NotificationActionReceiver.cancelNotification(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(SPHelper.isShowWindow(this) && !(getResources().getBoolean(R.bool.use_accessibility_service) && WatchingAccessibilityService.getInstance() == null)){
            NotificationActionReceiver.showNotification(this, false);
        }
    }

    private void refreshWindowSwitch(){
        mWindowSwitch.setChecked(SPHelper.isShowWindow(this));
        if(getResources().getBoolean(R.bool.use_accessibility_service)){
            if(WatchingAccessibilityService.getInstance() == null){
                mWindowSwitch.setChecked(false);
            }
        }
    }

    private void refreshNotificationSwitch(){
        if(mNotificationSwitch != null){
            mNotificationSwitch.setChecked(!SPHelper.isNotificationToggleEnabled(this));
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView == mNotificationSwitch){
            if(SPHelper.hasQSTileAdded(this)){
                SPHelper.setNotificationToggleEnabled(this, !isChecked);
            } else if(isChecked){
                Toast.makeText(this, R.string.toast_add_tile, Toast.LENGTH_LONG).show();
                buttonView.setChecked(false);
            } else {
                SPHelper.setNotificationToggleEnabled(this, !isChecked);
            }
            return;
        }
        if(isChecked && buttonView == mWindowSwitch && getResources().getBoolean(R.bool.use_accessibility_service)){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)){
                new AlertDialog.Builder(this)
                        .setMessage(R.string.dialog_enable_overlay_window_msg)
                        .setPositiveButton(R.string.dialog_enable_overlay_window_positive_btn
                                , new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                                        intent.setData(Uri.parse("package:"+getPackageName()));
                                        startActivity(intent);
                                        dialog.dismiss();
                                    }
                                })
                        .setNegativeButton(android.R.string.cancel
                                , new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SPHelper.setIsShowWindow(MainActivity.this, false);
                                        refreshWindowSwitch();
                                    }
                                })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                SPHelper.setIsShowWindow(MainActivity.this, false);
                                refreshWindowSwitch();
                            }
                        })
                        .create()
                        .show();
                buttonView.setChecked(false);
                return;
            } else if (WatchingAccessibilityService.getInstance() == null){
                new AlertDialog.Builder(this)
                        .setMessage(R.string.dialog_enable_accessibility_msg)
                        .setPositiveButton(R.string.dialog_enable_accessibility_positive_btn
                                , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SPHelper.setIsShowWindow(MainActivity.this, true);
                                Intent intent = new Intent();
                                intent.setAction("android.settings.ACCESSIBILITY_SETTINGS");
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel
                                , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                refreshWindowSwitch();
                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                refreshWindowSwitch();
                            }
                        })
                        .create()
                        .show();
                        SPHelper.setIsShowWindow(this, true);
                return;
            }
        }
        if(buttonView == mWindowSwitch){
            SPHelper.setIsShowWindow(this, isChecked);
            if(!isChecked){
                TasksWindow.dismiss(this);
            }else{
                TasksWindow.show(this, getPackageName()+"\n"+getClass().getName());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    class UpdateSwitchReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            refreshWindowSwitch();
            refreshNotificationSwitch();
        }
    }

}
