package com.tchip.fmtransmit;

public interface Constant {
	/** Debug：打印Log */
	public static final boolean isDebug = true;

	/** 日志Tag */
	public static final String TAG = "ZMS";

	/** FM发射 */
	public static final class FMTransmit {
		/** 系统设置：FM发射开关 */
		public static final String SETTING_ENABLE = "fm_transmitter_enable";

		/** 系统设置：FM发射频率 */
		public static final String SETTING_CHANNEL = "fm_transmitter_channel";

		public static final int CHANNEL_LOW = 8750;
		public static final int CHANNEL_MIDDLE = 9750;
		public static final int CHANNEL_HIGH = 10750;

		public static final String HINT_LOW = "87.5";
		public static final String HINT_MIDDLE = "97.5";
		public static final String HINT_HIGH = "107.5";
	}

	/** 广播 */
	public static final class Broadcast {
		/** 导航栏打开FM发射 */
		public static final String NAVIBAR_OPEN_FM = "com.tchip.FM_OPEN_SYSTEMUI";

		/** 导航栏关闭FM发射 */
		public static final String NAVIBAR_CLOSE_FM = "com.tchip.FM_CLOSE_SYSTEMUI";

		/** 语音设置FM频率 putExtra("freq", freq) */
		public static final String VOICE_SET_FM = "com.tchip.FM_SET_FREQ";
	}

	/** 路径 */
	public static final class Path {

		/** 字体目录 */
		public static final String FONT = "fonts/";
	}

	/** SharedPreferences名称 */
	public static final String SHARED_PREFERENCES_NAME = "FMTransmit";

}
