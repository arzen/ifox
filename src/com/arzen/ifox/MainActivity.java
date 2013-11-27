package com.arzen.ifox;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
/**
 * 默认为游戏主界面测试
 * @author Encore.liang
 *
 */
public class MainActivity extends Activity {
	
	private Button mBtnPay;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		mBtnPay = (Button) findViewById(R.id.btnPay);
		mBtnPay.setOnClickListener(mOnClickListener);

		iFox.init(this, null, null);
	}
	
	public OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (arg0.getId()) {
			case R.id.btnPay:
				iFox.chargePage(MainActivity.this, null, null);
				break;

			default:
				break;
			}
		}
	};
}
