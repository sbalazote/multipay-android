package com.multipay.android.tasks;

import java.io.InputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class LoadImages extends AsyncTask<String[], String, Bitmap[]> {

	private String LOGCAT_TAG = "LoadImages";

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Bitmap[] doInBackground(String[]... args) {
		Bitmap[] thumbnailsArray = new Bitmap[args[0].length];
		for (int i = 0; i < args[0].length; i++) {
			try {
				thumbnailsArray[i] = BitmapFactory.decodeStream((InputStream) new URL(args[0][i]).getContent());
			} catch (Exception e) {
				Log.e(LOGCAT_TAG , e.getMessage(), e);
			}
		}
		return thumbnailsArray;
	}

	@Override
	protected void onPostExecute(Bitmap[] images) {
		super.onPostExecute(images);
		// TODO filtrar las que no son credit cards.
	}
}