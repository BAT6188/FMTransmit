package com.tchip.fmtransmit.ui.activity;

import com.tchip.fmtransmit.Constant;
import com.tchip.fmtransmit.R;
import com.tchip.fmtransmit.model.Typefaces;
import com.tchip.fmtransmit.util.MyLog;
import com.tchip.fmtransmit.util.SettingUtil;
import com.tchip.fmtransmit.view.SwitchButton;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import android.provider.Settings;

public class MainActivity extends Activity {

	private TextView textHint;
	private SeekBar fmSeekBar;

	private Button fmFreqDecrease, fmFreqIncrease;

	/** 接受语音发送的消息广播 **/
	private FMReceiver fmReceiver;

	public class FMReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Constant.Broadcast.NAVIBAR_OPEN_FM)) {
				switchFm.setChecked(true);
			} else if (action.equals(Constant.Broadcast.NAVIBAR_CLOSE_FM)) {
				switchFm.setChecked(false);
			} else if (action.equals(Constant.Broadcast.VOICE_SET_FM)) {
				switchFm.setChecked(SettingUtil
						.isFmTransmitOnSetting(MainActivity.this));

				Bundle extras = intent.getExtras();
				if (extras != null) {
					int freq = Integer.parseInt(extras.getString("freq"));
					MyLog.v("[FMReceiver]VOICE_SET_FM:" + freq);
					fmSeekBar.setProgress(freq / 10 - 875);
					textHint.setText("  " + freq / 100.0f + "MHz");
				}
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);

		initialLayout();

		fmReceiver = new FMReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.Broadcast.NAVIBAR_OPEN_FM);
		filter.addAction(Constant.Broadcast.NAVIBAR_CLOSE_FM);
		filter.addAction(Constant.Broadcast.VOICE_SET_FM);
		registerReceiver(fmReceiver, filter);
	}

	protected void onDestory() {
		super.onDestroy();
		if (fmReceiver != null) {
			unregisterReceiver(fmReceiver);
		}
	}

	private SwitchButton switchFm;

	private void initialLayout() {

		textHint = (TextView) findViewById(R.id.textHint);
		textHint.setTypeface(Typefaces.get(this, Constant.Path.FONT
				+ "Font-Helvetica-Neue-LT-Pro.otf"));

		switchFm = (SwitchButton) findViewById(R.id.switchFm); // 开关
		switchFm.setChecked(isFmTransmitOn());
		switchFm.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Settings.System.putString(getContentResolver(),
						Constant.FMTransmit.SETTING_ENABLE, isChecked ? "1"
								: "0");
				SettingUtil.SaveFileToNode(SettingUtil.nodeFmEnable,
						(isChecked ? "1" : "0"));

				sendBroadcast(new Intent(
						isChecked ? "com.tchip.FM_OPEN_CARLAUNCHER"
								: "com.tchip.FM_CLOSE_CARLAUNCHER"));
			}
		});

		fmSeekBar = (SeekBar) findViewById(R.id.fmSeekBar);
		// 875-1080
		// 0- 205
		fmSeekBar.setMax(205);
		int nowFrequency = SettingUtil.getFmFrequceny(this); // 当前频率
		fmSeekBar.setProgress(nowFrequency / 10 - 875);
		textHint.setText("  " + nowFrequency / 100.0f + "MHz");
		fmSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				SettingUtil.setFmFrequency(getApplicationContext(),
						(seekBar.getProgress() + 875) * 10);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				float frequency = (progress + 875.0f) / 10;
				textHint.setText("  " + frequency + "MHz");
			}
		});

		// fm频率0.1增加减少
		fmFreqDecrease = (Button) findViewById(R.id.fmFreqDecrease);
		fmFreqIncrease = (Button) findViewById(R.id.fmFreqIncrease);
		fmFreqDecrease.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setFmFrequencySmallDeIncrease(false);
			}
		});
		fmFreqIncrease.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setFmFrequencySmallDeIncrease(true);
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			backToVice();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	private void backToVice() {
		finish();
		overridePendingTransition(R.anim.zms_translate_down_out,
				R.anim.zms_translate_down_in);
	}

	/**
	 * 小幅度的调整fm频率
	 * 
	 * @param deincrease
	 */
	private void setFmFrequencySmallDeIncrease(boolean deincrease) {
		int nowFrequency = SettingUtil.getFmFrequceny(this)
				+ (deincrease ? 10 : -10); // 当前频率
		if (nowFrequency >= 8750 && nowFrequency <= 10800) {
			fmSeekBar.setProgress(nowFrequency / 10 - 875);
			textHint.setText("  " + nowFrequency / 100.0f + "MHz");

			SettingUtil.setFmFrequency(this, nowFrequency);
		}
	}

	/**
	 * FM发射是否打开
	 * 
	 * @return
	 */
	private boolean isFmTransmitOn() {
		boolean isFmTransmitOpen = false;
		String fmEnable = Settings.System.getString(getContentResolver(),
				Constant.FMTransmit.SETTING_ENABLE);
		if (fmEnable.trim().length() > 0) {
			if ("1".equals(fmEnable)) {
				isFmTransmitOpen = true;
			} else {
				isFmTransmitOpen = false;
			}
		}
		return isFmTransmitOpen;
	}

}
