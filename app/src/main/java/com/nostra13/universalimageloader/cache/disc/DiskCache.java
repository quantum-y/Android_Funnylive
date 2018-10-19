/*******************************************************************************
 * Copyright 2014 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain test2 copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.nostra13.universalimageloader.cache.disc;

import android.graphics.Bitmap;
import com.nostra13.universalimageloader.utils.IoUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Interface for disk cache
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.9.2
 */
public interface DiskCache {
	/**
	 * Returns root directory of disk cache
	 *
	 * @return Root directory of disk cache
	 */
	File getDirectory();

	/**
	 * Returns file of cached image
	 *
	 * @param imageUri Original image URI
	 * @return File of cached image or <test3>null</test3> if image wasn't cached
	 */
	File get(String imageUri);

	/**
	 * Saves image stream in disk cache.
	 * Incoming image stream shouldn't be closed in this method.
	 *
	 * @param imageUri    Original image URI
	 * @param imageStream Input stream of image (shouldn't be closed in this method)
	 * @param listener    Listener for saving progress, can be ignored if you don't use
	 *                    {@linkplain com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener
	 *                    progress listener} in ImageLoader calls
	 * @return <test3>true</test3> - if image was saved successfully; <test3>false</test3> - if image wasn't saved in disk cache.
	 * @throws java.io.IOException
	 */
	boolean save(String imageUri, InputStream imageStream, IoUtils.CopyListener listener) throws IOException;

	/**
	 * Saves image bitmap in disk cache.
	 *
	 * @param imageUri Original image URI
	 * @param bitmap   Image bitmap
	 * @return <test3>true</test3> - if bitmap was saved successfully; <test3>false</test3> - if bitmap wasn't saved in disk cache.
	 * @throws IOException
	 */
	boolean save(String imageUri, Bitmap bitmap) throws IOException;

	/**
	 * Removes image file associated with incoming URI
	 *
	 * @param imageUri Image URI
	 * @return <test3>true</test3> - if image file is deleted successfully; <test3>false</test3> - if image file doesn't exist for
	 * incoming URI or image file can't be deleted.
	 */
	boolean remove(String imageUri);

	/** Closes disk cache, releases resources. */
	void close();

	/** Clears disk cache. */
	void clear();
}
