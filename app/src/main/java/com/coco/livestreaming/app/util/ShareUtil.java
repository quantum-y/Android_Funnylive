package com.coco.livestreaming.app.util;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.CursorLoader;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;

/**
 * @author silver
 *
 */
public class ShareUtil{
	public static String SHARE_MODE = "share";
	public static String SAVE_MODE = "save";
	public static String CAPTURE_MODE = "captureMode";
	
	public static String pattern = "yyyy-MM-dd HH:mm:ss";
	public static String pattern2 = "yyyy-MM-dd";
	public static String pattern3 = "yyyy-MM-dd hh-mm-ss";
	public static String userpattern = "MMM yyyy";
	public static String patternMDY = "MMM dd, yyyy";
	public static String pattern_mmss = "mm::ss";
	
	public static String[] week_days = new String []{"", "星期日 ", "星期一 ", "星期二 ", "星期三 ", "星期四 ", "星期五 ", "星期六 ",}; 
	public static String[] time_range = new String []{"凌晨", "上午", "下午", "晚上" }; 
	public static long chat_time_limit = 3 * 60 * 1000; //3min
	public static final String TAG = "silver";
	
	Activity m_context;
	String m_chooseTitle;
	String m_subject;
	String m_message;
	static File sdcardPath;
	//public static String saveFolderName = "unLog";
	public static String tempFileName = "pooTemp.png";
	
	public static boolean savePicture(View capture_layout) {
		Uri attachUri = getUploadUri(capture_layout,SAVE_MODE);
		return attachUri == null ? false : true;
	}
	
	public void shareCaptureReturn(View capture_layout, int requestCode) {
		if(m_context==null) return;
		View view = capture_layout;
		Uri attachUri = getUploadUri(view,SHARE_MODE);

		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		sharingIntent.setType("image/png");
		sharingIntent.putExtra(Intent.EXTRA_SUBJECT, m_subject);
		sharingIntent.putExtra(Intent.EXTRA_TEXT, m_message);
		if (attachUri != null)
			sharingIntent.putExtra(Intent.EXTRA_STREAM, attachUri);
		m_context.startActivityForResult(Intent.createChooser(sharingIntent, m_chooseTitle), requestCode);
	}	
	
	public void shareCapture(View capture_layout) {
		if(m_context==null) return;
		View view = capture_layout;
		Uri attachUri = getUploadUri(view,SHARE_MODE);

		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		sharingIntent.setType("image/png");
		sharingIntent.putExtra(Intent.EXTRA_SUBJECT, m_subject);
		sharingIntent.putExtra(Intent.EXTRA_TEXT, m_message);
		if (attachUri != null)
			sharingIntent.putExtra(Intent.EXTRA_STREAM, attachUri);
		m_context.startActivity(Intent.createChooser(sharingIntent, m_chooseTitle));
	}	
	
	public void appShare() {
		if(m_context==null) return;
		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		sharingIntent.setType("image/png");
		sharingIntent.putExtra(Intent.EXTRA_SUBJECT, m_subject);
		sharingIntent.putExtra(Intent.EXTRA_TEXT, m_message);
		m_context.startActivity(Intent.createChooser(sharingIntent, m_chooseTitle));
	}

	private static Bitmap getScreenshot(View view,String path) {
		Bitmap screenshot = null;
		try {
			view.setDrawingCacheEnabled(true);
			view.buildDrawingCache(true);
			screenshot = view.getDrawingCache();
			if(screenshot == null || ! overlayImageUpload(screenshot,new File(path)))
				return null;
			view.setDrawingCacheEnabled(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return screenshot;
	}
	
	private static boolean overlayImageUpload(Bitmap bmp,File f) {
		try {
			if (f.exists())
				f.delete();

			f.createNewFile();

			OutputStream outStream = new FileOutputStream(f);
			bmp.compress(Bitmap.CompressFormat.PNG, 30, outStream);
			outStream.close();

		} catch (IOException e) {
			e.printStackTrace();
			if(f.exists()) 
				f.delete();
			return false;
		}

		return true;
	}
	
	private static Uri getUploadUri(View shotview,String mode) {
		Uri uri = null;
		String path = getUploadPath(mode);
		if (path == null || shotview == null)
			return uri;

		Bitmap capturImg = getScreenshot(shotview,path);
		if (capturImg == null)
			return uri;
		uri =  Uri.fromFile(new File(path));
		return uri; 
	}

	private static String getUploadPath(String mode) {
		if (sdcardPath == null) sdcardPath = getSDcardPath();
		if (sdcardPath == null) return null;
		String saveFileName = null;
		if(mode.equals(SHARE_MODE))
			saveFileName = tempFileName;
		else 
			saveFileName = "screenshot_"+getStrDate(Calendar.getInstance(),pattern3)+".png";
		
		//File dir = new File(sdcardPath + "/" +saveFolderName);
		File dir = new File(Constants.IMG_FULL_PATH);
		if(!dir.isDirectory()) dir.mkdir(); 
		return dir + "/" + saveFileName;
	}

	public static File getSDcardPath() {
		try {
			return Environment.getExternalStorageDirectory();
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String getStrDate(Calendar cal, String pattern) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(cal.getTime());
	}
	
	public static int getAge(String birthDay){
		int age = 0;
		if(TextUtils.isEmpty(birthDay))
			return age;
		int birthYear = 0;
		
		try {
			String[] spliteStr = birthDay.split("-");
			birthYear = Integer.parseInt(spliteStr[0]);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		if(birthYear <= 0)
			return age;
		
		try {
			Calendar nowCal = Calendar.getInstance();
			age = nowCal.get(Calendar.YEAR) - birthYear;
		} catch (Exception e) {
			Log.e("convert error", birthDay+" can't covert to calendar.");
			return age;
		}		
		return age;
	}
	
	public static int getYear(String DateString){
		int year=0;
		  SimpleDateFormat format = new SimpleDateFormat(pattern2);
		  Calendar dateCal = Calendar.getInstance();
		  try{
			  Date date = format.parse(DateString);
			  dateCal.setTime(date);
			  year=dateCal.get(Calendar.YEAR);
		    }catch (Exception e) {
				Log.e("convert error", DateString+" can't covert to calendar.");
				return year;
			}
		  
		return year;
	}
	
	public static int getMonth(String DateString){
		int month=0;
		  SimpleDateFormat format = new SimpleDateFormat(pattern2);
		  Calendar dateCal = Calendar.getInstance();
		  try{
			  Date date = format.parse(DateString);
			  dateCal.setTime(date);
			  month=dateCal.get(Calendar.MONTH) + 1;
		    }catch (Exception e) {
				Log.e("convert error", DateString+" can't covert to calendar.");
				return month;
			}
		  
		return month;
	}
	
	public static int getDate(String DateString){
		int daynumber=0;
		  SimpleDateFormat format = new SimpleDateFormat(pattern2);
		  Calendar dateCal = Calendar.getInstance();
		  try{
			  Date date = format.parse(DateString);
			  dateCal.setTime(date);
			  daynumber=dateCal.get(Calendar.DAY_OF_MONTH);
		    }catch (Exception e) {
				Log.e("convert error", DateString+" can't covert to calendar.");
				return daynumber;
			}
		  
		return daynumber;
	}
	
	public static double getDistance(double x1, double y1, double x2, double y2)
	{
		double distance = 0;
		
		try
		{
			double R = 6371;
			double dLat = rad(x2-x1);
			double dLong = rad(y2-y1);
			double a = Math.sin(dLat / 2) * Math.sin(dLat) + Math.cos(rad(x1)) * Math.cos(rad(x2)) * Math.sin(dLong / 2) * Math.sin(dLong / 2);
			double c = 2 * Math.atan2(Math.sqrt(a),Math.sqrt(1 - a));
			distance = R * c;
			DecimalFormat twoDForm = new DecimalFormat("#.##");
			distance = Double.valueOf(twoDForm.format(distance));
		}
		catch(Exception e)
		{
			Log.i("Error", e.getMessage());
			return 0;
		}
		return distance;
	}	

	public static double rad(double x)
	{
		double radian=x*Math.PI/180;
		return  radian;
	}

	public static String getDetailTime(String date, String format)
	{
		String returnString = date;
		if(TextUtils.isEmpty(date) || TextUtils.isEmpty(format))
			return returnString;
		
		try 
		{
			//타겟시간
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			Date targetDate = dateFormat.parse(date);
			Calendar target = Calendar.getInstance();
			target.setTime (targetDate);
			
			//현재시간
			Date nowDate = new Date ();
			Calendar now = Calendar.getInstance();
			now.setTime (nowDate);
			
			//년도
			String year = getYearStr(now, target);
			
			//달,월
			String monthDay = getMonthStr(now, target);
			
			//시분
			String hm = getRHMStr(target);
			
			returnString = year + monthDay + hm;
			
		}catch (Exception e) 
		{
			e.printStackTrace(System.out);
		}			
		
		return returnString;
	}	
	
	static String getYearStr(Calendar now, Calendar target){
		String result = "";
		int diff = now.get(Calendar.YEAR) - target.get(Calendar.YEAR);
		if(diff > 0)
			result = target.get(Calendar.YEAR) + "年";
		
		return result;
	}
	
	static String getMonthStr(Calendar now, Calendar target){
		String result = "";
		long nowDays = getPastDay(now);
		long taDays = getPastDay(target);		
	    long diffDays = nowDays - taDays;		
	    
	    if(diffDays == 0)
	    	return result;
	    else{ 
	    	if(diffDays == 1)
	    		result = "昨天";
	    	else{ 
	    		now.add(Calendar.DAY_OF_MONTH, -now.get(Calendar.DAY_OF_WEEK) + 1);
	    		if(diffDays > 1 && target.after(now)){
	    			int dayOfWeek = target.get(Calendar.DAY_OF_WEEK);
	    			result = week_days[dayOfWeek];
	    		}else{
	    			result = String.valueOf(target.get(Calendar.MONTH) + 1) + "月" + target.get(Calendar.DAY_OF_MONTH) + "日";
	    		}
	    	}
	    }
	    
	    return result;
	}
	
	static String getRHMStr(Calendar target){
		String result = null;		
		int hour = target.get(Calendar.HOUR_OF_DAY);
		String range = time_range[(int)hour / 6];
		hour = hour % 12;
		String minute = String.format("%02d", target.get(Calendar.MINUTE)); 
		result = range + " " + hour + ":" + minute;
		return result;
	}
	
	public static String getNowStr(){
		return getNowStr(pattern);		
	}	
	
	public static String getNowStr(String pattern){
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(now.getTime());		
	}	
	
	public static long getNow(){
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		return now.getTimeInMillis();		
	}	
	
	public static long getTimeStr(String str){
		long daynumber=0;
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		Calendar dateCal = Calendar.getInstance();
		try{
			Date date = format.parse(str);
			dateCal.setTime(date);
			daynumber=dateCal.getTimeInMillis();
		}catch (Exception e) {
			Log.e("convert error", str+" can't covert to calendar.");
			return daynumber;
		}		  
		return daynumber;
	}
	
	public static String getYMDStr(String date, String format){
		String result = null;
		if(TextUtils.isEmpty(date) || TextUtils.isEmpty(format))
			return result;
		
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			SimpleDateFormat dateFormat2 = new SimpleDateFormat(pattern);
			Date targetDate = dateFormat2.parse(date);
			result = dateFormat.format(targetDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
			
		return result;
	}
	
	public static long getPastDay(Calendar cal){
		long result = 0;
		result = (cal.get(Calendar.YEAR) - 1 ) * 365 + cal.get(Calendar.DAY_OF_YEAR);
		return result;
	}
	
	/**
	 * check runtime memory
	 * @return
	 */
	public static boolean checkMemory(String title){
		long total = Runtime.getRuntime().totalMemory();
		Log.d(TAG, "total = " + total / 1000000 + " Mb");
		long free = Runtime.getRuntime().freeMemory();
		Log.d(TAG, title);
		Log.d(TAG, "free =  " + free / 1000 + " Kb");
		
		if(free < 100 * 1000){
			return false;
		}
		return true;
	}
	
	public static boolean excedMemory(String title, long need){
		long free = Runtime.getRuntime().freeMemory();
		Log.d(TAG, title);
		Log.d(TAG, "free =  " + free / 1000 + " Kb");
		
		if(free < need){
			System.gc();
			return true;
		}
		return false;
	}
	
	public static boolean isValid(String oval){
		return !TextUtils.isEmpty(oval) && !oval.equalsIgnoreCase("null");
	}
	
	public static boolean isValid(String oval, String symbol){
		return !TextUtils.isEmpty(oval) && !oval.equalsIgnoreCase("null") && !oval.equalsIgnoreCase(symbol);
	}
	
	public static boolean isValid(String oval, String symbol1, String symbol2){
		return !TextUtils.isEmpty(oval) && !oval.equalsIgnoreCase("null") 
				&& !oval.equalsIgnoreCase(symbol1)
				&& !oval.equalsIgnoreCase(symbol2);
	}
	
	public static double getValid(String oval, double dval){
		try {
			return isValid(oval) ? Double.parseDouble(oval) : dval ;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
		return dval;
	}
	
	public static double getValid(String oval, float dval){
		try {
			return isValid(oval) ? Float.parseFloat(oval) : dval ;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return dval;
	}
	
	public static int getValid(String oval, int dval){
		try {
			return isValid(oval) ? Integer.parseInt(oval) : dval ;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return dval;
	}
	
	public static boolean isCurrentActivity(Context context, Class<?> tarClass){
		boolean result = false;		
		if(context == null || tarClass == null)
			return result;
		
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		if(activityManager == null)
			return false;
		
        List<RunningTaskInfo> info = null;							        
        info = activityManager.getRunningTasks(1);
        if(info == null || info.get(0) == null || info.get(0).topActivity == null)
        	return result;        
		
		String curClsName = info.get(0).topActivity.getClassName();
		Log.e("silver", "curClsName=" + curClsName);
		
		return curClsName != null && curClsName.equals(tarClass.getName());
	}	
	
	public static boolean isInApp(Context context){
		boolean result = false;		
		if(context == null )
			return result;
		
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		if(activityManager == null)
			return false;
		
        List<RunningTaskInfo> info = null;							        
        info = activityManager.getRunningTasks(1);
        if(info == null || info.get(0) == null || info.get(0).topActivity == null)
        	return result;        
		
		String curClsName = info.get(0).topActivity.getClassName();
		Log.e("silver", "curClsName=" + curClsName);
		
		return curClsName != null && !curClsName.equals("com.android.launcher2.Launcher");
	}	
	
	public static String numberFormat(String pattern, double value){
		String result = null;
		DecimalFormat fm = new DecimalFormat(pattern);
		if(fm !=null)
			result = fm.format(value);
		
		return result;		
	}
	
	public static String getSemicolonStr(int value){
		String pattern = "###,###";
		return numberFormat(pattern, value);
	}

	public static String getUserCreatedDate(String date)
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(userpattern);
		SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat(pattern2);
		Date parsedate = null;
		try
		{
			parsedate = simpleDateFormat1.parse(date);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		if(parsedate != null)
			return simpleDateFormat.format(parsedate);
		else
			return "";
	}
	
//	public static ArrayList<SecondOpt> sortAsc (ArrayList<SecondOpt> input){
//		ArrayList<SecondOpt> result=new ArrayList<SecondOpt>();
//		SecondOpt temp=new SecondOpt();
//		for (int i=0;i<input.size();i++){
//
//			for (int j=i;j<input.size();j++){
//
//				if  (Long.parseLong(input.get(i).getId())>Long.parseLong(input.get(j).getId()))
//					{
//					 temp=input.get(i);
//					    input.set(i, input.get(j));
//					    input.set(j, temp);
//					}
//			 }
//
//		}
//
//			result=input;
//		return  result;
//	}
//
//	public static ArrayList<SecondOpt> sortDesc (ArrayList<SecondOpt> input){
//		ArrayList<SecondOpt> result=new ArrayList<SecondOpt>();
//		SecondOpt temp=new SecondOpt();
//		for (int i=0;i<input.size();i++){
//
//			for (int j=i;j<input.size();j++){
//
//				if  (Long.parseLong(input.get(i).getId())<Long.parseLong(input.get(j).getId()))
//					{
//					 temp=input.get(i);
//					    input.set(i, input.get(j));
//					    input.set(j, temp);
//					}
//			 }
//
//		}
//
//			result=input;
//		return  result;
//	}
//
//	public static ArrayList<ExtendAdItem> sort (ArrayList<ExtendAdItem> input,String sort){
//		ArrayList<ExtendAdItem> result=new ArrayList<ExtendAdItem>();
//
//		ExtendAdItem temp=new ExtendAdItem();
//		for (int i=0;i<input.size();i++){
//
//			for (int j=i;j<input.size();j++){
//					if("lowToHighPrice".equalsIgnoreCase(sort))
//					{
//						if  ((input.get(i).getPrice())>(input.get(j).getPrice()))
//						{
//						 temp=input.get(i);
//						    input.set(i, input.get(j));
//						    input.set(j, temp);
//						}
//					}
//					if("highToLowPrice".equalsIgnoreCase(sort) )
//					{
//						if  ((input.get(i).getPrice())<(input.get(j).getPrice()))
//						{
//						 temp=input.get(i);
//						    input.set(i, input.get(j));
//						    input.set(j, temp);
//						}
//					}
//					if(("desc").equalsIgnoreCase(sort)){
//						if  (input.get(i).getPosted_time().compareTo(input.get(j).getPosted_time())<0)
//						{
//						 temp=input.get(i);
//						    input.set(i, input.get(j));
//						    input.set(j, temp);
//						}
//
//					}
//					if(("time").equalsIgnoreCase(sort)){
//						if  (input.get(i).getUpdated_time().compareTo(input.get(j).getUpdated_time())<0)
//						{
//						 temp=input.get(i);
//						    input.set(i, input.get(j));
//						    input.set(j, temp);
//						}
//
//					}
//
//
//				}
//
//		}
//
//			result=input;
//		return  result;
//	}
	
	public static boolean checkCellPhone(String cellPhoneNr){
		String regx = "^[\\d-]{11,}";           
		Pattern pattern = Pattern.compile(regx);           
		Matcher matcher = pattern.matcher(cellPhoneNr);                  
		return matcher.matches();     
	}  
	
	public static void sendMail(Context context, String receiver, String title, String subject, String message){
		if(context == null)
			return;
		
		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		sharingIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{receiver});
		sharingIntent.putExtra(Intent.EXTRA_TITLE, title);
		sharingIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		sharingIntent.putExtra(Intent.EXTRA_TEXT, message);
		context.startActivity(sharingIntent);		
	}

//	public static void GoogleAnalyticsSend(String screenName, Activity act)
//	{
//		Tracker t = ((GlobalValue)act.getApplication()).getTracker(TrackerName.APP_TRACKER);
//		t.setScreenName(screenName);
//		t.send(new HitBuilders.AppViewBuilder().build());
//
//		FlurryAgent.logEvent(screenName);
//	}
//
//	public static void GoogleAnalyticsSend(String screenName, FragmentActivity act)
//	{
//		Tracker t = ((GlobalValue)act.getApplication()).getTracker(TrackerName.APP_TRACKER);
//		t.setScreenName(screenName);
//		t.send(new HitBuilders.AppViewBuilder().build());
//
//		FlurryAgent.logEvent(screenName);
//	}
	
	public static long getMilliTime(String str){
		long daynumber=0;
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		Calendar dateCal = Calendar.getInstance();
		try{
			Date date = format.parse(str);
			dateCal.setTime(date);
			daynumber=dateCal.getTimeInMillis();
		}catch (Exception e) {
			return daynumber;
		}		  
		return daynumber;
	}
	
	public static String getDiffmmss(String time1, String time2){
		String result = null;
		if(!isValid(time1) || !isValid(time2))
			return result;
		
		try {
			long diffTime = Math.abs(getMilliTime(time2) - getMilliTime(time1));			
			//result = convertTimeToStr(diffTime, "%02d:%02d:%02d"); 
			result = convertTimeToStr(diffTime, "%02d:%02d"); 
		} catch (Exception e) {
		}		
		return result;
	}
	
	public static long getDiffTimes(String time1, String time2){
		long result = 0;
		if(!isValid(time1) || !isValid(time2))
			return result;
		
		try {
			long diffTime = Math.abs(getMilliTime(time2) - getMilliTime(time1));
			return diffTime;
		} catch (Exception e) {
		}		
		return result;
	}
	
	@SuppressWarnings("unused")
	public static String convertTimeToStr(long time, String format) {
		String result = null;
		if (time < 0 || !isValid(format))
			return result;

		int sec = (int) (time / 1000);
		int hr = (int) (sec % (3600 * 24)) / 3600;
		int min = (int) (((int) sec % 3600) / 60);	
		sec = sec % 60;
        result = String.format(format, min, sec); 
		return result;
	}
	
	public static void recursiveRecycle(View root) {
        if (root == null)
            return;
        
        try {
        	root.setBackgroundDrawable(null);
       		root.setOnClickListener(null);
        } catch (Exception ignore) {}
 
        try {
        	root.setOnCreateContextMenuListener(null);
        } catch (Exception ignore) {}
 
        try {
        	root.setOnFocusChangeListener(null);
        } catch (Exception ignore) {}
 
        try {
        	root.setOnKeyListener(null);
        } catch (Exception ignore) {}
 
        try {
        	root.setOnLongClickListener(null);
        } catch (Exception ignore) {}
 
        try {
        	root.setOnClickListener(null);
        } catch (Exception ignore) {}
 
        try {
        	root.setTouchDelegate(null);
        } catch (Exception ignore) {}

        try{
	        if (root instanceof ViewGroup) {
	            ViewGroup group = (ViewGroup)root;
	            int count = group.getChildCount();
	            for (int i = 0; i < count; i++) {
	                recursiveRecycle(group.getChildAt(i));
	            }
	 
	            if (!(root instanceof AdapterView)) {
	                group.removeAllViews();
	            } 
	        }
        }catch (Exception e) {}
       
        try{
	        {
	        	if (root instanceof ImageView) {
	        		ImageView imageView = (ImageView)root;
	        		Drawable d = imageView.getDrawable();
	        		
	        		if (d != null && (d instanceof BitmapDrawable)) {
	        			Bitmap bm = ((BitmapDrawable) d).getBitmap();
	        			if(bm !=null && !bm.isRecycled())
	        				bm.recycle();
	        			d.setCallback(null);
	        		}
	        		imageView.setImageBitmap(null);
	        		imageView.setImageDrawable(null);
	        		imageView.setBackgroundDrawable(null);
	        	} 
	        }
        }catch(Exception ex){
        	
        }
        
        if (root instanceof WebView) {
            ((WebView)root).destroyDrawingCache();
            ((WebView)root).destroy();
        } 
        
        try {
        	root.setBackgroundDrawable(null);
        } catch (Exception ignore) {}
         
        try {
        	root.setAnimation(null);
        } catch (Exception ignore) {}
 
        try {
        	root.setContentDescription(null);
        } catch (Exception ignore) {}
 
        try {
        	root.setTag(null);
        } catch (Exception ignore) {}

        root = null;
 
        return;     
    }
	public static FileDescriptor getFD(Context content, Uri fileUri) {
		FileDescriptor fd;

		try {
			ParcelFileDescriptor pfd = content.getContentResolver().openFileDescriptor(fileUri, "r");
			if (pfd != null)
				fd = pfd.getFileDescriptor();
			else
				return null;
		} catch (FileNotFoundException e) {
			fd = null;
		}

		return fd;
	}

	public static String getRealPathFromURI(Context context, Uri uri) {
		String realPath;
		// SDK < API11
		if (Build.VERSION.SDK_INT < 11) {
			realPath = getRealPathFromURI_BelowAPI11(context, uri);
		}

		// SDK >= 11 && SDK < 19
		else if (Build.VERSION.SDK_INT < 19) {
			realPath = getRealPathFromURI_API11to18(context, uri);
		}

		// SDK > 19 (Android 4.4)
		else {
			realPath = getRealPathFromURI_API19(context, uri);
		}

		return realPath;
	}

	@SuppressLint("NewApi")
	public static String getRealPathFromURI_API19(final Context context, final Uri uri) {

		// check here to KITKAT or new version
		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/"
							+ split[1];
				}
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection,
						selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {

			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();

			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context
	 *            The context.
	 * @param uri
	 *            The Uri to query.
	 * @param selection
	 *            (Optional) Filter used in the query.
	 * @param selectionArgs
	 *            (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri,
									   String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection,
					selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri
				.getAuthority());
	}

	@SuppressLint("NewApi")
	public static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		String result = null;

		CursorLoader cursorLoader = new CursorLoader(
				context,
				contentUri, proj, null, null, null);
		Cursor cursor = cursorLoader.loadInBackground();

		if(cursor != null){
			int column_index =
					cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			result = cursor.getString(column_index);
		}
		return result;
	}

	public static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri){
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
		int column_index
				= cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
}