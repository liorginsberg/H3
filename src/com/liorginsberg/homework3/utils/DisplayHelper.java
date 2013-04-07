package com.liorginsberg.homework3.utils;

import com.liorginsberg.homework3.activities.ParseActivity;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class DisplayHelper {
	
	public static final int PRSENTAGE_X = 0;
	public static final int PRSENTAGE_Y = 1;

	public static float convertDpToPixel(float dp, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return px;
	}

	public static float convertPixelsToDp(float px, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return dp;
	}


	public static int getPixelPositionByPrecentage(DisplayMetrics displayMetrics, int axis , float presentage, int offsetY) {
		int pos = -1;
		
		if(axis == PRSENTAGE_X) {
			pos = (int) Math.floor(displayMetrics.widthPixels * presentage);
		} else if(axis == PRSENTAGE_Y) {
			
			pos = (int) Math.floor((displayMetrics.heightPixels-offsetY) * presentage);
		}		
		return pos;
	}
}