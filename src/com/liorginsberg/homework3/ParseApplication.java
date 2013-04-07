package com.liorginsberg.homework3;

import android.app.Application;

import com.parse.Parse;

public class ParseApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		// Add your initialization code here
		Parse.initialize(this, "VkP5veFFBA8qkKuf8ecALEk8VEpPRWyCPgcxcFxj", "yD46s2Gf909soijCg9iwse9b5DbeUD750zRvmJ72");
	}
}
