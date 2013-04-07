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

		etUsername = (EditText) findViewById(R.id.etUserName);
		etPassword = (EditText) findViewById(R.id.etPassword);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		tvSignup = (TextView)findViewById(R.id.tvSingup);
		tvSignup.setClickable(true);
		tvSignup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, SingupActivity.class);
				startActivity(intent);
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
								finish();
								Log.i(TAG, "Login Succeeded: " + user.getEmail());
							} else {
								Log.e(TAG, "Code: " + e.getCode() + " - Msg: " + e.getMessage());
								if(e.getCode() == ParseException.OBJECT_NOT_FOUND) {
									Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
								}

							}

						}
					});
				}
			}
		});
	}

}
