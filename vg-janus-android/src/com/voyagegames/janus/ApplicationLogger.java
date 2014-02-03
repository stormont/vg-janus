package com.voyagegames.janus;

import android.util.Log;

public class ApplicationLogger {

	public static void log(final String tag, final String msg) {
		Log.e(tag, msg);
	}

	public static void log(final String tag, final String msg, final Exception e) {
		Log.e(tag, msg, e);
	}
	
}
