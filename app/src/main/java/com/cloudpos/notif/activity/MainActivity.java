package com.cloudpos.notif.activity;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.cloudpos.util.TextViewUtil;
import com.wizarpos.notif.activity.R;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class MainActivity extends ConstantActivity {
	private static final String TAG = "MainActivity";
    private static String mFileName = null;
	private Context mContext;
	private String msg = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getApplicationContext();
		setContentView(R.layout.activity_main);

		GridView gridView = (GridView) this.findViewById(R.id.gridview);
		gridView.setAdapter(new MyAdapter());
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Log.e(TAG, "current position" + arg2);
				switch (arg2) {
				case 0:
					if(mFileName != null){
						msg = readParam(mContext, mFileName);
					}else{
						msg = getString(R.string.receive_failing);
					}
					writerInSuccessLog(msg);
					break;
					
				case 1:
					log_text.setText("");
					break;
					
				case 2:
					if(mFileName != null){
						sendReadParamResult(mContext, mFileName, true);
					}else{
						msg = getString(R.string.receive_failing);
						writerInSuccessLog(msg);
					}
					break;

				case 3:
					if(mFileName != null){
						sendReadParamResult(mContext, mFileName, false);
					}else{
						msg = getString(R.string.receive_failing);
						writerInSuccessLog(msg);
					}
					break;
					
				default:
					break;
				}
			}
		});
		log_text = (TextView) this.findViewById(R.id.text_result);
		log_text.setMovementMethod(ScrollingMovementMethod.getInstance());
		
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == DEFAULT_LOG) {
					log_text.append("\t" + msg.obj + "\n");
				} else if (msg.what == SUCCESS_LOG) {
					String str = "\t" + msg.obj + "\n";
					TextViewUtil.infoBlueTextView(log_text, str);
				} else if (msg.what == FAILED_LOG) {
					String str = "\t" + msg.obj + "\n";
					TextViewUtil.infoRedTextView(log_text, str);
				} else if (msg.what == CLEAR_LOG) {
					log_text.setText("");
				}
			}
		};
		
		Intent intent = getIntent();
		String fileName = intent.getStringExtra("filename");
		if(fileName != null){
			writerInFailedLog(getString(R.string.receive_success));
			mFileName = fileName;
			msg = readParam(mContext, mFileName);
			writerInSuccessLog(msg);
		}
	}
	
	class MyAdapter extends BaseAdapter {
		private String[] datas = {getString(R.string.autotest_run3),getString(R.string.settings)
				,getString(R.string.autotest_run4),getString(R.string.autotest_run5_on)};
		@Override
		public int getCount() {
			return datas.length;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(mContext, R.layout.gradview_item, null);
				holder.textview = (TextView)convertView.findViewById(R.id.tv_gridview);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.textview.setText(datas[position]);
			return convertView;
		}
	}
	
	class ViewHolder {
		public TextView textview;
	}
	
    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        super.onActivityResult(requestCode, resultCode, data);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public static final String URI_PARAM_FILE = "content://com.wizarpos.wizarviewagent.paramfilesprovider/file/";
	// 表示应用是否已经应用 或适配。
	public static final String KEY_READED = "readed";
	// 如果应用失败，可以上传错误信息。
	public static final String KEY_ERRLOG = "errlog";

	private String readParam(Context context,String fileName){
		Uri uri = Uri.parse(URI_PARAM_FILE + fileName);
		ContentResolver resolver = context.getContentResolver();
		try {
			StringBuilder builder = readInputStream(resolver.openInputStream(uri), "UTF8");
			return builder.toString();
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} 
		return null;
	}
	
	/**
     * read file
     * 
     * @param in
     * @param charsetName The name of a supported {@link java.nio.charset.Charset </code>charset<code>}
     * @return if file not exist, return null, else return content of file
     * @throws RuntimeException if an error occurs while operator BufferedReader
     */
    public static StringBuilder readInputStream(InputStream in, String charsetName) {
        StringBuilder fileContent = new StringBuilder("");

        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(in, charsetName);
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (!fileContent.toString().equals("")) {
                    fileContent.append("\r\n");
                }
                fileContent.append(line);
            }
            reader.close();
            return fileContent;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }

	private void sendReadParamResult(Context context, String fileName, boolean isSuccess){
		Uri uri = Uri.parse(URI_PARAM_FILE + fileName);
		ContentResolver resolver = context.getContentResolver();
		ContentValues vaules = new ContentValues();
		// 代表参数信息是已经已经应用成功。

		if(isSuccess){
			vaules.put("readed", true);
		}else{
			// 代表参数信息是无法应用。
			vaules.put("readed", false);
			vaules.put("errlog", getString(R.string.apply_fail));//提示服务器运营人员问题出在哪里。
		}

		// 将参数适配的结果通知给服务
		Uri resultUri = resolver.insert(uri, vaules);
		
		writerInFailedLog(resultUri.toString());
//		 }
	}
}
