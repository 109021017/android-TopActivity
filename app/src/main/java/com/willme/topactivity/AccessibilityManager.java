package com.willme.topactivity;

import android.content.Context;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

public class AccessibilityManager {

	private final static String ACCESSIBILITY_ENABLED = "accessibility_enabled";
	private final static String ENABLED_ACCESSIBILITY_SERVICES = "enabled_accessibility_services";
	
	private final static String TAG = AccessibilityManager.class.getSimpleName();
	
	public static boolean hasAccessibilityServiceEnabled(Context context){
		try {
			int accessibilityEnabled = android.provider.Settings.Secure.getInt(context.getContentResolver(), ACCESSIBILITY_ENABLED);
			if(accessibilityEnabled != 1){
				Log.w(TAG, "Accessibility Service Disabled!");
				return false;
			}else{
				String enabledServices = android.provider.Settings.Secure.getString(context.getContentResolver(), ENABLED_ACCESSIBILITY_SERVICES);
				if(enabledServices.contains(context.getPackageName())){
					return true;
				}
				return false;
			}
			
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
