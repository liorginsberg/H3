package com.liorginsberg.homework3.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class DisplayHelper {
	
	public static final int PRSENTAGE_X = 0;
	public static final int PRSENTAGE_Y = 1;

	public static float convertDpToPixel(Context context, float dp) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return px;
	}

	public static float convertPixelsToDp(Context context, float px) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return dp;
	}


	public static int getPixelPositionByPrecentage(Context context, DisplayMetrics displayMetrics, int axis , float presentage, int offsetY) {
		int pos = -1;
		
		if(axis == PRSENTAGE_X) {
			pos = (int) Math.floor((displayMetrics.widthPixels * presentage) - DisplayHelper.convertDpToPixel(context, 28));
			
		} else if(axis == PRSENTAGE_Y) {
			
			pos = (int) Math.floor((displayMetrics.heightPixels-offsetY) * presentage);
		}		
		return pos;
	}
}