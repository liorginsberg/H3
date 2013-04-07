package com.liorginsberg.homework3.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
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

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.liorginsberg.homework3.R;
import com.liorginsberg.homework3.utils.DisplayHelper;
import com.liorginsberg.homework3.utils.IntentIntegrator;
import com.liorginsberg.homework3.utils.IntentResult;

public class ParseActivity extends SherlockActivity {

	private static final String TAG = ParseActivity.class.getSimpleName();

	private IntentIntegrator integrator;

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
		setContentView(R.layout.activity_parse);

		overlay = (RelativeLayout) findViewById(R.id.overlay);

		integrator = new IntentIntegrator(this);

		textPanel = (LinearLayout) findViewById(R.id.textPanel);
		etText = (EditText) findViewById(R.id.etText);
		slideUp = new TranslateAnimation(0.0f, 0.0f, 0.0f, DisplayHelper.convertDpToPixel(-40, this));
		slideUp.setDuration(750);
		slideUp.setAnimationListener(collapseListener);
		slideDown = new TranslateAnimation(0.0f, 0.0f, DisplayHelper.convertDpToPixel(-40, this), 0.0f);
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
				getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

				int totalY = displayMetrics.heightPixels;
				int overlayY = overlay.getHeight();
				if (offsetY == 0) {
					offsetY = totalY - overlayY;
				}
				Log.i("GLOBAL WIDTH", "width in dp:" + DisplayHelper.convertPixelsToDp(displayMetrics.widthPixels, ParseActivity.this));
				Log.i("GLOBAL HEIGT", "height in dp:" + DisplayHelper.convertPixelsToDp(displayMetrics.heightPixels, ParseActivity.this));
			}
		};

		overlay.getViewTreeObserver().addOnGlobalLayoutListener(offsetYLayoutListener);
		mDetector = new GestureDetectorCompat(this, new MyGestureListener());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "onCreateOptionsMenu(Menu menu)");

		getSupportMenuInflater().inflate(R.menu.parse_activity_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "onOptionsItemSelected(MenuItem item)");
		Log.d(TAG, "item selected: " + item.getTitle());

		switch (item.getItemId()) {
		case R.id.scanCode:
			integrator.initiateScan();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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

			int finalYPosition = (int) (Math.floor(event.getY() - offsetY - DisplayHelper.convertDpToPixel(56, getApplicationContext())));

			ImageView image = new ImageView(ParseActivity.this);

			RelativeLayout.LayoutParams vp = new RelativeLayout.LayoutParams((int) DisplayHelper.convertDpToPixel(56, ParseActivity.this),
					(int) DisplayHelper.convertDpToPixel(56, ParseActivity.this));

			vp.setMargins((int) (Math.floor(event.getX() - DisplayHelper.convertDpToPixel(28, getApplicationContext()))), finalYPosition,
					0, 0);

			Log.i("GLOBAL WIDTH", "x pos in dp:" + DisplayHelper.convertPixelsToDp(event.getX(), ParseActivity.this));
			Log.i("GLOBAL WIDTH", "y pos in dp:" + DisplayHelper.convertPixelsToDp(finalYPosition, ParseActivity.this));
	
			
			image.setLayoutParams(vp);

			image.setImageResource(R.drawable.marker);

			image.setOnClickListener(myClickListener);

			AnimationSet bounceMarkerAnimationSet = new AnimationSet(true);
			bounceMarkerAnimationSet.setInterpolator(new BounceInterpolator());
			bounceMarkerAnimationSet.setDuration(1000);
			TranslateAnimation trans = new TranslateAnimation(0.0f, 0.0f, (DisplayHelper.convertDpToPixel(-finalYPosition,
					getApplicationContext())), 0.0f);
			ScaleAnimation scale = new ScaleAnimation(4, 1, 4, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			bounceMarkerAnimationSet.addAnimation(trans);
			bounceMarkerAnimationSet.addAnimation(scale);

			image.startAnimation(bounceMarkerAnimationSet);

			overlay.addView(image);
			super.onLongPress(event);
		}

		@Override
		public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {

			return true;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case IntentIntegrator.REQUEST_CODE:
				// special result catcher provided by the ZXing framework
				IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
				if (scanResult != null) {
					String result = scanResult.getContents();
					Log.i(TAG, "Scan result: " + result);
					if (result.equals("shenkar_qr_code_04.1")) {
						ImageView image = new ImageView(ParseActivity.this);

						RelativeLayout.LayoutParams vp = new RelativeLayout.LayoutParams((int) DisplayHelper.convertDpToPixel(56,
								ParseActivity.this), (int) DisplayHelper.convertDpToPixel(56, ParseActivity.this));

						DisplayMetrics dm = new DisplayMetrics();
						getWindowManager().getDefaultDisplay().getMetrics(dm);
						
						Log.i("DISPLAYX", "expected 360, tahles is:" + DisplayHelper.convertPixelsToDp(dm.widthPixels,getApplicationContext()));
						vp.setMargins(DisplayHelper.getPixelPositionByPrecentage(dm, DisplayHelper.PRSENTAGE_X, 0.2833f, 0),
								DisplayHelper.getPixelPositionByPrecentage(dm, DisplayHelper.PRSENTAGE_Y, 0.01266f, offsetY), 0, 0);
						image.setLayoutParams(vp);

						image.setImageResource(R.drawable.marker);

						overlay.addView(image);

					}
				} else {
					Log.i(TAG, "Scan result return bad or canceled");
				}
				break;
			default:
				break;
			}
		} else {
			Log.e(TAG, "The result from other activity was canceled");
		}
	}
}

// @Override
// protected void onCreate(Bundle savedInstanceState) {
// super.onCreate(savedInstanceState);
// setContentView(R.layout.activity_parse);
//
// ParseQuery parseQuery = new ParseQuery("Marker");
// parseQuery.whereExists("owner");
// parseQuery.findInBackground(new FindCallback() {
//
// @Override
// public void done(List<ParseObject> objects, ParseException e) {
// if(e == null) {
// for (ParseObject parseObject : objects) {
// Log.i(TAG, "" + parseObject.get("msg"));
// }
// }
// }
// });
//
//
//
// btnNoSignup = (Button) findViewById(R.id.btnNoneedSignup);
// btnNeedSignup = (Button) findViewById(R.id.btnNeedSignup);
//
// btnNeedSignup.setOnClickListener(new OnClickListener() {
//
// @Override
// public void onClick(View v) {
// ParseUser currentUser = ParseUser.getCurrentUser();
// if (currentUser != null) {
// Toast.makeText(ParseActivity.this, "username: " + currentUser.getUsername(),
// Toast.LENGTH_SHORT).show();
// ParseObject marker = new ParseObject("Marker");
// marker.put("marker_id", "shenkar_qr_code_04.1");
// marker.put("owner", currentUser.getUsername());
// marker.put("msg", "was here");
// marker.saveInBackground(new SaveCallback() {
//
// @Override
// public void done(ParseException e) {
// if (e != null) {
// Toast.makeText(ParseActivity.this, "Saved", Toast.LENGTH_SHORT).show();
// }
// }
// });
//
// } else {
//
// ParseObject marker = new ParseObject("Marker");
// marker.put("marker_id", "shenkar_qr_code_04.2");
// marker.put("owner", "");
// marker.put("msg", "");
//
// marker.saveInBackground(new SaveCallback() {
//
// @Override
// public void done(ParseException e) {
// if (e != null) {
// Toast.makeText(ParseActivity.this, "Saved", Toast.LENGTH_SHORT).show();
// }
// }
// });
// Log.d(TAG, "currentUser is null, about to start login activity");
// Intent intent = new Intent(ParseActivity.this, LoginActivity.class);
// startActivity(intent);
// }
// }
// });
//
// btnNoSignup.setOnClickListener(new OnClickListener() {
//
// @Override
// public void onClick(View v) {
// ParseUser.logOut();
//
// }
// });
// }

