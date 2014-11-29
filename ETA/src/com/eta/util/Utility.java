package com.eta.util;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * This class provide helper utility methods.
 * @author sweta
 *
 */
public class Utility {

	public static String getDevicePhoneNumber(Context context) {
		TelephonyManager tMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		return tMgr.getLine1Number();
	}
}
