/**
 * Copyright (C) 2013 Orthogonal Labs, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hipmob.gifanimationdrawable;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;

/**
 * <p>Creates an AnimationDrawable from a GIF image.</p>
 *
 * @author Femi Omojola <femi@hipmob.com>
 */
public abstract class GifAnimationDrawable extends AnimationDrawable {
	private boolean decoded;
	private int height, width;
	
	private GifDecoder mGifDecoder;
	private Bitmap mTmpBitmap;
	
	public GifAnimationDrawable(File f) throws IOException {
		this(f, false);
	}

	public GifAnimationDrawable(InputStream is) throws IOException {
		this(is, false);
	}

	public GifAnimationDrawable(File f, boolean inline) throws IOException {
		this(new BufferedInputStream(new FileInputStream(f), 32768), inline);
	}

	public GifAnimationDrawable(InputStream is, boolean inline) throws IOException {
		super();
		
		InputStream bis = is;
		if(!BufferedInputStream.class.isInstance(bis))
			bis = new BufferedInputStream(is, 32768);
		
		decoded = false;
		mGifDecoder = new GifDecoder();
		mGifDecoder.read(bis);
		mTmpBitmap = mGifDecoder.getFrame(0);
		android.util.Log.v("GifAnimationDrawable", "===>Lead frame: ["+width+"x"+height+"; "+mGifDecoder.getDelay(0)+";"+mGifDecoder.getLoopCount()+"]");
		
		height = mTmpBitmap.getHeight();
		width = mTmpBitmap.getWidth();
		addFrame(new BitmapDrawable(mTmpBitmap), mGifDecoder.getDelay(0));
		
		setOneShot(mGifDecoder.getLoopCount() != 0);
		setVisible(true, true);
		
		if (inline) {
			loader.run();
		} else {
			Thread loaderThread = new Thread(loader);
			loaderThread.start();
		}
	}
	
	private Runnable loader = new Runnable() {
		@Override
		public void run() {
			mGifDecoder.complete();
			int i, n = mGifDecoder.getFrameCount(), delay;
			int totalDelay = 0;
			for (i = 1; i < n; i++) {
				mTmpBitmap = mGifDecoder.getFrame(i);
				delay = mGifDecoder.getDelay(i);
				addFrame(new BitmapDrawable(mTmpBitmap), delay);
				
				totalDelay += delay;
			}
			
			decoded = true;
			mGifDecoder = null;
			
			onGotTotalTime(totalDelay);
		}
	};
	
	public boolean isDecoded() {
		return decoded;
	}

	public int getMinimumHeight() {
		return height;
	}
	
	public int getMinimumWidth() {
		return width;
	}
	
	public int getIntrinsicHeight() {
		return height;
	}
	
	public int getIntrinsicWidth() { 
		return width;
	}
	
	public abstract void onGotTotalTime(int totalTime);
}
