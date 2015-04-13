package com.willme.topactivity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MainActivity extends Activity implements OnCheckedChangeListener {
	
	CompoundButton mWindowSwitch;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		TasksWindow.show(this, "");
		mWindowSwitch = (CompoundButton) findViewById(R.id.sw_window);
		mWindowSwitch.setChecked(SPHelper.isShowWindow(this));
		mWindowSwitch.setOnCheckedChangeListener(this);
        if(getResources().getBoolean(R.bool.use_watching_service))
		    startService(new Intent(this, WatchingService.class));
	}

    @Override
    protected void onResume() {
        super.onResume();
        resetUI();
    }

    private void resetUI(){
        if(getResources().getBoolean(R.bool.use_accessibility_service)){
            if(!AccessibilityManager.hasAccessibilityServiceEnabled(this)){
                mWindowSwitch.setChecked(false);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked && buttonView == mWindowSwitch && getResources().getBoolean(R.bool.use_accessibility_service)){
            if (!AccessibilityManager.hasAccessibilityServiceEnabled(this)){
                new AlertDialog.Builder(this)
                        .setMessage(R.string.dialog_enable_accessibility_msg)
                        .setPositiveButton(R.string.dialog_enable_accessibility_positive_btn
                                , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setAction("android.settings.ACCESSIBILITY_SETTINGS");
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel
                                , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                resetUI();
                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                resetUI();
                            }
                        })
                        .create()
                        .show();
                        SPHelper.setIsShowWindow(this, isChecked);
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


}
