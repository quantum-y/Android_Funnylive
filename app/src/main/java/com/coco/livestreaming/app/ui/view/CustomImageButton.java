package com.coco.livestreaming.app.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;

public class CustomImageButton extends ImageButton {
	Paint paint = new Paint();
	boolean selected = false;

	public CustomImageButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CustomImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomImageButton(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		AlphaAnimation animation = new AlphaAnimation(selected ? 0.5f : 1.0f, selected ? 0.5f : 1.0f);
		animation.setDuration(0);
		animation.setFillAfter(true);
		this.startAnimation(animation);
		//this.setAlpha(selected ? 0.6f : 1.0f);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		this.eventButton(event);
		return super.onTouchEvent(event);
	}
	
	void eventButton(MotionEvent event)
	{
		final int action = event.getAction();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			selected = true;
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			selected = false;
			break;
		}
		invalidate();		
	}
}