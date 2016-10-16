package com.zhang.voice.diary.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.zhang.voice.diary.R;
import com.zhang.voice.diary.db.DiaryDao;
import com.zhang.voice.diary.model.Diary;
import com.zhang.voice.diary.utils.JsonParser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddDiaryActivity extends Activity {
	private TextView timeTextView = null;
	private TextView weekTextView = null;
	private Spinner weatherSpinner = null;
	private Button mPre = null;
	private LinearLayout mLinearLayout = null;
	// 语音听写对象
	private SpeechRecognizer mIat;
	String mTitleOrTextInfo = "";
	// 语音听写UI
	private RecognizerDialog mIatDialog;
	// 用HashMap存储听写结果
	private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
	private List<String> mTextInfos = new LinkedList<String>();
	private Toast mToast;
	private Calendar cal = Calendar.getInstance();
	private Date date = null;
	private SimpleDateFormat simpleDateFormat = null;
	public static final int WEEKDAYS = 7;
	private EditText diaryInfo = null;
	private EditText diaryTitle = null;
	public static String[] WEEK = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五",
			"星期六" };
	private ImageView back = null;
	private SharedPreferences preferences = null;
	private int ret = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		init();
		mLinearLayout = (LinearLayout)this.findViewById(R.id.adviceliner);
		if(MainActivity.mIsVoiceStart == true)
		{
			mLinearLayout.setVisibility(View.VISIBLE);
			mPre.setVisibility(View.VISIBLE);

			//语音初始化，在使用应用使用时需要初始化一次就好，如果没有这句会出现10111初始化失败
			 SpeechUtility.createUtility(AddDiaryActivity.this, "appid=552b97f1");
			// 初始化识别对象
				mIat = SpeechRecognizer.createRecognizer(this, mInitListener);
				// 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
				mIatDialog = new RecognizerDialog(this, mInitListener);
				mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setBackground();
	}

	@SuppressLint("SimpleDateFormat")
	private void init() {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.add_diary);
		preferences = getSharedPreferences("image", MODE_PRIVATE);
		date = cal.getTime();
		simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		timeTextView = (TextView) this.findViewById(R.id.time);
		timeTextView.setText(simpleDateFormat.format(date));
		weekTextView = (TextView) this.findViewById(R.id.week);
		weekTextView.setText(DateToWeek(date));
		mPre = (Button)this.findViewById(R.id.submit_advice);
		mPre.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(mTextInfos.size()>0)
				{
					String info = mTextInfos.remove(mTextInfos.size()-1);
					String infos = diaryInfo.getText().toString();
					String ininfos = infos.substring(0, infos.length()-info.length());
					diaryInfo.setText(ininfos);	
				}
				
			}
			
		});
		weatherSpinner = (Spinner) this.findViewById(R.id.weather);
		diaryInfo = (EditText) this.findViewById(R.id.edit_diary_info);
		diaryInfo.setOnTouchListener(new OnTouchListener()
		{

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				if(MainActivity.mIsVoiceStart == true)
				{
					mTitleOrTextInfo = "textinfo";
					mIatResults.clear();
					// 设置参数
					setParam();
					if (true) {
						// 显示听写对话框
						mIatDialog.setListener(recognizerDialogListener);
						mIatDialog.show();
						showTip("请开始说话…");
					} else {
						// 不显示听写对话框
						ret = mIat.startListening(recognizerListener);
						if (ret != ErrorCode.SUCCESS) {
							showTip("听写失败,错误码：" + ret);
						} else {
							showTip("请开始说话…");
						}
					}
				
					return true;
				}else
				{
					return false;	
				}
				
			}
			
		});
		diaryTitle = (EditText) this.findViewById(R.id.edit_title);
		diaryTitle.setOnTouchListener(new OnTouchListener()
		{

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				if(MainActivity.mIsVoiceStart == true)
				{

					mTitleOrTextInfo = "title";
					mIatResults.clear();
					// 设置参数
					setParam();
					if (true) {
						// 显示听写对话框
						mIatDialog.setListener(recognizerDialogListener);
						mIatDialog.show();
						showTip("请开始说话…");
					} else {
						// 不显示听写对话框
						ret = mIat.startListening(recognizerListener);
						if (ret != ErrorCode.SUCCESS) {
							showTip("听写失败,错误码：" + ret);
						} else {
							showTip("请开始说话…");
						}
					}
				
					return true;
				
				}else
				{
					return false;	
				}
				
			}
			
		});
		back = (ImageView) this.findViewById(R.id.back_add_diary);
		back.setOnClickListener(new BackListener());
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.weather, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		weatherSpinner.setAdapter(adapter);
		weatherSpinner.setPrompt(getString(R.string.weather));
		setBackground();
	}

	private void setBackground() {
		// 得到当前布局
		LinearLayout layout = (LinearLayout) this
				.findViewById(R.id.add_diary_layout);
		// 得到id,此处id是在设置背景里面产生的，此处暂不解释
		int id = preferences.getInt("id", 0);
		if (id == 0) {// id=0说明是初始化时的背景
			// 设置背景方法
			layout.setBackgroundResource(R.drawable.diary_view_bg);
		} else if (id == 1) {// id=1说明用户选择了第一幅图片
			layout.setBackgroundResource(R.drawable.diary_view_bg);
		} else if (id == 2) {// id=2说明用户选择了第二幅图片
			layout.setBackgroundResource(R.drawable.spring);
		} else if (id == 3) {// id=3说明用户选择了第三幅图片
			layout.setBackgroundResource(R.drawable.summer);
		} else if (id == 4) {// id=4说明用户选择了第四幅图片
			layout.setBackgroundResource(R.drawable.autumn);
		} else if (id == 5) {// id=4说明用户选择了第四幅图片
			layout.setBackgroundResource(R.drawable.winter);
		}
	}

	public static String DateToWeek(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);
		if (dayIndex < 1 || dayIndex > WEEKDAYS) {
			return null;
		}
		return WEEK[dayIndex - 1];
	}

	class BackListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			back();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			back();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void back() {
		if ((!diaryTitle.getText().toString().trim().equals(""))
				&& (!diaryInfo.getText().toString().trim().equals(""))) {
			DiaryDao diaryDao = new DiaryDao(AddDiaryActivity.this);
			Diary diary = new Diary();
			diary.setDate(timeTextView.getText().toString());
			diary.setWeek(weekTextView.getText().toString());
			diary.setWeather(weatherSpinner.getSelectedItem().toString());
			diary.setDiaryTitle(diaryTitle.getText().toString());
			diary.setDiaryInfo(diaryInfo.getText().toString());
			diaryDao.insert(diary);
			Intent intent = new Intent();
			intent.setClass(AddDiaryActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.push_below_in,
					R.anim.push_below_out);
			Toast.makeText(AddDiaryActivity.this, R.string.save_success, 0)
					.show();
		} else {
			Toast.makeText(AddDiaryActivity.this, R.string.empty_info, 0)
					.show();
			AddDiaryActivity.this.finish();
			overridePendingTransition(R.anim.push_below_in,
					R.anim.push_below_out);
		}
	}
	private InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(int code) {
			Log.d("-----------------------------------------", "SpeechRecognizer init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
				showTip("初始化失败，错误码：" + code);
			}
		}
	};
	public void setParam() {
		// 清空参数
		mIat.setParameter(SpeechConstant.PARAMS, null);

		// 设置听写引擎
		mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
		// 设置返回结果格式
		mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

		String lag = "zh_cn";//mSharedPreferences.getString("iat_language_preference","mandarin");
		if (lag.equals("en_us")) {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
		} else {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			// 设置语言区域
			mIat.setParameter(SpeechConstant.ACCENT, lag);
		}
		// 设置语音前端点
		mIat.setParameter(SpeechConstant.VAD_BOS, "4000");
		// 设置语音后端点
		mIat.setParameter(SpeechConstant.VAD_EOS, "1800");
		// 设置标点符号
		mIat.setParameter(SpeechConstant.ASR_PTT, "0");
		// 设置音频保存路径
		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()
				+ "/zhang/wavaudio.pcm");
		// 设置听写结果是否结果动态修正，为“1”则在听写过程中动态递增地返回结果，否则只在听写结束之后返回最终结果
		// 注：该参数暂时只对在线听写有效
		mIat.setParameter(SpeechConstant.ASR_DWA, "0");
	}
	private void showTip(final String str) {
		mToast.setText(str);
		mToast.show();
	}
	/**
	 * 听写UI监听器
	 */
	private RecognizerDialogListener recognizerDialogListener = new RecognizerDialogListener() {

		public void onResult(RecognizerResult results, boolean isLast) {
			printResult(results,mTitleOrTextInfo);
		}

		/**
		 * 识别回调错误.
		 */
		public void onError(SpeechError error) {
			showTip(error.getPlainDescription(true));
		}

	};
	/**
	 * 听写监听器。
	 */
	private RecognizerListener recognizerListener = new RecognizerListener() {


		@Override
		public void onBeginOfSpeech() {
			showTip("开始说话");
		}

		@Override
		public void onError(SpeechError error) {
			// Tips：
			// 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
			// 如果使用本地功能（语音+）需要提示用户开启语音+的录音权限。
			showTip(error.getPlainDescription(true));
		}

		@Override
		public void onEndOfSpeech() {
			showTip("结束说话");
		}

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			Log.d("----RecognizerResult-----", results.getResultString());
			printResult(results,mTitleOrTextInfo);

			if (isLast) {
				// TODO 最后的结果
			}
		}

		@Override
		public void onVolumeChanged(int volume) {
			showTip("当前正在说话，音量大小：" + volume);
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
		}
	};
	private void printResult(RecognizerResult results,String titleorTextinfo) {
		String text = JsonParser.parseIatResult(results.getResultString());

		String sn = null;
		// 读取json结果中的sn字段
		try {
			JSONObject resultJson = new JSONObject(results.getResultString());
			sn = resultJson.optString("sn");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		mIatResults.put(sn, text);

		StringBuffer resultBuffer = new StringBuffer();
		for (String key : mIatResults.keySet()) {
			resultBuffer.append(mIatResults.get(key));
		}
		if("title".equals(titleorTextinfo)==true)
		{
			diaryTitle.setText(resultBuffer.toString());
			//showTip("查询附近地点：" + resultBuffer.toString());
		}
		else if("textinfo".equals(titleorTextinfo)==true)
		{
			diaryInfo.append(resultBuffer.toString());
			mTextInfos.add(resultBuffer.toString());
		}
		///////mResultText.setText(resultBuffer.toString());
		///////mResultText.setSelection(mResultText.length());
	}

}