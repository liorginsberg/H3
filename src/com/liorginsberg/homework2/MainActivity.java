package com.liorginsberg.homework2;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {

	private RelativeLayout overlay;
	private GestureDetectorCompat mDetector;
	private int offsetY = 0;

	private LinearLayout textPanel;
	private TranslateAnimation slideUp;
	private TranslateAnimation slideDown;

	private ImageButton saveButton;

	private EditText etText;
	
	Animation.AnimationListener collapseListener = new Animation.AnimationListener() {
		
		public void onAnimationEnd(Animation animation) {
			textPanel.setVisibility(View.GONE);
		}

		public void onAnimationRepeat(Animation animation) {
		
		}

		public void onAnimationStart(Animation animation) {
		
		}
	};
	
	OnClickListener myClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			ImageView marker = (ImageView) v;
			ImageView lastMarker = (ImageView) textPanel.getTag();
			if (lastMarker != null) {
				lastMarker.setImageResource(R.drawable.marker);
			}
			marker.setImageResource(R.drawable.marker_select);
			textPanel.setVisibility(View.VISIBLE);
			textPanel.setTag(v);
			String fromMark = (String) v.getTag();
			if (fromMark != null) {
				etText.setText(fromMark);
			} else {
				etText.setText("");
			}

			textPanel.startAnimation(slideDown);
		}
	};
	private ImageButton removeButton;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		overlay = (RelativeLayout) findViewById(R.id.overlay);

		textPanel = (LinearLayout) findViewById(R.id.textPanel);
		etText = (EditText) findViewById(R.id.etText);
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
				ImageView marker = (ImageView) textPanel.getTag();
				marker.setImageResource(R.drawable.marker);
				marker.setTag(etText.getText().toString());
				textPanel.startAnimation(slideUp);
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(etText.getWindowToken(), 0);

			}
		});
		
		removeButton = (ImageButton) findViewById(R.id.remove);
		removeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ImageView marker = (ImageView) textPanel.getTag();
				overlay.removeView(marker);
				textPanel.startAnimation(slideUp);
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(etText.getWindowToken(), 0);

			}
		});


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


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return this.mDetector.onTouchEvent(event);
	}

	class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
		
		@Override
		public boolean onDown(MotionEvent event) {
			return true;
		}

		@Override
		public void onLongPress(MotionEvent event) {

			int finalYPosition = (int) (Math.floor(event.getY()
					- offsetY
					- DisplayHelper.convertDpToPixel(56,
							getApplicationContext())));

			ImageView image = new ImageView(MainActivity.this);

			RelativeLayout.LayoutParams vp = new RelativeLayout.LayoutParams(
					(int) DisplayHelper.convertDpToPixel(56, MainActivity.this),
					(int) DisplayHelper.convertDpToPixel(56, MainActivity.this));

			vp.setMargins(
					(int) (Math.floor(event.getX()
							- DisplayHelper.convertDpToPixel(28,
									getApplicationContext()))), finalYPosition,
					0, 0);

			image.setLayoutParams(vp);

			image.setImageResource(R.drawable.marker);

			image.setOnClickListener(myClickListener);

			AnimationSet bounceMarkerAnimationSet = new AnimationSet(true);
			bounceMarkerAnimationSet.setInterpolator(new BounceInterpolator());
			bounceMarkerAnimationSet.setDuration(1000);
			TranslateAnimation trans = new TranslateAnimation(0.0f, 0.0f,
					(DisplayHelper.convertDpToPixel(-finalYPosition,
							getApplicationContext())), 0.0f);
			ScaleAnimation scale = new ScaleAnimation(4, 1, 4, 1,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			bounceMarkerAnimationSet.addAnimation(trans);
			bounceMarkerAnimationSet.addAnimation(scale);

			image.startAnimation(bounceMarkerAnimationSet);
			
			overlay.addView(image);
			super.onLongPress(event);
		}

		@Override
		public boolean onFling(MotionEvent event1, MotionEvent event2,
				float velocityX, float velocityY) {
			
			return true;
		}
	}

}