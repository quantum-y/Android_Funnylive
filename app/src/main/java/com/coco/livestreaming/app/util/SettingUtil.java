package com.coco.livestreaming.app.util;

import com.coco.livestreaming.R;
import com.coco.livestreaming.app.ui.view.CustomProgressBar;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingUtil {
	
	static String encodeSymbol = "_";
	static int prev_progress = 0;	
	static SettingUtil instance = null;
	static DisplayImageOptions options;
	
	
	public static SettingUtil getInstance(){
		if(instance == null){
			instance = new SettingUtil();			
			initOption();
		}
		return instance;
	}
	
	public static void initOption(){
//		options = new DisplayImageOptions.Builder()
//		.cacheInMemory(false)
//		.cacheOnDisk(true)
//		.considerExifParams(true)
//		.bitmapConfig(Bitmap.Config.RGB_565)
//		.build();
	}

	public static void setText(TextView txt, String value) {
		if (txt == null)
			return;
		txt.setText(value);
	}
	
	public static void setShowView(View view, boolean value) {
		if (view == null)
			return;
		view.setVisibility(value ? View.VISIBLE : View.GONE);
	}
	
	public void setText(View view, int viewId, String value) {
		TextView txt = (TextView)view.findViewById(viewId);
		if (txt == null)
			return;
		txt.setText(value);
	}
	
	public void setText(View view, int viewId, int resId) {
		TextView txt = (TextView)view.findViewById(viewId);
		if (txt == null)
			return;
		txt.setText(resId);
	}
	
	public void setText(Activity activity, int viewId, int resId) {
		TextView txt = (TextView)activity.findViewById(viewId);
		if (txt == null)
			return;
		txt.setText(resId);
	}
	
	public void setText(Activity activity, int viewId, String value) {
		TextView txt = (TextView)activity.findViewById(viewId);
		if (txt == null)
			return;
		if(!TextUtils.isEmpty(value))
			value = value.trim();
		
		txt.setText(value);
	}
	
//	public static void setText(Activity activity, int viewId, String value, String dvalue) {
//		if(!ShareUtil.isValid(value))
//			value = dvalue;
//		else
//			value = value.trim();
//
//		TextView txt = (TextView)activity.findViewById(viewId);
//		if (txt == null)
//			return;
//		txt.setText(value);
//	}
//
//	public static void setText(Activity activity, int viewId, String value, String symbol, String dvalue) {
//		if(!ShareUtil.isValid(value, symbol))
//			value = dvalue;
//		else
//			value = value.trim();
//
//		TextView txt = (TextView)activity.findViewById(viewId);
//		if (txt == null)
//			return;
//		txt.setText(value);
//	}
	
	public void setText(Window window, int viewId, String value) {
		TextView txt = (TextView)window.findViewById(viewId);
		if (txt == null)
			return;
		txt.setText(value);
	}

	public void setText(Window window, int viewId, int resId) {
		TextView txt = (TextView)window.findViewById(viewId);
		if (txt == null)
			return;
		txt.setText(resId);
	}

	public void setText(Window window, int viewId, int resId, boolean visible) {
		TextView txt = (TextView)window.findViewById(viewId);
		if (txt == null)
			return;
		txt.setText(resId);
		txt.setVisibility(visible ? View.VISIBLE : View.GONE);
	}
	
	public void setText(View view, int viewId, String value, boolean isVisible) {
		TextView text = (TextView)view.findViewById(viewId);
		if (text == null)
			return;

		text.setText(value);
		text.setVisibility(isVisible ? View.VISIBLE : View.GONE);
	}
	
	public void setText(View view, int viewId, int resId, boolean isVisible) {
		TextView text = (TextView)view.findViewById(viewId);
		if (text == null)
			return;

		text.setText(resId);
		text.setVisibility(isVisible ? View.VISIBLE : View.GONE);
	}
	
	public void setText(Window window, int viewId, String value, boolean isVisible) {
		TextView text = (TextView)window.findViewById(viewId);
		if (text == null)
			return;

		text.setText(value);
		text.setVisibility(isVisible ? View.VISIBLE : View.GONE);
	}
	
	public void setText(Activity activity, int viewId, String value, boolean isVisible) {
		TextView text = (TextView)activity.findViewById(viewId);
		if (text == null)
			return;

		text.setText(value);
		text.setVisibility(isVisible ? View.VISIBLE : View.GONE);
	}
	
	public void setTxtColor(View view, int viewId, int color){
		TextView text = (TextView)view.findViewById(viewId);
		if (text == null)
			return;

		text.setTextColor(color);	
	}
	
	public void setTxtColor(Window window, int viewId, int color){
		TextView text = (TextView)window.findViewById(viewId);
		if (text == null)
			return;

		text.setTextColor(color);	
	}
	
	public void setTxtColor(Activity activity, int viewId, int color){
		TextView text = (TextView)activity.findViewById(viewId);
		if (text == null)
			return;

		text.setTextColor(color);	
	}

	public void setText(View view, int viewId, Spannable value, boolean isVisible) {

		TextView text = (TextView)view.findViewById(viewId);
		if (text == null)
			return;

		/*
		 * if(isVisible) view.setText(Html.fromHtml(value, new
		 * ImageGetter(m_commonAct), null)); else
		 */
		text.setText(value);
		text.setVisibility(isVisible ? View.VISIBLE : View.GONE);
	}
	
	public void setText(Activity activity, int viewId, Spannable value, boolean isVisible) {

		TextView text = (TextView)activity.findViewById(viewId);
		if (text == null)
			return;

		/*
		 * if(isVisible) view.setText(Html.fromHtml(value, new
		 * ImageGetter(m_commonAct), null)); else
		 */
		text.setText(value);
		text.setVisibility(isVisible ? View.VISIBLE : View.GONE);
	}
	
	public void setText(Window window, int viewId, Spannable value, boolean isVisible) {

		TextView text = (TextView)window.findViewById(viewId);
		if (text == null)
			return;

		/*
		 * if(isVisible) view.setText(Html.fromHtml(value, new
		 * ImageGetter(m_commonAct), null)); else
		 */
		text.setText(value);
		text.setVisibility(isVisible ? View.VISIBLE : View.GONE);
	}

	public void setTextStrike(View view, int viewId, String value) {

		TextView text = (TextView)view.findViewById(viewId);
		if (text == null)
			return;
		text.setText(value);
		text.setPaintFlags(text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
	}
	
	public void setTextStrike(Window window, int viewId, String value) {

		TextView text = (TextView)window.findViewById(viewId);
		if (text == null)
			return;
		text.setText(value);
		text.setPaintFlags(text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
	}
	
	public void setTextStrike(Activity activity, int viewId, String value) {

		TextView text = (TextView)activity.findViewById(viewId);
		if (text == null)
			return;
		text.setText(value);
		text.setPaintFlags(text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
	}

	public void setTextSize(View view, int viewId, int size){
		TextView text = (TextView)view.findViewById(viewId);
		if (text == null)
			return;
		text.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);		
	}
	
	public void setTextSize(Window window, int viewId, int size){
		TextView text = (TextView)window.findViewById(viewId);
		if (text == null)
			return;
		text.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);		
	}
	
	public void setTextSize(Activity activity, int viewId, int size){
		TextView text = (TextView)activity.findViewById(viewId);
		if (text == null)
			return;
		text.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);		
	}
	
	public void setTextRes(View view, int viewId, int resId) {

		TextView text = (TextView)view.findViewById(viewId);
		if (text == null)
			return;
		text.setText(resId);
	}

	public void setImg(View view, int viewId, int resId) {

		ImageView image = (ImageView)view.findViewById(viewId);
		if (image == null)
			return;
		image.setBackgroundResource(resId);
	}
	
	public void setImg(Activity activity, int viewId, int resId) {

		ImageView image = (ImageView)activity.findViewById(viewId);
		if (image == null)
			return;
		image.setBackgroundResource(resId);
	}

	public void setImgSrc(View view, int viewId, int resId) {

		ImageView image = (ImageView)view.findViewById(viewId);
		if (image == null)
			return;
		image.setImageResource(resId);
	}
	
	public void setImgSrc(Window window, int viewId, int resId) {

		ImageView image = (ImageView)window.findViewById(viewId);
		if (image == null)
			return;
		image.setImageResource(resId);
	}
	
	public void setImgSrc(Activity activity, int viewId, int resId) {

		ImageView image = (ImageView)activity.findViewById(viewId);
		if (image == null)
			return;
		image.setImageResource(resId);
	}

	public void setTxtViewEnable(View view, int viewId, boolean enable) {

		TextView text = (TextView)view.findViewById(viewId);
		if (text == null)
			return;
		text.setVisibility(enable ? View.VISIBLE : View.GONE);
	}
	
	public void setImgViewEnable(View view, int viewId, boolean enable) {

		ImageView image = (ImageView)view.findViewById(viewId);
		if (image == null)
			return;
		image.setVisibility(enable ? View.VISIBLE : View.GONE);
	}
	
	public void setImgViewEnable(Window window, int viewId, boolean enable) {

		ImageView image = (ImageView)window.findViewById(viewId);
		if (image == null)
			return;
		image.setVisibility(enable ? View.VISIBLE : View.GONE);
	}

	public void setViewEnable(View view, int viewId, boolean enable) {

		View lay = view.findViewById(viewId);
		if (lay == null)
			return;
		lay.setVisibility(enable ? View.VISIBLE : View.GONE);
	}
	
	public void setViewEnable(Window view, int viewId, boolean enable) {

		View lay = view.findViewById(viewId);
		if (lay == null)
			return;
		lay.setVisibility(enable ? View.VISIBLE : View.GONE);
	}
	
	public void setViewShow(View view, int viewId, boolean enable) {

		View lay = view.findViewById(viewId);
		if (lay == null)
			return;
		lay.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
	}
	
	public void setViewShow(Window view, int viewId, boolean enable) {

		View lay = view.findViewById(viewId);
		if (lay == null)
			return;
		lay.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
	}
	
	public void setViewEnable(Activity activity, int viewId, boolean enable) {

		View lay = activity.findViewById(viewId);
		if (lay == null)
			return;
		lay.setVisibility(enable ? View.VISIBLE : View.GONE);
	}
	
	public void setViewShow(Activity activity, int viewId, boolean enable) {

		View lay = activity.findViewById(viewId);
		if (lay == null)
			return;
		lay.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
	}	

	public void setListner(View view, int viewId, View.OnClickListener listner) {
		View v = view.findViewById(viewId);
		if (v != null)
			v.setOnClickListener(listner);
	}
	
	public void setLongListner(View view, int viewId, View.OnLongClickListener listner) {
		View v = view.findViewById(viewId);
		if (v != null)
			v.setOnLongClickListener(listner);
	}
	
	public void setListner(Window window, int viewId, View.OnClickListener listner) {
		View v = window.findViewById(viewId);
		if (v != null)
			v.setOnClickListener(listner);
	}
	
	public void setListner(Activity activity, int viewId, View.OnClickListener listner) {
		View v = activity.findViewById(viewId);
		if (v != null)
			v.setOnClickListener(listner);
	}

	public void setListner(View view, int viewId, View.OnLongClickListener listner) {
		View v = view.findViewById(viewId);
		if (v != null)
			v.setOnLongClickListener(listner);
	}
	
	public void setListner(Activity activity, int viewId, View.OnTouchListener listner) {
		View v = activity.findViewById(viewId);
		if (v != null)
			v.setOnTouchListener(listner);
	}
	
	public void setListner(View view, int viewId, View.OnTouchListener listner) {
		View v = view.findViewById(viewId);
		if (v != null)
			v.setOnTouchListener(listner);
	}
	
	public void setTimeText(View view, int viewId, String value, String formatPattern) {
//		TextView text = (TextView)view.findViewById(viewId);
//		if (text == null)
//			return;
//		if(formatPattern == null)
//			formatPattern = ShareUtil.pattern;
//
//		value = ShareUtil.getDetailTime(value, formatPattern);
//		text.setText(value);
	}

	public void setTimeText(View view, int viewId, String value) {
		setTimeText(view, viewId, value, null);
	}

	public static void setImage(final CustomProgressBar _spinner, ImageView imgView, String _path) {
		setImage(_spinner, imgView, _path, R.drawable.no_image);
	}

	public static void setImage(final CustomProgressBar _spinner, ImageView imgView, String _path, int no_image) {
		if(_spinner !=null)
			_spinner.setVisibility(View.VISIBLE);

		if(imgView == null)
			return;

		final int no_image_res = no_image > 0 ? no_image : R.drawable.no_image;
		if(!ShareUtil.isValid(_path)){
			imgView.setBackgroundResource(no_image_res);
		}

		if(options == null)
			initOption();

		if (_path.startsWith("/"))
			if (_path.startsWith("//"))
				_path = "http:" + _path;
			else
				_path = "file://" + _path;
		ImageLoadingListener temp = new SimpleImageLoadingListener() {
			final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				if(_spinner != null)
					_spinner.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view,	FailReason failReason) {
				view.setBackgroundResource(no_image_res);
				if(_spinner != null)
					_spinner.setVisibility(View.GONE);

				return;
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				if(_spinner != null)
					_spinner.setVisibility(View.GONE);

				prev_progress = 0;
				if (loadedImage != null) {
					ImageView imageView = (ImageView) view;
					imageView.setImageBitmap(loadedImage);
					boolean firstDisplay = !displayedImages.contains(imageUri);
					if (firstDisplay) {
						FadeInBitmapDisplayer.animate(imageView, 500);
						displayedImages.add(imageUri);
					}
				}
			}
		};
		ImageLoadingProgressListener temp1 = _spinner !=null ? new ImageLoadingProgressListener(){

			@Override
			public void onProgressUpdate(String imageUri, View view, int current, int total) {
				if(Math.abs(current - prev_progress) > 20) {
					if(total > 0)
						_spinner.setProgress(Math.round(100.0f * current / total));
					prev_progress = current;
				}
			}
		} : null;

		ImageLoader.getInstance().displayImage(_path, imgView, options, temp, temp1);
	}


	public static ArrayList<Integer> getNumberList(String str, String pattern){
		if(TextUtils.isEmpty(str))
			return null;
		ArrayList<Integer> result = new ArrayList<Integer>();
		String[] tmp = str.split(pattern);
		for (String member : tmp) {
			try {
				Integer intVal = Integer.parseInt(member);
				result.add(intVal);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	public static String getNeedStr(String rs, String[] array){
		String val = "";
		ArrayList<Integer> list = SettingUtil.getNumberList(rs, ",");
		if(list == null)
			return val;
		for (Integer id : list) {
			if(id < 0 || id >= array.length)
				continue;
			String tmp = array[id];
			if(!TextUtils.isEmpty(val))
				val += "、 ";
			val += tmp;
		}
		return val;
	}
	
	public static boolean isSDCARDMOUNTED() {
		try {
			String status = Environment.getExternalStorageState();
			return status!=null &&  (status.equals(Environment.MEDIA_MOUNTED) || status.equals(Environment.MEDIA_SHARED));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean isExistInLocal(String file){
		if(! isSDCARDMOUNTED())
			return false;
		
		File f = new File(file);
		try {
			return f.exists();
		} catch (Exception e) {			
		}		
		
		return false;
	}
	
	public static void SaveBitmapToFileCache(Bitmap bitmap, String strFilePath) {
        if(isExistInLocal(strFilePath))
        	return;
        
        File fileCacheItem = new File(strFilePath);
        OutputStream out = null;
 
        try
        {
            fileCacheItem.createNewFile();
            out = new FileOutputStream(fileCacheItem); 
            bitmap.compress(CompressFormat.JPEG, 100, out);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
            	if(out !=null)
                out.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
	}
	
	public static String getAppVersionInfo(Context context)
	{
		PackageManager pm = context.getPackageManager();//context为当前Activity上下文 
		PackageInfo pi = null;
		String version = "";
		try {
			pi = pm.getPackageInfo(context.getPackageName(), 0);
			version = pi.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return version;
	}
	
	public static int getDate(String DateString){
		int daynumber=0;
		  SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
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
	
	public static String getDirName(String dirFullPath){
		if(TextUtils.isEmpty(dirFullPath))
			return null;
		String[] splite = dirFullPath.split("/");		
		return splite[splite.length - 1];
	}
	
	public static String getFileNameWithExt(String path){
		if(TextUtils.isEmpty(path))
			return null;
		try {
			int start = path.lastIndexOf("/") + 1;
			int end = path.length();
			return path.substring(start, end);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}	
	
	public static String getFileName(String path){
		if(TextUtils.isEmpty(path))
			return null;
		try {
			int start = path.lastIndexOf("/") + 1;
			int end = path.lastIndexOf(".");
			return path.substring(start, end);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}	
	
	public static String getFileExt(String path){
		if(TextUtils.isEmpty(path))
			return null;
		try {
			int start = path.lastIndexOf(".") + 1;
			int end = path.length();
			return path.substring(start, end);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}	
	
	public static void goScanMediaFile(Context context, String file){
		if(context == null || TextUtils.isEmpty(file))
			return;
		
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"+ file)));		
	}
	
	@SuppressWarnings("resource")
	public boolean copyFile(String oldPath, String newPath) {  
		boolean result = false;
		try {   
            int bytesum = 0;   
            int byteread = 0;   
            if (!SettingUtil.isExistInLocal(oldPath)) 
         	   return result;
           
            InputStream inStream = new FileInputStream(oldPath); 
			FileOutputStream fs = new FileOutputStream(newPath);   
            byte[] buffer = new byte[1024];   
            while ( (byteread = inStream.read(buffer)) != -1) {   
               bytesum += byteread; 
               System.out.println(bytesum);   
               fs.write(buffer, 0, byteread);   
            }   
            inStream.close();
            result = true;
        }   
        catch (Exception e) {   
           e.printStackTrace();
           result = false;
        } 
       
        return result;
	} 
	
	public static int[] getWindowWidthHeight(Context context)
	{
		int[] widthheight = new int[]{0, 0};
		if(context != null)
		{
			try{
				DisplayMetrics displaymetrics = new DisplayMetrics();
				WindowManager wmanager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
				wmanager.getDefaultDisplay().getMetrics(displaymetrics);
				int width = displaymetrics.widthPixels;
				int height = displaymetrics.heightPixels;
				widthheight[0] = width;
				widthheight[1] = height;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return widthheight;
	}
	
//	public static String getLocalPath(String fileName){
//		return getLocalFolder() + "/" + fileName;
//	}
//
//	public static String getLocalFolder(){
//		String path = Environment.getExternalStorageDirectory().toString() + "/" + Constants.LOCAL_FILE_PATH;
//		File dir = new File(path);
//		if(!dir.exists())
//			dir.mkdir();
//		return path;
//	}
	
//	public static String getSaveFolder(){
//		String path = Environment.getExternalStorageDirectory().toString() + "/" + Constants.SAVE_FILE_PATH;
//		File dir = new File(path);
//		if(!dir.exists())
//			dir.mkdir();
//		return path;
//	}
//
//	public static String getErrorLogPath(){
//		return Environment.getExternalStorageDirectory().toString() + "/" + Constants.LOG_FILE_NAME;
//	}
//
//	public static String getServerPath(String fileName){
//		return Constants.Url.UPLOAD_FOLDER + "/" + fileName;
//	}
//
//	public static void animateView(final View view)
//	{
//		view.setVisibility(View.VISIBLE);
//		FlipAnimation anim = new FlipAnimation(90, 0, 0, view.getHeight() / 2);
//		anim.setDuration(250);
//		anim.setFillAfter(true);
//		anim.setInterpolator(new AccelerateDecelerateInterpolator());
//		anim.setAnimationListener(new Animation.AnimationListener() {
//			@Override
//			public void onAnimationStart(Animation animation) {
//			}
//			@Override
//			public void onAnimationEnd(Animation animation) {
//				view.clearAnimation();
//			}
//			@Override
//			public void onAnimationRepeat(Animation animation) {
//			}
//		});
//		view.startAnimation(anim);
//	}
	
	public void expand(final View v) {
	    v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	    final int targetHeight = v.getMeasuredHeight();

	    v.getLayoutParams().height = 0;
	    v.setVisibility(View.VISIBLE);
	    Animation a = new Animation()
	    {
	        @Override
	        protected void applyTransformation(float interpolatedTime, Transformation t) {
	            v.getLayoutParams().height = interpolatedTime == 1
	                    ? LayoutParams.WRAP_CONTENT
	                    : (int)(targetHeight * interpolatedTime);
	            v.requestLayout();
	        }

	        @Override
	        public boolean willChangeBounds() {
	            return true;
	        }
	    };

	    // 1dp/ms
	    a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
	    v.startAnimation(a);
	}

	public void collapse(final View v) {
	    final int initialHeight = v.getMeasuredHeight();

	    Animation a = new Animation()
	    {
	        @Override
	        protected void applyTransformation(float interpolatedTime, Transformation t) {
	            if(interpolatedTime == 1){
	                v.setVisibility(View.GONE);
	            }else{
	                v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
	                v.requestLayout();
	            }
	        }

	        @Override
	        public boolean willChangeBounds() {
	            return true;
	        }
	    };

	    // 1dp/ms
	    a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
	    v.startAnimation(a);
	}
	
	public static String arrayToStr(String[] array){
		if(array == null || array.length == 0)
			return "";
		
		String result = "";
		for (int i = 0; i < array.length; i++){
			result += array[i];
			if(i < array.length - 1)
				result += ",";
		}

		return result;
	}
	
//	public static String[] strToArray(String str){
//		if(! ShareUtil.isValid(str))
//			return null;
//
//		String result[] = str.split(",");
//		return result;
//	}
	
	public static List<String> strsToList(String[] path){
		if(path == null)
			return null;
		
		List<String> paths = new ArrayList<String>();
		for (int i = 0; i < path.length; i++) {
			paths.add(path[i]);
		}		
		
		return paths;
	}
	
	public static String[] arrayToStrs(ArrayList<String> path){
		if(path == null || path.size() == 0)
			return null;
		
		String[] paths = new String[path.size()];
		for (int i = 0; i < path.size(); i++) {
			paths[i] = path.get(i);
		}		
		
		return paths;
	}
	
//	public static ArrayList<String> listToArray(ArrayList<CustomGallery> path){
//		if(path == null)
//			return null;
//
//		ArrayList<String> paths = new ArrayList<String>();
//		for (int i = 0; i < path.size(); i++) {
//			if(path.get(i) == null)
//				continue;
//			paths.add(path.get(i).sdcardPath);
//		}
//
//		return paths;
//	}
	
	public int convertDpToPx(int dp, Context context){
		if(context == null || dp < 0)
			return 0;
		
		return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,  
                dp, context.getResources().getDisplayMetrics()); 
	}
	
	public int getTabIndex(int[] array, int resId) {
		int index = -1;
		if (array == null)
			return index;

		try {
			for (int i = 0; i < array.length; i++) {
				if (array[i] == resId) {
					index = i;
					return index;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return index;
	}	
	
	public static int getIndex(String[] array, String str){
		int index = -1;
		if (array == null || TextUtils.isEmpty(str))
			return index;
		
		for (int i = 0; i < array.length; i++) {
			if (str.equals(array[i])) {
				index = i;
				return index;
			}			
		}
		
		return index;		
	}
	
//	public static String removeStr(String parent, String str){
//		String result = null;
//		if (TextUtils.isEmpty(parent) || TextUtils.isEmpty(str))
//			return result;
//
//		String[] array = strToArray(parent);
//		if (array == null || array.length == 0)
//			return result;
//
//		result = "";
//		for (int i = 0; i < array.length; i++) {
//			if (!str.equalsIgnoreCase(array[i])) {
//				result += array[i] + ",";
//			}
//		}
//
//		if(result.length() > 0)
//			result = result.substring(0, result.length() - 1);
//
//		return result;
//	}
	
//	class ViewHolder{
//		TextView title, price;
//		ImageView adImage;
//		CustomProgressBar mSpinner;
//	}
//
//	public View getAdListView(final ExtendAdItem item, ViewGroup parent, LayoutInflater inflater) {
//		if(item == null || inflater == null)
//			return null;
//
//		final ViewHolder holder;
//
//		final View itemAdRoot = inflater.inflate(R.layout.item_ad, parent, false);
//		holder = new ViewHolder();
//		holder.title = (TextView) itemAdRoot.findViewById(R.id.ad_title);
//		holder.price = (TextView) itemAdRoot.findViewById(R.id.ad_price);
//		holder.adImage = (ImageView) itemAdRoot.findViewById(R.id.ad_image);
//		holder.mSpinner = (CustomProgressBar) itemAdRoot.findViewById(R.id.image_loading_progress);
//		itemAdRoot.setTag(holder);
//
//		if(holder.title !=null)
//			holder.title.setText(item.getTitle());
//
//		if(holder.price !=null)
//			holder.price.setText(ShareUtil.getSemicolonStr((item.getPrice())));
//
//		if(holder.adImage !=null)
//			SettingUtil.setImage(holder.mSpinner, holder.adImage, item.getPicture());
//
//		return itemAdRoot;
//	}
	
	public static String encodeFileName(String path){
		String result = null;
		if(TextUtils.isEmpty(path))
			return result;
		
		String name = getFileName(path);
		String ext = getFileExt(path);
		if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(ext))
			result = name + encodeSymbol + ext;
		
		return result;
	}

	public static String decodeFileName(String path){
		String result = null;
		if(TextUtils.isEmpty(path))
			return result;
		
		try {
			int start = path.lastIndexOf(encodeSymbol) + 1;
			int end = path.length();
			String name = path.substring(0, start - 1);
			String ext = path.substring(start, end);
			if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(ext))
				result = name + "." + ext;

		} catch (Exception e) {
			//e.printStackTrace();
		}
		
		return result;
	}
	
	public static String convertLocalPath(String path){
		String result = path;
		
		if(isExistInLocal(path))
			return path;
		
//		String fileName = getFileNameWithExt(path);
//		if(!TextUtils.isEmpty(fileName)){
//			fileName = encodeFileName(fileName);
//			if(!TextUtils.isEmpty(fileName)){
//				fileName = getLocalPath(fileName);
//				if(isExistInLocal(fileName))
//					result = fileName;
//			}
//		}
		
		return result;
	}
	
//	public static String getLoaderStorePath(String uri){
//		if(isExistInLocal(uri))
//			return uri;
//
//		return ImageLoader.getInstance().getFilePath(uri);
//	}
//
//
//	public void clearMemoryForImageLoader()
//	{
//		ImageLoader.getInstance().clearMemoryCache();
//	}

}