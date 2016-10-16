package com.zhang.voice.diary.activity;
import com.zhang.voice.diary.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class WelcomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.diary_welecome);
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.welcome_anim);
		ImageView view = (ImageView) findViewById(R.id.welcome_bg);
		view.setAnimation(animation);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				Intent intent = new Intent();
				intent.setClass(WelcomeActivity.this, AccessActivity.class);
				startActivity(intent);
				overridePendingTransition(0, 0);
				WelcomeActivity.this.finish();
			}
		});
	
	}
}
