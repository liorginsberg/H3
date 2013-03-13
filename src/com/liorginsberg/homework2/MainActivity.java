package com.liorginsberg.homework2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.GestureDetectorCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

	private boolean isTextShow = false;

	public static final int REQUEST_EDIT_MARKER = 1001;

	private RelativeLayout overlay;
	private GestureDetectorCompat mDetector;
	private int offsetY = 0;

	private List<Marker> markers;

	private View temp = null;

	private LinearLayout textPanel;
	private TranslateAnimation slideUp;
	private TranslateAnimation slideDown;

	private ImageButton saveButton;

	private TranslateAnimation slideUpAndDown;

	private EditText etText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		textPanel = (LinearLayout) findViewById(R.id.textPanel);
		etText = (EditText)findViewById(R.id.etText);
		slideUp = new TranslateAnimation(0.0f, 0.0f, 0.0f,
				DisplayHelper.convertDpToPixel(-40, this));
		slideUp.setDuration(750);
		slideUp.setAnimationListener(collapseListener);
		slideDown = new TranslateAnimation(0.0f, 0.0f,
				DisplayHelper.convertDpToPixel(-40, this), 0.0f);
		slideDown.setDuration(750);
		

		saveButton = (ImageButton) findViewById(R.id.save);
		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				View mark = (View)textPanel.getTag();
				mark.setTag(etText.getText().toString());
				textPanel.startAnimation(slideUp);

			}
		});

		markers = new ArrayList<Marker>();

		overlay = (RelativeLayout) findViewById(R.id.overlay);

		final OnGlobalLayoutListener offsetYLayoutListener = new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {

				DisplayMetrics displayMetrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(
						displayMetrics);

				int totalY = displayMetrics.heightPixels;
				int overlayY = overlay.getHeight();
				if (offsetY == 0) {
					offsetY = totalY - overlayY;
				}

			}
		};

		overlay.getViewTreeObserver().addOnGlobalLayoutListener(
				offsetYLayoutListener);
		mDetector = new GestureDetectorCompat(this, new MyGestureListener());
	}

	Animation.AnimationListener collapseListener = new Animation.AnimationListener() {
		public void onAnimationEnd(Animation animation) {
			textPanel.setVisibility(View.GONE);
		}

		public void onAnimationRepeat(Animation animation) {
			// not needed
		}

		public void onAnimationStart(Animation animation) {
			// not needed
		}
	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return this.mDetector.onTouchEvent(event);
	}

	class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
		private static final String DEBUG_TAG = "Gestures";

		OnClickListener myClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				textPanel.setVisibility(View.VISIBLE);
				textPanel.setTag(v);
				String fromMark = (String)v.getTag();
				if(fromMark != null) {
					etText.setText(fromMark);
				} else {
					etText.setText("");
				}
				
				textPanel.startAnimation(slideDown);				
			}

		};

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
					(int) DisplayHelper.convertDpToPixel(56, MainActivity.this),
					(int) DisplayHelper.convertDpToPixel(56, MainActivity.this));

			vp.setMargins(
					(int) (Math.floor(event.getX()
							- DisplayHelper.convertDpToPixel(28,
									getApplicationContext()))),
					(int) (Math.floor(event.getY()
							- offsetY
							- DisplayHelper.convertDpToPixel(56,
									getApplicationContext()))), 0, 0);
			image.setLayoutParams(vp);

			image.setImageResource(R.drawable.marker);

			
			image.setOnClickListener(myClickListener);

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