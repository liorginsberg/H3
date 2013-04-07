package com.liorginsberg.homework3.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.liorginsberg.homework3.R;
import com.liorginsberg.homework3.R.id;
import com.liorginsberg.homework3.R.layout;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SingupActivity extends Activity {

	private EditText etRegUsername;
	private EditText etRegEmail;
	private EditText etRegPassword;
	private Button btnSingnup;
	private EditText etRegPasswordconf;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_singup);

		etRegUsername = (EditText) findViewById(R.id.etRegUsername);
		etRegEmail = (EditText) findViewById(R.id.etRegEmail);
		etRegPassword = (EditText) findViewById(R.id.etRegPassword);
		etRegPasswordconf = (EditText) findViewById(R.id.etRegPasswordConf);
		btnSingnup = (Button) findViewById(R.id.btnSignup);

		btnSingnup.setOnClickListener(new OnClickListener() {


			@Override
			public void onClick(View v) {
				String username = etRegUsername.getText().toString();
				String email = etRegEmail.getText().toString();
				String password = etRegPassword.getText().toString();
				String passwordConf = etRegPasswordconf.getText().toString();

				if (!"".equals(username) && !"".equals(email) && !"".equals(password) && !"".equals(passwordConf)) {
					if (password.equals(passwordConf)) {

						ParseUser user = new ParseUser();
						user.setUsername(username);
						user.setPassword(password);
						user.setEmail(email);

						// other fields can be set just like with ParseObject
						// user.put("phone", "650-253-0000");

						user.signUpInBackground(new SignUpCallback() {
							public void done(ParseException e) {
								if (e == null) {
									Toast.makeText(SingupActivity.this, "You have register successfully enjoy", Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(SingupActivity.this, "Code: " + e.getCode() + " Msg: " + e.getMessage(), Toast.LENGTH_SHORT).show();
								}
							}
						});
					} else {
						Toast.makeText(SingupActivity.this, "Passwords not matched", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(SingupActivity.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
				}
			}
		});

	}

}
