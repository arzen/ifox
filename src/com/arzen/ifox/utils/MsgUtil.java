package com.arzen.ifox.utils;

import android.content.Context;
import android.widget.Toast;

public class MsgUtil {
	public static void msg(String message, Context con) {
		Toast.makeText(con, message, Toast.LENGTH_SHORT).show();
	}
	public static void msg(int messageId, Context con) {
		Toast.makeText(con, messageId, Toast.LENGTH_SHORT).show();
	}
}
