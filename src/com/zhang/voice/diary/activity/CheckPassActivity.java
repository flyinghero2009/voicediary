package com.zhang.voice.diary.activity;

import java.util.List;

import com.zhang.voice.diary.R;
import com.zhang.voice.diary.view.LockPatternUtils;
import com.zhang.voice.diary.view.LockPatternView;
import com.zhang.voice.diary.view.LockPatternView.Cell;
import com.zhang.voice.diary.view.LockPatternView.OnPatternListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CheckPassActivity extends Activity {
	
	private LockPatternView lockPatternView = null;
	private LockPatternUtils lockPatternUtils = null;
	private SharedPreferences sharedPreferences = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.check_pass);
		sharedPreferences = getSharedPreferences("image", MODE_PRIVATE);
		lockPatternView = (LockPatternView) findViewById(R.id.lock_check);
		lockPatternUtils = new LockPatternUtils(this);
		lockPatternView.setOnPatternListener(new OnPatternListener() {
			
			public void onPatternStart() {
				// TODO Auto-generated method stub
				
			}
			
			@SuppressWarnings("static-access")
			public void onPatternDetected(List<Cell> pattern) {
				SharedPreferences preferences=getSharedPreferences("pass", Context.MODE_PRIVATE);
				String pass=preferences.getString("lock_pwd", "");
				if (pass.trim().equals(lockPatternUtils.patternToString(pattern))) {
					Intent intent=new Intent(CheckPassActivity.this,MainActivity.class);
					startActivity(intent);
					CheckPassActivity.this.finish();
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				}
				else {
					Toast.makeText(CheckPassActivity.this, R.string.wrong_pass, Toast.LENGTH_LONG).show();
				}
			}
			
			public void onPatternCleared() {
				// TODO Auto-generated method stub
				
			}
			
			public void onPatternCellAdded(List<Cell> pattern) {
				// TODO Auto-generated method stub
				
			}
		});
		setBackground();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setBackground();
	}
	private void setBackground() {
		// �õ���ǰ����
		LinearLayout layout = (LinearLayout) this.findViewById(R.id.check_pass_layout);
		// �õ�id,�˴�id�������ñ����������ģ��˴��ݲ�����
		int id = sharedPreferences.getInt("id", 0);
		if (id == 0) {// id=0˵���ǳ�ʼ��ʱ�ı���
			// ���ñ�������
			layout.setBackgroundResource(R.drawable.diary_view_bg);
		} else if (id == 1) {// id=1˵���û�ѡ���˵�һ��ͼƬ
			layout.setBackgroundResource(R.drawable.diary_view_bg);
		} else if (id == 2) {// id=2˵���û�ѡ���˵ڶ���ͼƬ
			layout.setBackgroundResource(R.drawable.spring);
		} else if (id == 3) {// id=3˵���û�ѡ���˵����ͼƬ
			layout.setBackgroundResource(R.drawable.summer);
		} else if (id == 4) {// id=4˵���û�ѡ���˵��ķ�ͼƬ
			layout.setBackgroundResource(R.drawable.autumn);
		} else if (id == 5) {// id=4˵���û�ѡ���˵��ķ�ͼƬ
			layout.setBackgroundResource(R.drawable.winter);
		}
	}
}
