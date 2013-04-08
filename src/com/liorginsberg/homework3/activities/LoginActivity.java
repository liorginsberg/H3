package com.liorginsberg.homework3.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.liorginsberg.homework3.InHereApplication;
import com.liorginsberg.homework3.R;
import com.liorginsberg.homework3.R.id;
import com.liorginsberg.homework3.R.layout;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends Activity {

	private static final String TAG = LoginActivity.class.getSimpleName();

	private EditText etUsername;
	private EditText etPassword;
	private Button btnLogin;
	private TextView tvSignup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		final Bundle bundle = getIntent().getExtras();

		etUsername = (EditText) findViewById(R.id.etUserName);
		etPassword = (EditText) findViewById(R.id.etPassword);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		tvSignup = (TextView) findViewById(R.id.tvSingup);
		tvSignup.setClickable(true);
		tvSignup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, SingupActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent, InHereApplication.REQUEST_SINGUP);
			}
		});

		btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String userName = etUsername.getText().toString();
				String password = etPassword.getText().toString();

				if (!"".equals(userName) && !"".equals(password)) {
					ParseUser.logInInBackground(userName, password, new LogInCallback() {

						@Override
						public void done(ParseUser user, ParseException e) {
							if (user != null) {
								Intent resultIntent = new Intent();
								resultIntent.putExtra("markerID", bundle.getString("markerID"));
								resultIntent.putExtra("msg", bundle.getString("msg"));
								setResult(RESULT_OK, resultIntent);
								Log.i(TAG, "Login Succeeded: " + user.getEmail());
								finish();
							} else {
								Log.e(TAG, "Code: " + e.getCode() + " - Msg: " + e.getMessage());
								Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
							}

						}
					});
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// TODO arived from SingupActivity but not in flow of setting a marker

		if (requestCode == InHereApplication.REQUEST_SINGUP) {
			if (resultCode == RESULT_OK) {
				if (data.hasExtra("markerID") && data.hasExtra("msg")) {
					final String markerID = data.getStringExtra("markerID");
					final String msg = data.getStringExtra("msg");
					Intent resultIntent = new Intent();
					resultIntent.putExtra("markerID", markerID);
					resultIntent.putExtra("msg", msg);
					setResult(RESULT_OK, resultIntent);
					finish();
				}
			}
		}
	}
}
