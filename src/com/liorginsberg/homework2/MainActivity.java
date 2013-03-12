package com.liorginsberg.homework2;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {

	private RelativeLayout overlay;
	private GestureDetectorCompat mDetector;
	private int offsetY = 0;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		overlay = (RelativeLayout) findViewById(R.id.overlay);
		
		final OnGlobalLayoutListener offsetYLayoutListener = new OnGlobalLayoutListener(){

	        @Override
	        public void onGlobalLayout() {

	        	DisplayMetrics displayMetrics = new DisplayMetrics();
	    		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
	    		

	    		int totalY = displayMetrics.heightPixels;
	    		int overlayY = overlay.getHeight();
	    		if(offsetY == 0) { 
	    			offsetY = totalY - overlayY;  		
	    		}
	        }
		};
		
		
		overlay.getViewTreeObserver().addOnGlobalLayoutListener(offsetYLayoutListener);
		mDetector = new GestureDetectorCompat(this, new MyGestureListener());
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return this.mDetector.onTouchEvent(event);
	}

	class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
		private static final String DEBUG_TAG = "Gestures";

		@Override
		public boolean onDown(MotionEvent event) {
			// Get the Y OFfset

			Log.d(DEBUG_TAG, "x: " + event.getX() + " y: "
					+ (event.getY() - offsetY));
			return true;
		}

		@Override
		public void onLongPress(MotionEvent event) {

			ImageView image = new ImageView(MainActivity.this);
			RelativeLayout.LayoutParams vp = new RelativeLayout.LayoutParams(
					(int) DisplayHelper.convertDpToPixel(56, MainActivity.this), (int) DisplayHelper.convertDpToPixel(56, MainActivity.this));
			
			vp.setMargins((int) (Math.floor(event.getX() - DisplayHelper.convertDpToPixel(28, getApplicationContext()))),
					(int) (Math.floor(event.getY() - offsetY - DisplayHelper.convertDpToPixel(56, getApplicationContext()))), 0, 0);
			image.setLayoutParams(vp);

			image.setImageResource(R.drawable.marker);

			overlay.addView(image);
			super.onLongPress(event);
		}

		@Override
		public boolean onFling(MotionEvent event1, MotionEvent event2,
				float velocityX, float velocityY) {
			Log.d(DEBUG_TAG,
					"onFling: " + event1.toString() + event2.toString());
			return true;
		}
	}
}
