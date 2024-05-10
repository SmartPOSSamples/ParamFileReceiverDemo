package com.cloudpos.notif.activity;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class ConstantActivity extends Activity {
    public static final int TIME_OUT = 60000;
    protected static final int DEFAULT_LOG = 1;
    protected static final int SUCCESS_LOG = 2;
    protected static final int FAILED_LOG = 3;
    protected static final int CLEAR_LOG = 0;
    protected static Handler mHandler = null;
    protected TextView log_text;

    public static void writerInLog(String obj) {
        Message msg = new Message();
        msg.what = DEFAULT_LOG;
        msg.obj = obj;
        mHandler.sendMessage(msg);
    }

    public static void writerInSuccessLog(String obj) {
        Message msg = new Message();
        msg.what = SUCCESS_LOG;
        msg.obj = obj;
        mHandler.sendMessage(msg);
    }

    public static void writerInFailedLog(String obj) {
        Message msg = new Message();
        msg.what = FAILED_LOG;
        msg.obj = obj;
        mHandler.sendMessage(msg);
    }

    public static void clear() {

        if (mHandler != null) {
            Message msg = new Message();
            msg.what = CLEAR_LOG;
            mHandler.sendMessage(msg);
        }
    }

}
