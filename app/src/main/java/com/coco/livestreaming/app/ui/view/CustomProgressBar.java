package com.coco.livestreaming.app.ui.view;

import com.coco.livestreaming.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

public class CustomProgressBar extends ProgressBar {
	private int mFirstColor;
	private int mSecondColor;
	private int mCircleWidth;
	private int mProgress;
	@SuppressWarnings("unused")
	private int mSpeed;
	private Paint mPaint;
	
	public CustomProgressBar(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);		
	}

	/**
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public CustomProgressBar(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomProgressBar, defStyle, 0);
		int n = a.getIndexCount();
		
		for (int i = 0; i < n; i++)
		{
			int attr = a.getIndex(i);
			switch (attr)
			{
			case R.styleable.CustomProgressBar_firstColor:
				mFirstColor = a.getColor(attr, Color.WHITE);
				break;
			case R.styleable.CustomProgressBar_secondColor:
				mSecondColor = a.getColor(attr, Color.RED);
				break;
			case R.styleable.CustomProgressBar_circleWidth:
				mCircleWidth = a.getDimensionPixelSize(attr, (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 20, getResources().getDisplayMetrics()));
				break;
			case R.styleable.CustomProgressBar_speed:
				mSpeed = a.getInt(attr, 2000);		//default 20
				break;
			}
		}

		a.recycle();
		mPaint = new Paint();
	}
	
	public void setProgress(int progress)
	{
		mProgress = (int)Math.round(progress * 3.6);		
		postInvalidate();
	}
	
	public CustomProgressBar(Context context)
	{
		this(context, null);
	}
	
	protected void onDraw(Canvas canvas)
	{
		int centre = getWidth() / 2;				//circle center
		int radius = centre - mCircleWidth / 2;		//circle radius
		mPaint.setStrokeWidth(mCircleWidth);
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(mFirstColor);
		canvas.drawCircle(centre, centre, radius, mPaint);
		
		RectF oval = new RectF(centre - radius, centre - radius, centre + radius, centre + radius);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(mSecondColor);
		canvas.drawArc(oval, -90, mProgress, false, mPaint);	
	}
}
