package com.coco.livestreaming.app.debug;

import android.util.Log;


import com.coco.livestreaming.CocotvingApplication;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.StandardExceptionParser;

/**
 * log helper for debug
 * 
 * @author ssk
 * @date 2017/1/17
 */
public class Logger {
	private static final boolean USE_ANDROID_LOGCAT = true;
//	private static final boolean USE_BUGSENSE = false;
	private static final boolean USE_GA = false;

	/**
	 * debug log
	 * 
	 * @author ssk
	 * @param tag
	 * @param log
	 */
	public static void d(String tag, String log) {
		if (USE_ANDROID_LOGCAT)
			Log.d(tag, log);
	}

	/**
	 * information log
	 * 
	 * @author ssk
	 * @param tag
	 * @param log
	 */
	public static void i(String tag, String log) {
		if (USE_ANDROID_LOGCAT)
			Log.i(tag, log);
	}

	/**
	 * warning log
	 * 
	 * @author ssk
	 * @param tag
	 * @param log
	 */
	public static void w(String tag, String log) {
		if (USE_ANDROID_LOGCAT)
			Log.w(tag, log);
	}

	/**
	 * error log
	 * 
	 * @author ssk
	 * @param tag
	 * @param log
	 */
	public static void e(String tag, String log) {
		if (USE_ANDROID_LOGCAT)
			Log.e(tag, log);

	}
	
	/**
	 * process exceptions.
	 * 
	 * @author ssk
	 * @param tag TAG
	 * @param ex Exception object
	 */
	public static void ex(String tag, Exception ex){
		if (USE_ANDROID_LOGCAT){
			Log.d(tag, ex.toString());
			ex.printStackTrace();
		}
		
//		if (USE_BUGSENSE){
//            Mint.logException(ex);
//		}
		if(USE_GA){
			EasyTracker tracker = EasyTracker.getInstance(CocotvingApplication.getContext());
			tracker.send(MapBuilder
							.createException(new StandardExceptionParser(CocotvingApplication.getContext(), null)
												.getDescription(Thread.currentThread().getName(), ex), false)
							.build());
		}
	}
}
