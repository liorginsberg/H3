package com.liorginsberg.homework3;

import android.app.Application;

import com.parse.Parse;

public class InHereApplication extends Application {

	public static final int REQUEST_LOGIN = 101;
	public static final int REQUEST_SINGUP = 102;

	@Override
	public void onCreate() {
		super.onCreate();

		// Add your initialization code here
		Parse.initialize(this, "VkP5veFFBA8qkKuf8ecALEk8VEpPRWyCPgcxcFxj", "yD46s2Gf909soijCg9iwse9b5DbeUD750zRvmJ72");
	}
}
