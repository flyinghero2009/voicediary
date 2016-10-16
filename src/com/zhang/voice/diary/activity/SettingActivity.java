package com.zhang.voice.diary.activity;

import com.zhang.voice.diary.R;
import com.zhang.voice.diary.view.MyWin8Button;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class SettingActivity  extends Activity{
	private MyWin8Button mHelpButton = null;
	private MyWin8Button mVoiceButton = null;
	private MyWin8Button mToMyselfButton = null;
	private MyWin8Button mAboutButton = null;
	private MyWin8Button mScirtButton = null;
	private MyWin8Button mRemindButton = null;
	private SharedPreferences sp = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting_activity);
		mHelpButton = (MyWin8Button)this.findViewById(R.id.diary_help);
		mVoiceButton= (MyWin8Button)this.findViewById(R.id.diary_voice);
		mToMyselfButton = (MyWin8Button)this.findViewById(R.id.diary_tomyself);
		mAboutButton = (MyWin8Button)this.findViewById(R.id.diary_about);
		mScirtButton =  (MyWin8Button)this.findViewById(R.id.diary_scirit);
		mRemindButton = (MyWin8Button)this.findViewById(R.id.diary_remind);
		
		mVoiceButton.setOnClickListener(new SettingListener());
		mHelpButton.setOnClickListener(new SettingListener());
		mToMyselfButton.setOnClickListener(new SettingListener());
		mAboutButton.setOnClickListener(new SettingListener());
		mScirtButton.setOnClickListener(new SettingListener());
		mRemindButton.setOnClickListener(new SettingListener());
		if(MainActivity.mIsVoiceStart == true)
		{
			mVoiceButton.setImageResource(R.drawable.voice_stop);
		}
		sp = this.getSharedPreferences("setting_voice_diary", Context.MODE_WORLD_WRITEABLE);
		
	}
	private void showTip(final String str) {
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}
	class SettingListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.diary_voice:
				SharedPreferences.Editor edit = sp.edit();
				if(MainActivity.mIsVoiceStart == true)
				{
					//mVoiceButton.setBackgroundResource(R.drawable.voice_start);
					mVoiceButton.setImageResource(R.drawable.voice_start);
					edit.putBoolean("voice_start_stop", false);
					showTip("语音功能已关闭");
					MainActivity.mIsVoiceStart = false;
				}else
				{
					//mVoiceButton.setBackgroundResource(R.drawable.voice_stop);
					mVoiceButton.setImageResource(R.drawable.voice_stop);
					edit.putBoolean("voice_start_stop", true);
					showTip("语音功能已打开");
					MainActivity.mIsVoiceStart = true;
				}
				edit.commit();
				break;
			case R.id.diary_help:
				Intent intent = new Intent(SettingActivity.this,
						HelpActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
				break;
			case R.id.diary_tomyself:
				intent = new Intent(SettingActivity.this, AdviceActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
				break;
			case R.id.diary_about:
				intent = new Intent(SettingActivity.this,
						AboutActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
				break;
			case R.id.diary_scirit:
				intent = new Intent(SettingActivity.this,
						SetPasswordActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
				break;
			case R.id.diary_remind:
				intent = new Intent(SettingActivity.this,
						SetRemindActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
				break;

			default:
				break;
			}
		}
	}
}
