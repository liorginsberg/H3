package com.liorginsberg.homework3.activities;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.liorginsberg.homework3.InHereApplication;
import com.liorginsberg.homework3.R;
import com.liorginsberg.homework3.utils.DisplayHelper;
import com.liorginsberg.homework3.utils.IntentIntegrator;
import com.liorginsberg.homework3.utils.IntentResult;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ParseActivity extends SherlockActivity {

	// Loging identifier
	private static final String TAG = ParseActivity.class.getSimpleName();

	// Markers positions in percentages relative to the markers overlay view
	public static final float[][] markersPositions = { { 0.28194445f, 0.12657033f }, // shenkar_qr_code_04.1
			{ 0.50555557f, 0.12620424f }, // shenkar_qr_code_04.2
			{ 0.22361112f, 0.21579961f }, // shenkar_qr_code_04.3
			{ 0.36666667f, 0.30057803f }, // shenkar_qr_code_04.4
			{ 0.24583334f, 0.32851636f }, // shenkar_qr_code_04.5
			{ 0.25000000f, 0.5019268f }, // shenkar_qr_code_04.6
			{ 0.25555557f, 0.5963391f }, // shenkar_qr_code_04.7
			{ 0.25416666f, 0.81021196f }, // shenkar_qr_code_04.8
			{ 0.36111111f, 0.86223507f }, // shenkar_qr_code_04.9
			{ 0.24583334f, 0.88728327f } }; // shenkar_qr_code_04.10

	// Hold's reference to integrate with the ZXing QR framework
	private IntentIntegrator integrator;

	// layer for showing markers
	private RelativeLayout overlay;

	// layer for interaction with the user
	private RelativeLayout interactionLayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parse);

		overlay = (RelativeLayout) findViewById(R.id.overlay);

		interactionLayer = (RelativeLayout) findViewById(R.id.interactionLayer);

		integrator = new IntentIntegrator(this);

	}

	@Override
	protected void onResume() {
		super.onResume();

		ParseQuery parseQuery = new ParseQuery("Marker");
		parseQuery.whereExists("owner");
		parseQuery.findInBackground(new FindCallback() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {
					for (ParseObject parseObject : objects) {
						if (!parseObject.getString("owner").equals("")) {
							String markerID = parseObject.getString("marker_id");
							addMarkerToOverlay(markerID);
						}
					}// TODO error checking and handeling
				}
			}
		});

		// TODO start checking for new markers
	}

	@Override
	protected void onPause() {
		super.onPause();
		// TODO stop checking for new markers
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
		case R.id.menuLogout:
			ParseUser.logOut();
			return true;
		default:
			return super.onOptionsItemSelected(item);
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
					if (result.startsWith("shenkar_qr_code_04.")) {
						final String markerID = result;
						ParseQuery query = new ParseQuery("Marker");
						query.whereEqualTo("marker_id", markerID);

						query.findInBackground(new FindCallback() {

							@Override
							public void done(List<ParseObject> objects, ParseException e) {
								if (e == null) {
									final ParseObject currentMarkerObject = objects.get(0);
									String owner = currentMarkerObject.getString("owner");
									Log.i(TAG, "owner? = " + owner);
									if (owner == null || owner.equals("")) {

										LayoutInflater li = LayoutInflater.from(getApplicationContext());
										final LinearLayout ll = (LinearLayout) li.inflate(R.layout.prompt_marker, null);
										LinearLayout.LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
												LayoutParams.MATCH_PARENT);
										ll.setLayoutParams(lp);

										final EditText etMarkerMessage = (EditText) ll.findViewById(R.id.etMarkerMessage);

										ll.findViewById(R.id.btnSaveMarker).setOnClickListener(new OnClickListener() {

											@Override
											public void onClick(View v) {
												final String msg = etMarkerMessage.getText().toString();
												if (msg.equals("")) {
													startEmptyEditTextAnimation(etMarkerMessage);
												} else {
													// Taking The user to a
													// login singup route
													ParseUser user = ParseUser.getCurrentUser();
													if (user == null) {
														Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
														Bundle bundle = new Bundle();
														bundle.putString("markerID", markerID);
														bundle.putString("msg", msg);
														loginIntent.putExtras(bundle);
														startActivityForResult(loginIntent, InHereApplication.REQUEST_LOGIN);
													} else {
														currentMarkerObject.put("owner", user.get("usermame"));
														currentMarkerObject.put("msg", msg);

														currentMarkerObject.saveInBackground(new SaveCallback() {

															@Override
															public void done(ParseException e) {
																// show the new
																// marker on map
															}
														});

													}
												}

											}
										});

										ll.findViewById(R.id.btnCancelMarker).setOnClickListener(new OnClickListener() {

											@Override
											public void onClick(View v) {
												interactionLayer.removeView(ll);
											}
										});

										interactionLayer.addView(ll);

									} else {
										// show nice message with details of the
										// owner
										LayoutInflater li = LayoutInflater.from(getApplicationContext());
										final LinearLayout ll = (LinearLayout) li.inflate(R.layout.show_marker_details, null);
										LinearLayout.LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
												LayoutParams.MATCH_PARENT);
										ll.setLayoutParams(lp);
										ParseUser currentUser = ParseUser.getCurrentUser();

										// configure the message with specific
										// fields for owner
										if (currentUser != null && currentUser.getUsername().equals(owner)) {

											Log.i(TAG, "The markers owner is the current user");

											TextView tvOwner = (TextView) ll.findViewById(R.id.tvOwner);
											tvOwner.setText("You");
											final EditText etMsg = (EditText) ll.findViewById(R.id.etOwnerMsg);
											etMsg.setVisibility(View.VISIBLE);
											final Button btnEditMsg = (Button) ll.findViewById(R.id.btnEditMsg);
											btnEditMsg.setVisibility(View.VISIBLE);
											btnEditMsg.setOnClickListener(new OnClickListener() {

												@Override
												public void onClick(View v) {
													if (etMsg.getText().toString().equals("")) {
														startEmptyEditTextAnimation(etMsg);
													} else {
														currentMarkerObject.put("msg", etMsg.getText().toString());
														currentMarkerObject.saveInBackground(new SaveCallback() {

															@Override
															public void done(ParseException e) {
																Toast.makeText(getApplicationContext(), "Your massage hase been updated.",
																		Toast.LENGTH_SHORT).show();
															}
														});
													}
												}
											});

										} else {
											TextView tvOwner = (TextView) ll.findViewById(R.id.tvOwner);
											tvOwner.setText(currentMarkerObject.getString("owner"));
											TextView tvMsg = (TextView) ll.findViewById(R.id.tvMsg);
											tvMsg.setVisibility(View.VISIBLE);
											tvMsg.setText((String) currentMarkerObject.getString("msg"));
										}
										interactionLayer.addView(ll);
									}

								} else {
									Log.e(TAG, "ERROR: " + e.getMessage());
								}
							}
						});

					} else {
						// TODO make a nice messsage in the interactionLayer
						Toast.makeText(this, "InHere is not handaling this kind of content", Toast.LENGTH_SHORT).show();
					}
				} else {
					Log.i(TAG, "Scan result return bad or canceled");
				}
				break;
			case InHereApplication.REQUEST_LOGIN:
				if (data.hasExtra("markerID") && data.hasExtra("msg")) {
					final String markerID = data.getStringExtra("markerID");
					final String msg = data.getStringExtra("msg");

					ParseQuery query = new ParseQuery("Marker");
					query.whereEqualTo("marker_id", markerID);
					query.getFirstInBackground(new GetCallback() {

						@Override
						public void done(ParseObject object, ParseException e) {
							object.put("owner", ParseUser.getCurrentUser().getUsername());
							object.put("msg", msg);
							object.saveInBackground(new SaveCallback() {

								@Override
								public void done(ParseException e) {
									Toast.makeText(
											getApplicationContext(),
											"got back after succesfull update for the markers owner: "
													+ ParseUser.getCurrentUser().getUsername() + " of the marker: " + markerID
													+ " with the message: " + msg, Toast.LENGTH_LONG).show();
								}
							});
						}
					});
				}
				break;
			default:
				break;
			}
		} else {
			Log.e(TAG, "The result from other activity was canceled");
		}
	}

	private void addMarkerToOverlay(String markerID) {

		String[] extractIndex = markerID.split("\\.");
		int i = Integer.parseInt(extractIndex[1]) - 1;

		ImageView image = new ImageView(ParseActivity.this);

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams((int) DisplayHelper.convertDpToPixel(ParseActivity.this, 56),
				(int) DisplayHelper.convertDpToPixel(ParseActivity.this, 56));
		lp.setMargins((int) (overlay.getWidth() * markersPositions[i][0] - DisplayHelper.convertDpToPixel(this, 28)),
				(int) (overlay.getHeight() * markersPositions[i][1] - DisplayHelper.convertDpToPixel(this, 56)), 0, 0);

		image.setLayoutParams(lp);

		image.setImageResource(R.drawable.marker);

		overlay.addView(image);
	}

	private void startEmptyEditTextAnimation(EditText editText) {
		AnimatorSet set = new AnimatorSet();
		set.playTogether(ObjectAnimator.ofFloat(editText, "scaleX", 1, 1.5f, 1), ObjectAnimator.ofFloat(editText, "scaleY", 1, 1.5f, 1));
		set.setDuration(500).start();
	}
}
