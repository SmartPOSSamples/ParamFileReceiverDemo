package com.cloudpos.notif.receiver;

import com.cloudpos.notif.activity.MainActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ParamFileReceiver extends BroadcastReceiver {

	private static final String MSG_TYPE_PARAM = "param:";
	@Override
	public void onReceive(Context context, Intent intent) {
		String notification = intent.getStringExtra("notification");
		if(notification != null && MSG_TYPE_PARAM.equals(notification.subSequence(0, MSG_TYPE_PARAM.length()))){
			String fileName = notification.substring(MSG_TYPE_PARAM.length());; //除去多余的字符：以TYPE长度截取参数
			sendMsgToActivity(context, fileName);
		}
	}
	private void sendMsgToActivity(Context context,String fileName){
		Intent intent = new Intent();
		intent.setClass(context, MainActivity.class);
		intent.putExtra("filename", fileName);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

}
