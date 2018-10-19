package com.nostra13.universalimageloader.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.coco.livestreaming.app.util.Constants;
//import com.hyy.HCameraCropTest.camera.HighlightView;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class BitmapLoader {
	public static int mWidth; // mobile window width
	public static int mHeight;
	private static int lineWidth = 5;
	public static void init(Context context){		
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		display.getMetrics(displayMetrics);
		mWidth = displayMetrics.widthPixels;
		mHeight = displayMetrics.heightPixels;	
		
		if(mWidth > mHeight){
			int temp = mWidth;
			mWidth = mHeight;
			mHeight = temp;			
		}
	}
	
	public static Bitmap getBitmapFullWidth(String path){
		Bitmap bmp = getBitmap(path, Constants.UPLOAD_FILE_SIZE);
		return imageResizeWithNoCrop(bmp, mWidth, mWidth);
	}
	
	public static Bitmap getBitmap(String path, int width, int height) {
		if(TextUtils.isEmpty(path))
			return null;		
		int _width = 0, _height = 0;
		BitmapFactory.Options option = new BitmapFactory.Options();
		option.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, option);
		
		if(width == 0 && height == 0){
			//specially(chat)			
			Point pt = getLimitSize(option.outWidth, option.outHeight);
			_width = pt.x;
			_height = pt.y;
			
		}else{
			//generllay
			_width = width; _height = height;
		}
		
		int scale = 1;
		int max_image_size = (_width >= _height)? _width : _height;
		if (option.outHeight > max_image_size || option.outWidth > max_image_size) {
            scale = (int)Math.pow(2, (int)Math.round(Math.log(max_image_size/(double)Math.max(option.outHeight, option.outWidth)) / Math.log(0.5)));
        }
		option.inSampleSize = scale;
		option.inJustDecodeBounds = false;
		
		return BitmapFactory.decodeFile(path, option);		
	}
	
	public static Point getLimitSize(int width, int height)
	{
		Point pt = new Point();
		int _width = width, _height = height;
		
		if(_width >= _height){
			_width = (width > 175)? 175 : width;
			float v = (float)height / (float)width;
			_height = (int)(v * _width);
		}else{
			_height = (height > 175)? 175 : height;
			float v = (float)width / (float)height;
			_width = (int)(v * _height);
		}
		pt.x = _width; 
		pt.y = _height;
		return pt;
	}
	
	/**
	 * 비트맵을 요구하는 용량까지 압축하기 위한 압축률결정
	 * @param image
	 * @return
	 */
	public static int getCompRate(Bitmap image, long crop_sample_size) {  
	    
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	    image.compress(CompressFormat.JPEG, 100, baos);
	    int options = 100;  
	    while ( baos.toByteArray().length > crop_sample_size) {         
	        baos.reset();  
	        image.compress(CompressFormat.JPEG, options, baos);
	        options -= 10;
	    }
	    
	    return options > 30 ? options : 30;  
	}
	
	public static long getViewMemSize(View view){
		if(view == null)
			return 0;
		
		return view.getWidth() * view.getHeight() * 4;
	}
	
	public static long getBmpSize(Bitmap bmp){
		if(bmp == null)
			return 0;
		
		return bmp.getWidth() * bmp.getHeight() * 4;
	}
	
	/**
	 * 해당이미지파일을 지정된크기로 스케일하기 위한 값얻기
	 * @param filename  파일명
	 * @param unit_size 최종크기
	 * @return 스케일할 값
	 */
	public static int getImageCompressSize(String filename, long unit_size)
	{
		if(TextUtils.isEmpty(filename))
			return -1;
		File file = new File(filename);		
		long len = file.length();

		if (len <= 0)
			return -1;

		int result = (int) (len / unit_size);
		result = (int) ((Math.log10(len / unit_size) / Math.log10(2))); 
		
		if(result > 5)
			result = 5;
		if(result <= 0)
			result = 1;
		
		return result;
	}
	
	public static Bitmap getBitmap(String file, long compSize){
		Bitmap result = null;
		if(TextUtils.isEmpty(file) || compSize <= 0 )
			return result;
		
		int scale = getImageCompressSize(file, compSize);
		if(scale <= 0)
			return result;
		
		Bitmap tmp = getScaleBitmap(file, scale);
		if(tmp == null)
			return result;
		
		result = tmp;		
		
		return result;
	}
	
	public static Bitmap getScaleBitmap(String file, int scale){
		if(TextUtils.isEmpty(file))
			return null;		
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = scale;
		return BitmapFactory.decodeFile(file, options);		
	}
	
	public static Bitmap imageResizeWithNoCrop(Bitmap bmp, int width, int height)
	{
		if(bmp == null || width < 1 || height < 1)
			return null;
		
		float revisionRatio = 0;
		
		try {
			float scaleX = (float)mWidth / (float)width;
			float scaleY = (float)mHeight / (float)height;		
			revisionRatio = Math.min(scaleX, scaleY);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(revisionRatio == 0)
			return null;

		float _w = width * revisionRatio;
		float _h = 0;

		_w = width * revisionRatio;
		_h = height * revisionRatio;

		return Bitmap.createScaledBitmap(bmp, (int) _w, (int) _h, true);		
	}	
	
	/**
	 * compress and return image for upload 
	 */
	public static String getCompressSaveFileName(String fileName){
		if(TextUtils.isEmpty(fileName))
			return null;
		
		Bitmap bmp = BitmapLoader.getBitmap(fileName, Constants.UPLOAD_FILE_SIZE);
		if(bmp == null)
			return null;
		return saveImageFile(bmp);
	}

	// Bitmap Save to File
	public static String saveImageFile(Bitmap bm)
	{
		if(bm == null)
			return null;
		long time = System.currentTimeMillis();
		String result = time + ".jpg";

		String imgPath = Constants.IMG_FULL_PATH; //android.os.Environment.getExternalStorageDirectory() + "/funnylive/";
		File file = new File(imgPath);
		if(! file.exists()){
			file.mkdirs();
		}
		result = imgPath + result;
		try{
			file = new File(result);
			if(file.exists()){
				file.delete();
				file.createNewFile();
			}
			OutputStream out = new FileOutputStream(file);
			int options = BitmapLoader.getCompRate(bm, Constants.UPLOAD_FILE_SIZE);
			bm.compress(CompressFormat.JPEG, options, out);
			bm.recycle();
			out.close();
		}catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return result;
	}
	
	public static BitmapFactory.Options getImgSize(String fileName){
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(fileName, options);
			return options;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}

	public static int[] getBitmpReqSize(BitmapFactory.Options options, boolean isCrop){
		int[] result = new int[2];
		if(options == null)
			return result;
		
		int width = options.outWidth;
		int height = options.outHeight;	
		
		float revisionRatio = 0;
		
		if(isCrop){
			int min = Math.min(width, height);
			//revisionRatio = (float)(mWidth - HighlightView.lineWidth * 2) / min;
			revisionRatio = (float)(mWidth - lineWidth * 2) / min;
		}else{
			float scaleX = (float)mWidth / (float)width;
			float scaleY = (float)mHeight / (float)height;		
			revisionRatio = Math.min(scaleX, scaleY);
		}
		
		if(revisionRatio == 0)
			return result;

		float _w = 0;
		float _h = 0;

		_w = width * revisionRatio;
		_h = height * revisionRatio;
		
		result[0] = (int) _w;
		result[1] = (int) _h;
		return result;
	}
	
	public static Bitmap getScaleBitmapFromLoader(ImageView view, String imgUri, String uri, boolean isCrop)
	{
		Bitmap bmp = null;
		if(view == null || (TextUtils.isEmpty(imgUri) && TextUtils.isEmpty(uri)))
			return bmp;		
		
		BitmapFactory.Options options = getImgSize(imgUri);
		int[] reqSize = getBitmpReqSize(options, isCrop);
		int w = reqSize[0];
		int h = reqSize[1];

		try {
			bmp = ImageLoader.getInstance().decodeBitmap(view, imgUri, uri, w, h);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return bmp;
	}
	
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) {
	
	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;
	
	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / inSampleSize) > reqHeight
	                && (halfWidth / inSampleSize) > reqWidth) {
	            inSampleSize *= 2;
	        }
	    }
	
	    return inSampleSize;
	}	
	
	//when need image with direct sampling size
	public static Bitmap decodeSampledBitmap(String imgUri, boolean isCrop) {
		Bitmap bmp = null;
		if(TextUtils.isEmpty(imgUri))				
			return bmp;
		
		BitmapFactory.Options options = getImgSize(imgUri);
		int[] reqSize = getBitmpReqSize(options, isCrop);
		int w = reqSize[0];
		int h = reqSize[1];
		
		if(w * h == 0)
			return bmp;

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, w, h);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(imgUri, options);
	}
	
	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeResource(res, resId, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeResource(res, resId, options);
	}	
	
}